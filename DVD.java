public class DVD {

	// Fields:

	private String title; // Title of this DVD
	private String rating; // Rating of this DVD
	private int runningTime; // Running time of this DVD in minutes

	public DVD(String dvdTitle, String dvdRating, int dvdRunningTime) {
		this.title = dvdTitle;
		this.rating = dvdRating;
		this.runningTime = dvdRunningTime;
	}

	public String getTitle() {
		return this.title;
	}

	public String getRating() {
		return this.rating;
	}

	public int getRunningTime() {
		return this.runningTime;
	}

	public void setTitle(String newTitle) {
		this.title = newTitle;
	}

	public void setRating(String newRating) {
		this.rating = newRating;
	}

	public void setRunningTime(int newRunningTime) {
		this.runningTime = newRunningTime;
	}

	public String toString() {
		// update so only title shows in scroll pane
		return this.getTitle();
	}

	public String toString2() {
		String mergedString = this.title + "," + this.rating + "," + this.runningTime;
		return mergedString;

	}
}
