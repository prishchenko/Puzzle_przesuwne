package Puzzle_przesuwne;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

public class Puzzle extends GraphStateImpl{
	public int n; 
	public byte[][] board = null;
	int[][] moves = {
	        {0, -1},  // lewo
	        {0, 1},   // prawo
	        {-1, 0},  // gora
	        {1, 0}    // dol
	    };
	
	public Puzzle(int n) {
		this.n=n;
		int value = 0;
		board = new byte[n][n];
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				board[i][j]=(byte)value;
				value++;
			}
		}
	}
	public Puzzle(Puzzle toCopy) {
		this.n = toCopy.n; 
	    board = new byte[n][n];
	    for (int i = 0; i < n; i++)
	        for(int j = 0; j < n; j++)
	            board[i][j] = toCopy.board[i][j];
	}
	
	private List<Integer> getLegalMoves(){
		List<Integer> direction = new ArrayList<>();
		int emptyI = -1;
		int emptyJ = -1;
	
		for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (board[i][j] == 0) {
	            	emptyI = i;
	            	emptyJ = j;
	                break;
	            }
	        }
	        if (emptyI != -1) break;
	    }
	
		
		for (int d = 0; d < 4; d++) {
	        int newI = emptyI + moves[d][0];
	        int newJ = emptyJ + moves[d][1];
	        if (newI >= 0 && newI < n && newJ >= 0 && newJ < n) {
	            direction.add(d);
	        }
	    }
		
		return direction;
	}
	
	
	
	public void shake(int howManyMoves) {
		Random r = new Random ();
	    for(int i=0;i<howManyMoves; i++) {
	    	List<Integer> legalMoves = getLegalMoves();
	    	if (legalMoves.isEmpty()) {
	            continue;
	        }
	        
	        
	        int randomIndex = r.nextInt(legalMoves.size());
	        int direction = legalMoves.get(randomIndex);
	        
	        oneMove(direction);
	    }
		
	}
	
	public void oneMove(int direction) {
		int emptyI = -1;
	    int emptyJ = -1;
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (board[i][j] == 0) {
	                emptyI = i;
	                emptyJ = j;
	                break;
	            }
	        }
	        if (emptyI != -1) break;
	    }

	   
	    int dI = moves[direction][0];
	    int dJ = moves[direction][1];

	    
	    int newI = emptyI + dI;
	    int newJ = emptyJ + dJ;

	    
	    if (newI < 0 || newI >= n || newJ < 0 || newJ >= n) {
	        throw new IllegalArgumentException("Nieprawidlowy ruch w kierunku:" + direction);
	    }

	    
	    byte temp = board[newI][newJ];
	    board[newI][newJ] = board[emptyI][emptyJ];
	    board[emptyI][emptyJ] = temp;
	    
		
	}
	
	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> children = new ArrayList<>();
		
	    int emptyI = -1;
	    int emptyJ = -1;
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (board[i][j] == 0) {
	                emptyI = i;
	                emptyJ = j;
	                break;
	            }
	        }
	        if (emptyI != -1) break;
	    }

	    if (emptyI == -1 || emptyJ == -1) {
	        throw new IllegalStateException("Brak pustego miejsca na planszy.");
	    }

	    String[] moveNames = {"l", "p", "g", "d"};
	    List<Integer> legalMoves = getLegalMoves();

	    
	    for (int direction : legalMoves) {
	        Puzzle child = new Puzzle(this);
	        child.oneMove(direction);
	        child.setMoveName(moveNames[direction]);
	        children.add(child);
	    }

	    return children;
	}
	@Override
	public int hashCode() {
		byte[] flat = new byte[n * n];
		int k = 0;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				flat[k++] = board[i][j];
		return Arrays.hashCode(flat);
	}

	@Override
	public boolean isSolution() {
		 int value = 0;
		    for (int i = 0; i < n; i++) {
		        for (int j = 0; j < n; j++) {
		            if (board[i][j] != (byte) value) {
		                return false;
		            }
		            value++;
		        }
		    }
		    return true;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder(256);		
		for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
            	txt.append(board[i][j]);
            	txt.append(" ");
            }
            txt.append("\n");
        }
		return txt.toString();
	}
	

	 
	 public static void main(String[] args) {
		 double manhattanOpenSum = 0.0,manhattanClosedSum=0.0,manhattanTimeSum=0.0;
		 double MPTOpenSum = 0.0,MPTClosedSum=0.0,MPTTimeSum=0.0;
		 Puzzle puzzle = new Puzzle(3);
		 System.out.println(puzzle);
		 puzzle.shake(1000);
		 System.out.println(puzzle);
		 GraphSearchAlgorithm algorithm = new AStar(puzzle);
		 Puzzle.setHFunction(new HeuristicManhattan());
		 algorithm.execute();
		 Puzzle solution1 = (Puzzle) algorithm.getSolutions().get(0);
		 System.out.println("Solution: ");
		 System.out.println(solution1);
		 System.out.println("Path size: " + solution1.getPath().size());
		 System.out.println("Path" + solution1.getMovesAlongPath());
		 System.out.println("Closed: " + algorithm.getClosedStatesCount());
		 System.out.println("Open: " + algorithm.getOpenSet().size());
		 System.out.println("Time [ms]: " + algorithm.getDurationTime());
		
		 
		 System.out.println("\n----------\n");
		 GraphSearchAlgorithm algorithm2 = new AStar(puzzle);
		 Puzzle.setHFunction(new HeuristicMisplacedTiles());
		 algorithm2.execute();
		 Puzzle solution2 = (Puzzle)algorithm2.getSolutions().get(0);
		 System.out.println("Solution: ");
		 System.out.println(solution2);
		 System.out.println("Path size: " + solution2.getPath().size());
		 System.out.println("Path" + solution2.getMovesAlongPath());
		 System.out.println("Closed: " + algorithm2.getClosedStatesCount());
		 System.out.println("Open: " + algorithm2.getOpenSet().size());
		 System.out.println("Time [ms]: " + algorithm2.getDurationTime());
		 System.out.println("----------");
		 for(int i =0; i<100;i++) {
			 Puzzle puzzleSum = new Puzzle(3);
			 puzzleSum.shake(1000);
			 GraphSearchAlgorithm algorithmSum = new AStar(puzzleSum);
			 Puzzle.setHFunction(new HeuristicManhattan());
			 algorithmSum.execute();
			 manhattanOpenSum+=algorithmSum.getOpenSet().size();
			 manhattanClosedSum+=algorithmSum.getClosedStatesCount();
			 manhattanTimeSum+=algorithmSum.getDurationTime();
			 
			 GraphSearchAlgorithm algorithmSumMPT = new AStar(puzzleSum);
			 Puzzle.setHFunction(new HeuristicMisplacedTiles());
			 algorithmSumMPT.execute();
			 MPTOpenSum+=algorithmSumMPT.getOpenSet().size();
			 MPTClosedSum+=algorithmSumMPT.getClosedStatesCount();
			 MPTTimeSum+=algorithmSumMPT.getDurationTime();
		 }
		 System.out.println("Closed dla Manhattan: " + manhattanClosedSum/100);
		 System.out.println("Open dla Manhattan: " + manhattanOpenSum/100);
		 System.out.println("Time [ms] dla Manhattan: " + manhattanTimeSum/100);
		 System.out.println("----------");
		 System.out.println("Closed dla MPT: " + MPTClosedSum/100);
		 System.out.println("Open dla MPT: " + MPTOpenSum/100);
		 System.out.println("Time [ms] dla MPT: " + MPTTimeSum/100);
		}
}
