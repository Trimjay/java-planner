package timer;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SavedConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<String[]> saves = new ArrayList<String[]>();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public void addComp(String[] comp) {
		saves.add(comp);
	}
	
	public ArrayList<String[]> getComps() {
		return saves;
	}

	// used for standard timers not attached to a date
	public void saveThis(String name) {
		try {
			FileOutputStream fos = new FileOutputStream("./configs/" + name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error writing file");
			System.out.println(e);
		}
	}

	// used for calendar timers
	public void saveThis(Date date) {
		try {
			FileOutputStream fos = new FileOutputStream("./configs/datetimers/" + formatter.format(date));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error writing file");
			System.out.println(e);
		}
	}
}