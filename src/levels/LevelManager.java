package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.GameState;
import main.Game;
import utilz.LoadSave;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprite;
	private ArrayList<Level> levels;

	private int levelIndex = 0;

	public LevelManager(Game game) {
		this.game = game;
		importOutsideSprites();
		levels = new ArrayList<>();
		buildAllLevels();
	}

	public void loadNextLevel() {
		levelIndex++;
		if (levelIndex >= levels.size()) {
			levelIndex = 0;
			System.out.println("No more Levels! Game complete");
			GameState.state = GameState.MENU;
		}

		Level newLevel = levels.get(levelIndex);
		game.getPlaying().getEnemyManager().loadEnemies(newLevel);
		game.getPlaying().getPlayer().loadLevelData(newLevel.getLevelData());
		game.getPlaying().setMaxLevelOffset(newLevel.getLevelOffset());
	}

	private void buildAllLevels() {

		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for (BufferedImage img : allLevels)
			levels.add(new Level(img));
	}

	private void importOutsideSprites() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48];
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 12; ++j) {
				int index = i * 12 + j;
				levelSprite[index] = img.getSubimage(j * 32, i * 32, 32, 32);
			}
		}
	}

	public void draw(Graphics g, int levelOffset) {
		for (int j = 0; j < Game.TILES_IN_HEIGHT; ++j) {
			for (int i = 0; i < levels.get(levelIndex).getLevelData()[0].length; ++i) {
				int index = levels.get(levelIndex).getSpriteIndex(i, j);
				g.drawImage(levelSprite[index], Game.TILES_SIZE * i - levelOffset, Game.TILES_SIZE * j, Game.TILES_SIZE,
						Game.TILES_SIZE, null);
			}

		}

	}

	public void update() {

	}

	public Level getCurrentLevel() {
		return levels.get(levelIndex);
	}

	public int getAmountOfLevels() {
		return levels.size();
	}

}
