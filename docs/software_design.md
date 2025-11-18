# Software Design Document
## Exploding Kittens - Refactored Architecture

### Document Information
- **Project**: Exploding Kittens Card Game
- **Version**: 2.0 (Refactored)
- **Date**: 2025-11-18
- **Purpose**: Document major architectural changes from original to refactored design

---

## Major Design Changes

### 1. Strategy Pattern Implementation

This change introduces a card behavior system using the Strategy design pattern, consisting of four interconnected components: the PlayableCard interface, GameContext class, CardEffect value object, and CardFactory.

#### What Changed

**Before (Original Design):**
```java
// GameUI.java - Lines 1514-1601 (88-line switch statement)
switch (cardType) {
    case ATTACK:
        playAttack(false);
        return;
    case SHUFFLE:
        playShuffle();
        break;
    case EXPLODING_KITTEN:
        // inline business logic
        break;
    // ... 20+ more cases
}

// Card.java - Anemic domain model
class Card {
    private CardType cardType;  // Just data, no behavior
    public CardType getCardType() { return cardType; }
    // Only getters/setters
}

// Game.java - Card behaviors scattered
public void playAttack() { ... }           // 5 lines
public void playShuffle(int n) { ... }     // 5 lines
public boolean playExplodingKitten() { ... } // 13 lines
// ... 20+ more card methods

// GameUI.java - Duplicate card logic + UI
private void playExplodingKitten(int playerIndex) { ... }  // 108 lines!
private void playShuffle() { ... }                         // 31 lines

// No standard return type - inconsistent patterns
private void playAttack(boolean targeted) { ... }      // void
private boolean playExplodingKitten(int p) { ... }     // boolean
private int playSkip(boolean superSkip) { ... }        // int
```

**After (Refactored Design):**
```java
// PlayableCard.java - Strategy interface
interface PlayableCard {
    CardEffect play(GameContext context);
    boolean canBeNoped();
    boolean endsTurn();
    boolean validate(GameContext context);
    CardType getCardType();
    String getDescription();
}

// AttackCard.java - Self-contained behavior (~50 lines)
class AttackCard implements PlayableCard {
    private static final int TURNS_TO_ADD = 2;

    @Override
    public CardEffect play(GameContext context) {
        Player nextPlayer = context.getNextAlivePlayer();
        context.getGame().addTurnsToPlayer(nextPlayer, TURNS_TO_ADD);
        return new CardEffect(true, "Attack successful", true);
    }

    @Override
    public boolean canBeNoped() { return true; }

    @Override
    public boolean endsTurn() { return true; }

    @Override
    public boolean validate(GameContext context) {
        return context.getNextAlivePlayer() != null;
    }
}

// ShuffleCard.java - Self-contained behavior (~40 lines)
class ShuffleCard implements PlayableCard {
    private static final int MAX_SHUFFLES = 100;

    @Override
    public CardEffect play(GameContext context) {
        GameUIHandler ui = context.getUIHandler();
        int count = ui.askForShuffleCount(1, MAX_SHUFFLES);

        for (int i = 0; i < count; i++) {
            context.getDeck().shuffleDeck();
        }

        return new CardEffect(true, "Shuffled " + count + " times", false);
    }

    @Override
    public boolean canBeNoped() { return true; }

    @Override
    public boolean endsTurn() { return false; }
}

// GameContext.java - Centralized state access
class GameContext {
    private Game game;
    private Player currentPlayer;
    private Deck deck;
    private GameUIHandler uiHandler;

    public GameContext(Game game, Player currentPlayer, Deck deck) {
        this.game = game;
        this.currentPlayer = currentPlayer;
        this.deck = deck;
    }

    public Player getNextAlivePlayer() {
        // Encapsulates complex "find next alive player" logic
        int index = (currentPlayer.getPlayerID() + 1) % game.getNumberOfPlayers();
        while (game.checkIfPlayerDead(index)) {
            index = (index + 1) % game.getNumberOfPlayers();
        }
        return game.getPlayerAtIndex(index);
    }

    public Game getGame() { return game; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public Deck getDeck() { return deck; }
    public GameUIHandler getUIHandler() { return uiHandler; }
    public void setUIHandler(GameUIHandler handler) { this.uiHandler = handler; }
}

// CardEffect.java - Standardized return value
class CardEffect {
    private boolean success;
    private String message;
    private boolean shouldEndTurn;
    private List<Integer> affectedPlayers;
    private Map<String, Object> additionalData;

    public CardEffect(boolean success, String message, boolean shouldEndTurn) {
        this.success = success;
        this.message = message;
        this.shouldEndTurn = shouldEndTurn;
        this.affectedPlayers = new ArrayList<>();
        this.additionalData = new HashMap<>();
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public boolean shouldEndTurn() { return shouldEndTurn; }
}

// CardFactory.java - Centralized card creation
class CardFactory {
    private static CardFactory instance;
    private Map<CardType, Supplier<PlayableCard>> cardCreators;

    private CardFactory() {
        cardCreators = new HashMap<>();
        cardCreators.put(CardType.ATTACK, AttackCard::new);
        cardCreators.put(CardType.NOPE, NopeCard::new);
        cardCreators.put(CardType.SHUFFLE, ShuffleCard::new);
        cardCreators.put(CardType.EXPLODING_KITTEN, ExplodingKittenCard::new);
    }

    public static CardFactory getInstance() {
        if (instance == null) {
            instance = new CardFactory();
        }
        return instance;
    }

    public PlayableCard createCard(CardType type) {
        Supplier<PlayableCard> creator = cardCreators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown card type: " + type);
        }
        return creator.get();
    }
}

// Usage - Clean and simple
PlayableCard card = CardFactory.getInstance().createCard(CardType.ATTACK);
GameContext context = game.createGameContext();
CardEffect effect = card.play(context);

if (effect.isSuccess() && effect.shouldEndTurn()) {
    game.endTurn();
}
```

