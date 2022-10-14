import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Anzeige extends JPanel implements MouseListener, MouseMotionListener{
	@Serial
	private static final long serialVersionUID = 1L; //don't know what it is but java wants it
	private Image wPawn;
	private Image wRook;
	private Image wBishop;
	private Image wKnight;
	private Image wKing;
	private Image wQueen;
	private Image bPawn;
	private Image bRook;
	private Image bBishop;
	private Image bKnight;
	private Image bKing;
	private Image bQueen;

	private final Board board;
	private int size = 200;
	private Point selectedField;
	private Figur selectedFigure;
	private boolean mousePressed = false;
	private int actX;
	private int actY;
	private boolean whiteTurn = true;
	private boolean wait;
	private Move lastMove;
	private long lastLoadingTime = 0L;
	private final Brain brain;

	public Anzeige(Board nBoard) {
		board = nBoard;
		initImages();
		selectedField = new Point();
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setFocusable(true);
		brain = new Brain();
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		setBackground(Color.WHITE);
		Dimension d = getSize();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size*11, size*11);
		if (d.getHeight() < d.getWidth() - 20) {
			size = (int) (d.getHeight() / 10);
		} else {
			size = (int) ((d.getWidth() - 20) / 10);
		}
		g.setFont(new Font("TimesRoman", Font.PLAIN, size/3));

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((i + j) % 2 == 0) {
					if(lastMove != null && ((i == lastMove.start.x && j == lastMove.start.y) || (i == lastMove.end.x && j == lastMove.end.y))) {
						g.setColor(new Color(195, 216, 136));
					}else {
						g.setColor(new Color(222, 227, 230));
					}
				} else {
					if(lastMove != null && ((i == lastMove.start.x && j == lastMove.start.y) || (i == lastMove.end.x && j == lastMove.end.y))) {
						g.setColor(new Color(147, 178, 102));
					}else {
						g.setColor(new Color(120, 140, 170));
					}
				}
				g.fillRect(i * size + size, j * size + size, size, size);
				Point pos = new Point(i, j);
				Figur f = board.getFigur(pos);
				if(f != null && (selectedFigure == null || !selectedFigure.getPosition().equals(pos))) {
					drawImage(i * size + size, j * size + size, f.getName(), f.isWhite(), g);
				}
			}
		}

		g.setColor(Color.BLACK);

		if(wait) {
			g.drawString("Loading", 20, 15+size/3);
		}
		g.drawString(Double.toString(brain.getActEvaluation()), 20, 20+size/3*2);
		g.drawString(Double.toString(brain.getCurrentActEvaluation(whiteTurn, board)), 400, 20+size/3*2);
		//		g.drawString(Double.toString(board.evaluateBoard(true)), 50, 20+size/3*2);
		//		g.drawString(Double.toString(board.evaluateBoard(false)), 50, 20+size);

		if(mousePressed && selectedFigure != null) {
			drawImage(actX, actY, selectedFigure.getName(), selectedFigure.isWhite(), g);
		}
	}

	private void drawImage(int i, int j, String numberFigure, boolean isWhite, Graphics g) {
		Image figure;
		if(isWhite){
			figure = getImage(numberFigure, wPawn, wRook, wKnight, wBishop, wQueen, wKing);
		}
		else {
			figure = getImage(numberFigure, bPawn, bRook, bKnight, bBishop, bQueen, bKing);
		}
		g.drawImage(figure, i, j, size, size, null);
	}
	
	private Image getImage(String numberFigure, Image pawn, Image rook, Image knight, Image bishop, Image queen, Image king) {
		switch (numberFigure) {
		case ("pawn"):
			return pawn;
		case ("rook"):
			return rook;
		case ("knight"):
			return knight;
		case ("bishop"):
			return bishop;
		case ("queen"):
			return queen;
		case ("king"):
			return king;
		default:
			System.out.println("Error no Image at getImage()");
			return null;
		}
	}
	
	private void initImages() {
		try {
			wPawn = ImageIO.read(new File("res/WPawn.png"));
			wRook = ImageIO.read(new File("res/WRook.png"));
			wBishop = ImageIO.read(new File("res/WBishop.png"));
			wKnight = ImageIO.read(new File("res/WKnight.png"));
			wKing = ImageIO.read(new File("res/WKing.png"));
			wQueen = ImageIO.read(new File("res/WQueen.png"));
			bPawn = ImageIO.read(new File("res/BPawn.png"));
			bRook = ImageIO.read(new File("res/BRook.png"));
			bBishop = ImageIO.read(new File("res/BBishop.png"));
			bKnight = ImageIO.read(new File("res/BKnight.png"));
			bKing = ImageIO.read(new File("res/BKing.png"));
			bQueen = ImageIO.read(new File("res/BQueen.png"));
		}  catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mousePoint = e.getPoint();
		selectedField.x = ((mousePoint.x) / size) -1;
		selectedField.y = ((mousePoint.y) / size) -1;
		if(selectedField.x <= 7 && selectedField.y <= 7 && selectedField.x >= 0 && selectedField.y >= 0) {
			selectedFigure = board.getFigur(selectedField);
			mousePressed = true;
			if(selectedFigure != null && whiteTurn != selectedFigure.isWhite()) {
				selectedFigure = null;
				mousePressed = false;
			}
		}else {
			wait = true;
		}
		actX = mousePoint.x-size/2;
		actY = mousePoint.y-size/2;
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(selectedFigure != null) {
			Point mousePoint = e.getPoint();
			Point nField = new Point();
			nField.x = ((mousePoint.x) / size) -1;
			nField.y = ((mousePoint.y) / size) -1;
			if((selectedField.x != nField.x || selectedField.y != nField.y) && (nField.x >= 0 && nField.x < 8 && nField.y >= 0 && nField.y < 8)) {
				if(selectedFigure.getName().equals("pawn") && (nField.y == 0  || nField.y == 7)) {
					board.move(new Move(selectedField, nField, 3.113, true));
					lastMove = new Move(selectedField, nField, 3.113, true);
				}else {
					board.move(new Move(selectedField, nField, false));
					lastMove = new Move(selectedField, nField, false);
				}
				whiteTurn = !whiteTurn;
				if(!wait) {
//					actEvaluation = minimax(board, 4., Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, whiteTurn, 0);
					brain.evaluateBoard(whiteTurn, board);
				}
			}
			selectedField = new Point();
			mousePressed = false;
			selectedFigure = null;
		}
		if(wait && System.currentTimeMillis()-lastLoadingTime>200) {
			Move move = brain.playKi(board, whiteTurn);
			board.move(move);
			lastMove = move;
			whiteTurn = !whiteTurn;
			lastLoadingTime = System.currentTimeMillis();
			double count = figurenCounter(board.getFiguren());
			System.out.println("Count = " + count);
		}
		wait = false;
		this.repaint();
	}

	private double figurenCounter(ArrayList<Figur> figuren) {
		double sum = 0.;
		for(int i = 0; i < figuren.size(); i++) {
			sum += figuren.get(i).getValue();
		}
		return sum;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point mousePoint = e.getPoint();
		actX = mousePoint.x-size/2;
		actY = mousePoint.y-size/2;
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
