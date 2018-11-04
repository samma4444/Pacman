package pacman;


/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class Ghost2 extends Entity {

	private double moveSpeed = 2000; // vert speed shot moves
	private Game game; // the game in which the ship exists

	/*
	 * construct the shot input: game - the game in which the shot is being
	 * createda ref - a string with the name of the image associated to the
	 * sprite for the shot x, y - initial location of shot
	 */
	public Ghost2(Game g, String r, int newX, int newY) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
		dy = 0;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move shot
	 */
	@Override
	public void move(long delta) {
		super.move(delta); // calls the move method in Entity

	} // move

	/*
	 * collidedWith input: other - the entity with which the shot has collided
	 * purpose: notification that the shot has collided with something
	 */
	@Override
	public void collidedWith(Entity other) {
		if (other instanceof Player
				&& (game.playerPU == 0 || game.ghostD2 == 0)) {
			Game.lives--;
			game.notifyGD(2);
			game.ghost2DeathTimer = System.nanoTime();
		} else {
			game.notifyGD(2);
			game.ghost2.changeSprite("sprites/GhostP.png");
			game.ghost2DeathTimer = System.nanoTime();
			Game.score += 100;
		}

	} // collidedWith

} // ShipEntity class