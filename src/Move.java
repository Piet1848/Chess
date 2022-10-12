import java.awt.Point;

public class Move {
	public final Point start;
	public final Point end;
	public final double prio;
	public final boolean promotion;
	
	public Move(Point nStart, Point nEnd, boolean nPromotion) {
		if(nStart.x>=8 || nStart.x<=-1||nEnd.x>=8||nEnd.x<=-1||nEnd.y>=8||nEnd.y<=-1) {	//TODO remove
			System.out.print("Error in Move");
		}
		start = nStart;
		end = nEnd;
		prio = 0.;
		promotion = nPromotion;
	}
	
	public Move(Point nStart, Point nEnd, double nPrio, boolean nPromotion) {
		if(nStart.x>=8 || nStart.x<=-1||nEnd.x>=8||nEnd.x<=-1||nEnd.y>=8||nEnd.y<=-1) {
		System.out.print("Error in Move");
		}
		start = nStart;
		end = nEnd;
		prio = nPrio;
		promotion = nPromotion;
	}
}
