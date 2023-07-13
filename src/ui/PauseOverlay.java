package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.GameState;
import gamestates.Playing;

import static utilz.Constants.UI.URMButtons.*;
import main.Game;
import utilz.LoadSave;

public class PauseOverlay {

	private BufferedImage backgroundImg;

	private AudioOptions audioOptions;
	private UrmButton unpauseB, replayB, menuB;
	private Playing playing;

	private int bgX;
	private int bgY;
	private int bgW;
	private int bgH;

	public PauseOverlay(Playing playing) {

		this.playing = playing;
		loadBackground();
		audioOptions = playing.getGame().getAudioOptions();
		createUrmButtons();
	}

	private void createUrmButtons() {
		int menuX = (int) (313 * Game.SCALE);
		int replayX = (int) (387 * Game.SCALE);
		int unpauseX = (int) (462 * Game.SCALE);
		int urmY = (int) (325 * Game.SCALE);

		menuB = new UrmButton(menuX, urmY, URM_SIZE, URM_SIZE, 2);
		replayB = new UrmButton(replayX, urmY, URM_SIZE, URM_SIZE, 1);
		unpauseB = new UrmButton(unpauseX, urmY, URM_SIZE, URM_SIZE, 0);

	}

	private void loadBackground() {

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
		bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
		bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
		bgX = Game.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (25 * Game.SCALE);
	}

	public void update() {
		menuB.update();
		replayB.update();
		unpauseB.update();
		audioOptions.update();
	}

	public void draw(Graphics g) {

		// Background
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

		// URM Buttons
		unpauseB.draw(g);
		replayB.draw(g);
		menuB.draw(g);

		// Sound and Volume Buttons
		audioOptions.draw(g);
	}

	private boolean isInButton(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {
		audioOptions.mouseDragged(e);

	}

	public void mousePressed(MouseEvent e) {
		if (isInButton(e, menuB))
			menuB.setMousePressed(true);

		else if (isInButton(e, replayB))
			replayB.setMousePressed(true);

		else if (isInButton(e, unpauseB))
			unpauseB.setMousePressed(true);

		else
			audioOptions.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if (isInButton(e, menuB))
			if (menuB.isMousePressed()) {
				playing.resetAll();
				playing.setGamestate(GameState.MENU);
				playing.unpauseGame();
			}

			else if (isInButton(e, replayB)) {
				if (replayB.isMousePressed()) {
					playing.resetAll();
					playing.unpauseGame();
				}
			} else if (isInButton(e, unpauseB)) {
				if (unpauseB.isMousePressed())
					playing.unpauseGame();

			} else
				audioOptions.mouseReleased(e);
		menuB.resetBools();
		replayB.resetBools();
		unpauseB.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);
		replayB.setMouseOver(false);
		unpauseB.setMouseOver(false);

		if (isInButton(e, menuB))
			menuB.setMouseOver(true);

		else if (isInButton(e, replayB))
			replayB.setMouseOver(true);

		else if (isInButton(e, unpauseB))
			unpauseB.setMouseOver(true);

		else
			audioOptions.mouseMoved(e);
	}

}
