import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.io.IOException;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class DVDGUI implements DVDUserInterface {
	private static String APP_NAME = "DVD Manager 9001";

	private DVDCollection dvdlist;
	private DVD chosenDVD;
	private JLabel dvdImage;
	private String ratingFilter; // G, PG, PG-13, R, ""(for all ratings)

	private int ROWS;
	private int COLS;
	private String DEFAULT_IMAGE;
	private String IMAGE_EXT;
	private String IMAGE_FOLDER;

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

	public static String getTXTFile() {
		File dir = new File(".");
		File[] files = dir.listFiles();

		DefaultListModel<String> listModel = new DefaultListModel<>();

		for (File file : files) {
			String name = file.getName().toLowerCase();
			if (name.endsWith(".txt")) {
				String titleStripped = name.substring(0, name.length() - 4);
				listModel.addElement(titleStripped);
			}
		}

		JList<String> fileList = new JList<>(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(fileList);
		scrollPane.setPreferredSize(new java.awt.Dimension(300, 150));
		JButton okButton = new JButton("OK");
		JTextField newFileField = new JTextField(20);
		JButton createButton = new JButton("Create");
		JLabel selectLabel = new JLabel("Select a DVD Collection below", SwingConstants.CENTER);
		JPanel selectLabelPanel = new JPanel(new FlowLayout());
		selectLabelPanel.add(selectLabel);
		JPanel okButtonPanel = new JPanel(new FlowLayout());
		okButtonPanel.add(okButton);
		JLabel createLabel = new JLabel("Create new DVD Collection", SwingConstants.CENTER);
		JPanel createLabelPanel = new JPanel(new FlowLayout());
		createLabelPanel.add(createLabel);
		JPanel createButtonPanel = new JPanel(new FlowLayout());
		createButtonPanel.add(createButton);
		JPanel newFilePanel = new JPanel();
		newFilePanel.setLayout(new BoxLayout(newFilePanel, BoxLayout.Y_AXIS));
		newFilePanel.add(createLabelPanel);
		newFilePanel.add(newFileField);
		newFilePanel.add(createButtonPanel);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(selectLabelPanel);
		mainPanel.add(scrollPane);
		mainPanel.add(okButtonPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(newFilePanel);
		JDialog dialog = new JDialog((JFrame) null, APP_NAME, true);
		dialog.getContentPane().add(mainPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		String[] selectedFile = { null };
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = fileList.getSelectedValue();
				if (selected != null) {
					selectedFile[0] = selected + ".txt";
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Please select a file from the list.");
				}
			}
		});

		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String typed = newFileField.getText().trim();
				if (!typed.isEmpty()) {
					if (!typed.toLowerCase().endsWith(".txt")) {
						typed += ".txt";
					}
					selectedFile[0] = typed;
					dialog.dispose();
				} else {
					JOptionPane.showMessageDialog(dialog, "Add a filename to create");
				}
			}
		});

		dialog.setVisible(true);
		return selectedFile[0];
	}

	private void createWindow() {
		JFrame frame = new JFrame(APP_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI(frame);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void createUI(JFrame frame) {
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

		JButton getRandomDVDButton = new JButton("Feeling lucky?");

		refreshGetLuckyButtonVisibility(getRandomDVDButton);

		// DISPLAY SINGLE SELECTED DVD

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
		// DVD LIST SCROLL PANE

		JList<DVD> dvdJList = new JList<>(dvdlist.getDVDs(this.ratingFilter));
		JScrollPane allDVDsPane = new JScrollPane(dvdJList);

		getRandomDVDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DVD[] dvds = dvdlist.getDVDs(ratingFilter);
				if (dvds.length > 0) {
					int index = (int) (Math.random() * dvds.length);
					dvdJList.setSelectedIndex(index);
				}
			}
		});

		dvdJList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
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
			}
		});

		updateDVDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
						dvdJList.setListData(dvdlist.getDVDs(ratingFilter));

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
			}
		});

		removeDVDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dvdlist.removeDVD(chosenDVD.getTitle());
				JOptionPane.showMessageDialog(frame, "Removed: " + chosenDVD.getTitle());
				chosenDVD = null;

				titleLabel.setVisible(false);
				ratingLabel.setVisible(false);
				runtimeLabel.setVisible(false);

				dvdImage.setIcon(null);
				dvdImage.setText(null);
				removeDVDButton.setVisible(false);
				dvdJList.setListData(dvdlist.getDVDs(ratingFilter));
				dvdlist.save();
			}
		});

		getDVDsByRatingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String rating = JOptionPane.showInputDialog(frame,
						"Enter the rating (G, PG, PG-13, R, or \"\" to see all ratings)");

				rating = rating.toUpperCase();

				chosenDVD = null;
				dvdImage.setIcon(null);
				dvdImage.setText(null);
				titleLabel.setVisible(false);
				ratingLabel.setVisible(false);
				runtimeLabel.setVisible(false);
				removeDVDButton.setVisible(false);
				updateDVDButton.setVisible(false);

				dvdJList.setListData(dvdlist.getDVDs(rating));
				refreshGetLuckyButtonVisibility(getRandomDVDButton);
			}
		});

		getRuntimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel runtimePanel = new JPanel(new GridLayout(ROWS, COLS));
				runtimePanel.add(new JLabel(
						"The total DVD collection runtime is " + dvdlist.getTotalRunningTime() + " minutes!"));
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
						refreshGetLuckyButtonVisibility(getRandomDVDButton);
						break;
					}

				}
			}
		});

		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(addDVDButton);
		topPanel.add(getRuntimeButton);
		topPanel.add(getDVDsByRatingButton);

		topPanel.add(getRandomDVDButton);

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

			int width = 150;
			int height = 200;

			Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			dvdImage.setIcon(new ImageIcon(scaledImg));
			dvdImage.setText(null);
			dvdImage.setPreferredSize(new java.awt.Dimension(width, height));
		} catch (IOException e) {
			dvdImage.setIcon(null);
		}
	}

	private void refreshGetLuckyButtonVisibility(JButton button) {
		DVD[] currentDVDs = dvdlist.getDVDs(ratingFilter);
		if (currentDVDs.length > 0) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}
	}
//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++

}
