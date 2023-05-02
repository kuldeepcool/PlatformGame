package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;

import main.Game;

public abstract class Enemy extends Entity {

	protected int enemyState;
	protected int enemyType;
	protected int animationIndex;
	protected int animationTick;
	protected int animationSpeed = 25;
	protected int walkDir = LEFT;
	protected int tileY;

	protected float attackDistance = Game.TILES_SIZE;
	protected float fallSpeed;
	protected float gravity = 0.04f * Game.SCALE;
	protected float walkSpeed = 0.35f * Game.SCALE;

	protected boolean firstUpdate = true;
	protected boolean inAir;

	public Enemy(float x, float y, int width, int height, int enemyType) {

		super(x, y, width, height);
		this.enemyType = enemyType;
		initHitBox(x, y, width, height);
	}

	protected void firstUpdateCheck(int[][] levelData) {

		if (!IsEntityOnFloor(hitbox, levelData))
			inAir = true;
		firstUpdate = false;
	}

	protected void move(int[][] levelData) {

		float xSpeed = 0;

		if (walkDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;

		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData))
			if (IsFloor(hitbox, xSpeed, levelData)) {
				hitbox.x += xSpeed;
				return;
			}
		changeWalkDir();
	}

	protected void turnTowardsPlayer(Player player) {
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	protected boolean canSeePlayer(int[][] levelData, Player player) {
		int playerTileY = (int) (player.getHitBox().y / Game.TILES_SIZE);
		if (playerTileY == tileY) {
			if (isPlayerInRange(player)) {
				if (IsSightClear(levelData, hitbox, player.hitbox, tileY))
					return true;
			}
		}
		return false;
	}

	protected boolean isPlayerInRange(Player player) {

		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		return absValue <= attackDistance * 5;
	}

	protected boolean isPlayerCloseForAttack(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		return absValue <= attackDistance;
	}

	protected void newState(int enemyState) {
		this.enemyState = enemyState;
		animationTick = 0;
		animationIndex = 0;
	}

	protected void updateInAir(int[][] levelData) {

		if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, levelData)) {
			hitbox.y += fallSpeed;
			fallSpeed += gravity;
		} else {
			inAir = false;
			hitbox.y = GetEntityPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
		}
	}

	protected void updateAnimationTick() {

		animationTick++;
		if (animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= GetSpriteAmount(enemyType, enemyState)) {
				animationIndex = 0;
				if (enemyState == ATTACK)
					enemyState = IDLE;
			}
		}
	}

	protected void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	public int getAnimationIndex() {
		return animationIndex;
	}

	public int getEnemyState() {
		return enemyState;
	}
}
