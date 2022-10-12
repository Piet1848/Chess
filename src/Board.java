import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class Board {
	private Figur[][] board;
	private ArrayList<Figur> figuren;

	private ArrayList<Boolean> movedStack;
	private boolean[][] lastChangingBoard;
	
	public Board() {
		movedStack = new ArrayList<>();
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

	public void setPosition(ArrayList<Figur> position){
		figuren = position;
		movedStack = new ArrayList<>();
		board = new Figur[8][8];
		for(int i = 0; i < position.size(); i++){
			Point pos = position.get(i).getPosition();
			board[pos.x][pos.y] = position.get(i);
		}
	}
	
	public Figur[][] getPosition(){
		Figur[][] nBoard = new Figur[8][8];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				nBoard[i][j] = board[i][j];
			}
		}
		return nBoard;
	}
	
	public Figur getFigur(Point p) {
		if(p.getX() >= 8 || p.getX() <= -1 || p.getY() >= 8 || p.getY() <= -1) {
			return null;
		}
		return board[(int) p.getX()][(int) p.getY()];
	}

	public ArrayList<Move> getAllMoves(boolean isWhite){
		resetMoves();
		boolean[][] changingBoard = new boolean[8][8];
		ArrayList<Move> moves = new ArrayList<>();
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i).isWhite() == isWhite) {
				ArrayList<Prio> nMoves = figuren.get(i).possibleMoves(this);
				Point p = figuren.get(i).getPosition();
				if(nMoves == null){
					System.out.println("nMoves is null");
					System.out.println("figur: " +figuren.get(i).getName() + "_" + figuren.get(i).getPosition() + "_" + figuren.get(i).isWhite());
				}
				updateBoard(changingBoard, nMoves);
				moves.addAll(toArrayMove(nMoves, p));
			}
		}
		lastChangingBoard = changingBoard;
		return moves;
	}
	
	private void resetMoves() {
		for(int i = 0; i < figuren.size(); i++) {
			figuren.get(i).resetProtection();
		}
	}
	
	private void updateBoard(boolean[][] changingBoard, ArrayList<Prio> nMoves) {
		for(int i = 0; i < nMoves.size(); i++) {
			if(nMoves.get(i).point.x >= 8 || nMoves.get(i).point.x < 0  || nMoves.get(i).point.y < 0 || nMoves.get(i).point.y >= 8){
				System.out.println("Out of bounce: x=" + nMoves.get(i).point.x);
				System.out.println("Out of bounce: y=" + nMoves.get(i).point.y);
				System.out.println("i=" + i);
				System.out.println("Prio="+nMoves.get(i).prio);
			}
			changingBoard[nMoves.get(i).point.x][nMoves.get(i).point.y] = true;
		}
	}
	
	public ArrayList<Move> getFastSortedMoves(boolean isWhite){
		return oldSort(getAllMoves(isWhite));
	}
	
	private ArrayList<Move> oldSort(ArrayList<Move> moves){
		Move tempMove;
		for(int i = 1; i < moves.size(); i++) {
			tempMove = moves.get(i);
			double vTempMove = tempMove.prio;
			int j = i;
			while(j > 0 && moves.get(j-1).prio < vTempMove) {	//old .prio
				moves.set(j, moves.get(j-1));
				j--;
			}
			moves.set(j, tempMove);
		}
		return moves;
	}
	
	private ArrayList<Move> toArrayMove(ArrayList<Prio> movesPrio, Point p) {
		ArrayList<Move> n = new ArrayList<>();
		for(int i = 0; i < movesPrio.size(); i++) {
			if(movesPrio.get(i).point.getX() >= 8 || movesPrio.get(i).point.getX() <= -1 || movesPrio.get(i).point.getY() >= 8 || movesPrio.get(i).point.getY() <= -1) {
				System.out.print("Error2: ");
				System.out.println(board[(int) p.getX()][(int) p.getY()].getName() + " at: x" + p.getX() + ", y" + p.getY());
				printBoard();
			}
			n.add(new Move(p, movesPrio.get(i).point, movesPrio.get(i).prio, movesPrio.get(i).promotion));
		}
		return n;
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
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i).getName().equals("king")) {
				countKing++;
				if(countKing == 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Figur move(Move move) {
		Figur lFigur = board[move.end.x][move.end.y];
		movedStack.add(0, board[move.start.x][move.start.y].getMoved());
		//Castle
		if (lFigur != null && lFigur.getName().equals("rook") && board[move.start.x][move.start.y].getName().equals("king") && lFigur.isWhite() == board[move.start.x][move.start.y].isWhite()) {
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
		} else if (move.promotion) {
			if (move.prio == 3.113) {    //Pawn promotion (Queen)
				Figur pawn = board[move.start.x][move.start.y];
				Figur queen = new Queen(move.end, pawn.isWhite());
				board[move.end.x][move.end.y] = queen;
				board[move.start.x][move.start.y] = null;
				deleteFigur(lFigur);
				deleteFigur(pawn);
				figuren.add(queen);
			} else if (move.prio == 3.112) {    //Rook
				Figur pawn = board[move.start.x][move.start.y];
				Figur rook = new Rook(move.end, pawn.isWhite());
				board[move.end.x][move.end.y] = rook;
				board[move.start.x][move.start.y] = null;
				deleteFigur(lFigur);
				deleteFigur(pawn);
				figuren.add(rook);
			} else if (move.prio == 3.111) {    //Knight
				Figur pawn = board[move.start.x][move.start.y];
				Figur knight = new Knight(move.end, pawn.isWhite());
				board[move.end.x][move.end.y] = knight;
				board[move.start.x][move.start.y] = null;
				deleteFigur(lFigur);
				deleteFigur(pawn);
				figuren.add(knight);
			} else if (move.prio == 3.110) {    //Bishop
				Figur pawn = board[move.start.x][move.start.y];
				Figur bishop = new Bishop(move.end, pawn.isWhite());
				board[move.end.x][move.end.y] = bishop;
				board[move.start.x][move.start.y] = null;
				deleteFigur(lFigur);
				deleteFigur(pawn);
				figuren.add(bishop);
			}
		} else {    //normal
			board[move.start.x][move.start.y].moveTo(move.end);
			board[move.end.x][move.end.y] = board[move.start.x][move.start.y];
			board[move.start.x][move.start.y] = null;
			deleteFigur(lFigur);//TODO
		}
		return lFigur;
	}
	
	private void deleteFigur(Figur lFigur) {
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i) == lFigur) {
				figuren.remove(i);
				break;
			}
		}
	}

	public void redoMove(Move move, Figur lFigur) {
		if(board[move.end.x][move.end.y] == null) {	//Castle
			if(move.end.x == 0) {
				board[0][move.start.y] = board[2][move.start.y];
				board[0][move.start.y].moveTo(new Point(0,move.end.y));
				board[0][move.start.y].setMoved(false);
				board[2][move.start.y] = null;
				board[3][move.start.y] = board[1][move.start.y];
				if(move.start.x != 3) {
					System.out.println("Castle Error");
				}
				board[3][move.start.y].moveTo(new Point(3, move.start.y));
				board[1][move.start.y] = null;

			}else {
				board[7][move.start.y] = board[4][move.start.y];
				board[4][move.start.y] = null;
				board[7][move.start.y].moveTo(new Point(7,move.end.y));
				board[7][move.start.y].setMoved(false);
				board[3][move.start.y] = board[5][move.start.y];
				if(move.start.x != 3) {
					System.out.println("Castle Error");
				}
				board[5][move.start.y] = null;
				board[3][move.start.y].moveTo(new Point(3, move.start.y));
			}
		}else {
			board[move.start.x][move.start.y] = board[move.end.x][move.end.y];
			board[move.start.x][move.start.y].moveTo(move.start);
			board[move.end.x][move.end.y] = lFigur;
			if(lFigur != null) {
				figuren.add(lFigur);
			}
		}
		board[move.start.x][move.start.y].setMoved(movedStack.get(0));
		movedStack.remove(0);
	}

	public void redoPromotion(Move move, Figur lFigur) {
		deleteFigur(board[move.end.x][move.end.y]);	//remove promoted figure
		Figur pawn = new Pawn(move.start, board[move.end.x][move.end.y].isWhite(), true);
		board[move.start.x][move.start.y] = pawn;
		board[move.end.x][move.end.y] = lFigur;
		figuren.add(pawn);
		if(lFigur != null) {
			figuren.add(lFigur);
		}
	}

	public ArrayList<Figur> getRooks(boolean isWhite) {
		ArrayList<Figur> rooks = new ArrayList<>();
		for(int i = 0; i < figuren.size(); i++) {
			Figur f = figuren.get(i);
			if(f.getName().equals("rook") && figuren.get(i).isWhite() == isWhite && figuren.get(i).getMoved() == false) {
				rooks.add(f);
			}
		}
		return rooks;
	}
	
	public boolean winner() {
		for(int i = 0; i < figuren.size(); i++) {
			if(figuren.get(i).getName() == "king") {
				return figuren.get(i).isWhite();
			}
		}
		System.out.println("Error at Board.winner()");
		return false;
	}
	
	private void printBoard() {	//Just for testing
		System.out.println();
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Figur f = board[i][j];
				if(f == null) {
					System.out.print(" ");
				}else if(f.isWhite()) {
					if(f.getName().equals("king")) {
						System.out.print("K");
					}
					if(f.getName().equals("rook")) {
						System.out.print("R");
					}
					if(f.getName().equals("queen")) {
						System.out.print("Q");
					}
					if(f.getName().equals("bishop")) {
						System.out.print("B");
					}
					if(f.getName().equals("knight")) {
						System.out.print("S");
					}
					if(f.getName().equals("pawn")) {
						System.out.print("P");
					}
				}else {
					if(f.getName().equals("king")) {
						System.out.print("k");
					}
					if(f.getName().equals("rook")) {
						System.out.print("r");
					}
					if(f.getName().equals("queen")) {
						System.out.print("q");
					}
					if(f.getName().equals("bishop")) {
						System.out.print("b");
					}
					if(f.getName().equals("knight")) {
						System.out.print("s");
					}
					if(f.getName().equals("pawn")) {
						System.out.print("p");
					}
				}
				System.out.print("|");
			}
			System.out.println();
		}
	}

	public ArrayList<Figur> getFiguren(){
		return figuren;
	}
}
