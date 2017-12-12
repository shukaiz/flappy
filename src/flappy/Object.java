package flappy;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.Toolkit;

import hsa_ufa.Console;

public class Object {

	public int x;
	public int y;
	public int speed; // Accelerating speed of the object.
	public int timer = 0; // Bird sound effect timer.
	private static int frame = 0; // Birds are formed of 4 seperate images.
	private static int color; // Bird color. (0=yellow,1=red,2=blue)
	private static Image pipeUp;
	private static Image pipeDown;
	private static Image base;
	private static Image[] birdYellow = new Image[4];
	private static Image[] birdRed = new Image[4];
	private static Image[] birdBlue = new Image[4];
	private static AudioClip wing;

	public void reset() { // Used when game restarts.

		timer = 0;
		frame = 0;

	}

	public void initValues(int init_x, int init_y, int init_speed) {

		loadResources(); // Load all the picture and audio files.

		x = init_x;
		y = init_y;

		speed = init_speed;

	}

	public void drawPipes(Console c) {

		/*
		 * Draw the two pipes. One at top and one at bottom. Two pipes shares
		 * one set f coordinate.
		 */

		synchronized (c) {

			c.drawImage(pipeUp, x, y - 320);
			c.drawImage(pipeDown, x, y + 120);

		}

	}

	public void drawBase(Console c) { // Draw the base(ground).

		c.drawImage(base, x, 400);

	}

	public void drawBird(Console c, int timer, boolean gameOver) {

		if (frame == 3)
			frame = 0;
		else if ((timer % 7 == 0) && (gameOver == false))
			frame++;

		synchronized (c) {

			switch (color) { // Draw different colours of birds.
			case 0:
				c.drawImage(birdYellow[frame], x, y);
				break;
			case 1:
				c.drawImage(birdRed[frame], x, y);
				break;
			case 2:
				c.drawImage(birdBlue[frame], x, y);
				break;

			}

		}

	}

	public void jump(Object birdYellow) {

		if (timer == 0) // Play the sound effect when the timer
						// is 0. Commented in Game.java
			wing.play();

		timer++; // Wing sound effect timer.

		speed = -3; // Set the speed to negative
					// as jumping.

	}

	private static void loadResources() {

		/*
		 * Arrays are being used to load multiple files in one folder at the
		 * same time. Files are renamed into (1.png,2.png,etc,) in order to load
		 * them in a loop.
		 */

		for (int i = 1; i < 5; i++) {
			birdYellow[i - 1] = Toolkit.getDefaultToolkit().getImage("lib/bird/yellow/" + i + ".png");
			birdRed[i - 1] = Toolkit.getDefaultToolkit().getImage("lib/bird/red/" + i + ".png");
			birdBlue[i - 1] = Toolkit.getDefaultToolkit().getImage("lib/bird/blue/" + i + ".png");
		}

		base = Toolkit.getDefaultToolkit().getImage("lib/background/base.png");
		pipeUp = Toolkit.getDefaultToolkit().getImage("lib/pipes/up.png");

		pipeDown = Toolkit.getDefaultToolkit().getImage("lib/pipes/down.png");

		wing = Applet.newAudioClip(Object.class.getResource("wing.wav"));

		color = Game.random.nextInt(
				3); /*
					 * Generate the color of the bird using the random number
					 * generator which defined in Game.java
					 */

	}

}
