import javax.swing.*;

/**
 *  This class is an implementation of DVDUserInterface
 *  that uses JOptionPane to display the menu of command choices. 
 */

public class DVDGUI implements DVDUserInterface {
	 
//	 static private int count = 0;
	 private DVDCollection dvdlist;
	 
	 private String currMessage; 
	 
	 public static String getFilename() {
		String filename = JOptionPane.showInputDialog("Enter the filename");
//		if (title == null) {
//			return;		// dialog was cancelled
//		}
//		title = title.toUpperCase();
		 
		 return filename;
	 }
	 
	 public DVDGUI(DVDCollection dl)
	 {
		 dvdlist = dl;
		 
		 currMessage = "Select a Command";
	 }
	 
	 public void processCommands()
	 {
		 String[] commands = {"Add/Modify DVD",
				 	"Remove DVD",
				 	"Get DVDs By Rating",
				 	"Get Total Running Time",
				 	"Exit and Save"};
		 
		 int choice;
		 
		 do {
			
//			 String temp = String.valueOf(++count);

			 choice = JOptionPane.showOptionDialog(null,
//					 "Select a command", 
//					 temp, 
					 currMessage, 
					 "DVD Collection", 
					 JOptionPane.YES_NO_CANCEL_OPTION, 
					 JOptionPane.QUESTION_MESSAGE, 
					 null, 
					 commands,
					 commands[commands.length - 1]);
		 
			 switch (choice) {
			 	case 0: doAddOrModifyDVD(); break;
			 	case 1: doRemoveDVD(); break;
			 	case 2: doGetDVDsByRating(); break;
			 	case 3: doGetTotalRunningTime(); break;
			 	case 4: doSave(); break;
			 	default:  // do nothing
			 }
			 
		 } while (choice != commands.length-1);
		 System.exit(0);
	 }

	private void doAddOrModifyDVD() {
		String errorMsg = " ";
		String title;
		String rating;
		String time;

		// Request the title
		while (true) {
			title = JOptionPane.showInputDialog("Enter title\n" + errorMsg);
			if (title == null) { 
				System.out.println("dialogue was canceled");
				return;		// dialog was cancelled
			}

			if (title.length() == 0) {
				errorMsg = "Title can't be empty";
				continue;
			}
			
			
			System.out.println("title: " + title);
			System.out.println("length of title: " + title.length());
			
			title = title.toUpperCase();
			
			errorMsg = " ";
			break;
			
		}
		
		
		// Request the rating
		while (true) {
			rating = JOptionPane.showInputDialog("Enter rating for " + title + "\n" + errorMsg);
			if (rating == null) {
				return;		// dialog was cancelled
			}
			rating = rating.toUpperCase();
			
			if (!DVDCollection.isValidRating(rating)) {
				errorMsg = "Enter a valid rating";
				continue;
			}
			
			errorMsg = " ";
			break;
		}
			
		
		// Request the running time
		while (true) {
			time = JOptionPane.showInputDialog("Enter running time for " + title + "\n" + errorMsg);
			if (time == null) {
				return;
			}
		
			if (!DVDCollection.isValidPositiveNumber(time)) {
				errorMsg = "Enter a valid run time";
				continue;
			}
			
			errorMsg = " ";
			break;
		}
		
		// Add or modify the DVD (assuming the rating and time are valid
		dvdlist.addOrModifyDVD(title, rating, time);
		
		// Display current collection to the console for debugging
		System.out.println("Adding/Modifying: " + title + "," + rating + "," + time);
		System.out.println(dvdlist);
		
		this.currMessage = "Successfully Added/Updated the movie: " + title + "," + rating + "," + time;
		
	}
	
	private void doRemoveDVD() {
		String title;
		String errorMsg = " ";
		
		// Request the title
		while (true) {
			title = JOptionPane.showInputDialog("Enter title\n" + errorMsg);
			if (title == null) {
				return;		// dialog was cancelled
			}
			title = title.toUpperCase();
			
			if (!dvdlist.containsTitle(title)) {
				errorMsg = "That title doesn't exist";
				continue;
			}
			
			errorMsg = " ";
			break;
		}
		
		// Remove the matching DVD if found
		dvdlist.removeDVD(title);
                
        // Display current collection to the console for debugging
        System.out.println("Removing: " + title);
        System.out.println(dvdlist);
        
        currMessage = title + " has been removed from your collection";

	}
	
	private void doGetDVDsByRating() {
		String rating;
		String errorMsg = " ";
		
		// Request the rating
		while (true) {
			rating = JOptionPane.showInputDialog("Enter rating\n" + errorMsg);
			if (rating == null) {
				return;		// dialog was cancelled
			}
			rating = rating.toUpperCase();
			
			
			if (!DVDCollection.isValidRating(rating)) {
				errorMsg = "Enter a valid rating";
				continue;
			}
			
			errorMsg = " ";
			break;
			
		}
		
		String results = dvdlist.getDVDsByRating(rating);
		System.out.println("DVDs with rating " + rating);
		System.out.println(results);
		
		
		this.currMessage = results;
	}

        private void doGetTotalRunningTime() {
                 
                int total = dvdlist.getTotalRunningTime();
                this.currMessage = "Total Running Time of all DVDs is: " + total + " minutes.";
                
                System.out.println(total);
                
        }

	private void doSave() {
		dvdlist.save();
	}
		
}
