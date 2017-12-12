package flappy;

import java.awt.*;
import java.util.Random;
import hsa_ufa.Console;
import java.applet.AudioClip;
import java.applet.Applet;

public class Game {

	// Booleans are used to indicate game status.
	private static boolean gameOver = false;
	private static boolean gameStarted = false;
	private static boolean scrollStarted = false;

	// Random number generator.
	protected static Random random = new Random();

	private static Console c = new Console(288, 512, 24, "Flappy Bird");

	// Define two images for different digits on score (numbers are images).
	private static Image ones;
	private static Image tens;

	public static void main(String[] args) throws InterruptedException {

		c.enableMouse();

		// Define the bird.
		Object bird = new Object();

		// Define two bases which keep rotating.
		Object b1 = new Object();
		Object b2 = new Object();

		// Define two sets of pipes.
		Object p1 = new Object();
		Object p2 = new Object();

		// Define the default player of the game and read the high score from
		// the save file.
		Player player = new Player();
		player.readScore();

		// Initial value of the bird when game starts(x,y,speed).
		bird.initValues(60, 210, 0);

		// Initial values for bases. (b1/b2 are bases)
		b1.initValues(0, 400, 1);
		b2.initValues(336, 400, 1);

		// Initial values of pipes with randomly generated y coordinate.
		p1.initValues(288, random.nextInt(230) + 20, 1);
		p2.initValues(504, random.nextInt(230) + 20, 1);

		// Load the audio clips from the package.
		AudioClip die = Applet.newAudioClip(Game.class.getResource("die.wav"));
		AudioClip hitAudio = Applet.newAudioClip(Game.class.getResource("hit.wav"));
		AudioClip point = Applet.newAudioClip(Game.class.getResource("point.wav"));

		// Load the image files from the project folder.
		Image board = Toolkit.getDefaultToolkit().getImage("lib/background/scoreboard.png");
		Image replay = Toolkit.getDefaultToolkit().getImage("lib/background/replay.png");
		Image message = Toolkit.getDefaultToolkit().getImage("lib/background/message.png");
		Image backgroundDay = Toolkit.getDefaultToolkit().getImage("lib/background/day.png");
		Image backgroundNight = Toolkit.getDefaultToolkit().getImage("lib/background/night.png");

		int t = 0; // General timer.
		int day = random.nextInt(2); // Randomly generate day/night.
										// (Day:0, Night:1)

		boolean hit = false; // Boolean for if bird hits barriers.

		// --------START OF THE MAIN WHILE LOOP--------

		while (true) {

			synchronized (c) {

				// Draw the background (Day:0, Night:1).
				switch (day) {
				case 0:
					c.drawImage(backgroundDay, 0, 0);
					break;
				case 1:
					c.drawImage(backgroundNight, 0, 0);
					break;
				}

				// Draw the two pipes and bases.
				p1.drawPipes(c);
				p2.drawPipes(c);
				b1.drawBase(c);
				b2.drawBase(c);

				/*
				 * Draw the bird. Passing the timer and game over boolean to
				 * decide which frame of the bird needs to be drew and stop
				 * changing frames if the game is over.
				 */
				bird.drawBird(c, t, gameOver);

				// Draw the word (image) "Flappy Bird" and instruction.
				if (gameStarted == false)
					c.drawImage(message, 53, 30);

				// Printing the score when the game is started.
				if ((gameStarted == true) && (gameOver == false))
					drawScore(player);

				// Draw the score board(image) and final scores.
				if (gameOver == true) {
					c.drawImage(board, 26, 60);
					c.drawImage(replay, 90, 258);
					drawResult(player);
				}

			}

			Thread.sleep(10); // Sleep

			t++; // Main timer.

			/*
			 * Timer for pipes to be generated at the start. Pipes need to wait
			 * for 1 second before start moving scrolling left after the game
			 * started.
			 */
			if ((gameStarted == true) && (scrollStarted == false))
				p1.timer++;

			// Click or press to start the game.
			if ((c.isKeyDown(' ')) && (gameStarted == false))
				gameStarted = true;

			// Click or press space to jump.
			if ((t % 5 == 0) && (c.isKeyDown(' ')) && (bird.y >= 0) && (hit == false) && (gameOver == false)) //CHANGES!!!
				bird.jump(bird);

			/*
			 * Wing sound effect timer resets. Used because the high refreshing
			 * rate (100fps) causes the audio keep replaying as the space bar is
			 * being pressed which results in no sound plays at all. This timer
			 * limits the audio clip can only played once in a short amount of
			 * time.
			 */
			if (bird.speed > -3)
				bird.timer = 0;

			// Gravity acceleration (y++). The speed of the bird keep increasing
			// to create gravity.
			if ((t % 15 == 0) && (bird.speed <= 4) && (gameOver == false) && (gameStarted == true))
				bird.speed += 2;
			if ((gameOver == false) && (gameStarted == true))
				bird.y += bird.speed;

			// Scrolls the background when game is continuing.
			if ((hit == false) && (gameOver == false))
				backScrolls(b1, b2, p1, p2, player, point);

			// Check for collision between the bird and four pipes.
			if ((collisionDetect(bird.x, p1.x, bird.y, p1.y - 320, 34, 52, 24, 320)
					|| collisionDetect(bird.x, p2.x, bird.y, p2.y - 320, 34, 52, 24, 320)
					|| collisionDetect(bird.x, p1.x, bird.y, p1.y + 120, 34, 52, 24, 320)
					|| collisionDetect(bird.x, p2.x, bird.y, p2.y + 120, 34, 52, 24, 320)) && hit == false) {
				hit = true;
				hitAudio.play();
			}

			// Activate game over if bird hits ground (This piece of code will
			// only be executed once).
			if (bird.y >= 378) {
				gameOver = true;
				bird.y = 377;
				die.play();
				player.writeScore();
			}

			// Pipes start generating after 1 second.
			if (p1.timer == 100)
				scrollStarted = true;

			// Reset all variables when restarting the game. size of replay
			// button: 115*70
			if ((c.getMouseButton(0)) && (c.getMouseX() >= 90) && (c.getMouseX() <= 205) && (c.getMouseY() >= 258)
					&& (c.getMouseX() <= 328) && (gameOver == true)) {

				player.readScore();
				bird.initValues(60, 210, 0);
				b1.initValues(0, 400, 1);
				b2.initValues(336, 400, 1);
				p1.initValues(288, random.nextInt(230) + 20, 1);
				p2.initValues(504, random.nextInt(230) + 20, 1);
				t = 0;
				day = random.nextInt(2);
				hit = false;
				gameStarted = false;
				scrollStarted = false;
				gameOver = false;
				bird.reset();
				p1.reset();
				player.score = 0;

			}

		} // ----WHILE LOOP ENDS----

	} // ----MAIN CLASS ENDS----

