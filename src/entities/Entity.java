package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

	protected float x;
	protected float y;
	protected int width;
	protected int height;

	protected Rectangle2D.Float hitbox;

	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}

	protected void drawHitBox(Graphics g, int xLevelOffset) {
		// for debugging hit-box
		g.setColor(Color.black);
		g.drawRect((int) (hitbox.x - xLevelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	protected void initHitBox(float x, float y, int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, width, height);
	}
//
//	protected void updateHitBox() {
//		hitbox.x = (int) x;
//		hitbox.y = (int) y;
//	}

	public Rectangle2D.Float getHitBox() {
		return hitbox;
	}

}
