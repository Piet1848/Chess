import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/*
Gewichtung der einzelnen Faktoren zur Stellungsbewertung on the fly ändern
sowas wie König Position, Bauern Aufstellung etc
 */

public class  Brain {
	private int maxPosition;
	private final int targetTime = 10000;
	private final int timeRange = 7000;
	private int lastTime = targetTime;
	private int depth = 6;
	private double actEvaluation = 0.;

	private int maxNumberMoves = 25;

	private int minNumberMoves = 7;
	private int possibleMoves;
	
	public Move playKi(Board board, boolean whiteTurn) {
		if(lastTime > targetTime+timeRange*2) {
			depth -= 1;
		}else if(lastTime < targetTime-timeRange) {
			depth += 1;
		}
		System.out.print("Current depth: " + depth);
		ArrayList<Move> moves = getSortedMoves(whiteTurn, board);
		long startTime = System.currentTimeMillis();
		double currentEval = evaluateBoard(whiteTurn, board);
		System.out.print(" " + currentEval);

		actEvaluation = minimax(board, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, whiteTurn);
		System.out.print(" ActEval: " + actEvaluation);
		System.out.println(" " + (System.currentTimeMillis()-startTime)/1000.);
		lastTime = (int) (System.currentTimeMillis()-startTime);
		return moves.get(maxPosition);
	}
	
	private double minimax(@NotNull Board b, int currentDepth, double alpha, double beta, boolean maximizingPlayer) {
		if(!b.hasBothKings()) {
			if(b.winner()) {
				return Double.POSITIVE_INFINITY;
			}else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		if(currentDepth == depth) {
			return evaluateBoard(maximizingPlayer, b);
		}
		ArrayList<Move> moves;
		if(currentDepth == depth - 1) {
			moves = b.getFastSortedMoves(maximizingPlayer);
		}else {
			moves = getSortedMoves(maximizingPlayer, b);
		}
		
		if(moves.size() == 0) {
			return 0.;
		}
		int numberMoves = currentDepth > 3 ? maxNumberMoves - (int) (currentDepth*2.5) : maxNumberMoves;
		numberMoves = Math.max(numberMoves, minNumberMoves);
		numberMoves = Math.min(numberMoves, moves.size());
		int currentMaxPos = 0;
		if(maximizingPlayer) {
			double maxEval = Double.NEGATIVE_INFINITY;
			for(int i = 0; i < numberMoves; i++) {
				double eval = calcMove(b, currentDepth, alpha, beta, true, moves, i);
				if(maxEval < eval) {
					maxEval = eval;
					currentMaxPos = i;
					alpha = Math.max(alpha, eval);
				}
				if(beta <= alpha) {
					maxPosition = currentMaxPos;
					return maxEval;
				}
			}
			maxPosition = currentMaxPos;
			return maxEval;
		}else {
			double minEval = Double.POSITIVE_INFINITY;
			for(int i = moves.size()-1; i >= moves.size()-numberMoves; i--) {
				double eval = calcMove(b, currentDepth, alpha, beta, false, moves, i);
				if(minEval > eval) {
					minEval = eval;
					currentMaxPos = i;
					beta = Math.min(beta, eval);
				}
				if(beta <= alpha) {
					maxPosition = currentMaxPos;
					return minEval;
				}
			}
			maxPosition = currentMaxPos;
			return minEval;
		}
	}

	private double calcMove(Board b, int currentDepth, double alpha, double beta, boolean maximizingPlayer, ArrayList<Move> moves, int i) {
		ArrayList<Figur> oldPosition = b.getFiguren();
		ArrayList<Figur> position = new ArrayList<>();
		for (int j = 0; j < oldPosition.size(); j++) {
			position.add(oldPosition.get(j).clone());
		}
		b.move(moves.get(i));
		double eval = minimax(b, currentDepth + 1, alpha, beta, !maximizingPlayer);	//TODO
		b.setPosition(position);
		return eval;
	}

	private ArrayList<Move> getSortedMoves(boolean isWhite, Board board){
		ArrayList<Move> moves = board.getAllMoves(isWhite, true);
		possibleMoves = moves.size();
		return sortMoves(moves, isWhite, board);
	}

	private ArrayList<Move> sortMoves(ArrayList<Move> moves, boolean isWhite, Board board) {
		ArrayList<Double> values = new ArrayList<>();

		for(int i = 0; i < moves.size(); i++) {
			values.add(getValueMove(moves.get(i), isWhite, board));
		}

		//Sort
		Move tempMove;
		double temp;
		for(int i = 1; i < moves.size(); i++) {
			tempMove = moves.get(i);
			temp = values.get(i);
			int j = i;
			while(j > 0 && values.get(j-1) < temp) {
				moves.set(j, moves.get(j-1));
				values.set(j, values.get(j-1));
				j--;
			}
			moves.set(j, tempMove);
			values.set(j, temp);
		}
		//if(!isWhite)
		//	Collections.reverse(moves);
		return moves;
	}

	private double getValueMove(Move move, boolean isWhite, Board board) {
		ArrayList<Figur> oldPosition = board.getFiguren();
		ArrayList<Figur> position = new ArrayList<>();
		for (int i = 0; i < oldPosition.size(); i++) {
			position.add(oldPosition.get(i).clone());
		}
		board.move(move);
		double value = evaluateBoard(isWhite, board);	//TODO
		board.setPosition(position);
		return value;
	}

	private double evaluateBoard(int numOpponentMoves, boolean isWhite, Board board) {	//TODO to be completed
		double result = 0.;
		double multi = isWhite? 1. : -1.;
		ArrayList<Figur> figuren = board.getFiguren();
		double figurenAbsValue = figurenGetAbsValue(figuren)-2000;
		result += figurenCounter(figuren)*4.;	// 1 for one pawn up TODO differenz
		result += kingPosition(figurenAbsValue, board, figuren);
		result += centerPawn(figurenAbsValue, board);
		result += activePosition(numOpponentMoves) * multi;
		result += protectionEvaluation(figuren);
		return result;
	}

	public double evaluateBoard(boolean isWhite, Board board) {
		int possibleMovesIsWhite = board.getAllMoves(!isWhite, false).size();	//Faster because checkCheck false but not 100% correct
		possibleMoves = board.getAllMoves(isWhite, false).size();
		return evaluateBoard(possibleMovesIsWhite, isWhite, board);
	}


	private double protectionEvaluation(ArrayList<Figur> figuren) {
		double protectionSum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			Figur f = figuren.get(i);
			int multi = f.isWhite() ? 1 : -1;
			protectionSum += f.getProtection() * multi;
		}
		return protectionSum/2.;
	}

