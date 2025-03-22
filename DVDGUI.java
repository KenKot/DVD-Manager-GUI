import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an implementation of DVDUserInterface that uses JOptionPane to
 * display the menu of command choices.
 */

public class DVDGUI implements DVDUserInterface {
	private DVDCollection dvdlist;
	
	private int ROWS;
	private int COLS;

	public static String getFilename() {
		String filename = JOptionPane.showInputDialog("Enter the filename");
		return filename;
	}

	public DVDGUI(DVDCollection dl) {
		dvdlist = dl;
		
		this.ROWS = 5;
		this.COLS = 3;

	}

	public void processCommands() {
		createWindow();
		return;

//		 String[] commands = {"Add/Modify DVD",
//				 	"Remove DVD",
//				 	"Get DVDs By Rating",
//				 	"Get Total Running Time",
//				 	"Exit and Save"};
//		 
//		 int choice;
//		 
//		 do {
//			
//
//			 choice = JOptionPane.showOptionDialog(null,
//					 currMessage, 
//					 "DVD Collection", 
//					 JOptionPane.YES_NO_CANCEL_OPTION, 
//					 JOptionPane.QUESTION_MESSAGE, 
//					 null, 
//					 commands,
//					 commands[commands.length - 1]);
//		 
//			 switch (choice) {
//			 	case 0: doAddOrModifyDVD(); break;
//			 	case 1: doRemoveDVD(); break;
//			 	case 2: doGetDVDsByRating(); break;
//			 	case 3: doGetTotalRunningTime(); break;
//			 	case 4: doSave(); break;
//			 	default:  // do nothing
//			 }
//			 
//		 } while (choice != commands.length-1);
//		 System.exit(0);
	}

//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++

//	private static void createWindow() {
	private void createWindow() {
		JFrame frame = new JFrame("GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI(frame);
		frame.setSize(560, 200);
		frame.setLocationRelativeTo(null); // Center on screen
		frame.setVisible(true); // make visible
	}

//	private static void createUI(final JFrame frame) {
	private void createUI(final JFrame frame) {
		JPanel panel = new JPanel();
		LayoutManager layout = new FlowLayout();
		panel.setLayout(layout);

		JButton addDVDButton = new JButton("Add DVD");
		JButton getRuntimeButton = new JButton("Get runtime");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);

// +++++++++++++++++++++++++++++++++++++++++++++++
//		ACTION LISTENERS:
// +++++++++++++++++++++++++++++++++++++++++++++++

		getRuntimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel runtimePanel = new JPanel(new GridLayout(ROWS, COLS)); 
				runtimePanel.add(new JLabel("Runtime: " + dvdlist.getTotalRunningTime()));
				JOptionPane.showMessageDialog(frame, runtimePanel );
			}
		});

		addDVDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String errorMsg = "";
				JTextField title = new JTextField(10);
				JTextField rating = new JTextField(10);
				JTextField runtime = new JTextField(10);

				while (true) {

					JPanel newDVDPanel = new JPanel(new GridLayout(ROWS, COLS)); // change values
					// JPanel newDVDPanel = new JPanel(new FlowLayout()); // change values

					newDVDPanel.add(new javax.swing.JLabel("Title: "));
					newDVDPanel.add(title);
					newDVDPanel.add(new javax.swing.JLabel("Rating (G, PG, PG-13, R): "));
					newDVDPanel.add(rating);
					newDVDPanel.add(new javax.swing.JLabel("Run Time (minutes): "));
					newDVDPanel.add(runtime);

//					newDVDPanel.add(new javax.swing.JLabel("Error Message (to delete): " + errorMsg + "\n")); // TODO: Remove
					newDVDPanel.add(new javax.swing.JLabel(errorMsg)); // TODO: Remove
																		// "ERROR
																		// MESSAGE" PART

					int result = JOptionPane.showConfirmDialog(frame, newDVDPanel, "Add New DVD",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
						break;
					}

					if (result == JOptionPane.OK_OPTION) {
						String titleText = title.getText().toUpperCase();
						String ratingText = rating.getText().toUpperCase();
						String runtimeText = runtime.getText().toUpperCase();

						if (titleText.length() == 0) {
							errorMsg = "Title can't be empty";
							continue;
						}
						if (!DVDCollection.isValidRating(ratingText)) {
							errorMsg = "Enter a valid rating";
							continue;
						}

						if (!DVDCollection.isValidPositiveNumber(runtimeText)) {
							errorMsg = "Enter a valid run time";
							continue;
						}

						String message = "Succesfully Added " + titleText;
						JOptionPane.showMessageDialog(frame, message);
//						cancelButton.setEnabled(true);
//						DVDGUI.this.dvdlist.addOrModifyDVD(titleText, ratingText, runtimeText);
						dvdlist.addOrModifyDVD(titleText, ratingText, runtimeText);

//						TO DO - TO DELETE
//						System.out.println("Adding/Modifying: " + title + "," + rating + "," + time);
						System.out.println(DVDGUI.this.dvdlist);

						dvdlist.save();
						break;
					}

				}
			}
		});

		panel.add(addDVDButton);
		panel.add(getRuntimeButton);

		frame.getContentPane().add(panel, BorderLayout.CENTER);

	}

//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doRemoveDVD() {
		String title;
		String errorMsg = " ";

		// Request the title
		while (true) {
			title = JOptionPane.showInputDialog("Enter title\n" + errorMsg);
			if (title == null) {
				return; // dialog was cancelled
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
				return; // dialog was cancelled
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

	private void doSave() {
		dvdlist.save();
	}

}
