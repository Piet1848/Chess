import java.awt.Point;
import java.util.ArrayList;

public class Pawn implements Figur{
	private final FigurName name = FigurName.PAWN;
	private final boolean isWhite;
	private Point position;
	private boolean moved = false;
	private final double value;
	private final Point direktion;
	private double bonus = 0.;
	private int protection;

	public Pawn(Point nPos, boolean nWhite) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -1;
			direktion = new Point(0, -1);
		}else{
			value = 1;
			direktion = new Point(0, 1);
		}
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public ArrayList<Move> possibleMoves(Board b) {	//TODO en passante missing
		bonus = 0;
		ArrayList<Move> moves = new ArrayList<>();

		if(position.y >= 7 || position.y <= 0) {
			System.out.println("Error Pawn at back rank");
			return moves;
		}

		Point positionToCheck = addPoints(position, direktion);
		Figur f = b.getFigur(positionToCheck);
		if(f == null) {
			if(positionToCheck.getY() == 7 || positionToCheck.getY() == 0) {
				moves.add(new Move(position, positionToCheck, FigurName.QUEEN,this, null, moved));	//make Queen
				moves.add(new Move(position, positionToCheck, FigurName.ROOK,this, null, moved));	//Rook
				moves.add(new Move(position, positionToCheck, FigurName.KNIGHT,this, null, moved));	//Knight
				moves.add(new Move(position, positionToCheck, FigurName.BISHOP,this, null, moved));	//Bishop
			}else {
				moves.add(new Move(position, positionToCheck, name,this, null, moved));
				if(!moved) {
					positionToCheck = addPoints(positionToCheck, direktion);	//two steps forward
					if(b.getFigur(positionToCheck) == null)
						moves.add(new Move(position, positionToCheck, name,this, null, moved));	//double
				}
			}
		}

		positionToCheck = addPoints(addPoints(position, direktion), new Point(1, 0));
		f = b.getFigur(positionToCheck);
		if(f != null) {
			pawnCapture(moves, positionToCheck, f);
		}

		positionToCheck = addPoints(positionToCheck, new Point(-2, 0));
		f = b.getFigur(positionToCheck);
		if(f != null) {
			pawnCapture(moves, positionToCheck, f);
		}
		return moves;
	}
	
	private void pawnCapture(ArrayList<Move> moves, Point positionToCheck, Figur f) {
		if(f.isWhite() != isWhite) {
			f.addProtection(-1);
			if(positionToCheck.getY() == 7 || positionToCheck.getY() == 0) {
				moves.add(new Move(position, positionToCheck, FigurName.QUEEN,this, f, moved));	//make Queen
				moves.add(new Move(position, positionToCheck, FigurName.ROOK,this, f, moved));	//Rook
				moves.add(new Move(position, positionToCheck, FigurName.KNIGHT,this, f, moved));	//Knight
				moves.add(new Move(position, positionToCheck, FigurName.BISHOP,this, f, moved));	//Bishop
			}else {
				moves.add(new Move(position, positionToCheck, name,this, f, moved));	//pawn capture
			}
		}else {
			f.addProtection(1);
		}
	}
	
	@Override
	public void moveTo(Point nPoint) {
		position = nPoint;
		moved = true;
	}

	@Override
	public boolean isWhite() {
		return isWhite;
	}

	private Point addPoints(Point a, Point b) {
		return new Point((int) (a.getX()+b.getX()), (int) (a.getY()+b.getY()));
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getAbsValue(){
		return Math.abs(value);
	}

	@Override
	public FigurName getName() {
		return name;
	}

	@Override
	public boolean getMoved() {
		return moved;
	}

	@Override
	public double getBonus() {
		return bonus;
	}

	@Override
	public void resetProtection(boolean currentIsWhite) {
		if(isWhite == currentIsWhite)
			protection = 0;
	}
	
	@Override
	public void addProtection(int x) {
		protection += x;
	}

	@Override
	public double getProtection() {
		return protection;
	}

	@Override
	public void setMoved(boolean moved){
		this.moved = moved;
	}
}
