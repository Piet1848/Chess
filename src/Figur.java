import java.awt.Point;
import java.util.ArrayList;

public interface Figur {
	Point getPosition();
	ArrayList<Move> possibleMoves(Board b);
	void moveTo(Point nPoint);
	boolean isWhite();
	double getValue();
	double getAbsValue();
	FigurName getName();
	boolean getMoved();
	double getBonus();
	void resetProtection(boolean currentIsWhite);
	void addProtection(int x);
	double getProtection();
	void setMoved(boolean moved);
}
