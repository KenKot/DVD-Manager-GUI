import java.io.*;
import java.util.Scanner;

public class DVDCollection {
	// Data fields

	/** The current number of DVDs in the array */
	private int numdvds;

	/** The array to contain the DVDs */
	private DVD[] dvdArray;

	/** The name of the data file that contains dvd data */
	private String sourceName;

	/**
	 * Boolean flag to indicate whether the DVD collection was modified since it was
	 * last saved.
	 */
	private boolean modified;

	/**
	 * Constructs an empty directory as an array with an initial capacity of 7. When
	 * we try to insert into a full array, we will double the size of the array
	 * first.
	 */
	public DVDCollection() {
		numdvds = 0;
		dvdArray = new DVD[7];
	}

	public String toString() {
		// Return a string containing all the DVDs in the
		// order they are stored in the array along with
		// the values for numdvds and the length of the array.
		// See homework instructions for proper format.

		// What DVD.toString() returns: this.title + "," + this.rating + "," +
		// this.runningTime;

		String mergedString = "numdvds = " + this.numdvds + "\n" + "dvdArray.length = " + this.dvdArray.length + "\n";

		for (int i = 0; i < this.numdvds; ++i) {
			mergedString += "dvdArray[" + i + "] = " + dvdArray[i].toString() + "min\n";
		}

		return mergedString;
	}

	public void addOrModifyDVD(String title, String rating, String runningTime) {
		// NOTE: Be careful. Running time is a string here
		// since the user might enter non-digits when prompted.
		// If the array is full and a new DVD needs to be added,
		// double the size of the array first.

		// first check if rating and runningTime are valid
		if (title == null) {
			System.out.println("Enter a title");
			return;
		}

		if (!isValidPositiveNumber(runningTime)) {
			System.out.println("Enter a valid run time");
			return;
		}

		if (!isValidRating(rating)) {
			System.out.println("Enter a valid rating");
			return;
		}

		int runningTimeInteger = Integer.parseInt(runningTime);

		this.modified = true;

		// Check it it already exists (we don't need to add another DVD)
		for (int i = 0; i < numdvds; ++i) {
			DVD curr = dvdArray[i];
			if (title.equals(curr.getTitle())) {
				curr.setRating(rating);
				curr.setRunningTime(runningTimeInteger);
				return;
			}
		}

		// DVD title doesn't exist yet, so we need to create a new one
		// Check if array needs to grow
		if (this.numdvds >= dvdArray.length) {
			DVD[] newDVDArray = new DVD[dvdArray.length * 2];

			// Copy old array into new/bigger array
			for (int i = 0; i < dvdArray.length; ++i) {
				// Deep Copy Version:
				// DVD copiedDVD = new DVD(dvdArray[i].getTitle(), dvdArray[i].getRating(),
				// dvdArray[i].getRunningTime());
				// newDVDArray[i] = copiedDVD;

				// Shallow Copy Version:
				newDVDArray[i] = dvdArray[i];
			}

			dvdArray = newDVDArray;
		}

		DVD newDVD = new DVD(title, rating, runningTimeInteger);

		if (numdvds == 0) {
			dvdArray[0] = newDVD;
			++numdvds;
			return;
		}

		for (int i = numdvds - 1; i >= 0; --i) {
			// first array element's title >= than new title, we insert
			if (title.compareTo(dvdArray[i].getTitle()) >= 0) { // Asks: Does 'title' come after alphabetically?
				dvdArray[i + 1] = newDVD;
				++numdvds;
				return;
			} else {
				dvdArray[i + 1] = dvdArray[i];

				// edge case if we make it to the beginning
				if (i == 0) {
					dvdArray[0] = newDVD;
					++numdvds;
					return;
				}
			}

		}

	}

	public void removeDVD(String title) {
		boolean shiftElements = false;
		int dvdCount = numdvds; // In case numdvds changes and this effects a loop(not needed)

		if (dvdCount == 0)
			return;

		this.modified = true;

		if (dvdCount == 1) {
			if (title.equals(dvdArray[0].getTitle())) {
				dvdArray[0] = null;
				--numdvds;
				return;
			}
		}

		// Once we find DVD to remove, next element on top of it and slide all remaining
		// 1 over too
		for (int i = 0; i < dvdCount - 1; ++i) {
			if (title.equals(dvdArray[i].getTitle())) {
				shiftElements = true;
			}

			if (shiftElements) {
				dvdArray[i] = dvdArray[i + 1];
			}
		}

		if (shiftElements || (title.equals(dvdArray[dvdCount - 1].getTitle()))) {
			dvdArray[dvdCount - 1] = null;
			--numdvds;
		}
	}

	public String getDVDsByRating(String rating) {
		String mergedString = "";

		for (int i = 0; i < numdvds; ++i) {
			if (rating.equals(dvdArray[i].getRating())) {
				mergedString += dvdArray[i].toString() + "min";
			}
		}
		
		if (mergedString.length() == 0) return "No DVDs found";

		return mergedString;

	}

	public int getTotalRunningTime() {
		int totalRunningTime = 0;

		for (int i = 0; i < numdvds; ++i) {
			totalRunningTime += dvdArray[i].getRunningTime();
		}

		return totalRunningTime;
	}

	public void loadData(String filename) {
		File file = new File(filename);
		this.sourceName = filename;
		
		if (!file.exists())
			return;


		try {
			Scanner sc = new Scanner(file);

			while (sc.hasNextLine()) {
				String currLine = sc.nextLine();

				if (!isValidInputLine(currLine)) {
					sc.close();
					return;
				}

				String[] splitValues = currLine.split(",");
				String title = splitValues[0];
				String rating = splitValues[1];
				String runningTime = splitValues[2];

				if (!isValidRating(rating) || !isValidPositiveNumber(runningTime) || title == null) {
					sc.close();
					return;
				}

				addOrModifyDVD(title, rating, runningTime);

			}
			sc.close();

		} catch (Exception e) {
			System.out.println(e + " - loading error");
		}

	}

	public void save() {
		if (!this.modified)
			return;

		File file = new File(this.sourceName);

		try {
				file.createNewFile();
				FileWriter myWriter = new FileWriter(this.sourceName);

				for (int i = 0; i < numdvds; ++i) {
					myWriter.write(dvdArray[i].toString());
					myWriter.write("\n");
				}

				myWriter.close();
				this.modified = false;
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	// Additional private helper methods go here:

	static private boolean isValidInputLine(String input) {
		if (!input.matches("^.+,.+,.+$")) {
			return false;
		}

		return true;
	}

	static private boolean isValidPositiveNumber(String input) {
		// "0001" into parseInt later will yield 1
		if (input == null)
			return false;
		if (input.length() <= 0)
			return false;

		for (char digit : input.toCharArray()) {
			if (digit > '9' || digit < '0')
				return false;
		}

		return true;
	}

	static private boolean isValidRating(String input) {
		if (input == null)
			return false;
		if (input.equals("G"))
			return true;
		if (input.equals("PG"))
			return true;
		if (input.equals("PG-13"))
			return true;
		if (input.equals("R"))
			return true;
		return false;
	}

}
