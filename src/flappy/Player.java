package flappy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/*
 * The purpose of this class is to deal with the score of the player. 
 * And the reason of using a seperate class is that it leaves a 
 * space for names or other variables which could be added in the 
 * future updates.
 */

public class Player {

	public int score;
	public int highScore;

	// Read the score from the file.
	public void readScore() {

		File inFile = new File("lib/save.txt");

		try {

			Scanner freader = new Scanner(inFile);

			highScore = freader.nextInt();

			freader.close();

		} catch (IOException e) {

			System.err.println(e);
			System.exit(1);

		}

	}

	public void writeScore() {

		// Update the high score only if a new high score is made.
		if (score > highScore) {

			highScore = score;

			File outFile = new File("lib/save.txt");

			try {

				// File writer will overwrite the previous score.
				BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
				writer.write("" + score);
				writer.close();

			} catch (IOException e) {

				System.err.println(e);
				System.exit(1);

			}

		}

	}

}
