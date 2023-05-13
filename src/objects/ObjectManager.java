package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;
import static utilz.Constants.Projectiles.*;
import static utilz.HelpMethods.CanCannonSeePlayer;
import static utilz.HelpMethods.IsProjectileHittingLevel;

public class ObjectManager {

	private Playing playing;
	private BufferedImage[][] potionImages;
	private BufferedImage[][] containerImages;
	private BufferedImage[] cannonImages;
	private BufferedImage spikeImage;
	private BufferedImage cannonBallImage;

	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Cannon> cannons;
	private ArrayList<Projectile> projectiles = new ArrayList<>();

	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImages();
	}

	public void checkSpikeTouched(Player p) {
		for (Spike s : spikes)
			if (p.getHitBox().intersects(s.getHitbox()))
				p.kill();

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
			if (gc.isActive() && !gc.doAnimation) {
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

		// Spikes
		spikeImage = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

		// Cannons
		BufferedImage cannonSprite = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);
		cannonImages = new BufferedImage[7];
		for (int i = 0; i < cannonImages.length; ++i)
			cannonImages[i] = cannonSprite.getSubimage(i * 40, 0, 40, 26);

		// Cannon Ball
		cannonBallImage = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);

	}

	public void loadObjects(Level newLevel) {
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
		spikes = newLevel.getSpikes();
		cannons = newLevel.getCannons();
		projectiles.clear();
	}

	public void update(int[][] levelData, Player player) {
		for (Potion p : potions)
			if (p.isActive())
				p.update();

		for (GameContainer gc : containers)
			if (gc.isActive())
				gc.update();

		updateCannons(levelData, player);
		updateProjectiles(levelData, player);
	}

	private void updateProjectiles(int[][] levelData, Player player) {
		for (Projectile p : projectiles)
			if (p.isActive()) {
				p.updatePos();
				if (p.getHitbox().intersects(player.getHitBox())) {
					player.changeHealth(-25);
					p.setActive(false);
				} else if (IsProjectileHittingLevel(p, levelData))
					p.setActive(false);
			}

	}

	private boolean isPlayerInRange(Cannon c, Player player) {
		int absValue = (int) Math.abs(player.getHitBox().x - c.getHitbox().x);
		return absValue <= Game.TILES_SIZE * 5;
	}

	private boolean isPlayerInFrontOfCannon(Cannon c, Player player) {
		if (c.getObjectType() == CANNON_LEFT) {
			if (c.getHitbox().x > player.getHitBox().x)
				return true;

		} else if (c.getHitbox().x < player.getHitBox().x)
			return true;
		return false;
	}

	private void updateCannons(int[][] levelData, Player player) {

		// if cannon not animating
		// -if in same tileY
		// --if player in range
		// ---is player in front of cannon
		// ----check LOS
		// -----shoot
		for (Cannon c : cannons) {
			if (!c.doAnimation)
				if (c.getTileY() == player.getTileY())
					if (isPlayerInRange(c, player))
						if (isPlayerInFrontOfCannon(c, player))
							if (CanCannonSeePlayer(levelData, player.getHitBox(), c.getHitbox(), c.getTileY()))
								c.setAnimation(true);
			c.update();
			if (c.getAnimationIndex() == 4 && c.getAnimationTick() == 0)
				shootCannon(c);
		}

	}

	private void shootCannon(Cannon c) {
		int dir = 1;
		if (c.getObjectType() == CANNON_LEFT)
			dir = -1;

		projectiles.add(new Projectile((int) c.getHitbox().x, (int) c.getHitbox().y, dir));

	}

	public void draw(Graphics g, int xLevelOffset) {
		drawPotions(g, xLevelOffset);
		drawContainers(g, xLevelOffset);
		drawTraps(g, xLevelOffset);
		drawCannons(g, xLevelOffset);
		drawProjectiles(g, xLevelOffset);
	}

	private void drawProjectiles(Graphics g, int xLevelOffset) {
		for (Projectile p : projectiles)
			if (p.isActive()) {
				g.drawImage(cannonBallImage, (int) (p.getHitbox().x - xLevelOffset), (int) (p.getHitbox().y),
						CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
				p.drawHitbox(g, xLevelOffset);
			}

	}

	private void drawCannons(Graphics g, int xLevelOffset) {
		for (Cannon c : cannons) {

			int x = (int) (c.getHitbox().x - xLevelOffset);
			int width = CANNON_WIDTH;
			if (c.getObjectType() == CANNON_RIGHT) {
				x += width;
				width *= -1;
			}
			g.drawImage(cannonImages[c.getAnimationIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
			c.drawHitbox(g, xLevelOffset);
		}

	}

	private void drawTraps(Graphics g, int xLevelOffset) {
		for (Spike s : spikes) {
			g.drawImage(spikeImage, (int) (s.getHitbox().x - xLevelOffset),
					(int) (s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
			s.drawHitbox(g, xLevelOffset);
		}
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

		loadObjects(playing.getLevelManager().getCurrentLevel());

		for (Potion p : potions)
			p.reset();

		for (GameContainer gc : containers)
			gc.reset();

		for (Cannon c : cannons)
			c.reset();

	}

}
