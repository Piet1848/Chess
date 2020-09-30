import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Graphic extends JPanel implements MouseMotionListener, MouseListener{
	private Board board;
	private int size = 200;
	private Point selectedField;
	private int selectedFigure;
	private boolean mousePressed = false;
	private int actX;
	private int actY;

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

	public Graphic(Board nBoard) {
		board = nBoard;
		initImages();
		selectedField = new Point();
	}

	private void initImages() {
		try {
			wPawn = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\WPawn.png"));
			wRook = ImageIO.read(new File("C:\\Users\\Piet\\\\Desktop\\Bilder\\Informatik\\Chess\\WRook.png"));
			wBishop = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\WBishop.png"));
			wKnight = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\WKnight.png"));
			wKing = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\WKing.png"));
			wQueen = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\WQueen.png"));
			bPawn = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BPawn.png"));
			bRook = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BRook.png"));
			bBishop = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BBishop.png"));
			bKnight = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BKnight.png"));
			bKing = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BKing.png"));
			bQueen = ImageIO.read(new File("C:\\Users\\Piet\\Desktop\\Bilder\\Informatik\\Chess\\BQueen.png"));
		}  catch (IOException ex) {
			System.out.println(ex);
		}
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
		g.setFont(new Font("TimesRoman", Font.PLAIN, 10));

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((i + j) % 2 == 0) {
					g.setColor(Color.DARK_GRAY);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}
				g.fillRect(i * size + size, j * size + size, size, size);
				drawImage(i * size + size, j * size + size, (int) board.board[i][j], g);
			}
		}

		if(mousePressed) {
			drawImage(actX, actY, selectedFigure, g);
		}
	}

	private void drawImage(int i, int j, int numberFigure, Graphics g) {
		Image figure = null;
		switch (numberFigure) {  //tbd files nicht immer neu laden
		// white
		case (1):
			figure = wPawn;
		break;
		case (2):
			figure = wRook;
		break;
		case (3):
			figure = wKnight;
		break;
		case (4):
			figure = wBishop;
		break;
		case (5):
			figure = wQueen;
		break;
		case (6):
			figure = wKing;
		break;
		// black
		case (-1):
			figure = bPawn;
		break;
		case (-2):
			figure = bRook;
		break;
		case (-3):
			figure = bKnight;
		break;
		case (-4):
			figure = bBishop;
		break;
		case (-5):
			figure = bQueen;
		break;
		case (-6):
			figure = bKing;
		break;
		default:
			figure = null;
			break;
		}
		g.drawImage(figure, i, j, size, size, null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mousePoint = e.getPoint();
		selectedField.x = (int) ((mousePoint.x - 9) / size) -1;
		selectedField.y = (int) ((mousePoint.y - 38) / size) -1;
		if(selectedField.x <= 7 && selectedField.y <= 7 && selectedField.x >= 0 && selectedField.y >= 0) {
			selectedFigure = board.select(selectedField);
			mousePressed = true;
		}
		actX = mousePoint.x - 9-size/2;
		actY = mousePoint.y - 38-size/2;
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point mousePoint = e.getPoint();
		Point nField = new Point();
		nField.x = (int) ((mousePoint.x - 9) / size) -1;
		nField.y = (int) ((mousePoint.y - 38) / size) -1;
		board.move(nField);
		mousePressed = false;
		selectedField = new Point();
		selectedFigure = 0;
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point mousePoint = e.getPoint();
		actX = mousePoint.x - 9 -size/2;
		actY = mousePoint.y - 38 -size/2;
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
