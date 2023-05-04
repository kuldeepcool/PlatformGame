package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utilz.HelpMethods;

import static utilz.HelpMethods.GetLevelData;
import static utilz.HelpMethods.GetCrabs;
import static utilz.HelpMethods.GetPlayerSpawn;

public class Level {

	private BufferedImage img;
	private ArrayList<Crabby> crabs;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private Point playerSpawn;

	private int levelTilesWide;
	private int maxTilesOffset;
	private int maxLevelOffsetX;

	private int[][] levelData;

	public Level(BufferedImage img) {
		this.img = img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createSpikes();
		calculateLevelOffsets();
		calculatePlayerSpawn();
	}

	private void createSpikes() {
		spikes = HelpMethods.GetSpikes(img);
	}

	private void createContainers() {
		containers = HelpMethods.GetContainers(img);
	}

	private void createPotions() {
		potions = HelpMethods.GetPotions(img);
	}

	private void calculatePlayerSpawn() {
		playerSpawn = GetPlayerSpawn(img);
	}

	private void calculateLevelOffsets() {
		levelTilesWide = img.getWidth();
		maxTilesOffset = levelTilesWide - Game.TILES_IN_WIDTH;
		maxLevelOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

	private void createEnemies() {

		crabs = GetCrabs(img);
	}

	private void createLevelData() {

		levelData = GetLevelData(img);
	}

	public int getSpriteIndex(int x, int y) {
		return levelData[y][x];
	}

	public int[][] getLevelData() {
		return levelData;
	}

	public int getLevelOffset() {
		return maxLevelOffsetX;
	}

	public Point getPlayerSpawn() {
		return playerSpawn;
	}

	public ArrayList<Crabby> getCrabs() {
		return crabs;
	}

	public ArrayList<Potion> getPotions() {
		return potions;
	}

	public ArrayList<GameContainer> getContainers() {
		return containers;
	}

	public ArrayList<Spike> getSpikes() {
		return spikes;
	}

}
