package calendar;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

// Contains notes for days
public class CalendarSave implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<Date, DayBox> days;

	public CalendarSave() {
		this.days = new HashMap<Date, DayBox>();
	}

	public HashMap<Date, DayBox> getDays() {
		return days;
	}

	public void setDays(HashMap<Date, DayBox> days) {
		this.days = days;
	}

	public void addDay(Date date, DayBox day) {
		days.put(date, day);
	}

	public DayBox getDayBox(Date date) {
		return days.get(date);
	}

	public void saveThis() {
		try {
			FileOutputStream fos = new FileOutputStream("./calendar/Calendar");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error writing file");
			System.out.println(e);
		}
	}
}