import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Board {
	private Figur[][] board;
	private ArrayList<Figur> figuren;
	
	public Board() {
		board = new Figur[8][8];
		initBoard();
		updateArrayList();
	}
	
	public Board(Figur[][] nPosition) {
		board = nPosition;
		updateArrayList();
	}
	
	private void updateArrayList() {
		figuren = new ArrayList<>();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] != null) {
					figuren.add(board[i][j]);
				}
			}
		}
	}
	
	public Figur getFigur(Point p) {
		if(p.getX() >= 8 || p.getX() <= -1 || p.getY() >= 8 || p.getY() <= -1) {
			return null;
		}
		return board[(int) p.getX()][(int) p.getY()];
	}

	public ArrayList<Move> getAllMoves(boolean isWhite, boolean checkCheck){
		resetProtection(isWhite);
		boolean[][] changingBoard = new boolean[8][8];
		ArrayList<Move> moves = new ArrayList<>();
		for (Figur figur : figuren) {
			if (figur.isWhite() == isWhite) {
				ArrayList<Move> nMoves = figur.possibleMoves(this);
				Point p = figur.getPosition();
				if (nMoves == null) {
					System.out.println("nMoves is null");
					System.out.println("figur: " + figur.getName() + "_" + figur.getPosition() + "_" + figur.isWhite());
				}
				updateBoard(changingBoard, nMoves);
				moves.addAll(nMoves);
			}
		}
		if(checkCheck)
			checkCheck(moves, isWhite);
		return moves;
	}

	private void checkCheck(ArrayList<Move> moves, boolean isWhite){
		for (int i = 0; i < moves.size(); i++) {
			move(moves.get(i));
			if(inCheck(isWhite)){
				undoMove(moves.get(i));
				moves.remove(i--);
			}else {
				undoMove(moves.get(i));
			}
		}
	}
	
	private void resetProtection(boolean isWhite) {
		for (Figur figur : figuren) {
			figur.resetProtection(isWhite);
		}
	}
	
	private void updateBoard(boolean[][] changingBoard, ArrayList<Move> nMoves) {
		for(Move move : nMoves) {
			if(move.end.x >= 8 || move.end.x < 0  || move.end.y < 0 || move.end.y >= 8){
				System.out.println("Out of bounce: x=" + move.end.x);
				System.out.println("Out of bounce: y=" + move.end.y);
				System.out.println("StartFigur: " + move.startFigur.getName().name());
			}
			changingBoard[move.end.x][move.end.y] = true;
		}
	}
	
	private void initBoard() {
		board[0][0] = new Rook(new Point(0,0), true);
		board[7][0] = new Rook(new Point(7,0), true);
		board[0][7] = new Rook(new Point(0,7), false);
		board[7][7] = new Rook(new Point(7,7), false);
		
		board[1][0] = new Knight(new Point(1,0), true);
		board[6][0] = new Knight(new Point(6,0), true);
		board[1][7] = new Knight(new Point(1,7), false);
		board[6][7] = new Knight(new Point(6,7), false);
		
		board[2][0] = new Bishop(new Point(2,0), true);
		board[5][0] = new Bishop(new Point(5,0), true);
		board[2][7] = new Bishop(new Point(2,7), false);
		board[5][7] = new Bishop(new Point(5,7), false);
		
		board[4][0] = new Queen(new Point(4,0), true);
		board[4][7] = new Queen(new Point(4,7), false);
		
		board[3][0] = new King(new Point(3,0), true);
		board[3][7] = new King(new Point(3,7), false);
		
		for(int i = 0; i < 8; i++) {
			board[i][1] = new Pawn(new Point(i,1), true);
			board[i][6] = new Pawn(new Point(i,6), false);
		}
	}
	
	public boolean hasBothKings() {
		int countKing = 0;
		for (Figur figur : figuren) {
			if (figur.getName() == FigurName.KING) {
				countKing++;
				if (countKing == 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void move(Move move) {

		if(board[move.start.x][move.start.y] == null){
			System.out.println("" + move.start + "End: " + move.end);
		}

		Figur lFigur = board[move.end.x][move.end.y];
		//Castle
		if (move.castle()) {
			if (!lFigur.getMoved() && !board[move.start.x][move.start.y].getMoved()) {
				if (move.end.x == 0) {
					board[2][move.start.y] = lFigur;
					lFigur.moveTo(new Point(2, move.end.y));
					board[1][move.start.y] = board[move.start.x][move.start.y];
					board[move.start.x][move.start.y].moveTo(new Point(1, move.start.y));

				} else {
					board[4][move.start.y] = lFigur;
					lFigur.moveTo(new Point(4, move.end.y));
					board[5][move.start.y] = board[move.start.x][move.start.y];
					board[move.start.x][move.start.y].moveTo(new Point(5, move.start.y));
				}
				board[move.end.x][move.start.y] = null;
				board[move.start.x][move.start.y] = null;
			}
		} else {    //normal
			if(move.isPromotion()){
				board[move.end.x][move.end.y] = getNewFigur(move.promotion, move.end, move.startFigur.isWhite());
				figuren.add(board[move.end.x][move.end.y]);
				deleteFigur(move.startFigur);
			}else {
				board[move.start.x][move.start.y].moveTo(move.end);
				board[move.end.x][move.end.y] = board[move.start.x][move.start.y];
			}
			board[move.start.x][move.start.y] = null;

			if(move.isCapture())
				deleteFigur(lFigur);
		}
	}

	public void undoMove(Move move){
		if(move.castle()){
			board[move.startFigur.getPosition().x][move.startFigur.getPosition().y] = null;
			board[move.endFigur.getPosition().x][move.endFigur.getPosition().y] = null;
			board[move.start.x][move.start.y] = move.startFigur;
			board[move.end.x][move.end.y] = move.endFigur;
			move.startFigur.setMoved(false);
			move.endFigur.setMoved(false);
			move.startFigur.moveTo(move.start);
			move.endFigur.moveTo(move.end);
		}else {
			move.startFigur.moveTo(move.start);
			move.startFigur.setMoved(move.alreadyMoved);
			board[move.start.x][move.start.y] = move.startFigur;

			if(move.isPromotion()){
				deleteFigur(board[move.end.x][move.end.y]);
				figuren.add(move.startFigur);
			}
			if(move.isCapture()){
				figuren.add(move.endFigur);
				board[move.end.x][move.end.y] = move.endFigur;
			}else {
				board[move.end.x][move.end.y] = null;
			}
		}
	}
	
	private void deleteFigur(Figur lFigur) {
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i) == lFigur) {
				figuren.remove(i);
				break;
			}
		}
	}

	private Figur getNewFigur(FigurName name, Point pos, boolean isWhite){
		switch (name){
			case KING -> {
				return new King(pos, isWhite);
			}
			case PAWN -> {
				return new Pawn(pos, isWhite);
			}
			case ROOK -> {
				return new Rook(pos, isWhite);
			}
			case QUEEN -> {
				return new Queen(pos, isWhite);
			}
			case BISHOP -> {
				return new Bishop(pos, isWhite);
			}
			case KNIGHT -> {
				return new Knight(pos, isWhite);
			}
			default -> {	//should not occur
				return null;
			}
		}
	}

	public ArrayList<Figur> getNotMovedRooks(boolean isWhite) {
		ArrayList<Figur> rooks = new ArrayList<>();
		for (Figur f : figuren) {
			if (f.getName() == FigurName.ROOK && f.isWhite() == isWhite && !f.getMoved()) {
				rooks.add(f);
			}
		}
		return rooks;
	}
	
	public boolean winner() {
		for (Figur figur : figuren) {
			if (figur.getName() == FigurName.KING) {
				return figur.isWhite();
			}
		}
		System.out.println("Error at Board.winner()");
		return false;
	}

	private boolean inCheck(boolean isWhite){
		getAllMoves(!isWhite, false);
		Figur king = findKing(isWhite);
		return king.getProtection() != 0;
	}

	private Figur findKing(boolean isWhite){
		for (Figur figur : figuren) {
			if (figur.isWhite() == isWhite && figur.getName() == FigurName.KING) {
				return figur;
			}
		}
		System.out.println("ERROR findKing: No King found!");
		return null;
	}
	
	public void printBoard() {	//Just for testing
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Figur f = board[i][j];
				if(f == null) {
					System.out.print(" ");
				}else if(f.isWhite()) {
					System.out.print(f.getName().name().toUpperCase());
				}else {
					System.out.print(f.getName().name().toLowerCase());
				}
				System.out.print("|");
			}
			System.out.println();
		}
	}

	public ArrayList<Figur> getFiguren(){
		return figuren;
	}

	public Figur[][] getBoard(){
		return board;
	}
}
