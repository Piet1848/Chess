import java.awt.Point;

public class Board {
	public double[][] board;
	private Point selectedField;
	private int selectedFigure;
	private boolean lastWhite;
	public Board() {
		initBoard();
	}

	private void initBoard() {
		board = new double[8][8];
		//pawns
		for(int i = 0; i < 8; i++) {
			board[1][i] = 1;
			board[6][i] = -1;
		}
		//white
		board[0][0] = 2; //rook
		board[0][1] = 3; //knight
		board[0][2] = 4; //bishop
		board[0][3] = 5; //queen
		board[0][4] = 6; //king
		board[0][5] = 4;
		board[0][6] = 3;
		board[0][7] = 2;
		//black
		board[7][0] = -2;
		board[7][1] = -3;
		board[7][2] = -4;
		board[7][3] = -6;
		board[7][4] = -5;
		board[7][5] = -4;
		board[7][6] = -3;
		board[7][7] = -2;
	}

	public int select(Point nSelectedField) {
		selectedField = nSelectedField;
		selectedFigure = (int) board[selectedField.x][selectedField.y];
		board[selectedField.x][selectedField.y] = 0;
		return selectedFigure;
	}

	public void move(Point nField) {
		//check if move is possible
		if(selectedFigure < 0) {
			deleteB();
		}else {
			deleteW();
		}
		if(lastWhite ^ selectedFigure > 0) {
			boolean moved;
			Point change = posChange(nField);
			switch(selectedFigure) {
			case(1):moved = wPawn(nField, change);
			break;
			case(2):moved = rook(nField, change);
			break;
			case(3):moved = knight(nField, change);
			break;
			case(4):moved = bishop(nField, change);
			break;
			case(5):moved = queen(nField, change);
			break;
			case(6):moved = king(nField, change);
			break;
			case(-1):moved = bPawn(nField, change);
			break;
			case(-2):moved = rook(nField, change);
			break;
			case(-3):moved = knight(nField, change);
			break;
			case(-4):moved = bishop(nField, change);
			break;
			case(-5):moved = queen(nField, change);
			break;
			case(-6):moved = king(nField, change);
			break;
			default: moved = false;
			break;
			}
			if(moved) {
				lastWhite = !lastWhite;
			}
		}else {
			board[selectedField.x][selectedField.y] = selectedFigure;
		}
	}

