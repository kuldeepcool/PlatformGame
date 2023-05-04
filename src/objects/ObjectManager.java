package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;

public class ObjectManager {

	private Playing playing;
	private BufferedImage[][] potionImages;
	private BufferedImage[][] containerImages;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;

	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImages();
	}

	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	}

	public void applyEffectToPlayer(Potion p) {
		if (p.getObjectType() == RED_POTION)
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		else
			playing.getPlayer().changePower(BLUE_POTION_VALUE);
	}

	public void checkObjectHit(Rectangle2D.Float attackbox) {
		for (GameContainer gc : containers)
			if (gc.isActive()) {
				if (gc.getHitbox().intersects(attackbox)) {
					gc.setAnimation(true);
					int type = 0;
					if (gc.getObjectType() == BARREL)
						type = 1;
					potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width / 2),
							(int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
					return;
				}
			}
	}

	private void loadImages() {

		// Potions
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImages = new BufferedImage[2][7];

		for (int j = 0; j < potionImages.length; ++j)
			for (int i = 0; i < potionImages[j].length; ++i)
				potionImages[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

		// Barrels and Boxes
		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImages = new BufferedImage[2][8];

		for (int j = 0; j < containerImages.length; ++j)
			for (int i = 0; i < containerImages[j].length; ++i)
				containerImages[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
	}

	public void loadObjects(Level newLevel) {
		potions = newLevel.getPotions();
		containers = newLevel.getContainers();
	}

	public void update() {
		for (Potion p : potions)
			if (p.isActive())
				p.update();

		for (GameContainer gc : containers)
			if (gc.isActive())
				gc.update();
	}

	public void draw(Graphics g, int xLevelOffset) {
		drawPotions(g, xLevelOffset);
		drawContainers(g, xLevelOffset);
	}

	private void drawContainers(Graphics g, int xLevelOffset) {
		for (GameContainer gc : containers)
			if (gc.isActive()) {
				int type = 0;
				if (gc.getObjectType() == BARREL)
					type = 1;
				g.drawImage(containerImages[type /* gc.getObjectType() == BARREL ? 1 : 0 */][gc.getAnimationIndex()],
						(int) (gc.getHitbox().x - gc.getxDrawOffset() - xLevelOffset),
						(int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
				gc.drawHitbox(g, xLevelOffset);
			}
	}

	private void drawPotions(Graphics g, int xLevelOffset) {
		for (Potion p : potions)
			if (p.isActive()) {
				int type = 0;
				if (p.getObjectType() == RED_POTION)
					type = 1;
				g.drawImage(potionImages[type /* p.getObjectType() */][p.getAnimationIndex()],
						(int) (p.getHitbox().x - p.getxDrawOffset() - xLevelOffset),
						(int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT, null);
				p.drawHitbox(g, xLevelOffset);
			}
	}

	public void resetAllObjects() {
		for (Potion p : potions)
			p.reset();

		for (GameContainer gc : containers)
			gc.reset();
	}

}