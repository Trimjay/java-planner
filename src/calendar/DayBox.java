package calendar;

import java.util.Date;
import java.io.Serializable;
import timer.SavedConfig;

// Contains any notes and timers saved for a date
public class DayBox implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date date;
	private String notes;
	private SavedConfig timer = new SavedConfig();

	public DayBox() {
		
	}

	public DayBox(Date date, String notes) {
		this.date = date;
		this.notes = notes;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public SavedConfig getTimer() {
		return timer;
	}

	public void setTimer(SavedConfig timer) {
		this.timer = timer;
	}
}
