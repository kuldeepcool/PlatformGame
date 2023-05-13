package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import main.Game;

import static utilz.Constants.Projectiles.*;

public class Projectile {

	private Rectangle2D.Float hitbox;

	private int direction;

	private boolean active = true;

	public Projectile(int x, int y, int direction) {

		int xOffset = (int) (-3 * Game.SCALE); // To spawn cannon ball at correct positi on
		int yOffset = (int) (5 * Game.SCALE);

		if (direction == 1)
			xOffset = (int) (29 * Game.SCALE); // If`going right ->

		hitbox = new Rectangle2D.Float(x + xOffset, y + yOffset, CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT);
		this.direction = direction;

	}

	public void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.blue);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	public void updatePos() {
		hitbox.x += direction * SPEED;
	}

	public void setPos(int x, int y) {
		hitbox.x = x;
		hitbox.y = y;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