	private void deleteW() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] == 0.1) {
					board[i][j] = 0;
				}
			}
		}
	}

	private void deleteB() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] == -0.1) {
					board[i][j] = 0;
				}
			}
		}
	}
	//TODO promotion
	private boolean bPawn(Point nField, Point change) {
		if(change.x == 1 && change.y == 0) {
			if(board[nField.x][nField.y] == 0) {
				if(nField.x == 0) {
					board[nField.x][nField.y] = -5; //TODO
				}else {
					board[nField.x][nField.y] = -1;
				}
				return true;
			}else {
				board[selectedField.x][selectedField.y] = -1;
				return false;
			}
		}else if(change.x == 2 && change.y == 0) {
			if(selectedField.x == 6 && board[nField.x][nField.y] == 0 && board[nField.x+1][nField.y] == 0) {
				board[nField.x][nField.y] = -1;
				board[nField.x+1][nField.y] = -0.1; //en passant
				return true;
			}else {
				board[selectedField.x][selectedField.y] = -1;
				return false;
			}
		}else if(change.x == 1 && Math.abs(change.y) == 1) {
			if(board[nField.x][nField.y] >= 1) {
				if(nField.x == 0) {
					board[nField.x][nField.y] = -5;//TODO
				}else {
					board[nField.x][nField.y] = -1;
				}
				return true;
			}else if(board[nField.x][nField.y] == 0.1){
				board[nField.x][nField.y] = 1;
				board[nField.x+1][nField.y] = 0;
				return true;
			}else{
				board[selectedField.x][selectedField.y] = -1;
				return false;
			}
		}else {
			board[selectedField.x][selectedField.y] = -1;
			return false;
		}
	}


	private boolean wPawn(Point nField, Point change) {
		if(change.x == -1 && change.y == 0) {
			if(board[nField.x][nField.y] == 0) {
				if(nField.x == 7) {
					board[nField.x][nField.y] = 5; //TODO real promotion
				}else {
					board[nField.x][nField.y] = 1;
				}
				return true;
			}else {
				board[selectedField.x][selectedField.y] = 1;
				return false;
			}
		}else if(change.x == -2 && change.y == 0) {
			if(selectedField.x == 1 && board[nField.x][nField.y] == 0 && board[nField.x-1][nField.y] == 0) {
				board[nField.x][nField.y] = 1;
				board[nField.x-1][nField.y] = 0.1; //en passant
				return true;
			}else {
				board[selectedField.x][selectedField.y] = 1;
				return false;
			}
		}else if(change.x == -1 && Math.abs(change.y) == 1) {
			if(board[nField.x][nField.y] <= -1) {
				if(nField.x == 7) {
					board[nField.x][nField.y] = 5; //TODO real promotion
				}else {
					board[nField.x][nField.y] = 1;
				}
				return true;
			}else if(board[nField.x][nField.y] == -0.1){
				board[nField.x][nField.y] = 1;
				board[nField.x-1][nField.y] = 0;
				return true;
			}else {
				board[selectedField.x][selectedField.y] = 1;
				return false;
			}
		}else {
			board[selectedField.x][selectedField.y] = 1;
			return false;
		}
	}

	private boolean king(Point nField, Point change) {
		if(Math.abs(change.x) <= 1 && Math.abs(change.y) <= 1 && board[nField.x][nField.y]*-selectedFigure >= 0) {//TODO check for attack
			board[nField.x][nField.y] = selectedFigure;
			return true;
		}else {
			board[selectedField.x][selectedField.y] = selectedFigure;
			return false;
		}
	}

	private boolean kingIsAttaced(boolean white) {
		//TODO
		return false;
	}

	private boolean queen(Point nField, Point change) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean bishop(Point nField, Point change) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean knight(Point nField, Point change) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean rook(Point nField, Point change) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean[][] movementRook(Point figure){ //to be tested
		boolean[][] posMoves = new boolean[8][8];
		int whiteTurn = 1;
		if(!lastWhite) {
			whiteTurn = -1;
		}

		int i = figure.x;
		while(i < 8 && !(board[i-1][figure.y] != 0 || board[i][figure.y] * whiteTurn >= 1)) {
			posMoves[i][figure.y] = true;
			i++;
		}
		
		i = figure.x;
		while(i >= 0 && !(board[i+1][figure.y] != 0 || board[i][figure.y] * whiteTurn >= 1)) {
			posMoves[i][figure.y] = true;
			i--;
		}
		
		i = figure.y;
		while(i >= 0 && !(board[figure.x][i+1] != 0 || board[figure.x][i] * whiteTurn >= 1)) {
			posMoves[figure.x][i] = true;
			i--;
		}
		
		i = figure.y;
		while(i < 8 && !(board[figure.x][i-1] != 0 || board[figure.x][i] * whiteTurn >= 1)) {
			posMoves[figure.x][i] = true;
			i++;
		}
		
		return posMoves;
	}

	private boolean[][] movementBishop(Point figure){ // to be tested
		boolean[][] posMoves = new boolean[8][8];
		int whiteTurn = 1;
		if(!lastWhite) {
			whiteTurn = -1;
		}

		int i = figure.x;
		int j = figure.y;
		while(i < 8 && !(board[i-1][j-1] != 0 || board[i][j] * whiteTurn >= 1)) {
			posMoves[i][j] = true;
			i++;
			j++;
		}
		
		i = figure.x;
		j = figure.y;
		while(i < 8 && !(board[i-1][j+1] != 0 || board[i][j] * whiteTurn >= 1)) {
			posMoves[i][j] = true;
			i++;
			j--;
		}
		
		i = figure.x;
		j = figure.y;
		while(i < 8 && !(board[i+1][j+1] != 0 || board[i][j] * whiteTurn >= 1)) {
			posMoves[i][j] = true;
			i--;
			j--;
		}
		
		i = figure.x;
		j = figure.y;
		while(i < 8 && !(board[i+1][j-1] != 0 || board[i][j] * whiteTurn >= 1)) {
			posMoves[i][j] = true;
			i--;
			j++;
		}
		
		return posMoves;
	}
	
	private boolean [][] movementKnight(Point figure) {
		boolean[][] posMoves = new boolean[8][8];
		int whiteTurn = 1;
		if(!lastWhite) {
			whiteTurn = -1;
		}
		int nX = selectedField.x+2;
		int nY = selectedField.y+1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x+1;
		nY = selectedField.y+2;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-1;
		nY = selectedField.y+2;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-2;
		nY = selectedField.y+1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x+2;
		nY = selectedField.y-1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x+1;
		nY = selectedField.y-2;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-1;
		nY = selectedField.y-2;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-2;
		nY = selectedField.y-1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		return posMoves;
	}
	

	private boolean[][] movementPawn(Point figure){
		boolean[][] posMoves = new boolean[8][8];
		int whiteTurn = 1;
		if(!lastWhite) {
			whiteTurn = -1;
		}
		if(lastWhite) {
			
		}
		
		return posMoves;
	}

	private boolean[][] movementKing(Point figure){
		boolean[][] posMoves = new boolean[8][8];
		int whiteTurn = 1;
		if(!lastWhite) {
			whiteTurn = -1;
		}
		
		int nX = selectedField.x;
		int nY = selectedField.y+1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x+1;
		nY = selectedField.y+1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-1;
		nY = selectedField.y+1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x+1;
		nY = selectedField.y;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-1;
		nY = selectedField.y;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x;
		nY = selectedField.y-1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		nX = selectedField.x+1;
		nY = selectedField.y-1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}

		nX = selectedField.x-1;
		nY = selectedField.y-1;
		if(nX >= 0 && nX < 8 && nY >= 0 && nY < 8) {
			if(board[nX][nY] == 0 || board[nX][nY] * whiteTurn <= -1)
				posMoves[nX][nY] = true;
		}
		
		return posMoves;
	}

	private boolean[][] movementQueen(Point figure){
		boolean[][] rookMoves = movementRook(figure);
		boolean[][] bishopMoves = movementBishop(figure);
		boolean[][] queenMoves = new boolean[8][8];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(rookMoves[i][j] || bishopMoves[i][j]) {
					queenMoves[i][j] = true;
				}
			}
		}
		return queenMoves;
	}

	private Point posChange(Point nField) {
		return new Point(selectedField.x - nField.x, selectedField.y - nField.y);
	}
}
