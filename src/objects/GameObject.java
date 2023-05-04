package objects;

import static utilz.Constants.ANIMATION_SPEED;
import static utilz.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class GameObject {

	protected int x;
	protected int y;
	protected int objectType;
	protected int animationTick;
	protected int animationIndex;
	protected int xDrawOffset;
	protected int yDrawOffset;

	protected boolean doAnimation;
	protected boolean active = true;

	protected Rectangle2D.Float hitbox;

	public GameObject(int x, int y, int objectType) {
		this.x = x;
		this.y = y;
		this.objectType = objectType;
	}

	protected void updateAnimationTick() {
		animationTick++;
		if (animationTick >= ANIMATION_SPEED) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= GetSpriteAmount(objectType)) {
				animationIndex = 0;
				if (objectType == BARREL || objectType == BOX) {
					doAnimation = false;
					active = false;
				}
			}
		}
	}

	public void reset() {
		animationIndex = 0;
		animationTick = 0;
		active = true;

		if (objectType == BARREL || objectType == BOX)
			doAnimation = false;
		else
			doAnimation = true;
	}

	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
	}

	public void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.blue);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	public int getObjectType() {
		return objectType;
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

	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	public int getxDrawOffset() {
		return xDrawOffset;
	}

	public int getyDrawOffset() {
		return yDrawOffset;
	}

	public int getAnimationIndex() {
		return animationIndex;
	}

}
