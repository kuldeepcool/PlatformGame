package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	// Store Image Series for animations
	private BufferedImage[][] animations;

	// Level Data
	private int[][] levelData;

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

	// Status Bar UI
	private BufferedImage statusBarImg;

	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);

	private int maxHealth = 100;
	private int currentHealth = maxHealth;
	private int healthWidth = healthBarWidth;

	// Attack Box
	private Rectangle2D.Float attackBox;

	private boolean attackChecked;

	private int flipX = 0;
	private int flipW = 1;

	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		loadAnimations();
		initHitBox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));
		initAttackbox();
	}

	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}

	private void initAttackbox() {

		attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));

	}

	public void update() {
		updateHealthBar();
		if (currentHealth <= 0) {
			playing.setGameOver(true);
			return;
		}

		updateAttackBox();
		updatePosition();
		if (attacking)
			checkAttack();
		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if (attackChecked || animationIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);

	}

	private void updateAttackBox() {

		if (right)
			attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);

		else if (left)
			attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);

		attackBox.y = hitbox.y + (int) (Game.SCALE * 10);

	}

	private void updateHealthBar() {

		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}

	public void render(Graphics g, int levelOffset) {
		g.drawImage(animations[playerAction][animationIndex], (int) (hitbox.x - xDrawOffset) - levelOffset + flipX,
				(int) (hitbox.y - yDrawOffset), width * flipW, height, null);
		drawHitBox(g, levelOffset);
		drawAttackBox(g, levelOffset);
		drawUI(g);
	}

	private void drawAttackBox(Graphics g, int levelOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) (attackBox.x - levelOffsetX), (int) attackBox.y, (int) attackBox.width,
				(int) attackBox.height);

	}

	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
	}

	private void updateAnimationTick() {

		animationTick++;
		if (animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= GetSpriteAmount(playerAction)) {
				animationIndex = 0;
				attacking = false;
				attackChecked = false;
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
		if (attacking) {
			playerAction = ATTACK_1;
			if (startAnimation != ATTACK_1) {
				animationIndex = 1;
				animationTick = 0;
				return;
			}
		}
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

		if (left) {

			xSpeed -= playerSpeed;
			flipX = width;
			flipW = -1;
		}

		if (right) {
			xSpeed += playerSpeed;
			flipX = 0;
			flipW = 1;
		}
		if (!inAir)
			if (!IsEntityOnFloor(hitbox, levelData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData)) {
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
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityPosNextToWall(hitbox, xSpeed);
		}
	}

	public void changeHealth(int value) {

		currentHealth += value;
		if (currentHealth <= 0) {
			currentHealth = 0;
			// gameOver();
		} else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[7][8];

		for (int i = 0; i < animations.length; ++i)
			for (int j = 0; j < animations[i].length; ++j)
				animations[i][j] = img.getSubimage(j * 64, i * 40, 64, 40);

		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

	}

	public void loadLevelData(int[][] levelData) {
		this.levelData = levelData;
		if (!IsEntityOnFloor(hitbox, levelData))
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

	public void resetAll() {
		resetDirBooleans();
		// inAir = false;
		if (IsEntityOnFloor(hitbox, levelData))
			inAir = true;
		attacking = false;
		moving = false;
		playerAction = IDLE;
		currentHealth = maxHealth;
		hitbox.x = x;
		hitbox.y = y;
		attackBox.x = x;
		attackBox.y = y;

	}

}
