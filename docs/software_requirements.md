# Software Requirements Specification
## Exploding Kittens Card Game

### Document Information
- **Project**: Exploding Kittens Game Implementation
- **Version**: 1.0
- **Last Updated**: 2025-11-18
- **Scope**: Core card mechanics for Exploding Kitten, Nope, Shuffle, and Attack cards

---

## 1. Exploding Kitten Card

### Overview
The Exploding Kitten card is the central mechanic of the game. Drawing this card results in immediate player elimination unless countered with a Defuse card.

### Card Properties
- **Quantity in Deck**: Variable (dynamically inserted during gameplay)
- **Game Type**: Base game (EXPLODING_KITTENS)
- **Can Be Played Voluntarily**: No
- **Ends Turn**: Yes (always)
- **Can Be Noped**: No

### Functional Requirements

#### 1: Death Mechanism
When a player draws an Exploding Kitten card:
- The player is immediately eliminated from the game
- The player is marked as dead
- The player's turn count is set to 0
- The player can no longer take actions in the game

#### 2: Defuse Card Interaction
If a player has a Defuse card when drawing an Exploding Kitten:
- The player survives the explosion
- The Defuse card is removed from the player's hand
- The player must reinsert the Exploding Kitten into the deck
- The player's turn ends after reinsertion

#### 3: Card Reinsertion
After defusing an Exploding Kitten:
- The player chooses a position to insert the card (index 0 to deck size)
- The insertion position is kept secret from other players
- The card is placed at the exact position specified
- Invalid positions are rejected

#### 4: Streaking Kitten Special Interaction
When a player has a Streaking Kitten card:
- If the player draws an Exploding Kitten and has NO other Exploding Kitten in hand
- The player may choose to keep the Exploding Kitten in their hand instead of defusing
- If kept, the Exploding Kitten is added to hand and the player continues their turn
- If defused normally, standard defuse rules apply

#### 5: Curse Mechanic Integration
When a cursed player draws an Exploding Kitten:
- The player's hand is shuffled
- The player cannot see which card is their Defuse
- The player must guess cards one by one to find the Defuse
- Wrong guesses permanently discard cards
- Finding the Defuse removes the curse and allows survival
- Failing to find the Defuse results in death

#### 6: Multiple Exploding Kittens
The system must handle:
- Multiple Exploding Kittens in a player's hand (via Streaking Kitten)
- Multiple Exploding Kittens in the deck
- Sequential Exploding Kitten draws
- Stealing Exploding Kittens between players

### 1.4 Business Logic

1. **Instant Effect**: Drawing an Exploding Kitten triggers immediate resolution
2. **No Voluntary Play**: Players cannot play this card from their hand
3. **Defuse Consumption**: Each defuse uses exactly one Defuse card
4. **Turn Termination**: Whether the player survives or dies, their turn ends
5. **Cannot Be Countered**: Nope cards cannot prevent Exploding Kitten effects
6. **Theft Risk**: If stolen via 2-card combo, the Exploding Kitten can kill the thief on their draw phase

### Use Cases

#### 1: Standard Defuse
**Preconditions**: Player has Defuse card in hand, draws Exploding Kitten
**Flow**:
1. Player draws Exploding Kitten from deck
2. System checks if player has Defuse card
3. System prompts player to choose insertion position (0 to deck size)
4. Player selects position
5. System removes Defuse from player's hand
6. System inserts Exploding Kitten at chosen position
7. Player's turn ends

**Outcome**: Player survives, Exploding Kitten back in deck, Defuse consumed

#### 2: Player Elimination
**Preconditions**: Player has NO Defuse card, draws Exploding Kitten
**Flow**:
1. Player draws Exploding Kitten from deck
2. System checks if player has Defuse card
3. No Defuse found
4. Player is eliminated from game
5. Player is marked as dead
6. Player's turn count set to 0
7. Game continues with next player

**Outcome**: Player eliminated, Exploding Kitten remains in discard

#### 3: Cursed Player Defuse
**Preconditions**: Cursed player has Defuse card, draws Exploding Kitten
**Flow**:
1. Player draws Exploding Kitten
2. System shuffles player's hand
3. System presents shuffled cards without revealing types
4. Player guesses which card is Defuse
5. If wrong: card is permanently discarded, repeat step 4
6. If correct: standard defuse procedure executed
7. Player is uncursed
8. Player's turn ends

**Outcome**: Player survives if Defuse found, curse removed, guessed cards discarded

