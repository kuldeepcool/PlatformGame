package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.PauseButtons.*;

import main.Game;
import utilz.LoadSave;

public class PauseOverlay {

	private BufferedImage backgroundImg;
	private SoundButton musicButton, sfxButton;

	private int bgX;
	private int bgY;
	private int bgW;
	private int bgH;

	public PauseOverlay() {

		loadBackground();
		createSoundButtons();
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

	}

	public void draw(Graphics g) {

		// Background
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

		// Sound Buttons
		musicButton.draw(g);
		sfxButton.draw(g);
	}

	private boolean isInButton(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		if (isInButton(e, musicButton))
			musicButton.setMousePressed(true);
		else if (isInButton(e, sfxButton))
			sfxButton.setMousePressed(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isInButton(e, musicButton)) {
			if (musicButton.isMousePressed())
				musicButton.setMuted(!musicButton.isMuted());
		} else if (isInButton(e, sfxButton))
			if (sfxButton.isMousePressed())
				sfxButton.setMuted(!sfxButton.isMuted());

		musicButton.resetBools();
		sfxButton.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		musicButton.setMouseOver(false);
		sfxButton.setMouseOver(false);

		if (isInButton(e, musicButton))
			musicButton.setMouseOver(true);
		else if (isInButton(e, sfxButton))
			sfxButton.setMouseOver(true);
	}

}
