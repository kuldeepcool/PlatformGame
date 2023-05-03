package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.GameState;
import gamestates.Playing;

import static utilz.Constants.UI.URMButtons.*;
import static utilz.Constants.UI.PauseButtons.*;
import static utilz.Constants.UI.VolumeButtons.*;

import main.Game;
import utilz.LoadSave;

public class PauseOverlay {

	private BufferedImage backgroundImg;
	private SoundButton musicButton, sfxButton;
	private UrmButton unpauseB, replayB, menuB;
	private VolumeButton volumeB;
	private Playing playing;

	private int bgX;
	private int bgY;
	private int bgW;
	private int bgH;

	public PauseOverlay(Playing playing) {

		this.playing = playing;
		loadBackground();
		createSoundButtons();
		createUrmButtons();
		createVolumeButtons();
	}

	private void createVolumeButtons() {

		int vX = (int) (309 * Game.SCALE);
		int vY = (int) (278 * Game.SCALE);
		volumeB = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
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

	private void createSoundButtons() {
		int soundX = (int) (450 * Game.SCALE);
		int musicY = (int) (140 * Game.SCALE);
		int sfxY = (int) (186 * Game.SCALE);
		musicButton = new SoundButton(soundX, musicY, SOUND_SIZE, SOUND_SIZE);
		sfxButton = new SoundButton(soundX, sfxY, SOUND_SIZE, SOUND_SIZE);
	}

	private void loadBackground() {

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
		bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
		bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
		bgX = Game.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (25 * Game.SCALE);
	}

	public void update() {
		musicButton.update();
		sfxButton.update();
		menuB.update();
		replayB.update();
		unpauseB.update();
		volumeB.update();
	}

	public void draw(Graphics g) {

		// Background
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

		// Sound Buttons
		musicButton.draw(g);
		sfxButton.draw(g);

		// URM Buttons
		unpauseB.draw(g);
		replayB.draw(g);
		menuB.draw(g);

		// Volume Buttons
		volumeB.draw(g);
	}

	private boolean isInButton(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {
		if (volumeB.isMousePressed())
			volumeB.changeX(e.getX());

	}

	public void mousePressed(MouseEvent e) {
		if (isInButton(e, musicButton))
			musicButton.setMousePressed(true);

		else if (isInButton(e, sfxButton))
			sfxButton.setMousePressed(true);

		else if (isInButton(e, menuB))
			menuB.setMousePressed(true);

		else if (isInButton(e, replayB))
			replayB.setMousePressed(true);

		else if (isInButton(e, unpauseB))
			unpauseB.setMousePressed(true);

		else if (isInButton(e, volumeB))
			volumeB.setMousePressed(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isInButton(e, musicButton)) {

			if (musicButton.isMousePressed())
				musicButton.setMuted(!musicButton.isMuted());

		} else if (isInButton(e, sfxButton)) {

			if (sfxButton.isMousePressed())
				sfxButton.setMuted(!sfxButton.isMuted());

		} else if (isInButton(e, menuB)) {

			if (menuB.isMousePressed()) {
				GameState.state = GameState.MENU;
				playing.unpauseGame();
			}

		} else if (isInButton(e, replayB)) {

			if (replayB.isMousePressed()) {
				playing.resetAll();
				playing.unpauseGame();
			}
		} else if (isInButton(e, unpauseB)) {

			if (unpauseB.isMousePressed())
				playing.unpauseGame();
		}

		musicButton.resetBools();
		sfxButton.resetBools();
		menuB.resetBools();
		replayB.resetBools();
		unpauseB.resetBools();
		volumeB.resetBools();
	}

	public void mouseMoved(MouseEvent e) {

		musicButton.setMouseOver(false);
		sfxButton.setMouseOver(false);
		menuB.setMouseOver(false);
		replayB.setMouseOver(false);
		unpauseB.setMouseOver(false);
		volumeB.setMouseOver(false);

		if (isInButton(e, musicButton))
			musicButton.setMouseOver(true);

		else if (isInButton(e, sfxButton))
			sfxButton.setMouseOver(true);

		else if (isInButton(e, menuB))
			menuB.setMouseOver(true);

		else if (isInButton(e, replayB))
			replayB.setMouseOver(true);

		else if (isInButton(e, unpauseB))
			unpauseB.setMouseOver(true);

		else if (isInButton(e, volumeB))
			volumeB.setMouseOver(true);
	}

}