#### 4: Streaking Kitten Choice
**Preconditions**: Player has Streaking Kitten, NO other Exploding Kitten in hand, draws Exploding Kitten
**Flow**:
1. Player draws Exploding Kitten
2. System detects Streaking Kitten in hand
3. System offers choice: keep in hand or defuse normally
4. If keep: Exploding Kitten added to hand, turn continues
5. If defuse: standard defuse procedure
6. Player's turn status updated accordingly

**Outcome**: Either Exploding Kitten in hand or back in deck

---

## 2. Nope Card

### Overview
The Nope card is a counter card that can negate the effect of almost any action card played by any player. It creates strategic depth through chain reactions.

### Card Properties
- **Quantity in Deck**: 4
- **Game Type**: Base game (EXPLODING_KITTENS)
- **Can Be Played Voluntarily**: Yes (in response to other cards)
- **Ends Turn**: No
- **Can Be Noped**: Yes (Nope can be Noped)

### Functional Requirements

#### 1: Counter Mechanism
When a Nope card is played:
- The effect of the target card is negated
- The target card is still consumed/discarded
- The Nope card is removed from the player's hand
- Any player (not just the target) can play Nope

#### 2: Chain Reaction System
When a Nope is played:
- The system checks all other players for Nope cards
- Players can play Nope to counter the previous Nope
- This process repeats recursively
- The final Nope determines whether the original card's effect occurs

#### 3: Universal Applicability
Nope cards CAN counter:
- Attack cards (ATTACK, TARGETED_ATTACK)
- Shuffle cards
- Skip cards (SKIP, SUPER_SKIP)
- See the Future cards
- Alter the Future cards
- Reverse cards
- Swap Top and Bottom cards
- Mark cards
- Curse of the Cat Butt cards
- Catomic Bomb cards
- Garbage Collection cards
- All cat card combos (2-card and 3-card combinations)

#### 4: Limitations
Nope cards CANNOT counter:
- Exploding Kitten cards
- Defuse cards
- Other Nope cards (in the sense of preventing them - they counter them instead)

#### 5: Player Participation
For each card played:
- All players except the card player are queried
- Players are asked in turn order
- Each player can choose to play Nope or pass
- The process continues until all players pass or no more Nopes exist

#### 6: Effect Resolution
The final resolution follows:
- If odd number of Nopes: original card is blocked
- If even number of Nopes (including 0): original card takes effect
- The resolution is immediate and final

### Business Rules

1. **Instant Response**: Nope must be played immediately when prompted
2. **Single Use**: Each Nope card counters one card (consumed when played)
3. **No Target Restriction**: Any player can Nope any applicable card
4. **Chain Unlimited**: No limit to Nope chain length
5. **Turn Continuation**: Playing Nope doesn't end anyone's turn
6. **Voluntary Participation**: Players can choose not to Nope even if they have the card

### Use Cases

#### 1: Simple Nope
**Preconditions**: Player A plays Attack, Player B has Nope card
**Flow**:
1. Player A plays Attack card
2. System asks all other players if they want to Nope
3. Player B chooses to play Nope
4. No other players play Nope
5. Attack is negated
6. Player A's Attack card is discarded
7. Player B's Nope card is discarded
8. Player A's turn continues normally

**Outcome**: Attack blocked, both cards consumed, turn continues

