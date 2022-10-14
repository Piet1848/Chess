import java.awt.Point;
import java.util.ArrayList;

public class Bishop implements Figur{
	private final String name = "bishop";
	private Point position;
	private final boolean isWhite;
	private boolean moved = false;
	private final Point[] directions = {new Point(1, 1), new Point(-1,-1), new Point(-1,1), new Point(1,-1)};
	private final double value;
	private int protection;

	public Bishop(Point nPos, boolean nWhite) {
		position = nPos;
		isWhite = nWhite;
		if(!nWhite) {
			value = -3;
		}else {
			value = 3;
		}
	}

	public Bishop(Point nPos, boolean nWhite, boolean nMoved, int nProtection) {
		position = nPos;
		isWhite = nWhite;
		if (!nWhite) {
			value = -3;
		} else {
			value = 3;
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
			Point direktion = directions[i];
			Point positionToCheck = addPoints(position, direktion);
			Figur f = b.getFigur(positionToCheck);
			if(!(positionToCheck.getX() == 8 || positionToCheck.getX() == -1 || positionToCheck.getY() == 8 || positionToCheck.getY() == -1)) {
				while(f == null) {
					moves.add(new Prio(positionToCheck, 0., false));
					positionToCheck = addPoints(positionToCheck, direktion);
					f = b.getFigur(positionToCheck);
					if(positionToCheck.getX() >= 8 || positionToCheck.getX() <= -1 || positionToCheck.getY() >= 8 || positionToCheck.getY() <= -1) {
						f = b.getFigur(position);	//ends the while
					}
				}
				if(f != null) {
					if (f.isWhite() != isWhite) {
						f.addProtection(-1);
						moves.add(new Prio(positionToCheck, f.getAbsValue(), false));
					} else {
						f.addProtection(1);
					}
				}
			}
		}
		return moves;
	}

	@Override
	public void moveTo(Point nPoint) {
		position = nPoint;
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
		if(moved) {
			return 0.4;
		}
		return 0;
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
		return new Bishop((Point) position.clone(), isWhite, moved, protection);
	}
}