#### Design Principles Applied

- **Strategy Pattern**: Encapsulate card behaviors as interchangeable algorithms
- **Facade Pattern**: GameContext provides simplified interface to complex subsystems
- **Value Object Pattern**: CardEffect is immutable object representing results
- **Factory Pattern**: CardFactory centralizes object creation
- **Open/Closed Principle**: Open for extension (new cards), closed for modification
- **Single Responsibility Principle**: Each card class has one responsibility
- **Polymorphism over Conditionals**: Replace switch statements with method dispatch
- **Law of Demeter**: Cards only talk to GameContext, not deep object chains
- **Dependency Inversion**: Cards depend on GameContext abstraction, not concrete Game
- **Command-Query Separation**: play() method returns information without hiding state changes

#### Analysis

The original design had from architectural problems that made the codebase difficult to maintain and extend. Card behavior was scattered across multiple files with an 88-line switch statement in GameUI serving as the primary dispatch mechanism. The Card class itself was an anemic domain model that only contained data with no behavior, violating core object-oriented principles. Each card type had its logic split between Game.java methods, GameUI.java methods, and switch case handlers, creating extensive duplication. Testing was nearly impossible since card logic required instantiating the entire game infrastructure including UI components and simulating user input via Scanner. Moreover, the return types were inconsistent across different card methods with some returning void, others boolean, and others int, which makes it impossible to handle card effects uniformly.

The Strategy pattern implementation addresses these issues through four interconnected components. The PlayableCard interface defines a standard contract that all cards must implement, enabling polymorphic behavior and eliminating the need for switch statements. Cards like AttackCard and ShuffleCard become self-contained units of, with all their behavior encapsulated in a single, focused class. The GameContext class acts as a facade, providing cards with clean access to game state without tight coupling to Game's internal structure. Instead of cards directly calling game.getPlayerAtIndex(i).getHandSize(), they use simple methods like context.getNextAlivePlayer(), which encapsulates complex logic such as finding the next alive player. This reduces the knowledge each card needs about the game's internal workings.

