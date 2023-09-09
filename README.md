# Project: Luna Chess Engine
Luna is a Java-based chess engine that places its primary emphasis on the engine's core functionality. While it includes a graphical user interface (GUI) to make the game playable and showcase the engine's capabilities, the heart of the project revolves around several key components.

1. Chess Game Implementation

Luna employs a unique approach to chess game representation. It utilizes bit boards, which represent the entire chessboard as a single 64-bit integer. This enables efficient piece movement calculations and legality checks. Each possible position's piece movements are precomputed and stored in a hash table. To determine legal moves, Luna employs a "Threatening line" approach, computed in advance, indicating which pieces threaten the king or have the potential to do so, preventing illegal moves.

2. Search Speed Optimization

With a vast number of possible positions in chess, especially after only a few moves, Luna employs the alpha-beta pruning algorithm to eliminate irrelevant positions from consideration. Additionally, it employs a transposition table to store previously evaluated positions, significantly enhancing search speed. This combination of techniques allows Luna to efficiently navigate the vast chess space.

3. Move Ordering for Efficiency

Efficient alpha-beta pruning depends on searching for likely good moves first. Luna accomplishes this through move ordering, which assesses the likelihood of a move being favorable or unfavorable. By searching for promising positions early and less promising ones later, Luna optimizes its search process.

4. Position Evaluation

To determine the favorability of chess positions, Luna relies on evaluation functions. While the current evaluation function may be relatively straightforward, it plays a critical role in helping the engine discern which positions benefit each player. This aspect of the engine is essential for making informed decisions during gameplay.

