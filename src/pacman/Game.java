package pacman;

/* Game.java
 * Space Invaders Main Program
 *
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
												// a key is pressed
	private boolean leftPressed = false; // true if left arrow key currently
											// pressed
	private boolean upPressed = false; // true if up arrow key currently pressed
	private boolean downPressed = false; // true if down arrow key currently pressed
	private boolean rightPressed = false; // true if right arrow key currently
											// pressed

	private boolean gameRunning = true; //if the game is running
	private ArrayList entities = new ArrayList(); // list of entities
													// in game
	private ArrayList removeEntities = new ArrayList(); // list of entities
														// to remove this loop

	private int[][] shipP = new int[40][30]; //tracks the ship's position 
	private int[][] board = new int[40][30]; //the ship's board
	private int shipX = 0; //the ship's x position
	private int shipY = 0; //the ship's y position
	
	
	private int[][] ghostB = new int[80][60]; //the ghosts's board

	private int[][] ghostP = new int[80][60]; //the position of the red ghost relative to it's board
	private int[][] ghostP2 = new int[80][60]; //the position of the pink ghost relative to it's board

	
	
	private long timePU1 = 0; //start of power up time
	private int shipXS = 0; //if the ship is moving in the x axis 
	                        //1 for right, -1 for left
	private int shipYS = 0; //if the ship is moving in the y axis 
    						//1 for up, -1 for down

	int playerPU = 0; // equals 1 if you have a power up

	int ghostD = 0; // tracks for the red ghost whether to run 
					// away or towards pacman
	int ghostD2 = 0; // tracks for the pink ghost whether to run 
					 // away or towards pacman

	long ghost1DeathTimer = 0; //tracks death timer for the red ghost
	long ghost2DeathTimer = 0; //tracks death timer for the pink ghost

	private double ghostX = 0; // the x position of the red ghost on pacman's board
	private double ghostY = 0; // the y position of the red ghost on pacman's board
	private int ghostX2 = 0; // the x position of the red ghost on the ghosts's board
	private int ghostY2 = 0; // the y position of the red ghost on the ghosts's board

	private double ghost2X = 0; // the x position of the pink ghost on pacman's board
	private double ghost2Y = 0; // the y position of the pink ghost on pacman's board
	private int ghostX22 = 0; // the x position of the pink ghost on the ghosts's board
	private int ghostY22 = 0; // the y position of the pink ghost on the ghosts's board 

	private String[] spriteShip = new String[8]; //contains all the different sprites for the ship
	private Entity ship; // the ship
	Entity ghost; // the red ghost
	Entity ghost2; // the pink ghost

	private double moveSpeedX = 2000; // hor. vel. of ship (px/s) and double the speed of the ghost
	private double moveSpeedY = 2000; // ver. vel. of ship (px/s) and double the speed of the ghost

	private String message = ""; // message to display while waiting
									// for a key press

	private boolean logicRequiredThisLoop = false; // true if logic
													// needs to be
													// applied this loop

	
	static int totalD = 0; // total number of dots remaining
	static int score = 0; // your score
	static int lives = 3; // number of lives left
	boolean menu = false; // if your going to the menu this is true
	boolean startGame = false; // if your starting the game this is true

    BufferedImage menuI; //background image for the menu
	BufferedImage instruction; // background image for the instructions
	BufferedImage deadS; // background image for when you die
	BufferedImage winS; // background image for when you win

	boolean dead = false; // true if your dead
	boolean win = false; // true if you won
	
	int map = 0;
	
    Random r = new Random();
	
	//Construct our game and set it running. 
	public Game() {
		
		//loads the menu
		loadMenu();
		
		// create a frame to contain game
		JFrame container = new JFrame("Commodore 64 Pacman");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, 800, 600);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// get graphics context for the accelerated surface and make it black
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		// initialize entities
		initEntities();
		
		// start the game
		gameLoop();

	} // constructor
	
	//creates all the sprites needed for the ship
	private void initSprite() {
		
		//the d in the name is for direction
		//the m is for mouth shape
		spriteShip[0] = "sprites/D1M1.png";
		spriteShip[1] = "sprites/D1M2.png";
		spriteShip[2] = "sprites/D2M1.png";
		spriteShip[3] = "sprites/D2M2.png";
		spriteShip[4] = "sprites/D3M1.png";
		spriteShip[5] = "sprites/D3M2.png";
		spriteShip[6] = "sprites/D4M1.png";
		spriteShip[7] = "sprites/D4M2.png";
	}//initSprite
	
	//initializes the starting map
	private void initBoard1() {

		deInit();//clears the board
		
		// outer wall
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if (i == 0) {
					board[i][c] = 1;
					if (c != 29) {

					}
				} else if (c == 0) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
				if (i == 39) {
					board[i][c] = 1;
				} else if (c == 29) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
			}// for
		}// for

		// entrances
		for (int i = 0; i < 6; i++) {
			board[21][i] = 1;
			board[20][i] = 2;
			board[19][i] = 2;
			board[18][i] = 1;
			board[16][i] = 1;
			board[23][i] = 1;
			board[21][29 - i] = 1;
			board[18][29 - i] = 1;
			board[23][29 - i] = 1;
			board[16][29 - i] = 1;
			board[20][29 - i] = 2;
			board[19][29 - i] = 2;
		}//for

		board[22][5] = 1;
		board[17][5] = 1;
		board[22][24] = 1;
		board[17][24] = 1;
		// end of entrances

		// left boxes top
		for (int i = 0; i < 12; i = i + 3) {
			board[37 - i][27] = 1;
			board[37 - i][26] = 1;
			board[36 - i][27] = 1;
			board[36 - i][26] = 1;

			board[37 - i][23] = 1;
			board[37 - i][24] = 1;
			board[36 - i][23] = 1;
			board[36 - i][24] = 1;

			board[36 - i][2] = 1;
			board[36 - i][3] = 1;
			board[37 - i][2] = 1;
			board[37 - i][3] = 1;

			board[36 - i][5] = 1;
			board[36 - i][6] = 1;
			board[37 - i][5] = 1;
			board[37 - i][6] = 1;

			board[2 + i][2] = 1;
			board[2 + i][3] = 1;
			board[3 + i][2] = 1;
			board[3 + i][3] = 1;

			board[2 + i][5] = 1;
			board[2 + i][6] = 1;
			board[3 + i][5] = 1;
			board[3 + i][6] = 1;

			board[2 + i][24] = 1;
			board[2 + i][23] = 1;
			board[3 + i][24] = 1;
			board[3 + i][23] = 1;

			board[2 + i][26] = 1;
			board[2 + i][27] = 1;
			board[3 + i][26] = 1;
			board[3 + i][27] = 1;

		}//for

		// left boxes top
		for (int i = 0; i < 6; i++) {
			board[25][27 - i] = 1;
			board[25][7 - i] = 1;
			board[14][27 - i] = 1;
			board[14][7 - i] = 1;

		}//for
		
		
		// four long sticks on each corner
		for (int i = 0; i < 11; i++) {
			board[37 - i][21] = 1;
			board[37 - i][8] = 1;
			board[2 + i][8] = 1;
			board[2 + i][21] = 1;
		}//for

		//center block and ghost jail	
		for (int i = 0; i < 8; i++) {
			board[16 + i][12] = 4;
			board[16 + i][13] = 2;
			board[16 + i][14] = 2;
			board[16 + i][15] = 2;
			board[16 + i][16] = 2;
			board[16 + i][17] = 4;
		}//for
		for (int i = 0; i < 5; i++) {
			board[16][13 + i] = 4;
			board[23][13 + i] = 4;
		}//for

		
		// side blocks
		for (int i = 0; i < 10; i++) {
			board[0 + i][12] = 1;
			board[0 + i][13] = 2;
			board[0 + i][14] = 2;
			board[0 + i][15] = 2;
			board[0 + i][16] = 2;
			board[0 + i][17] = 1;
		}//for
		for (int i = 0; i < 10; i++) {
			board[39 - i][12] = 1;
			board[39 - i][13] = 2;
			board[39 - i][14] = 2;
			board[39 - i][15] = 2;
			board[39 - i][16] = 2;
			board[39 - i][17] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[10][12 + i] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[29][12 + i] = 1;
		}//for

		// dots
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if(board[i][c] == 0){
				    board[i][c] = 3;
				
				}//if
			}// for
		}// for
		
		//no dots at entrance
		board[17][0] = 0;
		board[22][0] = 0;
		board[22][1] = 0;
		board[17][1] = 0;
		board[17][2] = 0;
		board[22][2] = 0;
		board[22][3] = 0;
		board[17][3] = 0;
		board[17][4] = 0;
		board[22][4] = 0;

		board[17][29] = 0;
		board[22][29] = 0;
		board[22][28] = 0;
		board[17][28] = 0;
		board[17][27] = 0;
		board[22][27] = 0;
		board[22][26] = 0;
		board[17][26] = 0;
		board[17][25] = 0;
		board[22][25] = 0;
		
		//walls for ghosts
		for (int i = 1; i < 40; i++) {
			for (int c = 1; c < 30; c++) {
				if (board[i][c] == 1) {
					ghostB[i * 2][c * 2] = 1;
					ghostB[(i * 2) + 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) + 1] = 1;
					ghostB[(i * 2) + 1][(c * 2) + 1] = 1;

					ghostB[(i * 2) - 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(c * 2) - 1] = 1;
				}//if
				if (i == 20 || i == 22 || i == 17 || i == 19) {
					ghostB[i * 2][0] = 1;
					ghostB[(i * 2) + 1][0] = 1;
					ghostB[(i * 2) - 1][0] = 1;
					ghostB[i * 2][(30 * 2) - 1] = 1;
					ghostB[(i * 2) + 1][(30 * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(30 * 2) - 1] = 1;
				}//if
				if (i == 1) {
					for (int d = 0; d < 60; d++) {
						ghostB[i][d] = 1;
					}//for

				}//if
				if (c == 1) {
					for (int d = 0; d < 80; d++) {
						ghostB[d][c] = 1;
					}//for

				}//if

			}//for
		}//for

		// power ups
		board[1][1] = 8;
		board[1][28] = 8;
		board[38][1] = 8;
		board[38][28] = 8;

	}//initBoard1
	
	//initializes the starting map
	private void initBoard2() {

		deInit();//clears the board
		
		// outer wall
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if (i == 0) {
					board[i][c] = 1;
					if (c != 29) {

					}
				} else if (c == 0) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
				if (i == 39) {
					board[i][c] = 1;
				} else if (c == 29) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
			}// for
		}// for

		// entrances
		for (int i = 0; i < 6; i++) {
			board[21][i] = 1;
			board[20][i] = 2;
			board[19][i] = 2;
			board[18][i] = 1;
			board[16][i] = 1;
			board[23][i] = 1;
			board[21][29 - i] = 1;
			board[18][29 - i] = 1;
			board[23][29 - i] = 1;
			board[16][29 - i] = 1;
			board[20][29 - i] = 2;
			board[19][29 - i] = 2;
		}//for

		board[22][5] = 1;
		board[17][5] = 1;
		board[22][24] = 1;
		board[17][24] = 1;
		// end of entrances

		
		
		//center block and ghost jail	
		for (int i = 0; i < 8; i++) {
			board[16 + i][12] = 4;
			board[16 + i][13] = 2;
			board[16 + i][14] = 2;
			board[16 + i][15] = 2;
			board[16 + i][16] = 2;
			board[16 + i][17] = 4;
		}//for
		for (int i = 0; i < 5; i++) {
			board[16][13 + i] = 4;
			board[23][13 + i] = 4;
		}//for

		
		// side blocks
		for (int i = 0; i < 10; i++) {
			board[0 + i][12] = 1;
			board[0 + i][13] = 2;
			board[0 + i][14] = 2;
			board[0 + i][15] = 2;
			board[0 + i][16] = 2;
			board[0 + i][17] = 1;
		}//for
		for (int i = 0; i < 10; i++) {
			board[39 - i][12] = 1;
			board[39 - i][13] = 2;
			board[39 - i][14] = 2;
			board[39 - i][15] = 2;
			board[39 - i][16] = 2;
			board[39 - i][17] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[10][12 + i] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[29][12 + i] = 1;
		}//for

		// dots
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if(board[i][c] == 0){
				    board[i][c] = 3;
				
				}//if
			}// for
		}// for
		
		//no dots at entrance
		board[17][0] = 0;
		board[22][0] = 0;
		board[22][1] = 0;
		board[17][1] = 0;
		board[17][2] = 0;
		board[22][2] = 0;
		board[22][3] = 0;
		board[17][3] = 0;
		board[17][4] = 0;
		board[22][4] = 0;

		board[17][29] = 0;
		board[22][29] = 0;
		board[22][28] = 0;
		board[17][28] = 0;
		board[17][27] = 0;
		board[22][27] = 0;
		board[22][26] = 0;
		board[17][26] = 0;
		board[17][25] = 0;
		board[22][25] = 0;
		
		//walls for ghosts
		for (int i = 1; i < 40; i++) {
			for (int c = 1; c < 30; c++) {
				if (board[i][c] == 1) {
					ghostB[i * 2][c * 2] = 1;
					ghostB[(i * 2) + 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) + 1] = 1;
					ghostB[(i * 2) + 1][(c * 2) + 1] = 1;

					ghostB[(i * 2) - 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(c * 2) - 1] = 1;
				}//if
				if (i == 20 || i == 22 || i == 17 || i == 19) {
					ghostB[i * 2][0] = 1;
					ghostB[(i * 2) + 1][0] = 1;
					ghostB[(i * 2) - 1][0] = 1;
					ghostB[i * 2][(30 * 2) - 1] = 1;
					ghostB[(i * 2) + 1][(30 * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(30 * 2) - 1] = 1;
				}//if
				if (i == 1) {
					for (int d = 0; d < 60; d++) {
						ghostB[i][d] = 1;
					}//for

				}//if
				if (c == 1) {
					for (int d = 0; d < 80; d++) {
						ghostB[d][c] = 1;
					}//for

				}//if

			}//for
		}//for

		// power ups
		board[1][1] = 8;
		board[1][28] = 8;
		board[38][1] = 8;
		board[38][28] = 8;

	}//initBoard2
	
	//initializes the starting map
	private void initBoard() {
		
		
		deInit(); //clears the board before making a new one

		// outer wall
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if (i == 0) {
					board[i][c] = 1;
					if (c != 29) {

					}
				} else if (c == 0) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
				if (i == 39) {
					board[i][c] = 1;
				} else if (c == 29) {
					board[i][c] = 1;
					if (i == 20 || i == 22 || i == 17 || i == 19) {
						board[i][c] = 0;
					}
				}// if
			}// for
		}// for

		//center boxes and the ghost jail
		for (int i = 0; i < 8; i++) {
			board[16 + i][12] = 4;
			board[16 + i][13] = 2;
			board[16 + i][14] = 2;
			board[16 + i][15] = 2;
			board[16 + i][16] = 2;
			board[16 + i][17] = 4;
		}//for
		for (int i = 0; i < 5; i++) {
			board[16][13 + i] = 4;
			board[23][13 + i] = 4;
		}//for
		
		// side blocks
		for (int i = 0; i < 10; i++) {
			board[0 + i][12] = 1;
			board[0 + i][13] = 2;
			board[0 + i][14] = 2;
			board[0 + i][15] = 2;
			board[0 + i][16] = 2;
			board[0 + i][17] = 1;
		}//for
		for (int i = 0; i < 10; i++) {
			board[39 - i][12] = 1;
			board[39 - i][13] = 2;
			board[39 - i][14] = 2;
			board[39 - i][15] = 2;
			board[39 - i][16] = 2;
			board[39 - i][17] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[10][12 + i] = 1;
		}//for
		for (int i = 0; i < 6; i++) {
			board[29][12 + i] = 1;
		}//for

		// entrances
		for (int i = 0; i < 6; i++) {
			board[21][i] = 1;
			board[20][i] = 2;
			board[19][i] = 2;
			board[18][i] = 1;
			board[16][i] = 1;
			board[23][i] = 1;
			board[21][29 - i] = 1;
			board[18][29 - i] = 1;
			board[23][29 - i] = 1;
			board[16][29 - i] = 1;
			board[20][29 - i] = 2;
			board[19][29 - i] = 2;
		}//for

		board[22][5] = 1;
		board[17][5] = 1;
		board[22][24] = 1;
		board[17][24] = 1;
		// end of entrances
		
		
		//large up down lines
		for(int x = 2;x<11;x++){
			
			board[3][x] = 1; 
			board[36][x] = 1; 
			board[3][x + 17] = 1;
			board[36][x + 17] = 1; 
		}//for
		
		//small up down lines
		for(int x = 0;x<10;x+=2){
			
			board[x + 5][2] = 1; 
			board[x + 5][3] = 1; 
			board[x + 5][4] = 1; 
			board[x + 5][5] = 1; 
			
			board[x + 26][2] = 1; 
			board[x + 26][3] = 1; 
			board[x + 26][4] = 1; 
			board[x + 26][5] = 1; 
			
			board[x + 5][27] = 1; 
			board[x + 5][26] = 1; 
			board[x + 5][25] = 1; 
			board[x + 5][24] = 1; 
			
			board[x + 26][27] = 1; 
			board[x + 26][26] = 1; 
			board[x + 26][25] = 1; 
			board[x + 26][24] = 1; 
		}//for
		
		//long poles across
		for(int x = 5;x<35;x++){
			if(x != 19 && x != 20){
				board[x][9] = 1; 
				board[x][10] = 1; 
				board[x][7] = 1; 
				board[x][19] = 1; 
				board[x][20] = 1; 
				board[x][22] = 1; 
			}//if
		}//for
		

		// dots
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				if(board[i][c] == 0){
				    board[i][c] = 3;
				
				}//if
			}// for
		}// for
		
		//no dots at entrance
		board[17][0] = 0;
		board[22][0] = 0;
		board[22][1] = 0;
		board[17][1] = 0;
		board[17][2] = 0;
		board[22][2] = 0;
		board[22][3] = 0;
		board[17][3] = 0;
		board[17][4] = 0;
		board[22][4] = 0;

		board[17][29] = 0;
		board[22][29] = 0;
		board[22][28] = 0;
		board[17][28] = 0;
		board[17][27] = 0;
		board[22][27] = 0;
		board[22][26] = 0;
		board[17][26] = 0;
		board[17][25] = 0;
		board[22][25] = 0;
		
		
		//walls for ghosts
		for (int i = 1; i < 40; i++) {
			for (int c = 1; c < 30; c++) {
				if (board[i][c] == 1) {
					ghostB[i * 2][c * 2] = 1;
					ghostB[(i * 2) + 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) + 1] = 1;
					ghostB[(i * 2) + 1][(c * 2) + 1] = 1;

					ghostB[(i * 2) - 1][c * 2] = 1;
					ghostB[(i * 2)][(c * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(c * 2) - 1] = 1;
				}//if
				if (i == 20 || i == 22 || i == 17 || i == 19) {
					ghostB[i * 2][0] = 1;
					ghostB[(i * 2) + 1][0] = 1;
					ghostB[(i * 2) - 1][0] = 1;
					ghostB[i * 2][(30 * 2) - 1] = 1;
					ghostB[(i * 2) + 1][(30 * 2) - 1] = 1;
					ghostB[(i * 2) - 1][(30 * 2) - 1] = 1;
				}//if
				if (i == 1) {
					for (int d = 0; d < 60; d++) {
						ghostB[i][d] = 1;
					}//for

				}//if
				if (c == 1) {
					for (int d = 0; d < 80; d++) {
						ghostB[d][c] = 1;
					}//for

				}//if

			}//for
		}//for

		// power ups
		board[1][1] = 8;
		board[1][28] = 8;
		board[38][1] = 8;
		board[38][28] = 8;

	}//initBoard
	/*
	 * initEntities input: none output: none purpose: Initialise the starting
	 * state of the ship and alien entities. Each entity will be added to the
	 * array of entities in the game.
	 */
	private void initEntities() {
		
		//Initializes the board and the sprites 
		initSprite();

		
		//Chooses one of two possible to play
		if(r.nextInt(2) == 0 && map != 1){
			initBoard();
			map = 1;
		}else if(r.nextInt(2) == 1 && map != 1){
			initBoard1();
			map = 1;
		}else{
			initBoard2();
			map = 1;
		}//else
		
		// create all the food for pacman
		for (int row = 0; row < 40; row++) {
			for (int col = 0; col < 30; col++) {
				if (board[row][col] == 3) {
					Entity food = new Food(this, "sprites/dot.png", (row * 20),
							(col * 20));
					entities.add(food);
				}//if

			} // for
		} // outer for
		
		//creates the player
		ship = new Player(this, spriteShip, 20 * 20, 2 * 20);
		entities.add(ship);
		shipP[20][2] = 2;
		shipX = 20;
		shipY = 2;

		// create the red ghost and put in center of screen
		ghost = new Ghost(this, "sprites/GhostR.png", 38 * 10, 28 * 10);
		entities.add(ghost);
		ghostX = 19;
		ghostY = 14;

		ghostX2 = 38;
		ghostY2 = 28;
		ghostP[38][28] = 2;

		// creates the pink ghost and puts it in the center of the screen
		ghost2 = new Ghost2(this, "sprites/GhostP.png", 40 * 10, 28 * 10);
		entities.add(ghost2);
		ghost2X = 20;
		ghost2Y = 14;

		ghostX22 = 40;
		ghostY22 = 28;
		ghostP2[40][28] = 3;
		
		//creates all the walls
		for (int row = 0; row < 40; row++) {
			for (int col = 0; col < 30; col++) {
				if (board[row][col] == 1) {
					Entity wall = new Wall(this, "sprites/boxW.png",
							(row * 20), (col * 20));
					entities.add(wall);
				}//if

			} // for
		} // outer for
		
		//creates all the walls that only allow ghosts to pass through
		for (int row = 0; row < 40; row++) {
			for (int col = 0; col < 30; col++) {
				if (board[row][col] == 4) {
					Entity wall2 = new Wall(this, "sprites/wall2.png",
							(row * 20), (col * 20));
					entities.add(wall2);

				}//if
			} // for
		} // outer for
		
		//creates the power ups
		for (int row = 0; row < 40; row++) {
			for (int col = 0; col < 30; col++) {
				if (board[row][col] == 8) {
					Entity powerUp = new powerUp(this, "sprites/bigDot.png",
							(row * 20), (col * 20));
					entities.add(powerUp);
				}//if

			} // for
		} // outer for
	} // initEntities

	/*
	 * Notification from a game entity that the logic of the game should be run
	 * at the next opportunity
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	} // updateLogic

	/*
	 * Remove an entity from the game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	/*
	 * Notification that the player has died.
	 */
	public void notifyDeath() {

		dead = true;
		newGame();
		
	} // notifyDeath
	
	//for restarting the game
	public void newGame(){
		lives = 3;
		map = 0;
		deInit(); // clears the board
		//sets the starting speed,power up to none
		shipXS = 0; 
		shipYS = 0;
		playerPU = 0;
		waitingForKeyPress = true;
		score = 0;
		ghost1DeathTimer = 0;
		ghost2DeathTimer = 0;
		ghostD = 0;
		ghostD2 = 0;
		
		//clears all entities
		for (int j = 0; j < entities.size(); j++) {
			entities.get(j);
			removeEntity((Entity) entities.get(j));
			
		} // inner for
	}//newGame
	
	//clears the board
	public void deInit(){
		// outer wall
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				board[i][c] = 0;
			}// for
		}// for
		// outer wall
		for (int i = 0; i < 80; i++) {
			for (int c = 0; c < 60; c++) {
				ghostB[i][c] = 0;
			}// for
		}// for
	}//deInit
	
	/*
	 * Notification that the play has killed all aliens
	 */
	public void notifyWin() {
		
		win = true;
		newGame();
		
	} // notifyWin

	public void pause() {

		if (waitingForKeyPress == false) {
			return;
		}//if
	}//pause

	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs
	 * throughout game play. Responsible for the following activities: -
	 * calculates speed of the game loop to update moves - moves the game
	 * entities - draws the screen contents (entities, text) - updates game
	 * events - checks input
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		newGame();
		// keep loop running until game ends
		while (gameRunning) {

			// calc. time since last update, will be used to calculate
			// entities movement
			long timePU2 = System.nanoTime();
			lastLoopTime = System.currentTimeMillis();

			pause();

			// get graphics context for the accelerated surface and make it
			// black
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);

			if (lives <= 0) {
				notifyDeath();

			}//if
		

			// brute force collisions, compare every entity
			// against every other entity. If any collisions
			// are detected notify both entities that it has
			// occurred\
			for (int i = 0; i < entities.size(); i++) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);
					if (me == ship || him == ship) {
						try{
							if (me.collidesWith(him)) {
								me.collidedWith(him);
								him.collidedWith(me);
	
							} // if
						
				        } catch (Exception e) {
				            System.out.println("error");
				        }
						
					}

				} // inner for
			} // outer for

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();


			// decides which screen to use
			if (waitingForKeyPress && !menu && !dead && !win) {

				g.setColor(Color.white);
				g.drawImage(menuI, 0, 0, this); // set background
				newGame();

			} else if (menu) {
				waitingForKeyPress = true;
				g.drawImage(instruction, 0, 0, this); // set background
				newGame();

			} else if (dead) {
				waitingForKeyPress = true;
				g.drawImage(deadS, 0, 0, this); // set background
				newGame();

			} else if (win) {
				waitingForKeyPress = true;
				g.drawImage(winS, 0, 0, this); // set background
				newGame();

			} else if (!waitingForKeyPress && !menu) {
				for (int i = 0; i < entities.size(); i++) {

					Entity entity = (Entity) entities.get(i);
					if (entity != null) {
						entity.draw(g);
					}//if
				} // for
				g.drawString(("Dots Remaining:" + " " + totalD), 10, 280);
				g.drawString(("Score:" + " " + score), 10, 300);
				g.drawString(("Lives:" + " " + lives), 10, 320);

				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.move(10);
				} // for
				
				//sets the ghost's current speed to zero
				ghost2.setHorizontalMovement(0);
				ghost2.setVerticalMovement(0);
				ghost.setHorizontalMovement(0);
				ghost.setVerticalMovement(0);
				

				//moves both ghosts
				if (playerPU != 1 || ghostD == 0
						&& (timePU2 - ghost1DeathTimer >= 3e+9)) {
					moveGhost();

				} else if (playerPU == 1 && ghostD != 0
						&& (timePU2 - ghost1DeathTimer >= 3e+9)) {
					moveGhostB();

				}//else
				if (playerPU != 1 || ghostD2 == 0
						&& (timePU2 - ghost2DeathTimer >= 3e+9)) {
					moveGhost2();
				} else if (playerPU == 1 && ghostD2 != 0
						&& (timePU2 - ghost2DeathTimer >= 3e+9)) {
					moveGhost2B();
				}//else
			}//else 

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();
			if (playerPU == 1) {
				if (timePU2 - timePU1 >= 1e+10) {
					playerPU = 0;
					ghost.changeSprite("sprites/GhostR.png");
					ghost2.changeSprite("sprites/GhostP.png");
				}//if
			}//if
			
			
			
			//sets the number of dots to zero before we recount it 
			totalD = 0;
			
			//counts the number of dots remaining
			for (int i = 0; i < 40; i++) {
				for (int c = 0; c < 30; c++) {

					if (board[i][c] == 3) {
						totalD++;
					}//if
				}//for
			}//for
			
			// respond to user moving 
			if ((leftPressed) && (!rightPressed)) {

				shipXS = -1;
				shipYS = 0;
				ship.changeSprite("sprites/D2M1.png");
			} else if ((rightPressed) && (!leftPressed)) {

				shipXS = 1;
				shipYS = 0;
				ship.changeSprite("sprites/D4M1.png");
			} // else

			if ((upPressed) && (!downPressed)) {
				shipYS = -1;
				shipXS = 0;
				ship.changeSprite("sprites/D3M1.png");
			} else if ((!upPressed) && (downPressed)) {
				shipYS = 1;
				shipXS = 0;
				ship.changeSprite("sprites/D1M1.png");
			} // else

			
			//sets the speeds to zero so they don't add up
			ship.setHorizontalMovement(0);
			ship.setVerticalMovement(0);
			
			//moves the ship and changes the sprite based on the current sprite 
			//and the direction it was told to go through user commands
			//also runs error checking to check whether it s allowed in that spot or no
			if (shipXS < 0) {
				shipP[shipX][shipY] = 0;
				shipP[shipX - 1][shipY] = 2;
				shipX = shipX - 1;
				if (ship.z == "sprites/D2M1.png") {
					ship.changeSprite("sprites/D2M2.png");
				} else {
					ship.changeSprite("sprites/D2M1.png");
				}//else
				if (checkPosition()) {

					ship.setHorizontalMovement(-moveSpeedX);

				} else {
					shipXS = 0;
					shipP[shipX][shipY] = 0;
					shipP[shipX + 1][shipY] = 2;
					shipX = shipX + 1;
					
				}//else
			} else if (shipXS > 0) {
				shipP[shipX][shipY] = 0;
				shipP[shipX + 1][shipY] = 2;
				shipX = shipX + 1;
				if (ship.z == "sprites/D4M1.png") {
					ship.changeSprite("sprites/D4M2.png");
				} else {
					ship.changeSprite("sprites/D4M1.png");
				}//else
				
				if (checkPosition()) {
					ship.setHorizontalMovement(moveSpeedX);

				} else {
					shipXS = 0;
					shipP[shipX][shipY] = 0;
					shipP[shipX - 1][shipY] = 2;
					shipX = shipX - 1;
					// createBuffer();
				}//else
			}//else

			if (shipYS < 0) {
				shipP[shipX][shipY] = 0;
				if (shipY != 0) {
					shipP[shipX][shipY - 1] = 2;
					shipY = shipY - 1;
				} else {
					shipP[shipX][28] = 2;
					shipY = 28;
				}//else
				if (ship.z == "sprites/D3M1.png") {
					ship.changeSprite("sprites/D3M2.png");
				} else {
					ship.changeSprite("sprites/D3M1.png");
				}//else

				// createBuffer();
				if (checkPosition()) {
					if (shipY != 0) {
						ship.setVerticalMovement(-moveSpeedY);
					} else {
						ship.y = 29 * 20;
					}//else

				} else {
					shipP[shipX][shipY] = 0;
					shipP[shipX][shipY + 1] = 2;
					shipY = shipY + 1;
					// createBuffer();
				}//else
			} else if (shipYS > 0) {
				shipP[shipX][shipY] = 0;
				if (shipY != 29) {
					shipP[shipX][shipY + 1] = 2;
					shipY = shipY + 1;
				} else {
					shipP[shipX][1] = 2;
					shipY = 1;
				}//else
				if (ship.z == "sprites/D1M1.png") {
					ship.changeSprite("sprites/D1M2.png");
				} else {
					ship.changeSprite("sprites/D1M1.png");
				}//else
				// createBuffer();
				if (checkPosition()) {
					if (shipY != 29) {
						ship.setVerticalMovement(moveSpeedY);
					} else {
						ship.y = 0;
					}//else
				} else {
					shipP[shipX][shipY] = 0;
					shipP[shipX][shipY - 1] = 2;
					shipY = shipY - 1;
					// createBuffer();
				}//else

			}//else

			
			// pause
			try {
				Thread.sleep(60);
			} catch (Exception e) {
			}//catch

		} // while

	} // gameLoop

	/*
	 * startGame input: none output: none purpose: start a fresh game, clear old
	 * data
	 */
	private void startGame() {
		// clear out any existing entities and initalize a new set
		entities.clear();

		initEntities();

		// blank out any keyboard settings that might exist
		leftPressed = false;
		rightPressed = false;
		upPressed = false;
		downPressed = false;
	} // startGame

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since
									// waiting for 'any' key press

		/*
		 * The following methods are required for any class that extends the
		 * abstract class KeyAdapter. They handle keyPressed, keyReleased and
		 * keyTyped events.
		 */
		@Override
		public void keyPressed(KeyEvent e) {

			// if waiting for keypress to start game, do nothing
			if (e.getKeyCode() == KeyEvent.VK_1) {
				win = false;
				menu = false;
				dead = false;
				startGame = true;
			} // if

			// if waiting for keypress to go to the menu, do nothing
			if (e.getKeyCode() == KeyEvent.VK_2) {
				menu = true;
			} // if
			// waiting , do nothing
			if (waitingForKeyPress) {
				return;
			} // if
			
			// respond to move left, right, up or down
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			} // if

		} // keyPressed

		@Override
		public void keyReleased(KeyEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right, up or down
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = false;
			} // if

		} // keyReleased

		@Override
		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start game
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				} // else
			} // if waitingForKeyPress

			// if escape is pressed, end game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler
	
	//runs this when a ghost dies
	public void notifyGD(int i) {
		if (i == 1) {
			ghost.x = 38 * 10;
			ghost.y = 28 * 10;
			ghostX = 19;
			ghostY = 14;

			ghostX2 = 38;
			ghostY2 = 28;
			ghostP[38][28] = 2;
			ghostD = 0;
		} else if (i == 2) {
			ghost2.x = 40 * 10;
			ghost2.y = 28 * 10;
			ghost2X = 20;
			ghost2Y = 14;

			ghostX22 = 40;
			ghostY22 = 28;
			ghostP2[40][28] = 3;
			ghostD2 = 0;
		}//if

	}//notifyGD
	
	//checks whether the player can move into a square or not 
	public boolean checkPosition() {
		for (int i = 0; i < 40; i++) {
			for (int c = 0; c < 30; c++) {
				
				//return false if you can't
				if (((board[i][c] == 1 || board[i][c] == 4) && shipP[i][c] == 2)) {

					return false;
					
				//if it moves into a square with a dot 
				} else if ((board[i][c] == 3 && shipP[i][c] == 2)) {

					totalD--;
					score++;
					board[i][c] = 0;
					
				//if it touches a power up turn all ghosts blue and track the current time
				//remove the power up
				} else if ((board[i][c] == 8 && shipP[i][c] == 2)) {

					ghost.changeSprite("sprites/GhostDying.png");
					ghost2.changeSprite("sprites/GhostDying.png");
					timePU1 = System.nanoTime();
					playerPU = 1;
					ghostD = 1;
					ghostD2 = 1;
					board[i][c] = 0;
				}//else

			}// for
		}// for
		//if you enter a empty square return true
		return true;
	}//checkPosition

	//checks whether the ghost can enter where it's trying to enter
	//the integer is for which ghost it is
	public boolean checkGhost(int d) {
		for (int i = 0; i < 80; i++) {
			for (int c = 0; c < 60; c++) {
				if ((ghostB[i][c] == 1 && ghostP[i][c] == 2) && d == 1) {

					return false;
				} else if ((ghostB[i][c] == 1 && ghostP2[i][c] == 2) && d == 2) {

					return false;

				}//else

			}// for
		}// for
		return true;
	}//checkGhost
	
	//when the ghost wants to move up
	//true if it can move up
	//also moves it if it can
	//when it returns false the calling function will move the ghost another direction
	public boolean moveGhostU() {

		ghostP[ghostX2][ghostY2] = 0;
		ghostP[ghostX2][ghostY2 - 1] = 2;
		ghostY = ghostY - 0.5;
		ghostY2 = ghostY2 - 1;
		if (checkGhost(1) == true) {

			ghost.setVerticalMovement(-moveSpeedX / 2);
			return true;
		} else {

			ghostP[ghostX2][ghostY2] = 0;
			ghostP[ghostX2][ghostY2 + 1] = 2;
			ghostY = ghostY + 0.5;
			ghostY2 = ghostY2 + 1;
			return false;

		}//else

	}//moveGhostU
	
	//when the ghost wants to move down
	//true if it can move down
	//also moves it if it can
	//when it returns false the calling function will move the ghost another direction
	public boolean moveGhostD() {

		ghostP[ghostX2][ghostY2] = 0;
		ghostP[ghostX2][ghostY2 + 1] = 2;
		ghostY = ghostY + 0.5;
		ghostY2 = ghostY2 + 1;
		if (checkGhost(1)) {
			ghost.setVerticalMovement(moveSpeedX / 2);
			return true;
		} else {
			ghostP[ghostX2][ghostY2] = 0;
			ghostP[ghostX2][ghostY2 - 1] = 2;
			ghostY = ghostY - 0.5;
			ghostY2 = ghostY2 - 1;
			return false;

		}//else

	}//moveGhostD

	//when the ghost wants to move left
	//true if it can move left
	//also moves it if it can
	//when it returns false the calling function will move the ghost another direction
	public boolean moveGhostL() {

		ghostP[ghostX2][ghostY2] = 0;
		ghostP[ghostX2 - 1][ghostY2] = 2;
		ghostX = ghostX - 0.5;
		ghostX2 = ghostX2 - 1;
		if (checkGhost(1)) {

			ghost.setHorizontalMovement(-moveSpeedX / 2);
			return true;
		} else {
			ghostP[ghostX2][ghostY2] = 0;
			ghostP[ghostX2 + 1][ghostY2] = 2;
			ghostX = ghostX + 0.5;
			ghostX2 = ghostX2 + 1;
			return false;
		}//else

	}//moveGhostL
	
	//when the ghost wants to move right
	//true if it can move right
	//also moves it if it can
	//when it returns false the calling function will move the ghost another direction
	public boolean moveGhostR() {
		
		//move the x,y position of the ghost on the various graphs i have
		ghostP[ghostX2][ghostY2] = 0;
		ghostP[ghostX2 + 1][ghostY2] = 2;
		ghostX = ghostX + 0.5;
		ghostX2 = ghostX2 + 1;
		
		//if it is not a illegal move, move it and return true
		if (checkGhost(1)) {
			
			ghost.setHorizontalMovement(moveSpeedX / 2);
			return true;
		} else {
			
			//if it is not a legal move revert the x,y to before
			ghostP[ghostX2][ghostY2] = 0;
			ghostP[ghostX2 - 1][ghostY2] = 2;
			ghostX = ghostX - 0.5;
			ghostX2 = ghostX2 - 1;
			return false;
		}//else
	}//moveGhostR
	
	//moves all the ghosts, this one moves it towards the player
	public void moveGhost() {
		
	
		
		//figures out what direction to move in, it causes the ghost to try to close the distance 
		//between the player and him
		double x = Math.abs((shipX) - (ghostX));
		double y = Math.abs((shipY) - (ghostY));
		
		//picks a direction to move in based on how whether it needs to move in the negative or positive direction of 
		//the axis it chose by comparing x and y. if the move is a illegal move it picks a direction perpendicular to the move
		//it meant to move in
		if (x >= y) {
			
			//if the ship'x position is greater move in positive x direction
			if (shipX >= ghostX) {
				
				//first move it in the direction it meant to go it
				if (moveGhostR() == false) {
					
					//if it can't compare the 2 perpendicular and move 
					//in the one that will bring it closer to the ship
					if (shipY < ghostY) {
						if (moveGhostU() == false) {
							if (moveGhostD() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD() == false) {
							if (moveGhostU() == false) {

							}//if
						}//if
					}//if
				
				}//if
			
			//if the ship's x position is less move in the negative x direction	
			} else if (shipX < ghostX) {
				
				//move in direction it's meant to, if it returns false continue
				if (moveGhostL() == false) {
					
					//figures out which y direction to move it
					if (shipY < ghostY) {
						if (moveGhostU() == false) {
							if (moveGhostD() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD() == false) {
							if (moveGhostU() == false) {

							}//if
						}//if
					}//else
				}//if
			}//else
			
		//if your first move should be on the y axis	
		} else {
			
			//figures out the direction
			if (shipY >= ghostY) {
				
				//does the regular move if it can
				if (moveGhostD() == false) {
					
					//figures out direction on x 
					if (shipX <= ghostX) {
						//move right
						if (moveGhostR() == false) {
							
							//move left
							if (moveGhostL() == false) {

							}//if
						}//if
					} else {
						
						//move left
						if (moveGhostL() == false) {
							
							//move right
							if (moveGhostR() == false) {

							}//if
						}//if
					}//else
				}//if
			
				
		    //basically same as above but goes up instead of down to start with
			} else if (shipY < ghostY) {
				if (moveGhostU() == false) {
					if (shipX < ghostX) {
						if (moveGhostR() == false) {
							if (moveGhostL() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL() == false) {
							if (moveGhostR() == false) {

							}//if
						}//if
					}//else
				}//if
			}//else
		}//else

	}//moveGhost
	
	//moves it away from the player
	//does almost the same thing as the method above
	public void moveGhostB() {
		double x = Math.abs((shipX) - (ghostX));
		double y = Math.abs((shipY) - (ghostY));

		if (x >= y) {

			if (shipX < ghostX) {

				if (moveGhostR() == false) {
					if (shipY < ghostY) {
						if (moveGhostU() == false) {
							if (moveGhostD() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD() == false) {
							if (moveGhostU() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipX >= ghostX) {
				if (moveGhostL() == false) {
					if (shipY < ghostY) {

						if (moveGhostU() == false) {
							if (moveGhostD() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD() == false) {
							if (moveGhostU() == false) {

							}//if
						}//if
					}//else
				}//if

			}//else
		} else {

			if (shipY <= ghostY) {

				if (moveGhostD() == false) {
					if (shipX <= ghostX) {
						if (moveGhostR() == false) {
							if (moveGhostL() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL() == false) {
							if (moveGhostR() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipY >= ghostY) {
				if (moveGhostU() == false) {
					if (shipX < ghostX) {
						if (moveGhostR() == false) {
							if (moveGhostL() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL() == false) {
							if (moveGhostR() == false) {

							}//if
						}//if
					}//else
				}//if
			}//else
		}//else

	}//moveGhostB
	
	//moves the second ghost
	public void moveGhost2() {
		//this ghost will prefer moving in the x direction
		double x = Math.abs((shipX) - (ghost2X));
		double y = Math.abs((shipY) - (ghost2Y));
		x = x + (int) (Math.random() * 5);
		
		
		if (x >= y) {
			//decides whether to go up or down
			if (shipX > ghost2X) {
				//calls for movements first right then either up or down
				if (moveGhostR2() == false) {
					if (shipY < ghost2Y) {
						if (moveGhostU2() == false) {
							if (moveGhostD2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD2() == false) {
							if (moveGhostU2() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipX < ghost2X) {

				if (moveGhostL2() == false) {
					if (shipY < ghost2Y) {
						if (moveGhostU2() == false) {
							if (moveGhostD2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD2() == false) {
							if (moveGhostU2() == false) {

							}//if
						}//if
					}//else
				}//if

			}//else
		} else {

			if (shipY > ghost2Y) {

				if (moveGhostD2() == false) {
					if (shipX <= ghost2X) {
						if (moveGhostR2() == false) {
							if (moveGhostL2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL2() == false) {
							if (moveGhostR2() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipY < ghost2Y) {
				if (moveGhostU2() == false) {
					if (shipX < ghost2X) {
						if (moveGhostR2() == false) {
							if (moveGhostL2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL2() == false) {
							if (moveGhostR2() == false) {

							}//if
						}//if
					}//else
				}//if
			}//else
		}//else

	}//moveGhostB

	//moves the pink ghost away from the ship when needed
	public void moveGhost2B() {
		
		double x = Math.abs((shipX) - (ghost2X));
		double y = Math.abs((shipY) - (ghost2Y));
		x = x + (int) (Math.random() * 5);

		if (x >= y) {

			if (shipX < ghost2X) {

				if (moveGhostR2() == false) {
					if (shipY < ghost2Y) {
						if (moveGhostU2() == false) {
							if (moveGhostD2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD2() == false) {
							if (moveGhostU2() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipX >= ghost2X) {

				if (moveGhostL2() == false) {
					if (shipY < ghost2Y) {
						if (moveGhostU2() == false) {
							if (moveGhostD2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostD2() == false) {
							if (moveGhostU2() == false) {

							}//if
						}//if
					}//else
				}//if

			}//else
		} else {

			if (shipY <= ghost2Y) {

				if (moveGhostD2() == false) {
					if (shipX <= ghost2X) {
						if (moveGhostR2() == false) {
							if (moveGhostL2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL2() == false) {
							if (moveGhostR2() == false) {

							}//if
						}//if
					}//else
				}//if

			} else if (shipY >= ghost2Y) {
				if (moveGhostU2() == false) {
					if (shipX < ghost2X) {
						if (moveGhostR2() == false) {
							if (moveGhostL2() == false) {

							}//if
						}//if
					} else {
						if (moveGhostL2() == false) {
							if (moveGhostR2() == false) {

							}//if
						}//if
					}//else
				}//if
			}//else
		}//else

	}//moveGhost2B
	
	//does the movements for the second ghost when it wants to go up
	public boolean moveGhostU2() {
		
		ghostP2[ghostX22][ghostY22] = 0;
		ghostP2[ghostX22][ghostY22 - 1] = 2;
		ghost2Y = ghost2Y - 0.5;
		ghostY22 = ghostY22 - 1;
		if (checkGhost(2) == true) {

			ghost2.setVerticalMovement(-moveSpeedX / 2);
			return true;
		} else {

			ghostP2[ghostX22][ghostY22] = 0;
			ghostP2[ghostX22][ghostY22 + 1] = 2;
			ghost2Y = ghost2Y + 0.5;
			ghostY22 = ghostY22 + 1;
			return false;

		}//else
	}//moveGhostU2
	
    //moves the second ghost down 
	public boolean moveGhostD2() {

		ghostP2[ghostX22][ghostY22] = 0;
		ghostP2[ghostX22][ghostY22 + 1] = 2;
		ghost2Y = ghost2Y + 0.5;
		ghostY22 = ghostY22 + 1;
		if (checkGhost(2)) {
			ghost2.setVerticalMovement(moveSpeedX / 2);
			return true;
		} else {
			ghostP2[ghostX22][ghostY22] = 0;
			ghostP2[ghostX22][ghostY22 - 1] = 2;
			ghost2Y = ghost2Y - 0.5;
			ghostY22 = ghostY22 - 1;
			return false;

		}//else
	}//moveGhostD2
	
	//moves the second ghost left
	public boolean moveGhostL2() {

		ghostP2[ghostX22][ghostY22] = 0;
		ghostP2[ghostX22 - 1][ghostY22] = 2;
		ghost2X = ghost2X - 0.5;
		ghostX22 = ghostX22 - 1;
		if (checkGhost(2)) {

			ghost2.setHorizontalMovement(-moveSpeedX / 2);
			return true;
		} else {
			ghostP2[ghostX22][ghostY22] = 0;
			ghostP2[ghostX22 + 1][ghostY22] = 2;
			ghost2X = ghost2X + 0.5;
			ghostX22 = ghostX22 + 1;
			return false;
			// createBuffer();
		}//else

	}//moveGhostL2
	
	//moves the second ghost right
	public boolean moveGhostR2() {

		ghostP2[ghostX22][ghostY22] = 0;
		ghostP2[ghostX22 + 1][ghostY22] = 2;
		ghost2X = ghost2X + 0.5;
		ghostX22 = ghostX22 + 1;
		if (checkGhost(2)) {

			ghost2.setHorizontalMovement(moveSpeedX / 2);
			return true;
		} else {
			ghostP2[ghostX22][ghostY22] = 0;
			ghostP2[ghostX22 - 1][ghostY22] = 2;
			ghost2X = ghost2X - 0.5;
			ghostX22 = ghostX22 - 1;
			// createBuffer();
			return false;
		}//else
	}//moveGhostR2
	public void loadMenu(){
		//menu images
		try {
			menuI = ImageIO.read(getClass().getResourceAsStream("/sprites/menu.png"));
			instruction = ImageIO.read(getClass().getResourceAsStream("/sprites/instruction.png"));
			deadS = ImageIO.read(getClass().getResourceAsStream("/sprites/deadS.png"));
			winS = ImageIO.read(getClass().getResourceAsStream("/sprites/winS.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		
		// instantiate this object
		new Game();
	} // main

} // Game

