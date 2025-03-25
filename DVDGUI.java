import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class DVDGUI implements DVDUserInterface {
	private DVDCollection dvdlist;
	private DVD chosenDVD;
	private JLabel dvdImage;
	private String ratingFilter; // G, PG, PG-13, R, ""(for all ratings)

	private int ROWS;
	private int COLS;
	private String DEFAULT_IMAGE;
	private String IMAGE_EXT;
	private String IMAGE_FOLDER;

	public static String getFilename() {
		String filename = JOptionPane.showInputDialog("Enter the filename");
		return filename;
	}

	public DVDGUI(DVDCollection dl) {
		dvdlist = dl;
		this.ratingFilter = ""; // "" will return all ratings

		this.ROWS = 5;
		this.COLS = 3;
		this.IMAGE_EXT = ".JPG";
		this.DEFAULT_IMAGE = "DEFAULT" + this.IMAGE_EXT;
		this.IMAGE_FOLDER = "images/";
	}

	public void processCommands() {
		createWindow();
		return;
	}

	private void createWindow() {
		JFrame frame = new JFrame("DVD Manager 9001");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI(frame);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void createUI(final JFrame frame) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JButton addDVDButton = new JButton("Add DVD");
		JButton getRuntimeButton = new JButton("Get runtime");
		JButton getDVDsByRatingButton = new JButton("Get DVDs by Rating");
		JButton cancelButton = new JButton("Cancel");

		cancelButton.setEnabled(false);

		JButton removeDVDButton = new JButton("Remove");
		removeDVDButton.setVisible(false);
		JButton updateDVDButton = new JButton("Update");
		updateDVDButton.setVisible(false);
//DISPLAY SINGLE SELECTED DVD

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		dvdImage = new JLabel();
		infoPanel.add(dvdImage);

		JLabel titleLabel = new JLabel("Title: ");
		JLabel ratingLabel = new JLabel("Rating: ");
		JLabel runtimeLabel = new JLabel("Runtime: ");
		titleLabel.setVisible(false);
		ratingLabel.setVisible(false);
		runtimeLabel.setVisible(false);

		infoPanel.add(titleLabel);
		infoPanel.add(ratingLabel);
		infoPanel.add(runtimeLabel);
		infoPanel.add(removeDVDButton);
		infoPanel.add(updateDVDButton);

		dvdImage.setPreferredSize(new java.awt.Dimension(100, 200));
//DVD LIST SCROLL PANE

		JList<DVD> dvdJList = new JList<>(dvdlist.getDVDs(this.ratingFilter));
		JScrollPane allDVDsPane = new JScrollPane(dvdJList);

		dvdJList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				chosenDVD = dvdJList.getSelectedValue();

				if (chosenDVD != null) {
					titleLabel.setText("Title: " + chosenDVD.getTitle());
					ratingLabel.setText("Rating: " + chosenDVD.getRating());
					runtimeLabel.setText("Runtime: " + chosenDVD.getRunningTime() + " minutes");
					removeDVDButton.setVisible(true);
					updateDVDButton.setVisible(true);

					titleLabel.setVisible(true);
					ratingLabel.setVisible(true);
					runtimeLabel.setVisible(true);
					setDVDImage(chosenDVD.getTitle());
				} else {
					updateDVDButton.setVisible(false);
				}
			}
		});

		updateDVDButton.addActionListener(e -> {
			String errorMsg = "";
			JTextField title = new JTextField(10);
			JTextField rating = new JTextField(10);
			JTextField runtime = new JTextField(10);

			String oldTitle = chosenDVD.getTitle();
			String oldRating = chosenDVD.getRating();
			String oldRuntime = String.valueOf(chosenDVD.getRunningTime());

			title.setText(oldTitle);
			rating.setText(oldRating);
			runtime.setText(oldRuntime);

			while (true) {

				JPanel newDVDPanel = new JPanel(new GridLayout(ROWS, COLS));
				// JPanel newDVDPanel = new JPanel(new FlowLayout());

				newDVDPanel.add(new javax.swing.JLabel("Title: "));
				newDVDPanel.add(title);
				newDVDPanel.add(new javax.swing.JLabel("Rating (G, PG, PG-13, R): "));
				newDVDPanel.add(rating);
				newDVDPanel.add(new javax.swing.JLabel("Run Time (minutes): "));
				newDVDPanel.add(runtime);

				newDVDPanel.add(new javax.swing.JLabel(errorMsg));

				int result = JOptionPane.showConfirmDialog(frame, newDVDPanel, "Update DVD",
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

					String message = "Succesfully updated " + titleText;
					JOptionPane.showMessageDialog(frame, message);

					if (!titleText.equals(oldTitle)) {
						// needed to handle cases where the title is updated,
						// since addorModify will create new dvd if title doesnt exist in collection
						// but will update the attributes if it does
						dvdlist.removeDVD(oldTitle);
					}

					dvdlist.addOrModifyDVD(titleText, ratingText, runtimeText);
					dvdlist.save();
					dvdJList.setListData(dvdlist.getDVDs(this.ratingFilter));

					DVD updated = dvdlist.getDVDByTitle(titleText);
					chosenDVD = updated;
					dvdJList.setSelectedValue(updated, true);

					titleLabel.setText("Title: " + updated.getTitle());
					ratingLabel.setText("Rating: " + updated.getRating());
					runtimeLabel.setText("Runtime: " + updated.getRunningTime() + " minutes");

					removeDVDButton.setVisible(true);
					updateDVDButton.setVisible(true);
					break;
				}

			}
		});
		removeDVDButton.addActionListener(e -> {
			dvdlist.removeDVD(chosenDVD.getTitle());
			JOptionPane.showMessageDialog(frame, "Removed: " + chosenDVD.getTitle());
			chosenDVD = null;

			titleLabel.setVisible(false);
			ratingLabel.setVisible(false);
			runtimeLabel.setVisible(false);

			dvdImage.setIcon(null);
			dvdImage.setText(null);
//			titleLabel.setText("Title: ");
//			ratingLabel.setText("Rating: ");
//			runtimeLabel.setText("Runtime: ");
			removeDVDButton.setVisible(false);
			dvdJList.setListData(dvdlist.getDVDs(this.ratingFilter));
//			refreshJList();
			dvdlist.save();

		});

		getDVDsByRatingButton.addActionListener(e -> {
			String rating = JOptionPane.showInputDialog(frame,
					"Enter the rating (G, PG, PG-13, R, or \"\" to see all ratings)");
			
			// dont run on empty string?
			rating = rating.toUpperCase();

//			is this good? - make into 1 function?
			chosenDVD = null;
			dvdImage.setIcon(null);
			dvdImage.setText(null);
			titleLabel.setVisible(false);
			ratingLabel.setVisible(false);
			runtimeLabel.setVisible(false);
			removeDVDButton.setVisible(false);
			updateDVDButton.setVisible(false);

//			while (true) {
//				if (!DVDCollection.isValidRating(rating)) {
//					JOptionPane.showMessageDialog(frame, "Invalid rating entered.");
//					return;
//				}
//			}
//			
			dvdJList.setListData(dvdlist.getDVDs(rating));
		});

		getRuntimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel runtimePanel = new JPanel(new GridLayout(ROWS, COLS));
				runtimePanel.add(new JLabel("Runtime: " + dvdlist.getTotalRunningTime()));
				JOptionPane.showMessageDialog(frame, runtimePanel);
			}
		});

		addDVDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String errorMsg = "";
				JTextField title = new JTextField(10);
				JTextField rating = new JTextField(10);
				JTextField runtime = new JTextField(10);

				while (true) {

					JPanel newDVDPanel = new JPanel(new GridLayout(ROWS, COLS));
					// JPanel newDVDPanel = new JPanel(new FlowLayout());

					newDVDPanel.add(new javax.swing.JLabel("Title: "));
					newDVDPanel.add(title);
					newDVDPanel.add(new javax.swing.JLabel("Rating (G, PG, PG-13, R): "));
					newDVDPanel.add(rating);
					newDVDPanel.add(new javax.swing.JLabel("Run Time (minutes): "));
					newDVDPanel.add(runtime);

					newDVDPanel.add(new javax.swing.JLabel(errorMsg));

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
						dvdlist.addOrModifyDVD(titleText, ratingText, runtimeText);

						dvdlist.save();
//						refreshJList();
						dvdJList.setListData(dvdlist.getDVDs(ratingFilter));
						break;
					}

				}
			}
		});

		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(addDVDButton);
		topPanel.add(getRuntimeButton);
		topPanel.add(getDVDsByRatingButton);

		JPanel centerPanel = new JPanel(new FlowLayout());
		centerPanel.add(allDVDsPane);
		centerPanel.add(infoPanel);

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(centerPanel, BorderLayout.CENTER);

		frame.getContentPane().add(panel);

	}

	private void setDVDImage(String title) {
		File folder = new File(this.IMAGE_FOLDER);
		File[] files = folder.listFiles();
		File imageFile = null;

		for (File file : files) {
			if (file.getName().equals(title + this.IMAGE_EXT)) {
				imageFile = file;
				break;
			}
		}

		if (imageFile == null) {
			imageFile = new File(this.IMAGE_FOLDER + this.DEFAULT_IMAGE);
		}

		try {
			BufferedImage img = ImageIO.read(imageFile);
			dvdImage.setIcon(new ImageIcon(img));
			dvdImage.setText(null);
		} catch (IOException e) {
			dvdImage.setIcon(null);
		}
	}

//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++

}