The CardEffect value object standardizes return values across all cards, making card effects composable and testable. Every card now returns the same type, carrying information about success, messages, whether the turn should end, affected players, and additional metadata. This enables consistent effect handling, easy testing through direct result verification, and the ability to collect and process multiple card effects uniformly. The CardFactory completes the pattern by centralizing card creation, making it easy to add new cards. This achieves true Open/Closed Principle compliance where adding a new card such as ReverseCard requires creating one new class file and adding one line to register it, compared to the original design requiring modifications in five different locations.

---

### 2. Service Layer Extraction

This change extracts complex business logic into dedicated service classes, specifically the NopeHandler for managing Nope card chain reactions and the CardValidator for centralizing validation rules. This separation creates a clear service layer between the domain model and application logic.

#### What Changed

**Before (Original Design):**
```java
// GameUI.java - Lines 526-571 (45 lines of complex recursive logic embedded in UI)
private boolean checkAllPlayersForNope(int playerIndex) {
    for (int i = 0; i < game.getNumberOfPlayers(); i++) {
        if (i != playerIndex) {
            if (game.checkIfPlayerHasCard(i, CardType.NOPE)) {
                System.out.print(MessageFormat.format(
                    messages.getString("askIfWantToPlayNope"), i));
                int playNopeCard = scanner.nextInt();  // UI coupled with logic

                if (playNopeCard == 1) {
                    playNope(i);
                    // RECURSIVE CALL - checks for counter-Nopes
                    if (checkAllPlayersForNope(i)) {
                        return false;  // Even number of Nopes
                    }
                    return true;  // Odd number of Nopes
                } else if (playNopeCard != 2) {
                    i--;  // Invalid input handling
                    System.out.println(messages.getString("invalidMessage"));
                }
            }
        }
    }
    return false;
}

// Validation scattered everywhere
class GameUI {
    private void checkCardIfInvalid(CardType cardType) {
        CardType[] unplayableCards = {
            CardType.EXPLODING_KITTEN,
            CardType.IMPLODING_KITTEN,
            CardType.STREAKING_KITTEN,
            CardType.DEFUSE
        };
        for (CardType unplayable : unplayableCards) {
            if (cardType == unplayable) {
                throw new UnsupportedOperationException("Cannot play " + cardType);
            }
        }
    }
}

// Game.java - Validation duplicated
private boolean checkUserOutOfBounds(int playerIndex) {
    return playerIndex < 0 || playerIndex >= numberOfPlayers;
}

// Player bounds check appears in 10+ places
if (playerIndex < 0 || playerIndex >= numberOfPlayers) { ... }

// Card presence check duplicated everywhere
if (game.checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) { ... }
```

