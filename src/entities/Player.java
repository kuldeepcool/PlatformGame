package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
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

	private float playerSpeed = 1.0f * Game.SCALE;

	// Jumping ,Gravity, Fall
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = false;

	// Hit-Box
	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;

	private int[][] levelData;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitBox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));
	}

	public void update() {

		updatePosition();
		updateAnimationTick();
		setAnimation();
	}

	public void render(Graphics g, int levelOffset) {
		g.drawImage(animations[playerAction][animationIndex], (int) (hitbox.x - xDrawOffset) - levelOffset,
				(int) (hitbox.y - yDrawOffset), width, height, null);
		drawHitBox(g, levelOffset);
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

		if (inAir) {
			if (airSpeed < 0)
				playerAction = JUMP;
			else
				playerAction = FALLING;
		}
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
		if (jump)
			jump();

//		if (!left && !right && !inAir)
//			return;

		if (!inAir)
			if ((!left && !right) || (right && left))
				return;

		float xSpeed = 0;

		if (left)
			xSpeed -= playerSpeed;

		if (right)
			xSpeed += playerSpeed;
		if (!inAir)
			if (!isEntityOnFloor(hitbox, levelData))
				inAir = true;

		if (inAir) {
			if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;

	}

	private void jump() {
		if (inAir)
			return;
		inAir = true;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		if (canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityPosNextToWall(hitbox, xSpeed);
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
		if (!isEntityOnFloor(hitbox, levelData))
			inAir = true;
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

	public void setJump(boolean jump) {
		this.jump = jump;

	}

}
