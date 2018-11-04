package pacman;

/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class Food extends Entity {

  private Game game; // the game in which the ship exists

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public Food(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dy = 0;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move shot
   */
  @Override
public void move (long delta){
    super.move(delta);  // calls the move method in Entity

    // if shot moves off top of screen, remove it from entity list
    if (y < -100) {
      game.removeEntity(this);
    } // if

  } // move


  /* collidedWith
   * input: other - the entity with which the shot has collided
   * purpose: notification that the shot has collided
   *          with something
   */
    @Override
	public void collidedWith(Entity other) {
	 
	    if(other instanceof Player){
			game.removeEntity(this);
		 
	    }
	    if(Game.totalD == 0){
        	game.notifyWin();
        }
    }

}