package timer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;

import calendar.CalendarSave;

public class DateTimer extends TimerUI {

	private Date date;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private CalendarSave calendar;
	
	public DateTimer(Date date, CalendarSave calendar) {
		this.date = date;
		this.calendar = calendar;
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveConfig(date);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
			public void windowOpened(WindowEvent e) {
				File dateTimerFile = new File("./configs/datetimers/" + formatter.format(date));
				if (dateTimerFile.exists()) {
					loadConfig(dateTimerFile);
				} else {
					addClock();
				}
			}
		});
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DateFormat getFormatter() {
		return formatter;
	}

	public void setFormatter(DateFormat formatter) {
		this.formatter = formatter;
	}

	// saves all clocks and their details to a file
	private void saveConfig(Date date) {
		SavedConfig newConfig = new SavedConfig();
		for (int i = 0; i < components.size(); i++) {
				newConfig.addComp(components.get(i).getSaveFields());
			}
		
		if (calendar.getDayBox(date) == null) {
			
		}
		calendar.getDayBox(date).setTimer(newConfig);
		newConfig.saveThis(date);
	}
}