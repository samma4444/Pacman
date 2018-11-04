package pacman;

/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class Wall extends Entity {

	private Game game; // the game in which the alien exists

	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of
	 * alien
	 */
	public Wall(Game g, String r, int newX, int newY) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
	} // constructor

	@Override
	public void collidedWith(Entity other) {
		// collisions with aliens are handled in ShotEntity and ShipEntity
	} // collidedWith

} // AlienEntity class
