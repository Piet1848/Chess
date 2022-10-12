import java.util.ArrayList;

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
	private final double maxDifference = 10.;
	private double actEvaluation = 0.;

	private int maxNumberMoves = 20;

	private int minNumberMoves = 6;
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

		actEvaluation = minimax(board, 1, currentEval-maxDifference, currentEval+maxDifference, whiteTurn);

		System.out.println(" " + (System.currentTimeMillis()-startTime)/1000.);
		lastTime = (int) (System.currentTimeMillis()-startTime);
		return moves.get(maxPosition);
	}
	
	private double minimax(Board b, int currentDepth, double alpha, double beta, boolean maximizingPlayer) {
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
		int numberMoves = maxNumberMoves - currentDepth;
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
		Figur lostFigur = b.move(moves.get(i));
		double eval = minimax(b, currentDepth + 1, alpha, beta, !maximizingPlayer);	//TODO
		if(moves.get(i).promotion) {
			b.redoPromotion(moves.get(i), lostFigur);
		}else {
			b.redoMove(moves.get(i), lostFigur);
		}
		return eval;
	}

	private ArrayList<Move> getSortedMoves(boolean isWhite, Board board){
		ArrayList<Move> moves = board.getAllMoves(isWhite);
		possibleMoves = moves.size();
		return sortMoves((moves), isWhite, board);
	}

	private ArrayList<Move> sortMoves(ArrayList<Move> moves, boolean isWhite, Board board) {
		Move tempMove;
		double temp;
		ArrayList<Double> values = new ArrayList<>();
		for(int i = 0; i < moves.size(); i++) {
			values.add(getValueMove(moves.get(i), isWhite, board));
		}
		for(int i = 1; i < moves.size(); i++) {
			tempMove = moves.get(i);
			temp = values.get(i);
			int j = i;
			while(j > 0 && values.get(j-1) < values.get(j)) {	//old .prio
				moves.set(j, moves.get(j-1));
				values.set(j, values.get(j-1));
				j--;
			}
			moves.set(j, tempMove);
			values.set(j, temp);
		}
		return moves;
	}

	private double getValueMove(Move move, boolean isWhite, Board board) {
		Figur lostFigur = board.move(move);
		double value = evaluateBoard(isWhite, board);	//TODO
		if(move.prio >= 3.11 && move.prio <= 3.12) {
			board.redoPromotion(move, lostFigur);
		}else {
			board.redoMove(move, lostFigur);
		}
		return value;
	}

	private double evaluateBoard(int oldPossmoves, boolean isWhite, Board board) {	//TODO to be completed
		double result = 0.;
		double multi = 1.;
		if(!isWhite) {
			multi = -1.;
		}
		ArrayList<Figur> figuren = board.getFiguren();
		double figurenAbsValue = figurenGetAbsValue(figuren)-2000;
		result += figurenCounter(figuren)*3.;	// 1 for one pawn up TODO differenz
		result += kingPosition(figurenAbsValue, board, figuren);
		result += centerPawn(figurenAbsValue, board);
		result += activePosition(oldPossmoves, multi);
		result += protectionEvaluation(figuren);
		return result;
	}

	public double evaluateBoard(boolean isWhite, Board board) {
		int possibelMovesIsWhite = board.getAllMoves(isWhite).size();
		possibleMoves = board.getAllMoves(!isWhite).size();
		return evaluateBoard(possibelMovesIsWhite, isWhite, board);
	}


	private double protectionEvaluation(ArrayList<Figur> figuren) {
		double protectionSum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			protectionSum -= figuren.get(i).getProtection();
		}
		return protectionSum;
	}

	private double activePosition(int oldPossmoves, double multi) {	//TODO
//		if((oldPossmoves-possibleMoves)*multi/15. > 0.9) {
//			System.out.println("Error13 " + (oldPossmoves-possibleMoves)*multi/15.);
//		}

		if(possibleMoves == 0) {
//			System.out.println("Error no Possible Moves (Board.activePosition()");
			return Double.POSITIVE_INFINITY*multi;
		}
		return (oldPossmoves-possibleMoves)/7.*multi;
	}

	private double centerPawn(double figurenAbsValue, Board b) {
		Figur[][] board = b.getPosition();
		double sum = 0.;
		Figur toCheck = board[3][3];
		if(toCheck != null) {
			double multi = 1.;
			if(!toCheck.isWhite()) {
				multi = -1.;
			}
			if(toCheck.getName().equals("pawn")) {
				sum += 1*multi;
			}else {
				sum += 0.4*multi;
			}
		}
		toCheck = board[3][4];
		if(toCheck != null) {
			double multi = 1.;
			if(!toCheck.isWhite()) {
				multi = -1.;
			}
			if(toCheck.getName().equals("pawn")) {
				sum += 1*multi;
			}else {
				sum += 0.4*multi;
			}
		}
		toCheck = board[4][3];
		if(toCheck != null) {
			double multi = 1.;
			if(!toCheck.isWhite()) {
				multi = -1.;
			}
			if(toCheck.getName().equals("pawn")) {
				sum += 1*multi;
			}else {
				sum += 0.4*multi;
			}
		}
		toCheck = board[4][4];
		if(toCheck != null) {
			double multi = 1.;
			if(!toCheck.isWhite()) {
				multi = -1.;
			}
			if(toCheck.getName().equals("pawn")) {
				sum += 1*multi;
			}else {
				sum += 0.4*multi;
			}
		}
		return sum*figurenAbsValue/50.;
	}

	private double figurenGetAbsValue(ArrayList<Figur> figuren) {
		double sum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			sum += Math.abs(figuren.get(i).getValue());
		}
		return sum;
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
						if(kingX < 8 && kingX > -1 && board[kingX][kingY+1] != null) {
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
						if(kingX < 8 && kingX > -1 && board[kingX][kingY-1] != null) {
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
				sum += (3.5-Math.abs(3.5-kingY))*multi*(30-figurenAbsValue)/85.;	//endgame position
				sum += (3.5-Math.abs(3.5-kingX))*multi*(30-figurenAbsValue)/85.;
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

	private double figurenCounter(ArrayList<Figur> figuren) {
		double sum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			sum += figuren.get(i).getValue();
		}
		return sum/figuren.size()*78;
	}
	
	public double getActEvaluation() {
		return actEvaluation;
	}
}
