package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.canMoveHere;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	// Store Image Series for animations
	private BufferedImage[][] animations;

	// Animation
	private int animationTick;
	private int animationIndex;
	private int animationSpeed = 20;
	private int playerAction = IDLE;

	// Player Movements
	private boolean moving = false;
	private boolean attacking = false;
	private boolean left, right, up, down, jump;

	private float playerSpeed = 2.0f;

	// Jumping ,Gravity, Fall
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = 2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = false;

	// Hit-Box
	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;

	private int[][] levelData;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitBox(x, y, 20 * Game.SCALE, 28 * Game.SCALE);
	}

	public void update() {

		updatePosition();
		updateAnimationTick();
		setAnimation();
	}

	public void render(Graphics g) {
		g.drawImage(animations[playerAction][animationIndex], (int) (hitbox.x - xDrawOffset),
				(int) (hitbox.y - yDrawOffset), width, height, null);
		drawHitBox(g);
	}

	private void updateAnimationTick() {

		animationTick++;
		if (animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= GetSpriteAmount(playerAction)) {
				animationIndex = 0;
				attacking = false;
			}
		}
	}

	private void setAnimation() {

		int startAnimation = playerAction;

		if (moving)
			playerAction = RUNNING;
		else
			playerAction = IDLE;
		if (attacking)
			playerAction = ATTACK_1;
		if (startAnimation != playerAction) {
			resetAnimationTick();
		}
	}

	private void resetAnimationTick() {
		animationTick = 0;
		animationIndex = 0;
	}

	private void updatePosition() {

		moving = false;
		if (!left && !right)
			return;

		float xSpeed = 0;
		float ySpeed = 0;

		if (left && !right)
			xSpeed = -playerSpeed;

		else if (right && !left)
			xSpeed = playerSpeed;

		if (up && !down)
			ySpeed = -playerSpeed;

		else if (down && !up)
			ySpeed = playerSpeed;

//		if (canMoveHere(x + xSpeed, y + ySpeed, width, height, levelData)) {
//			this.x += xSpeed;
//			this.y += ySpeed;
//			moving = true;
//		}
		if (canMoveHere(hitbox.x + xSpeed, hitbox.y + ySpeed, hitbox.width, hitbox.height, levelData)) {
			hitbox.x += xSpeed;
			hitbox.y += ySpeed;
			moving = true;
		}
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[9][6];

		for (int i = 0; i < animations.length; ++i) {
			for (int j = 0; j < animations[i].length; ++j)
				animations[i][j] = img.getSubimage(j * 64, i * 40, 64, 40);
		}

	}

	public void loadLevelData(int[][] levelData) {
		this.levelData = levelData;
	}

	public void resetDirBooleans() {
		left = right = up = down = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

}
