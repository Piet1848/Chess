import java.awt.Point;
import java.util.ArrayList;

public class Pawn implements Figur{
	private final String name = "pawn";
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

	public Pawn(Point nPos, boolean nWhite, boolean nMoved, double nBonus, int nProtection) {
		position = nPos;
		isWhite = nWhite;
		moved = true;
		if(!nWhite) {
			value = -1;
			direktion = new Point(0, -1);
		}else{
			value = 1;
			direktion = new Point(0, 1);
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
	public ArrayList<Prio> possibleMoves(Board b) {	//TODO en passante missing
		bonus = 0;
		ArrayList<Prio> moves = new ArrayList<>();
		Point positionToCheck = addPoints(position, direktion);
		Figur f = b.getFigur(positionToCheck);
		
		if(position.y >= 7 || position.y <= 0) {
			System.out.println("Error Pawn at back rank");
			return moves;
		}
		if(f == null) {
			if(positionToCheck.getY() == 7 || positionToCheck.getY() == 0) {
				moves.add(new Prio(positionToCheck, 3.113, true));	//make Queen
				moves.add(new Prio(positionToCheck, 3.112, true));	//Rook
				moves.add(new Prio(positionToCheck, 3.111, true));	//Knight
				moves.add(new Prio(positionToCheck, 3.110, true));	//Bishop
			}else {
				moves.add(new Prio(positionToCheck, 0., false));
				if(moved == false) {
					positionToCheck = addPoints(positionToCheck, direktion);	//two steps forward
					if(b.getFigur(positionToCheck) == null)
						moves.add(new Prio(positionToCheck, 0.1, false));	//double Move mostly better than one (opening)
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
	
	private void pawnCapture(ArrayList<Prio> moves, Point positionToCheck, Figur f) {
		if(f.isWhite() != isWhite) {
			f.addProtection(-1);
			if(positionToCheck.getY() == 7 || positionToCheck.getY() == 0) {
				moves.add(new Prio(positionToCheck, f.getAbsValue()+3.113, true));	//make Queen
				moves.add(new Prio(positionToCheck, f.getAbsValue()+3.112, true));	//Rook
				moves.add(new Prio(positionToCheck, f.getAbsValue()+3.111, true));	//Knight
				moves.add(new Prio(positionToCheck, f.getAbsValue()+3.110, true));	//Bishop
			}else {
				moves.add(new Prio(positionToCheck, f.getAbsValue()+0.5, false));	//pawn capture
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
	public void setMoved(boolean nMoved) {
		moved = nMoved;
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
	public String getName() {
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
	public Figur clone(){
		return new Pawn((Point) position.clone(), isWhite, moved, bonus, protection);
	}
}
