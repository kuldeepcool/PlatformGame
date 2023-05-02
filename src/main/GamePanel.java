package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import static main.Game.GAME_WIDTH;
import static main.Game.GAME_HEIGHT;;

//Display Gif
//private Image gifImage;
//URL gifURL = getClass().getResource("/menu_background.gif");
//gifImage = Toolkit.getDefaultToolkit().createImage(gifURL);
//int imageWidth = gifImage.getWidth(this);
//int imageHeight = gifImage.getHeight(this);
//float scaleX = (float) GAME_WIDTH / imageWidth;
//float scaleY = (float) GAME_HEIGHT / imageHeight;
//int scaledWidth = (int) (imageWidth * scaleX);
//int scaledHeight = (int) (imageHeight * scaleY);
//g.drawImage(gifImage, 0, 0, scaledWidth, scaledHeight, null);

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private Game game;

	public GamePanel(Game game) {

		mouseInputs = new MouseInputs(this);
		this.game = game;

		setPanelSize();

		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
	}

	private void setPanelSize() {

		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
		System.out.println("size :" + GAME_WIDTH + " : " + GAME_HEIGHT);
	}

	public void updateGame() {

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		game.render(g);
	}

	public Game getGame() {
		return game;
	}
}
