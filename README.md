# Luna-ChessEngine
Luna is a java-based chess engine. The main focus of the project is the engine itself. The GUI of the game is mostly to showcase the engine and to make it playable.
The project can be separated into a few parts.
1. The chess game itself.
The piece movement and all the calculations are made with bit boards, meaning the move is represent as a 64 long that represents the board of chess. The movement of each piece in each possible position is calculated before the game starts and stored in Hash. For checking legal moves, it uses “Threatening line”, also calculated before the game, those line show which piece threatens the king or might threaten him, to not allow illegal moves.
2. Search speed
From the starting position, after 10 moves ( 5 for each player), there are 69*10^12 positions, that’s all. We use alpha beta pruning algorithm to cut off  irrelevant positions, and we also save searched positions using transposition table to store position we already evaluated.  
3. Assuming what will be a good move
Alpha beta pruning is effective when the likely good moves are searched first, meaning we need to assume what moves will be good to make it more efficient. For that we have move ordering, that evaluates how likely a move will be good or bad, so we’ll search for good positions first and bad last.
4. Evaluate positions
For the engine to know what positions are good, we need to evaluate correctly what positions are benefits for who. For that we have evaluate functions to evaluate the position, thought in the current state it’s quit simple.