	private static void drawScore(Player player) {

		/*
		 * Print the score on different coordinate if it contains more than one
		 * figure. As the font of the number are actually pictures, algorithms
		 * are used in this method as the score which is one variable needs to
		 * be seperated into multiple if it is more than 10 which contains more
		 * than one figure.
		 */

		// One digit being drew if score is less than ten.
		if (player.score < 10) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score + ".png");
			c.drawImage(ones, 135, 50);
			return;
		}

		/*
		 * Draw the score if it contains two figures (10-19). Note: coordinate
		 * of the number is different as "1" is not monospaced. The algorithm
		 * here is that using the remainder after the score is devided by 10 as
		 * the ones digit and uses score devided by 10 as tens digit. The number
		 * will not be rounded when doing division as the variable type is
		 * integer.
		 */

		else if (player.score < 20) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score / 10 + ".png");
			c.drawImage(tens, 120, 50);
			c.drawImage(ones, 137, 50);
			return;
		}

		// Draw the score if it contains two figures and aboves 20.
		else if (player.score < 100) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score / 10 + ".png");
			c.drawImage(tens, 120, 50);
			c.drawImage(ones, 145, 50);
			return;
		}

	}

	private static void drawResult(Player player) {

		/*
		 * Draw the final score and metal when the game is finished. Algorithms
		 * used here are the same as the "drawScore" method.
		 */

		// Size of the number: 24*36; Number 1: 16*36 (Reference)

		// ------Score of the session------
		if (player.score < 10) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score + ".png");
			if (player.score == 1)
				c.drawImage(ones, 220, 166, 8, 18);
			else
				c.drawImage(ones, 220, 166, 12, 18);
		}

		if ((player.score >= 10) && (player.score < 20)) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/1.png");
			if (player.score % 10 == 1)
				c.drawImage(ones, 220, 166, 8, 18);
			else
				c.drawImage(ones, 220, 166, 12, 18);
			c.drawImage(tens, 210, 166, 8, 18);
		}

		if ((player.score >= 20) && (player.score < 100)) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.score / 10 + ".png");
			if (player.score % 10 == 1)
				c.drawImage(ones, 220, 166, 8, 18);
			else
				c.drawImage(ones, 220, 166, 12, 18);
			c.drawImage(tens, 206, 166, 12, 18);
		}

		// ------Best score------
		if (player.highScore < 10) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.highScore + ".png");
			if (player.highScore == 1)
				c.drawImage(ones, 220, 206, 8, 18);
			else
				c.drawImage(ones, 220, 206, 12, 18);
		}

		if ((player.highScore >= 10) && (player.highScore < 20)) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.highScore % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/1.png");
			if (player.highScore % 10 == 1)
				c.drawImage(ones, 220, 206, 8, 18);
			else
				c.drawImage(ones, 220, 206, 12, 18);
			c.drawImage(tens, 210, 206, 8, 18);
		}

		if ((player.highScore >= 20) && (player.highScore < 100)) {
			ones = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.highScore % 10 + ".png");
			tens = Toolkit.getDefaultToolkit().getImage("lib/font/" + player.highScore / 10 + ".png");
			if (player.highScore % 10 == 1)
				c.drawImage(ones, 220, 206, 8, 18);
			else
				c.drawImage(ones, 220, 206, 12, 18);
			c.drawImage(tens, 206, 206, 12, 18);
		}

		// ------Metals------
		Image bronze = Toolkit.getDefaultToolkit().getImage("lib/metals/bronze.png");
		Image gold = Toolkit.getDefaultToolkit().getImage("lib/metals/gold.png");
		Image silver = Toolkit.getDefaultToolkit().getImage("lib/metals/silver.png");
		Image platinum = Toolkit.getDefaultToolkit().getImage("lib/metals/platinum.png");

		if (player.score < 10)
			c.drawImage(bronze, 58, 174);
		else if (player.score < 20)
			c.drawImage(silver, 58, 174);
		else if (player.score < 30)
			c.drawImage(gold, 58, 174);
		else
			c.drawImage(platinum, 58, 174);

	}

	private static void backScrolls(Object b1, Object b2, Object p1, Object p2, Player player, AudioClip point) {

		// Background(bases) scrolls.
		b1.x -= 2;
		b2.x -= 2;

		// Scroll the pipes when game is started.
		if (scrollStarted == true) {
			p1.x -= 2;
			p2.x -= 2;
		}

		// Regenerate bases. Moves the one on the left to the right after it's
		// completely out of boundry. (length of the base is 336)
		if (b1.x <= -336)
			b1.x = b2.x + 336;
		if (b2.x <= -336)
			b2.x = b1.x + 336;

		// Regenerate pipes.
		if (p1.x <= -52) {
			p1.x = p2.x + 196;
			p1.y = random.nextInt(250) + 20;
		}
		if (p2.x <= -52) {
			p2.x = p1.x + 196;
			p2.y = random.nextInt(250) + 20;
		}

		// Add score if bird passes a pipe.
		if ((p1.x == 52) || (p2.x == 52)) {
			player.score++;
			point.play();
		}

	}

	// Collision detection.
	private static boolean collisionDetect(int x1, int x2, int y1, int y2, int w1, int w2, int h1, int h2) {

		if ((x1 < x2 + w2) && (y1 < y2 + h2) && (x2 < x1 + w1) && (y2 < y1 + h1))
			return true;

		return false;

		// Size of the sprite:
		// Bird: 34 * 24
		// Pipe: 52 * 320

	}

}