	private double activePosition(int numOpponentMoves) {	//TODO
		if(possibleMoves == 0) {
//			System.out.println("Error no Possible Moves (Board.activePosition()");
			return Double.POSITIVE_INFINITY;
		}
		return (possibleMoves-numOpponentMoves)/14.;
	}

	private double centerPawn(double figurenAbsValue, Board b) {
		Figur[][] board = b.getPosition();
		double sum = 0.;
		sum += getCenterValue(board[3][3]);
		sum += getCenterValue(board[3][4]);
		sum += getCenterValue(board[4][3]);
		sum += getCenterValue(board[4][4]);
		return sum*figurenAbsValue/85.;
	}

	private double getCenterValue(Figur toCheck) {
		if(toCheck != null) {
			double multi = 1.;
			if(!toCheck.isWhite()) {
				multi = -1.;
			}
			if(toCheck.getName().equals("pawn")) {
				return multi;
			}else {
				return 0.35*multi;
			}
		}
		return 0;
	}

	private double kingPosition(double figurenAbsValue, Board b, ArrayList<Figur> figuren) {
		Figur[][] board = b.getPosition();
		double sum = 0.;
		boolean whiteKing = false;
		boolean blackKing = false;
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i).getName().equals("king")) {
				Figur king = figuren.get(i);
				int kingX = king.getPosition().x;
				int kingY = king.getPosition().y;
				double multi = 1.;
				if(king.isWhite()) {
					whiteKing = true;
					int coveredFront = 0;
					if(kingY<7) {
						if(board[kingX][kingY+1] != null) {
							coveredFront++;
						}
						if(kingX > 0 && board[kingX-1][kingY+1] != null) {
							coveredFront++;
						}
						if(kingX < 7 && board[kingX+1][kingY+1] != null) {
							coveredFront++;
						}
						sum += coveredFront*figurenAbsValue/78.;
					}
				}else {
					blackKing = true;
					multi = -1.;
					if(kingY>0) {
						int coveredFront = 0;
						if(board[kingX][kingY-1] != null) {
							coveredFront++;
						}
						if(kingX > 0 && board[kingX-1][kingY-1] != null) {
							coveredFront++;
						}
						if(kingX < 7 && board[kingX+1][kingY-1] != null) {
							coveredFront++;
						}
						sum -= coveredFront*figurenAbsValue/78.;
					}
				}
				if(kingX < 2 || kingX > 5){
					sum *= 2.;
					sum += multi*(figurenAbsValue-30)/15.;
				}else if(king.getMoved()) {
					sum -= multi*(figurenAbsValue-30)/20.;
				}
				sum += (3.5-Math.abs(3.5-kingY))*multi*(30-figurenAbsValue)/80.;	//endgame position
				sum += (3.5-Math.abs(3.5-kingX))*multi*(30-figurenAbsValue)/80.;
			}else {
				sum += figuren.get(i).getBonus();
			}
		}
		if(!blackKing) {
			return Double.POSITIVE_INFINITY;
		}else if(!whiteKing) {
			return Double.NEGATIVE_INFINITY;
		}
		return sum;
	}

	private double figurenGetAbsValue(ArrayList<Figur> figuren) {
		double sum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			sum += Math.abs(figuren.get(i).getValue());
		}
		return sum;
	}

	private double figurenCounter(ArrayList<Figur> figuren) {
		double sum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			sum += figuren.get(i).getValue();
		}
		return sum;
	}
	
	public double getActEvaluation() {
		return actEvaluation;
	}

	public double getCurrentActEvaluation(boolean isWhite, Board board){
		return evaluateBoard(isWhite, board);
	}
}
