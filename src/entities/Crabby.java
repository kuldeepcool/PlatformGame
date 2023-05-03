package entities;

import static utilz.Constants.EnemyConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import static utilz.Constants.Directions.*;
import main.Game;

public class Crabby extends Enemy {

	// Attack Box
	private Rectangle2D.Float attackBox;
	private int attackBoxOffsetX;

	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		initHitBox(x, y, (int) (22 * Game.SCALE), (int) (19 * Game.SCALE));
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (82 * Game.SCALE), (int) (19 * Game.SCALE));
		attackBoxOffsetX = (int) (Game.SCALE * 30);
	}

	public void update(int[][] levelData, Player player) {

		updateBehaviour(levelData, player);
		updateAnimationTick();
		updateAttackBox();

	}

	private void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
	}

	private void updateBehaviour(int[][] levelData, Player player) {

		if (firstUpdate)
			firstUpdateCheck(levelData);

		if (inAir)
			updateInAir(levelData);

		else
			switch (enemyState) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				if (canSeePlayer(levelData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player))
						newState(ATTACK);
				}
				move(levelData);
				break;
			case ATTACK:
				if (animationIndex == 0)
					attackChecked = false;
				if (animationIndex == 3 && !attackChecked)
					checkPlayerHit(attackBox, player);
				break;
			case HIT:
				break;
			}
	}

	public void drawAttackBox(Graphics g, int xLevelOffset) {
		g.setColor(Color.red);
		g.drawRect((int) (attackBox.x - xLevelOffset), (int) attackBox.y, (int) attackBox.width,
				(int) attackBox.height);
	}

	public int flipX() {
		if (walkDir == RIGHT)
			return width;
		else
			return 0;
	}

	public int flipW() {
		if (walkDir == RIGHT)
			return -1;
		else
			return 1;

	}
}
