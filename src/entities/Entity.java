package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {

	protected float x;
	protected float y;
	protected float airSpeed;
	protected float walkSpeed;

	protected int width;
	protected int height;
	protected int animationTick;
	protected int animationIndex;
	protected int state;
	protected int maxHealth;
	protected int currentHealth;

	protected boolean inAir = false;

	// Attack Box
	protected Rectangle2D.Float attackBox;

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

	protected void initHitBox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
	}

	protected void drawAttackBox(Graphics g, int levelOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) (attackBox.x - levelOffsetX), (int) attackBox.y, (int) attackBox.width,
				(int) attackBox.height);

	}
//
//	protected void updateHitBox() {
//		hitbox.x = (int) x;
//		hitbox.y = (int) y;
//	}

	public Rectangle2D.Float getHitBox() {
		return hitbox;
	}

	public int getEnemyState() {
		return state;
	}

	public int getAnimationIndex() {
		return animationIndex;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

}