**After (Refactored Design):**
```java
// NopeHandler.java - Dedicated service class (~80 lines)
class NopeHandler {
    private Game game;
    private GameUIHandler uiHandler;

    public NopeHandler(Game game, GameUIHandler uiHandler) {
        this.game = game;
        this.uiHandler = uiHandler;
    }

    public boolean processNopeChain(PlayableCard playedCard, Player playingPlayer) {
        if (!canCardBeNoped(playedCard)) {
            return false;  // Card cannot be noped
        }

        int nopeCount = 0;
        boolean continueChecking = true;

        while (continueChecking) {
            continueChecking = false;

            for (Player player : getOtherPlayers(playingPlayer)) {
                if (player.hasCard(CardType.NOPE)) {
                    boolean wantsToNope = uiHandler.askToPlayNope(player);

                    if (wantsToNope) {
                        player.removeCard(CardType.NOPE);
                        uiHandler.displayMessage("Player " + player.getPlayerID() + " played Nope!");
                        nopeCount++;
                        continueChecking = true;  // Check for counter-Nope
                        break;
                    }
                }
            }
        }

        return nopeCount % 2 == 1;  // Odd number = blocked
    }

    public boolean canCardBeNoped(PlayableCard card) {
        return card.canBeNoped();
    }

    private List<Player> getOtherPlayers(Player excludePlayer) {
        return Arrays.stream(game.getAllPlayers())
            .filter(p -> p.getPlayerID() != excludePlayer.getPlayerID())
            .collect(Collectors.toList());
    }
}

// CardValidator.java - Centralized validation (~60 lines)
class CardValidator {

    public static boolean validateCardPlay(PlayableCard card, GameContext context) {
        Player player = context.getCurrentPlayer();

        if (!isCardInHand(player, card.getCardType())) {
            return false;
        }

        if (!card.validate(context)) {
            return false;
        }

        return true;
    }

    public static boolean canPlayerPlayCard(Player player, CardType cardType) {
        if (player.getIsDead()) {
            return false;
        }

        if (isUnplayableCard(cardType)) {
            return false;
        }

        return player.hasCard(cardType);
    }

    public static boolean isCardInHand(Player player, CardType cardType) {
        return player.hasCard(cardType);
    }

    public static boolean validatePlayerIndex(int playerIndex, int numberOfPlayers) {
        return playerIndex >= 0 && playerIndex < numberOfPlayers;
    }

    public static boolean validateDeckIndex(int deckIndex, int deckSize) {
        return deckIndex >= 0 && deckIndex <= deckSize;
    }

    private static boolean isUnplayableCard(CardType cardType) {
        return cardType == CardType.EXPLODING_KITTEN ||
               cardType == CardType.IMPLODING_KITTEN ||
               cardType == CardType.STREAKING_KITTEN ||
               cardType == CardType.DEFUSE;
    }
}

// Usage - Clean separation
NopeHandler nopeHandler = new NopeHandler(game, uiHandler);
boolean isBlocked = nopeHandler.processNopeChain(card, currentPlayer);

if (!isBlocked && CardValidator.validateCardPlay(card, context)) {
    CardEffect effect = card.play(context);
}
```

#### Design Principles Applied

