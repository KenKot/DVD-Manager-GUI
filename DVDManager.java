import java.util.*;

public class DVDManager {
	public static void main(String[] args) {
		DVDUserInterface dlInterface;
		DVDCollection dl = new DVDCollection();
		
		String filename = DVDGUI.getTXTFile();
		dl.loadData(filename);
		
		dlInterface = new DVDGUI(dl);
		dlInterface.processCommands();
	}
}
