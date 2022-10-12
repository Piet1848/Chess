import java.awt.Point;

public class Prio {
	public final Point point;
	public final double prio;
	public final boolean promotion;
	
	public Prio(Point nP, double nPrio, boolean nPromotion) {
		point = nP;
		prio = nPrio;
		promotion = nPromotion;
	}
}
