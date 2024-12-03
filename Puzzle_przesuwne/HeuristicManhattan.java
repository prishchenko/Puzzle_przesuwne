package Puzzle_przesuwne;

import sac.State;
import sac.StateFunction;

public class HeuristicManhattan extends StateFunction {
	@Override
	public double calculate(State state) {
		 Puzzle p = (Puzzle) state;
		 int n = p.board.length;
		 int h=0;
		 for (int i=0; i<n; i++) {
			 for (int j=0; j<n; j++) {
				 int value = p.board[i][j];
				 if (p.board[i][j] != 0) {
					 //obliczamy miejsce docelowe
					 int destinationI = value / n;
					 int destinationJ = value % n;
					 h += Math.abs(i - destinationI) + Math.abs(j - destinationJ);
				 }
	           }
			 }
		 return h;
		 }
		 
		 
}
