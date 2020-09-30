import java.awt.Dimension;
import javax.swing.JFrame;

public class Main {
	private JFrame graphic;
	private Board board;
	private Graphic a;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		board = new Board();
		graphics();
	}
	
	private void graphics() {
		a = new Graphic(board);
		graphic = new JFrame("Chess");
		graphic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphic.getContentPane().add(a);
		graphic.setSize(2000, 2020);
		graphic.addMouseListener(a);
		graphic.addMouseMotionListener(a);
		graphic.setVisible(true);
		graphic.setMinimumSize(new Dimension(750, 770));
		graphic.repaint();
	}
}
