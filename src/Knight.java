import java.awt.Point;
import java.util.ArrayList;

public class Knight implements Figur{
	private final String name = "knight";
	private Point position;
	private final boolean isWhite;
	private final Point[] directions = {new Point(-2,1), new Point(-1,2), new Point(1,2), new Point(2,1), new Point(2,-1), new Point(1,-2), new Point(-1,-2), new Point(-2,-1)};
	private final double value;
	private boolean moved;
	private double bonus;
	private int protection;

	public Knight(Point nPos, boolean nWhite) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -2.9;
		}else {
			value = 2.9;
		}
	}

	public Knight(Point nPos, boolean nWhite, boolean nMoved, int nProtection) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -2.9;
		}else {
			value = 2.9;
		}
		moved = nMoved;
		protection = nProtection;
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
			if(!(positionToCheck.getX() >= 8 || positionToCheck.getX() <= -1 || positionToCheck.getY() >= 8 || positionToCheck.getY() <= -1)) {	//TODO could be more efficient
				if(f == null) {
					moves.add(new Prio(positionToCheck, 0., false));
				}else if(f.isWhite() != isWhite) {
					f.addProtection(-1);
					moves.add(new Prio(positionToCheck, f.getAbsValue(), false));	//capture
				}else {
					f.addProtection(1);
				}
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
	public double getAbsValue(){
		return Math.abs(value);
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
	public Figur clone(){
		return new Knight((Point) position.clone(), isWhite, moved, protection);
	}
}