- **Single Responsibility Principle**: NopeHandler handles Nope chains, CardValidator handles validation
- **Separation of Concerns**: Business logic separated from UI and domain models
- **Dependency Inversion**: NopeHandler depends on GameUIHandler interface, not on a concrete UI class
- **DRY (Don't Repeat Yourself)**: Validation logic is centralized

#### Analysis

The original design embedded complex business logic deep within UI code, creating significant maintenance and testing challenges. The Nope chain mechanism, which allows players to counter each other's Nope cards recursively, consisted of intricate logic mixed with console I/O calls scattered throughout GameUI.java. This recursive algorithm was tightly coupled to Scanner input, making it impossible to test without simulating actual user keyboard input. Similarly, validation logic was duplicated across the codebase with the same player bounds check (playerIndex < 0 || playerIndex >= numberOfPlayers) appearing in over 10 different locations. Card playability checks were scattered between Game and GameUI with no consistent pattern, and the unplayable cards array lived in GameUI despite being a core business rule. This meant that changing a validation rule, such as allowing Defuse cards to be played proactively, required tracking down and modifying code in multiple files.

The service layer extraction addresses these issues by creating two focused service classes that handle complex algorithms independent of UI concerns. NopeHandler encapsulates the entire Nope chain resolution process in a clear, testable class. Instead of recursive calls buried in UI code, the handler uses an iterative while loop that continues checking for counter-Nopes until no more players wish to respond. Moreover, NopeHandler depends on the GameUIHandler interface rather than a concrete Scanner input, meaning the same logic works seamlessly with other UI interfaces. CardValidator centralizes all validation rules in one location with static utility methods covering player index validation, deck index validation, card playability checks, and the definitive list of unplayable cards. Every validation operation in the system now calls these methods, eliminating duplication and creating a single source of truth.

With these changes, testing becomes straightforward as both services can be unit tested in complete isolation. For NopeHandler, tests can mock the GameUIHandler to simulate different sequences of player responses and verify the correct blocked/allowed outcome without any actual UI interaction. CardValidator tests simply verify that validation methods return correct boolean values for edge cases like negative indices, out-of-bounds indices, dead players, and unplayable card types. Maintenance of the codebase improves dramatically because Nope chain bugs mean checking NopeHandler.java file rather than searching through 1000+ lines of GameUI code. Changing validation rules requires modifying only CardValidator, with all dependent code automatically using the updated rules. The code becomes more reusable since NopeHandler works with any UI implementation and CardValidator works anywhere validation is needed.

---

### 3. UI Abstraction

This change introduces the GameUIHandler interface to abstract all user interface operations, decoupling domain logic from the presentation layer.

#### What Changed

**Before (Original Design):**
```java
// Card logic directly coupled to console I/O
class GameUI {
    private Scanner scanner;
    private ResourceBundle messages;

    private void playShuffle() {
        System.out.println("How many times to shuffle?");
        int count = scanner.nextInt();  // Direct console dependency

        if (count > 100) {
            System.out.println("Max 100 shuffles");  // Direct console output
            // Try again with more prompts
        }

        game.playShuffle(count);
    }

    private boolean playExplodingKitten(int playerIndex) {
        System.out.println("You drew an Exploding Kitten!");

        if (game.checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) {
            System.out.print("Choose position to insert (0 to " + deckSize + "): ");
            int position = scanner.nextInt();  // Blocking keyboard input
            // More console I/O mixed with logic
        }
    }
}

// System.out and Scanner calls scattered throughout:
// - In card play methods
// - In validation logic
// - In game state display
// - Impossible to reuse with GUI, web, or AI
```

**After (Refactored Design):**
```java
// GameUIHandler.java - Abstract UI operations interface
interface GameUIHandler {
    int askForShuffleCount(int min, int max);
    int askForInsertionIndex(int maxIndex);
    int askForDefuseCardSelection(Player player);
    void displayMessage(String message);
    boolean confirmAction(String prompt);
    boolean askToPlayNope(Player player);
}

// ConsoleUIHandler.java - Console implementation (~100 lines)
class ConsoleUIHandler implements GameUIHandler {
    private Scanner scanner;
    private ResourceBundle messages;

    public ConsoleUIHandler(Scanner scanner, ResourceBundle messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    @Override
    public int askForShuffleCount(int min, int max) {
        while (true) {
            System.out.print(messages.getString("askShuffleCount"));
            try {
                int count = scanner.nextInt();
                if (count >= min && count <= max) {
                    return count;
                }
                System.out.println(messages.getString("invalidRange"));
            } catch (InputMismatchException e) {
                System.out.println(messages.getString("invalidInput"));
                scanner.next();  // Clear invalid input
            }
        }
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public boolean confirmAction(String prompt) {
        System.out.print(prompt + " (1=Yes, 2=No): ");
        return scanner.nextInt() == 1;
    }
}

// ShuffleCard.java - UI-independent domain logic
class ShuffleCard implements PlayableCard {
    @Override
    public CardEffect play(GameContext context) {
        GameUIHandler ui = context.getUIHandler();
        int count = ui.askForShuffleCount(1, 100);  // Abstract UI call

        for (int i = 0; i < count; i++) {
            context.getDeck().shuffleDeck();
        }

        return new CardEffect(true, "Shuffled " + count + " times", false);
    }
}

// ExplodingKittenCard.java - UI-independent
class ExplodingKittenCard implements PlayableCard {
    @Override
    public CardEffect play(GameContext context) {
        GameUIHandler ui = context.getUIHandler();
        Player player = context.getCurrentPlayer();

        if (player.hasCard(CardType.DEFUSE)) {
            int position = ui.askForInsertionIndex(context.getDeck().getDeckSize());
            // Defuse logic - no console dependencies
            return new CardEffect(true, "Defused and reinserted", true);
        }

        ui.displayMessage("You exploded!");
        return new CardEffect(false, "Player eliminated", true);
    }
}

// Can now easily support multiple UIs:

// Swing GUI
class SwingUIHandler implements GameUIHandler {
    private JFrame frame;

    @Override
    public int askForShuffleCount(int min, int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, min, max, 1));
        JOptionPane.showMessageDialog(frame, spinner, "Shuffle Count",
            JOptionPane.QUESTION_MESSAGE);
        return (Integer) spinner.getValue();
    }
}

// Web UI
class WebUIHandler implements GameUIHandler {
    private HttpSession session;

    @Override
    public int askForShuffleCount(int min, int max) {
        // Send request to web client, await response
        return (Integer) session.getAttribute("shuffleCount");
    }
}

// Usage - same game logic, different UI
GameContext context = game.createGameContext();
context.setUIHandler(new ConsoleUIHandler(scanner, messages));  // Console
// OR
context.setUIHandler(new SwingUIHandler(jFrame));  // GUI
// OR
context.setUIHandler(new WebUIHandler(session));  // Web
// OR
context.setUIHandler(new AIUIHandler(aiEngine));  // AI

// All use identical card implementations!
```

#### Design Principles Applied

- **Dependency Inversion Principle**: Domain logic depends on GameUIHandler abstraction, not concrete Scanner/JFrame/etc.
- **Interface Segregation Principle**: GameUIHandler defines specific methods for specific needs
- **Strategy Pattern**: Different UI implementations are interchangeable strategies
- **Separation of Concerns**: Domain logic completely separate from presentation layer
- **Open/Closed Principle**: Can add new UI types without modifying card logic

#### Analysis

The original design tightly coupled game logic to console I/O through pervasive use of System.out.println and scanner.nextInt calls embedded directly in card behavior methods. The playShuffle method in GameUI contained console prompts, input reading, validation with error messages printed to System.out, and retry logic all mixed together with the actual shuffle operation. The playExplodingKitten method had over 100 lines mixing Defuse card selection logic with console I/O for prompting, reading positions, handling curse mechanics with blind card selection, and displaying results. This meant that every card method needed to know about Scanner, System.out, and ResourceBundle for internationalized messages. Testing these methods required either simulating keyboard input by feeding strings to Scanner's InputStream or running integration tests with actual user interaction. Most importantly, reusing this logic with any other interface type was impossible. For instance, creating a GUI version would require completely rewriting all card methods to use Swing components, a web version would need yet another complete rewrite using HTTP request/response handling, and implementing AI players would require finding every Scanner call and replacing it with AI decision logic.

The GameUIHandler interface solves this by defining an abstract contract for all UI operations needed by the game, with specific methods for each type of user interaction. The askForShuffleCount(min, max) method encapsulates "prompt user for a number within range," askForInsertionIndex(maxIndex) encapsulates "choose deck position," displayMessage(String) handles output, and confirmAction(String) handles yes/no decisions. Card implementations call these abstract methods through the GameContext, knowing nothing about whether those calls will result in console prompts, dialog boxes, web forms, or AI algorithms. The ConsoleUIHandler provides the console implementation with all the Scanner reading, error handling, retry logic, and message formatting contained in one place. Each method handles input validation, exception catching for invalid input, and looping until valid input is received, centralizing all that complexity rather than scattering it across every card method.

The benefits of this abstraction becomes clear when supporting multiple UI types. Creating a Swing GUI version requires implementing one SwingUIHandler class with methods that show JOptionPane dialogs, JSpinner controls, and JTextArea outputs. The web version implements WebUIHandler to send requests to clients and await responses via HTTP sessions. An AI player implements AIUIHandler where each method calls into an AI decision engine rather than prompting users. All three alternatives use the exact same AttackCard, ShuffleCard, ExplodingKittenCard, and NopeCard implementations without any modifications. The game logic is completely UI-agnostic.
