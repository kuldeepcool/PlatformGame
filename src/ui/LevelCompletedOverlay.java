package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import static utilz.Constants.UI.URMButtons.*;

import gamestates.GameState;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class LevelCompletedOverlay {

	private Playing playing;

	private UrmButton menuButton, nextButton;
	private BufferedImage levelCompleteOverlayImg;

	// Dimensions of image
	private int bgX;
	private int bgY;
	private int bgW;
	private int bgH;

	public LevelCompletedOverlay(Playing playing) {
		this.playing = playing;
		initImg();
		initButtons();
	}

	private void initButtons() {
		int menuButtonX = (int) (330 * Game.SCALE);
		int nextButtonX = (int) (445 * Game.SCALE);
		int y = (int) (195 * Game.SCALE);
		nextButton = new UrmButton(nextButtonX, y, URM_SIZE, URM_SIZE, 0);
		menuButton = new UrmButton(menuButtonX, y, URM_SIZE, URM_SIZE, 2);
	}

	private void initImg() {
		levelCompleteOverlayImg = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_COMPLETED);
		bgW = (int) (levelCompleteOverlayImg.getWidth() * Game.SCALE);
		bgH = (int) (levelCompleteOverlayImg.getHeight() * Game.SCALE);
		bgX = Game.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (75 * Game.SCALE);
	}

	public void draw(Graphics g) {

		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

		g.drawImage(levelCompleteOverlayImg, bgX, bgY, bgW, bgH, null);
		nextButton.draw(g);
		menuButton.draw(g);
	}

	public void update() {
		nextButton.update();
		menuButton.update();
	}

	private boolean isIn(UrmButton b, MouseEvent e) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		nextButton.setMouseOver(false);
		menuButton.setMouseOver(false);

		if (isIn(menuButton, e))
			menuButton.setMouseOver(true);
		else if (isIn(nextButton, e))
			nextButton.setMouseOver(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(menuButton, e)) {
			if (menuButton.isMousePressed()) {
				playing.resetAll();
				playing.setGamestate(GameState.MENU);
			}
		} else if (isIn(nextButton, e))
			if (nextButton.isMousePressed()) {
				playing.loadNextLevel();
				playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLevelIndex());
			}

		menuButton.resetBools();
		nextButton.resetBools();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(menuButton, e))
			menuButton.setMousePressed(true);
		else if (isIn(nextButton, e))
			nextButton.setMousePressed(true);
	}

}
