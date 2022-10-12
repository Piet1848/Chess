import java.awt.Point;
import java.util.ArrayList;

public class King implements Figur{
	private final String name = "king";
	private Point position;
	private boolean isWhite;
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
			if(positionToCheck.getX() == 8 || positionToCheck.getX() == -1 || positionToCheck.getY() == 8 || positionToCheck.getY() == -1) {
				f = b.getFigur(position);	//ends the loop
			}
			if(f == null) {
				if(moved) {
					moves.add(new Prio(positionToCheck, 0., false));
				}else {
					moves.add(new Prio(positionToCheck, -0.6, false));
				}
			}else if(f.isWhite() != isWhite) {
				f.addProtection(-1);
				if(moved) {
					moves.add(new Prio(positionToCheck, f.getValue(), false));
				}else {
					moves.add(new Prio(positionToCheck, f.getValue()-0.5, false));
				}
			}else if(!f.getName().equals("king")){
				f.addProtection(1);
			}
		}
		if(!moved) {	//TODO check if in Check
			ArrayList<Figur> rooks = b.getRooks(isWhite);
			for(int i = 0; i < rooks.size(); i++) {
				Point move;
				if(rooks.get(i).getPosition().x-position.x < 0) {	//Kingside Castle
					move = new Point(-1,0);
				}else {	//Queenside Castle
					move = new Point(1,0);
				}
				Point positionToCheck = addPoints(position, move);
				while(b.getFigur(positionToCheck) == null) {
					positionToCheck = addPoints(positionToCheck, move);
				}
				if(b.getFigur(positionToCheck) == rooks.get(i)) {
					moves.add(new Prio(rooks.get(i).getPosition(), 0.8, false));
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
		return 0;
	}
}
