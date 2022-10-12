import java.awt.Point;
import java.util.ArrayList;

public class Knight implements Figur{
	private final String name = "knight";
	private Point position;
	private boolean isWhite;
	private final Point[] directions = {new Point(-2,1), new Point(-1,2), new Point(1,2), new Point(2,1), new Point(2,-1), new Point(1,-2), new Point(-1,-2), new Point(-2,-1)};
	private double value = 2.9;
	private boolean moved;
	private double bonus;
	private int protection;

	public Knight(Point nPos, boolean nWhite) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value *= -1;
		}
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public ArrayList<Prio> possibleMoves(Board b) {
		ArrayList<Prio> moves = new ArrayList<>();
		for(int i = 0; i < directions.length; i++) {
			Point positionToCheck = addPoints(position, directions[i]);
			Figur f = b.getFigur(positionToCheck);
			if(positionToCheck.getX() >= 8 || positionToCheck.getX() <= -1 || positionToCheck.getY() >= 8 || positionToCheck.getY() <= -1) {	//TODO could be more efficient
				f = b.getFigur(position);	//ends the loop
			}
			if(f == null) {
				moves.add(new Prio(positionToCheck, 0., false));
			}else if(f.isWhite() != isWhite) {
				f.addProtection(-1);
				moves.add(new Prio(positionToCheck, f.getValue(), false));	//capture
			}else {
				f.addProtection(1);
			}
		}
		return moves;
	}

	@Override
	public void moveTo(Point nPoint) {	//TODO
		position = nPoint;
		if(isWhite) {
			bonus = 1.;
			if(nPoint.x != 0 && nPoint.x != 7 && nPoint.y != 0 && nPoint.y != 7) {
				bonus = 1.8;
			}
		}else {
			bonus = -1.;
			if(nPoint.x != 0 && nPoint.x != 7 && nPoint.y != 0 && nPoint.y != 7) {
				bonus = -1.8;
			}
		}
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
	public String getName() {
		return name;
	}

	@Override
	public void setMoved(boolean moved) {
		this.moved = moved;
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
	public void resetProtection() {
		protection = 0;
	}

	@Override
	public void addProtection(int x) {
		protection += x;
	}

	@Override
	public double getProtection() {
		if(protection < 0) {
			return value;
		}
		return 0;
	}
}
