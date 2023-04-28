package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static utilz.Constants.UI.URMButtons.*;
import utilz.LoadSave;

public class UrmButton extends PauseButton {

	private BufferedImage[] urmImgs;

	private boolean mouseOver;
	private boolean mousePressed;

	private int rowIndex;
	private int index;

	public UrmButton(int x, int y, int width, int height, int rowIndex) {
		super(x, y, width, height);
		this.rowIndex = rowIndex;
		loadUrmImgs();
	}

	private void loadUrmImgs() {
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTONS);
		urmImgs = new BufferedImage[3];
		for (int i = 0; i < urmImgs.length; ++i)
			urmImgs[i] = temp.getSubimage(i * URM_SIZE_DEFAULT, rowIndex * URM_SIZE_DEFAULT, URM_SIZE_DEFAULT,
					URM_SIZE_DEFAULT);
	}

	public void update() {

		index = 0;
		if (mouseOver)
			index = 1;
		if (mousePressed)
			index = 2;

	}

	public void draw(Graphics g) {

		g.drawImage(urmImgs[index], x, y, URM_SIZE, URM_SIZE, null);
	}

	public void resetBools() {

		mouseOver = false;
		mousePressed = false;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}

}
