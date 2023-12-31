import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class EightPuzzle {
    private int[][] board;
    private int nodesVisited; //for testing purposed
    private PriorityQueue<Node> priorityQueue;
    private Set<EightPuzzle> visitedStates; //for tracking already visited states
    private Heuristic heuristic;

    public int[][] getBoard() {
        return board;
    }
//constructor:
    public EightPuzzle(int[][] initialBoard, Heuristic heuristic) {
        this.heuristic = heuristic;
        // Input has to be a 3x3 array
        if (initialBoard.length != 3 || initialBoard[0].length != 3) {
            throw new IllegalArgumentException("Invalid board size");
        }
        // Initialize the board
        board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = initialBoard[i][j];
            }
        }
        nodesVisited = 0; //for testing purposes
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Find the position of the empty tile (in our case this is 0)
    private int[] findEmptyTile() {
        int[] position = new int[2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return null; // Should not happen in a valid puzzle!
    }

    // Find the position of a tile on the board
    private int[] findTile(int tile) {
        int[] position = new int[2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == tile) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return null; // Should not happen in a valid puzzle
    }

    //Calculate the Manhatten Distance to estimate how far each tile is from its goal position
    //Manhattan Distance = ∣x1−x2| + |y1-y2|

    private int manhattanDistance(int[] pos1, int[] pos2) {
        return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
    }

    // Calculate the total Manhattan heuristic for the puzzle
    public int calculateManhattanHeuristic() {
        int heuristic = 0;
        for (int tile = 1; tile <= 8; tile++) {
            int[] currentPos = findTile(tile);
            int[] goalPos = {(tile - 1) / 3, (tile - 1) % 3};
            heuristic += manhattanDistance(currentPos, goalPos);
        }
        return heuristic;
    }

    // Calculate the Hamming Distance
    public int calculateHammingDistance() {
        int[][] goal = {{1, 2, 3},{4, 5, 6},{7, 8, 0}};
        int heuristic = 0;
        for(int tile = 1; tile <= 8; tile++) {
            int[] currentPos = findTile(tile);
            if(goal[currentPos[0]][currentPos[1]] != tile) {
                heuristic++;
            }
        }
        return heuristic;
    }

    // Move the empty tile up
    public void moveUp() {
        int[] emptyPosition = findEmptyTile();
        int row = emptyPosition[0];
        int col = emptyPosition[1];

        if (row > 0) {
            // Swap with the tile above
            int temp = board[row][col];
            board[row][col] = board[row - 1][col];
            board[row - 1][col] = temp;
        }
    }

    // Move the empty tile down
    public void moveDown() {
        int[] emptyPosition = findEmptyTile();
        int row = emptyPosition[0];
        int col = emptyPosition[1];

        if (row < 2) {
            // Swap with the tile below
            int temp = board[row][col];
            board[row][col] = board[row + 1][col];
            board[row + 1][col] = temp;
        }
    }

    // Move the empty tile left
    public void moveToLeft() {
        int[] emptyPosition = findEmptyTile();
        int row = emptyPosition[0];
        int col = emptyPosition[1];

        if (col > 0) {
            // Swap with the tile to the left
            int temp = board[row][col];
            board[row][col] = board[row][col - 1];
            board[row][col - 1] = temp;
        }
    }

    // Move the empty tile left
    public void moveToRight() {
        int[] emptyPosition = findEmptyTile();
        int row = emptyPosition[0];
        int col = emptyPosition[1];

        if (col < 2) {
            // Swap with the tile to the right
            int temp = board[row][col];
            board[row][col] = board[row][col + 1];
            board[row][col + 1] = temp;
        }
    }

    //to check if the goal state is reached
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EightPuzzle that = (EightPuzzle) obj;
        return Arrays.deepEquals(board, that.board);
    }

    //State of board produces same hash code
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    // A* algorithm to solve the puzzle
    public void solvePuzzle() {
        priorityQueue = new PriorityQueue<>();
        visitedStates = new HashSet<>();
        priorityQueue.add(new Node(heuristic == Heuristic.MANHATTAN ? calculateManhattanHeuristic() : calculateHammingDistance(), this)); // Initial state

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            EightPuzzle currentPuzzle = currentNode.puzzle;

            if (currentPuzzle.isSolved()) {
                //currentPuzzle.printBoard();
                //System.out.println("Puzzle Solved!");
                return;
            }

            if (!visitedStates.contains(currentPuzzle)) { //to avoid loops

                visitedStates.add(currentPuzzle);
                nodesVisited++;

                // Generate and enqueue neighboring states
                for (int i = 0; i < 4; i++) {
                    EightPuzzle neighbor = new EightPuzzle(currentPuzzle.board, heuristic);
                    switch (i) {
                        case 0:
                            neighbor.moveUp();
                            break;
                        case 1:
                            neighbor.moveDown();
                            break;
                        case 2:
                            neighbor.moveToLeft();
                            break;
                        case 3:
                            neighbor.moveToRight();
                            break;
                    }

                    if (!currentPuzzle.equals(neighbor)) {
                        int totalCost = -1111;
                        switch (heuristic) {
                            case MANHATTAN:
                                totalCost = neighbor.calculateManhattanHeuristic();
                                break;
                            case HAMMING:
                                totalCost = neighbor.calculateHammingDistance();
                        }
                        //totalCost = neighbor.calculateManhattanHeuristic(); // Assuming uniform path cost
                        //neighbor.printBoard();
                        //System.out.println("Total Cost: " + totalCost);
                        //System.out.println();
                        priorityQueue.add(new Node(totalCost, neighbor));
                    }
                }
            }
        }
        System.out.println("No solution found.");
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public void resetNodesVisited() {
        nodesVisited = 0;
    }

    //Check if the puzzle is in the goal state
    public boolean isSolved() {
        int[][] goalState = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        return Arrays.deepEquals(board, goalState);
    }
}
