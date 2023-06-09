package utilz;

import static utilz.Constants.EnemyConstants.CRABBY;
import static utilz.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Projectile;
import objects.Spike;

public class HelpMethods {

	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] levelData) {
		if (!IsSolid(x, y, levelData))
			if (!IsSolid(x + width, y + height, levelData))
				if (!IsSolid(x + width, y, levelData))
					if (!IsSolid(x, y + height, levelData))
						return true;
		return false;
	}

	private static boolean IsSolid(float x, float y, int[][] levelData) {

		int maxWidth = levelData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth)
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT)
			return true;

		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;

		return IsTileSolid((int) xIndex, (int) yIndex, levelData);
	}

	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
		return IsSolid(p.getHitbox().x + p.getHitbox().width / 2, p.getHitbox().y + p.getHitbox().height / 2, lvlData);

	}

	public static boolean IsTileSolid(int xTile, int yTile, int[][] levelData) {

		int value = levelData[yTile][xTile];
		if (value >= 48 || value < 0 || value != 11)
			return true;
		return false;
	}

	public static float GetEntityPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
		if (xSpeed > 0) {
			// Right Collision
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else {
			// Left Collision
			return currentTile * Game.TILES_SIZE;
		}
	}

	public static float GetEntityPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			// Falling or Touching Floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else {
			// Jumping
			return currentTile * Game.TILES_SIZE;
		}
	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] levelData) {
		// Check pixels below bottom-left and bottom-right
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, levelData))
			if (!IsSolid(hitbox.x + hitbox.width + 1, hitbox.y + hitbox.height + 1, levelData))
				return false;
		return true;
	}

	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] levelData) {
		if (xSpeed > 0)
			return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, levelData);
		else
			return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, levelData);
	}

	public static boolean CanCannonSeePlayer(int[][] levelData, Rectangle2D.Float playerHitBox,
			Rectangle2D.Float cannonHitbox, int tileY) {

		int firstTileX = (int) (playerHitBox.x / Game.TILES_SIZE);
		int secondTileX = (int) (cannonHitbox.x / Game.TILES_SIZE);

		if (firstTileX > secondTileX)
			return IsAllTilesClear(secondTileX, firstTileX, tileY, levelData);

		else
			return IsAllTilesClear(firstTileX, secondTileX, tileY, levelData);

	}

	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] levelData) {
		for (int i = 0; i < xEnd - xStart; ++i)
			if (IsTileSolid(xStart + i, y, levelData))
				return false;
		return true;
	}

	public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] levelData) {

		if (IsAllTilesClear(xStart, xEnd, y, levelData))
			for (int i = 0; i < xEnd - xStart; ++i)
				if (!IsTileSolid(xStart + i, y + 1, levelData))
					return false;
		return true;
	}

	public static boolean IsSightClear(int[][] levelData, Rectangle2D.Float enemyHitbox, Rectangle2D.Float playerHitbox,
			int tileY) {

		int firstTileX = (int) (enemyHitbox.x / Game.TILES_SIZE);
		int secondTileX = (int) (playerHitbox.x / Game.TILES_SIZE);

		if (firstTileX > secondTileX)
			return IsAllTilesWalkable(secondTileX, firstTileX, tileY, levelData);

		else
			return IsAllTilesWalkable(firstTileX, secondTileX, tileY, levelData);
	}

	public static int[][] GetLevelData(BufferedImage img) {

		int[][] levelData = new int[img.getHeight()][img.getWidth()];

		for (int j = 0; j < img.getHeight(); ++j) {
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48)
					value = 0;
				levelData[j][i] = value;
			}
		}
		return levelData;
	}

	public static ArrayList<Crabby> GetCrabs(BufferedImage img) {

		ArrayList<Crabby> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == CRABBY)
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		return list;
	}

	public static ArrayList<Potion> GetPotions(BufferedImage img) {

		ArrayList<Potion> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == RED_POTION || value == BLUE_POTION)
					list.add(new Potion(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}

	public static ArrayList<GameContainer> GetContainers(BufferedImage img) {

		ArrayList<GameContainer> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == BARREL || value == BOX)
					list.add(new GameContainer(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));

			}
		return list;
	}

	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		ArrayList<Cannon> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == CANNON_LEFT || value == CANNON_RIGHT)
					list.add(new Cannon(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));

			}
		return list;
	}

	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == SPIKE)
					list.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE));

			}
		return list;
	}

	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int j = 0; j < img.getHeight(); ++j)
			for (int i = 0; i < img.getWidth(); ++i) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == 100)
					return new Point(i * Game.TILES_SIZE, j * Game.TILES_SIZE);
			}
		return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
	}

}