#### 2: Nope Chain War
**Preconditions**: Multiple players have Nope cards, Player A plays Shuffle
**Flow**:
1. Player A plays Shuffle card
2. Player B plays Nope (blocks Shuffle)
3. Player C plays Nope (blocks Player B's Nope, allowing Shuffle)
4. Player D plays Nope (blocks Player C's Nope, blocking Shuffle)
5. No more Nopes played
6. Odd number of Nopes (3 total)
7. Shuffle is blocked
8. All played Nopes discarded

**Outcome**: Shuffle blocked, 3 Nopes consumed, Player A continues turn

#### 3: Declined Nope Opportunity
**Preconditions**: Player A attacks Player B, Player C has Nope
**Flow**:
1. Player A plays Attack targeting next player (Player B)
2. System asks all other players for Nope
3. Player C has Nope but declines to play it
4. No Nopes played
5. Attack resolves normally
6. Player B must take 2 turns

**Outcome**: Attack successful, Player B takes 2 turns, Player C keeps Nope

---

## 3. Shuffle Card

### Overview
The Shuffle card randomizes the draw pile, disrupting opponent strategies and protecting against known card positions.

### Card Properties
- **Quantity in Deck**: 4
- **Game Type**: Base game (EXPLODING_KITTENS)
- **Can Be Played Voluntarily**: Yes
- **Ends Turn**: No
- **Can Be Noped**: Yes

### Functional Requirements

#### 1: Deck Randomization
When a Shuffle card is played:
- The entire draw pile is randomized
- Randomization uses Fisher-Yates algorithm for true randomness
- Each card has equal probability of ending up in any position
- The shuffle uses cryptographically secure random number generation

#### 2: Multiple Shuffle Option
The player playing Shuffle must specify:
- Number of times to shuffle (minimum: 1)
- Maximum shuffles allowed: 100
- Each shuffle completely randomizes the deck
- Invalid input prompts re-entry

#### 3: Turn Continuation
After shuffling:
- The current player's turn continues
- The player can play additional cards
- The player must still draw at end of turn
- No turn count modification occurs

#### 4: Strategic Disruption
Shuffle disrupts:
- Known card positions from See the Future
- Card arrangements from Alter the Future
- Tracked Exploding Kitten positions
- Any player's knowledge of deck order

#### 5: Input Validation
The system must validate:
- Input is a positive integer
- Input is not greater than 100
- Non-integer input is rejected with error message
- Zero or negative values are rejected

### Business Rules

1. **Player Choice**: Player determines number of shuffles (1-100)
2. **Complete Randomization**: Each shuffle fully randomizes entire deck
3. **Turn Preservation**: Playing Shuffle does not end turn
4. **Can Be Countered**: Nope cards can prevent Shuffle
5. **Wasted on Nope**: If Noped, Shuffle is discarded with no effect
6. **Timing Matters**: Best played before drawing to randomize dangerous positions
7. **Maximum Limit**: 100-shuffle cap prevents abuse and delays

### Use Cases

#### 1: Disrupting See the Future
**Preconditions**: Opponent played See the Future, player has Shuffle
**Flow**:
1. Opponent A plays See the Future and sees Exploding Kitten on top
2. Player B plays Shuffle on their turn
3. Player B chooses to shuffle 3 times
4. System applies Fisher-Yates shuffle 3 times
5. Deck is completely randomized
6. Opponent A's knowledge is now invalid
7. Player B continues turn

**Outcome**: Deck randomized, opponent's knowledge invalidated, turn continues

#### 2: Safety Shuffle
**Preconditions**: Player suspects Exploding Kitten near top, has Shuffle
**Flow**:
1. Player plays Shuffle card
2. Player chooses to shuffle 1 time
3. No other players play Nope
4. Deck is randomized
5. Exploding Kitten (if present) moves to random position
6. Player continues turn
7. Player draws at end of turn from randomized deck

**Outcome**: Deck randomized, draw risk redistributed

#### 3: Over-Shuffle Prevention
**Preconditions**: Player attempts to shuffle more than 100 times
**Flow**:
1. Player plays Shuffle card
2. Player enters 150 for number of shuffles
3. System displays maximum shuffle error message
4. Player prompted to enter valid number
5. Player enters 5
6. Deck is shuffled 5 times
7. Turn continues

**Outcome**: Deck shuffled 5 times, invalid input handled

---

## 4. Attack Card

### Overview
The Attack card forces the next player to take 2 consecutive turns without the current player drawing. It enables attack chaining and complex turn manipulation.

### Card Properties
- **Quantity in Deck**: 3
- **Game Type**: Base game (EXPLODING_KITTENS)
- **Can Be Played Voluntarily**: Yes
- **Ends Turn**: Yes (immediately)
- **Can Be Noped**: Yes

### Functional Requirements

#### 1: Forced Double Draw
When an Attack card is played:
- The next alive player is targeted
- That player's turn count is increased by 2
- The attacking player's turn ends immediately
- The attacking player does NOT draw a card

#### 2: Attack Chaining
Players can chain attacks:
- When attacked, a player can play their own Attack card
- This passes 4 turns to the next player (original 2 + new 2)
- Chains can continue indefinitely if players have Attack cards
- Each Attack in chain adds 2 turns to the final target

#### 3: Targeted Attack Variant
Targeted Attack allows:
- Specifying which player to attack (not just next player)
- Same 2-turn penalty applies
- Target must be an alive player
- Cannot target self

#### 4: Attack Queue System
The system maintains an attack queue:
- Normal attacks stored as value 5
- Targeted attacks stored as target player index (0-4)
- Queue processed in FIFO (First In, First Out) order
- All queued attacks resolved before target's first turn

#### 5: Turn Tracking Persistence
The system tracks turns across players:
- `turnTracker` array stores each player's pending turns
- `currentPlayerNumberOfTurns` holds active player's remaining turns
- `attackCounter` identifies which player is being attacked
- `numberOfAttacks` accumulates total attacks being applied

#### 6: Dead Player Handling
Attack targeting must:
- Automatically skip dead players
- Use circular iteration through player list
- Never target eliminated players
- Find next alive player in turn order

#### 7: Skip Card Interaction
When attacked player has Skip cards:
- Each Skip reduces turn count by 1
- Super Skip sets turn count to 0 immediately
- Player can use multiple Skips to reduce attack burden
- Need 2 Skips to completely cancel 1 Attack

#### 8: Nope Interaction in Chains
During attack chains:
- Each individual Attack can be Noped
- If Noped, that specific Attack is cancelled
- Chain can continue if player has more Attacks
- Final turn count reflects only non-Noped attacks

### Business Rules

1. **Immediate Turn End**: Playing Attack ends turn without drawing
2. **Turn Addition**: Each Attack adds exactly 2 turns
3. **Chain Multiplication**: Chains multiply turns (2, 4, 6, 8, etc.)
4. **Next Player Target**: Normal Attack targets next alive player in order
5. **Dead Player Skip**: Attack rotation skips eliminated players
6. **Cannot Self-Attack**: Players cannot target themselves
7. **Queue Resolution**: All queued attacks resolved before target plays
8. **Nope Per Attack**: Each Attack in chain can be individually Noped
9. **Skip Mitigation**: Attacked players can use Skip cards to reduce turns
10. **Attack Flag**: System sets `attacked` flag to track attack state

### Use Cases

#### 1: Simple Attack
**Preconditions**: Player A has Attack card, Player B is next
**Flow**:
1. Player A plays Attack card
2. No other players play Nope
3. System adds 2 turns to Player B's turn count
4. Player A's turn ends without drawing
5. Turn passes to Player B
6. Player B must take 2 consecutive turns
7. Player B draws at end of each turn

**Outcome**: Player B takes 2 turns, Player A avoided 1 draw

#### 2: Attack Chain
**Preconditions**: Player A attacks Player B, Player B has Attack, Player C is next
**Flow**:
1. Player A plays Attack targeting Player B
2. System prompts Player B to play Attack (if they have it)
3. Player B chooses to play Attack
4. Attack chains to Player C
5. Player C now has 4 turns (2 from A's attack + 2 from B's attack)
6. Player A's turn ends
7. Player B's turn ends without starting
8. Player C must take 4 consecutive turns

**Outcome**: Player C takes 4 turns, Players A and B avoided draws

#### 3: Targeted Attack
**Preconditions**: Player A has Targeted Attack, wants to attack Player C
**Flow**:
1. Player A plays Targeted Attack card
2. System prompts for target player selection
3. Player A selects Player C (index 2)
4. No other players play Nope
5. System adds 2 turns to Player C's turn count
6. Player A's turn ends
7. Play continues in normal order to Player C
8. Player C takes 2 turns when their turn arrives

**Outcome**: Player C has 2 pending turns, specific targeting succeeded

#### 4: Noped Attack in Chain
**Preconditions**: Player A attacks Player B, Player B attacks Player C, Player D has Nope
**Flow**:
1. Player A plays Attack targeting Player B
2. Player B plays Attack targeting Player C
3. Player D plays Nope on Player B's Attack
4. Player B's Attack is cancelled
5. Only Player A's Attack remains in queue
6. Player B must take 2 turns (not Player C)
7. Player D's Nope is consumed

**Outcome**: Player B takes 2 turns, chain broken, Player C safe

#### 5: Attack Mitigation with Skip
**Preconditions**: Player A attacks Player B, Player B has 2 Skip cards
**Flow**:
1. Player A plays Attack
2. Player B receives 2 turns
3. On Player B's first turn, they play Skip
4. Turn count reduced from 2 to 1
5. On Player B's next turn, they play another Skip
6. Turn count reduced from 1 to 0
7. Player B draws only after each skip
8. Player B avoided the attack penalty

**Outcome**: Attack mitigated, Player B used 2 Skips, drew 2 cards