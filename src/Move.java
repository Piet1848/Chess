import java.awt.Point;

public class Move {
	public final Point start;
	public final Point end;
	public final FigurName promotion;	//if there is no Promotion the promotion is the same as the startFigur
	public final Figur startFigur;
	public final Figur endFigur;

	public final boolean alreadyMoved;

	public Move(Point start, Point end, FigurName promotion, Figur startFigur, Figur endFigur) {	//when already moved doesn't matter
		this.start = start;
		this.end = end;
		this.promotion = promotion;
		this.startFigur = startFigur;
		this.endFigur = endFigur;
		this.alreadyMoved = false;
	}

	public Move(Point start, Point end, FigurName promotion, Figur startFigur, Figur endFigur, boolean alreadyMoved) {	//For Kings and Rooks
		this.start = start;
		this.end = end;
		this.promotion = promotion;
		this.startFigur = startFigur;
		this.endFigur = endFigur;
		this.alreadyMoved = alreadyMoved;
	}

	public boolean isPromotion(){
		return promotion != startFigur.getName();
	}

	public boolean isCapture(){
		return endFigur != null;
	}

	public boolean castle(){
		return endFigur != null && startFigur.isWhite() == endFigur.isWhite();
	}
}
