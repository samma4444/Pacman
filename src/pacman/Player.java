package pacman;

/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class Player extends Entity {

	public static int d = 0;

	private Game game; // the game in which the ship exists

	/*
	 * construct the player's ship input: game - the game in which the ship is
	 * being created ref - a string with the name of the image associated to the
	 * sprite for the ship x, y - initial location of ship
	 */
	public Player(Game g, String[] c, int newX, int newY) {

		super(c[d], newX, newY); // calls the constructor in Entity
		game = g;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move ship
	 */
	@Override
	public void move(long delta) {

		super.move(delta); // calls the move method in Entity
	} // move

	/*
	 * collidedWith input: other - the entity with which the ship has collided
	 * purpose: notification that the player's ship has collided with something
	 */
	@Override
	public void collidedWith(Entity other) {

	} // collidedWith

} // ShipEntity class