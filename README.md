# ChessJourney

ChessJourney is a chess training software focused on improving your tactical skills. It provides a user-friendly interface for solving chess puzzles and tracking your progress.

## Objectives
- Provide a platform for users to practice and improve their chess tactics.
- Present positions and exercises with varying difficulty levels.
- Track statistics for individual users.
- Utilize the Chesspresso library and CockroachDB for persistence.

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

## Compilation and Running

### Compile the Project
To compile the project, run the following command in the root directory:
```bash
mvn clean install
```
### Run the Project
To run the project, use the following command:
```bash
mvn javafx:run
```
## Usage Examples

### Creating Pieces with PieceFactory

You can create chess pieces using the PieceFactory:
```java
import com.davidp.chessjourney.domain.common.*;

Piece whiteKing = PieceFactory.createWhiteKing();
Piece blackQueen = PieceFactory.createBlackQueen();
```

### Creating Positions

To create a position for a piece on the board:

```java
Pos kingPos = new Pos(Row.ONE, Col.E);
Pos queenPos = new Pos(Row.EIGHT, Col.D);

PiecePosition whiteKingPosition = new PiecePosition(whiteKing, kingPos);
PiecePosition blackQueenPosition = new PiecePosition(blackQueen, kingPos);
```

### Using FenService

You can parse FEN strings to get the game state:

```java
import com.davidp.chessjourney.domain.common.*;

Fen fen = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
GameState gameState = fen.parse();

System.out.println("Active Color: " + gameState.getActiveColor());
System.out.println("Castling Availability: " + gameState.getCastlingAvailability());
System.out.println("En Passant Target Square: " + gameState.getEnPassantTargetSquare());
```
## Contributing

Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the MIT License.



