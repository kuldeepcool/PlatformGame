package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;
import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {

	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private ObjectManager objectManager;
	private PauseOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;

	private boolean paused = false;
	private boolean gameOver;
	private boolean levelCompleted;

	private int xLevelOffset;
	private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
	private int maxLevelOffsetX; // converted to pixels
	private int[] smallCloudsYPos;

	private BufferedImage backgroundImg, bigCloudsImg, smallCloudsImg;

	private Random random = new Random();

	public Playing(Game game) {

		super(game);
		initClasses();
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
		bigCloudsImg = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
		smallCloudsImg = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
		smallCloudsYPos = new int[8];
		for (int i = 0; i < smallCloudsYPos.length; ++i)
			smallCloudsYPos[i] = (int) (90 * Game.SCALE) + random.nextInt((int) (100 * Game.SCALE));

		calcLvlOffset();
		loadStartLevel();
	}

	public void loadNextLevel() {
		resetAll();
		levelManager.loadNextLevel();
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	}

	private void loadStartLevel() {
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
	}

	private void calcLvlOffset() {
		maxLevelOffsetX = levelManager.getCurrentLevel().getLevelOffset();
	}

	private void initClasses() {
		levelManager = new LevelManager(game);
		enemyManager = new EnemyManager(this);
		objectManager = new ObjectManager(this);

		player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
		player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

		pauseOverlay = new PauseOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);
		levelCompletedOverlay = new LevelCompletedOverlay(this);
	}

	@Override
	public void update() {

		if (paused)
			pauseOverlay.update();

		else if (levelCompleted)
			levelCompletedOverlay.update();

		else if (!gameOver) {
			levelManager.update();
			objectManager.update();
			player.update();
			enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			checkCloseToBorder();
		}
	}

	private void checkCloseToBorder() {

		int playerX = (int) player.getHitBox().x;
		int diff = playerX - xLevelOffset;

		if (diff > rightBorder)
			xLevelOffset += diff - rightBorder;
		else if (diff < leftBorder)
			xLevelOffset += diff - leftBorder;
		if (xLevelOffset > maxLevelOffsetX)
			xLevelOffset = maxLevelOffsetX;
		else if (xLevelOffset < 0)
			xLevelOffset = 0;
	}

	@Override
	public void draw(Graphics g) {

		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

		drawClouds(g);

		levelManager.draw(g, xLevelOffset);
		player.render(g, xLevelOffset);
		enemyManager.draw(g, xLevelOffset);
		objectManager.draw(g, xLevelOffset);

		if (paused) {
			g.setColor(new Color(0, 0, 0, 200));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if (gameOver)
			gameOverOverlay.draw(g);
		else if (levelCompleted)
			levelCompletedOverlay.draw(g);
	}

	private void drawClouds(Graphics g) {
		for (int i = 0; i < 3; ++i)
			g.drawImage(bigCloudsImg, i * BIG_CLOUD_WIDTH - (int) (xLevelOffset * 0.3), (int) (204 * Game.SCALE),
					BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
		for (int i = 0; i < smallCloudsYPos.length; ++i)
			g.drawImage(smallCloudsImg, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLevelOffset * 0.7), smallCloudsYPos[i],
					SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
	}

	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		objectManager.checkObjectTouched(hitbox);
	}

	public void checkObjectHit(Rectangle2D.Float attackbox) {
		objectManager.checkObjectHit(attackbox);
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
	}

	public void resetAll() {
		// TODO : reset player, enemy level etc.
		gameOver = false;
		paused = false;
		levelCompleted = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
		objectManager.resetAllObjects();

	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!gameOver)
			if (e.getButton() == MouseEvent.BUTTON1)
				player.setAttacking(true);

	}

	public void mouseDragged(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mousePressed(e);
			else if (levelCompleted)
				levelCompletedOverlay.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseReleased(e);
			else if (levelCompleted)
				levelCompletedOverlay.mouseReleased(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseMoved(e);
			else if (levelCompleted)
				levelCompletedOverlay.mouseMoved(e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch (e.getKeyCode()) {

			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_ESCAPE:
				paused = !paused;
				break;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!gameOver)
			switch (e.getKeyCode()) {

			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			}
	}

	public void setMaxLevelOffset(int levelOffset) {
		this.maxLevelOffsetX = levelOffset;
	}

	public void unpauseGame() {
		paused = false;
	}

	public void windowFocusLost() {

		player.resetDirBooleans();
	}

	public Player getPlayer() {

		return player;
	}

	public EnemyManager getEnemyManager() {

		return enemyManager;
	}

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public void setLevelCompleted(boolean levelCompleted) {
		this.levelCompleted = levelCompleted;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

}
