import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DVDGUI implements DVDUserInterface {
	private DVDCollection dvdlist;
	private DVD chosenDVD;

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
	}

	private void createWindow() {
		JFrame frame = new JFrame("GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI(frame);
		frame.setSize(700, 400);
		frame.setLocationRelativeTo(null); // Center on screen
		frame.setVisible(true); // make visible
	}

	private void createUI(final JFrame frame) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JButton addDVDButton = new JButton("Add DVD");
		JButton getRuntimeButton = new JButton("Get runtime");
		JButton cancelButton = new JButton("Cancel");

		cancelButton.setEnabled(false);

		JButton removeDVDButton = new JButton("Remove");
		removeDVDButton.setVisible(false);

//DISPLAY SINGLE SELECTED DVD

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel("Title: ");
		JLabel ratingLabel = new JLabel("Rating: ");
		JLabel runtimeLabel = new JLabel("Runtime: ");
		infoPanel.add(titleLabel);
		infoPanel.add(ratingLabel);
		infoPanel.add(runtimeLabel);
		infoPanel.add(removeDVDButton);

//DVD LIST SCROLL PANE

		JList<DVD> dvdJList = new JList<>(dvdlist.getDVDs());
		JScrollPane allDVDsPane = new JScrollPane(dvdJList);

		dvdJList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				chosenDVD = dvdJList.getSelectedValue();

				if (chosenDVD != null) {
					titleLabel.setText("Title: " + chosenDVD.getTitle());
					ratingLabel.setText("Rating: " + chosenDVD.getRating());
					runtimeLabel.setText("Runtime: " + chosenDVD.getRunningTime() + " minutes");
					removeDVDButton.setVisible(true);
				} else {
					removeDVDButton.setVisible(false);
				}
			}
		});

		removeDVDButton.addActionListener(e -> {
			dvdlist.removeDVD(chosenDVD.getTitle());
			JOptionPane.showMessageDialog(frame, "Removed: " + chosenDVD.getTitle());
			chosenDVD = null;
			titleLabel.setText("Title: ");
			ratingLabel.setText("Rating: ");
			runtimeLabel.setText("Runtime: ");
			removeDVDButton.setVisible(false);
			dvdJList.setListData(dvdlist.getDVDs());
//			refreshJList();
			dvdlist.save();
		});
// +++++++++++++++++++++++++++++++++++++++++++++++
//		ACTION LISTENERS:
// +++++++++++++++++++++++++++++++++++++++++++++++

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
						dvdJList.setListData(dvdlist.getDVDs());
						break;
					}

				}
			}
		});

		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(addDVDButton);
		topPanel.add(getRuntimeButton);

		JPanel centerPanel = new JPanel(new FlowLayout());
		centerPanel.add(allDVDsPane);
		centerPanel.add(infoPanel);

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(centerPanel, BorderLayout.CENTER);

		frame.getContentPane().add(panel);

	}

//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++



}
