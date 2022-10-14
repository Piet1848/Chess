import javax.swing.JFrame;

public class Main {
	private final Board board;
	private JFrame graphic;
	private Anzeige anzeige;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		board = new Board();
		initGraphic();
	}

	private void initGraphic() {
		graphic = new JFrame("Chess");
		graphic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		anzeige = new Anzeige(board);
		graphic.add(anzeige);
		graphic.setSize(1000, 1000);
		graphic.setVisible(true);
	}
}
