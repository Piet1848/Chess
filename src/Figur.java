import java.awt.Point;
import java.util.ArrayList;

public interface Figur {
	Point getPosition();
	ArrayList<Prio> possibleMoves(Board b);
	void moveTo(Point nPoint);
	boolean isWhite();
	double getValue();
	double getAbsValue();
	String getName();
	void setMoved(boolean moved);
	boolean getMoved();
	double getBonus();
	void resetProtection(boolean currentIsWhite);
	void addProtection(int x);
	double getProtection();
	Figur clone();
}
