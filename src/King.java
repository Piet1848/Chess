import java.awt.Point;
import java.util.ArrayList;

public class King implements Figur{
	private final FigurName name = FigurName.KING;
	private Point position;
	private final boolean isWhite;
	private boolean moved = false;
	private final Point[] directions = {new Point(1, 1), new Point(-1,-1), new Point(-1,1), new Point(1,-1), new Point(1, 0), new Point(-1,0), new Point(0,1), new Point(0,-1)};
	private final double value;
	private double bonus;
	private int protection;
	
	public King(Point nPos, boolean nWhite) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -1000;
		}else {
			value = 1000;
		}
	}

	public King(Point nPos, boolean nWhite, boolean nMoved, double nBonus, int nProtection){
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -1000;
		}else {
			value = 1000;
		}
		moved = nMoved;
		bonus = nBonus;
		protection = nProtection;
	}
	
	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public ArrayList<Move> possibleMoves(Board b) {
		ArrayList<Move> moves = new ArrayList<>();
		for (Point direktion : directions) {
			Point positionToCheck = addPoints(position, direktion);
			Figur f = b.getFigur(positionToCheck);
			if (!(positionToCheck.getX() == 8 || positionToCheck.getX() == -1 || positionToCheck.getY() == 8 || positionToCheck.getY() == -1)) {
				if (f == null) {
					moves.add(new Move(position, positionToCheck, name, this, null, moved));
				} else if (f.isWhite() != isWhite) {
					f.addProtection(-1);
					moves.add(new Move(position, positionToCheck, name, this, f, moved));
				} else {
					f.addProtection(1);
				}
			}
		}
		if(!moved) {	//TODO check if in Check
			ArrayList<Figur> rooks = b.getNotMovedRooks(isWhite);
			for(Figur rook : rooks) {
				Point move;
				if(rook.getPosition().x-position.x < 0) {	//Kingside Castle
					move = new Point(-1,0);
				}else {	//Queenside Castle
					move = new Point(1,0);
				}
				Point positionToCheck = addPoints(position, move);
				while(b.getFigur(positionToCheck) == null) {
					positionToCheck = addPoints(positionToCheck, move);
				}
				if(b.getFigur(positionToCheck) == rook) {
					moves.add(new Move(position, rook.getPosition(), name, this, rook, moved));
				}
			}
			if(isWhite) {
				bonus = rooks.size();
			}else {
				bonus = -rooks.size();
			}
		}
		return moves;
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
		return new Point((int)(a.getX()+b.getX()), (int) (a.getY()+b.getY()));
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
		if(x < 0)	//only attacs
			protection += x;
	}

	@Override
	public double getProtection() {
		return 0;
	}

	@Override
	public void setMoved(boolean moved){
		this.moved = moved;
	}
}
