import java.awt.Point;
import java.util.ArrayList;

public class Rook implements Figur{
	private final String name = "rook";
	private Point position;
	private boolean isWhite;
	private boolean moved = false;
	private final Point[] directions = {new Point(1, 0), new Point(-1,0), new Point(0,1), new Point(0,-1)};
	private double value = 5.;
	private int protection;
	
	public Rook(Point nPos, boolean nWhite) {
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
			Point direktion = directions[i];
			Point positionToCheck = addPoints(position, direktion);
			Figur f;
			if(positionToCheck.getX() == 8 || positionToCheck.getX() == -1 || positionToCheck.getY() == 8 || positionToCheck.getY() == -1) {
				f = b.getFigur(position);	//ends the loop
			}else {
				f = b.getFigur(positionToCheck);
			}
			while(f == null) {
				if(moved) {
					moves.add(new Prio(positionToCheck, 0.1, false));
				}else {
					moves.add(new Prio(positionToCheck, -0.3, false));
				}
				positionToCheck = addPoints(positionToCheck, direktion);
				f = b.getFigur(positionToCheck);
				if(positionToCheck.getX() >= 8 || positionToCheck.getX() <= -1 || positionToCheck.getY() >= 8 || positionToCheck.getY() <= -1) {
					f = b.getFigur(position);	//ends the loop
				}
			}
			if(f.isWhite() != isWhite) {
				f.addProtection(-1);
				moves.add(new Prio(positionToCheck, 0.5, false));	//capture
			}else {
				f.addProtection(1);
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
		if(moved) {
			return -0.7;
		}
		return 0;
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
