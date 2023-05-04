package objects;

import static utilz.Constants.ObjectConstants.BOX;

import main.Game;

public class GameContainer extends GameObject {

	public GameContainer(int x, int y, int objectType) {
		super(x, y, objectType);
		createHitbox();
	}

	private void createHitbox() {
		if (objectType == BOX) {
			initHitbox(25, 18);

			xDrawOffset = (int) (7 * Game.SCALE);
			yDrawOffset = (int) (12 * Game.SCALE);

		} else {
			initHitbox(23, 25);
			xDrawOffset = (int) (8 * Game.SCALE);
			yDrawOffset = (int) (5 * Game.SCALE);
		}

		hitbox.y += yDrawOffset + (int) (Game.SCALE * 2);
		hitbox.x += xDrawOffset / 2;
	}

	public void update() {
		if (doAnimation)
			updateAnimationTick();
	}
}