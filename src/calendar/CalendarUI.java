package calendar;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import timer.DateTimer;

import javax.swing.JButton;

public class CalendarUI {

	private static boolean boxInit = false; // True when month/year boxes initialised
	private CalendarSave theCalendar = new CalendarSave();
	JFrame frame;
	private static String[] months = java.util.Arrays.copyOfRange(new DateFormatSymbols().getMonths(), 0, 12);
	private static Calendar cal = Calendar.getInstance();
	private static int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	private static int firstDayInt; // Day of the week on which a month begins
	private JTextArea[] dayBoxes = new JTextArea[42];
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private Integer[] years = new Integer[] { 2018, 2019, 2020, 2021, 2022, 2023 };
	final private String[] headLabels = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday", "Sunday" };
	private boolean dayFilter = false; // Turned on to avoid errors with text updating when changing month
	private Date today = parseDate(cal.getTime());
	private JLabel imageLabel = new JLabel();

	// Removes time of day from date for checking days on the calendar
	private Date parseDate(Date date) {
		try {
			return formatter.parse(formatter.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return date;
		}
	}

	// Saves notes for dates upon text being altered
	public void updateBox(int x, JTextArea dayBox) {
		cal.add(Calendar.DATE, x - firstDayInt + 1);
		DayBox thisDay = new DayBox(parseDate(cal.getTime()), dayBox.getText());
		theCalendar.addDay(parseDate(cal.getTime()), thisDay);
		cal.add(Calendar.DATE, -x + firstDayInt - 1);
	}

	// Sets day of the week of the first day of month
	public void getFirstDay() {
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		firstDayInt = cal.get(Calendar.DAY_OF_WEEK);
		firstDayInt -= 1;
		if (firstDayInt == 0) {
			firstDayInt = 7; // Changes first day of week from Sunday to Monday
		}
	}

	public CalendarUI() throws IOException {
		initialize();
	}

	// Displays days of the week
	private void initDayHeads() {
		for (int i = 0; i < 7; i++) {
			JLabel dayLabel = new JLabel();
			dayLabel.setOpaque(false);
			dayLabel.setBorder(null);
			dayLabel.setText(headLabels[i]);
			dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dayLabel.setBounds(37 + (133 * i), 96, 125, 20);
			frame.getContentPane().add(dayLabel);
		}
	}

	// Label showing numerical day of the month
	private void initDayLabel(JTextArea dayBox) {
		JLabel dateLabel = new JLabel();
		dateLabel.setBounds(101, 0, 20, 20);
		dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dateLabel.setVisible(true);
		dayBox.add(dateLabel);
	}

	// Initialises editable boxes on the calendar
	ImageIcon plusIcon = new ImageIcon(ImageIO.read(new File("./images/widgets/plus.jpg")));
	ImageIcon clockIcon = new ImageIcon(ImageIO.read(new File("./images/widgets/clock.jpg")));	
	public void initDayBoxes() throws IOException {
		for (int i = 0; i < dayBoxes.length; i++) {
			int x = i;
			JTextArea dayBox = new JTextArea(10, 10);
			dayBox.setLineWrap(true);
			dayBox.setWrapStyleWord(true);
			dayBox.getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {}
				public void changedUpdate(DocumentEvent e) {}
				public void insertUpdate(DocumentEvent e) {
					if (!dayFilter) {
						System.out.println("a");
						updateBox(x, dayBox);
					}
				}
			});

			dayBox.setBounds(37 + ((i % 7) * 133), 130 + (135 * (i / 7)), 125, 125);
			frame.getContentPane().add(dayBox);
			dayBoxes[i] = dayBox;
			initDayLabel(dayBox);

			// Button for adding timers for a given day
			JButton dateTimerBut = new JButton(plusIcon);
			dateTimerBut.setBounds(104, 104, 20, 20);
			dateTimerBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cal.add(Calendar.DATE, x - firstDayInt + 1);
					if (theCalendar.getDayBox(parseDate(cal.getTime())) == null)  {
						theCalendar.getDays().put(parseDate(cal.getTime()), new DayBox());
						dateTimerBut.setIcon(clockIcon);
					}
					DateTimer dateTimer = new DateTimer(parseDate(cal.getTime()), theCalendar);
					dateTimer.getFrame().setVisible(true);
					cal.add(Calendar.DATE, - x + firstDayInt - 1);
				}
			});
			dateTimerBut.setVisible(true);
			dayBox.add(dateTimerBut);
		}
	}

	// Sets the contents of each box to saved day when changing month
	private void setDayBox(int i) {
		dayFilter = true;
		JButton thisButton = (JButton) dayBoxes[i].getComponent(1);
		cal.add(Calendar.DATE, i - firstDayInt + 1);
		if (theCalendar.getDays().containsKey(parseDate(cal.getTime()))) {
			DayBox thisBox = theCalendar.getDayBox(parseDate(cal.getTime()));
			dayBoxes[i].setText(theCalendar.getDayBox(parseDate(cal.getTime())).getNotes());
			if (!(thisBox.getTimer() == null)) {				
				thisButton.setIcon(clockIcon);
			}
			else {
				thisButton.setIcon(plusIcon);
			}
		} else {
			dayBoxes[i].setText(null);
			thisButton.setIcon(plusIcon);
		}
		if (parseDate(cal.getTime()).compareTo(today) < 0) {
			dayBoxes[i].getComponent(1).setEnabled(false);
		}
		else {
			dayBoxes[i].getComponent(1).setEnabled(true);
		}

		JLabel dateLabel = (JLabel) dayBoxes[i].getComponent(0);
		dateLabel.setText(Integer.toString(cal.get(Calendar.DATE)));
		cal.add(Calendar.DATE, - i + firstDayInt - 1);
		dayFilter = false;
	}

	// Grays out days months other than the one selected
	public void grayMonths() {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < 7; i++) {
			if (i < firstDayInt - 1) {
				dayBoxes[i].setBackground(Color.LIGHT_GRAY);
			} else {
				dayBoxes[i].setBackground(Color.WHITE);
			}
			setDayBox(i);
		}
		for (int i = 7; i < 28; i++) {
			setDayBox(i);
		}
		for (int i = 28; i < 42; i++) {
			if (firstDayInt - 2 + daysInMonth < i) {
				dayBoxes[i].setBackground(Color.LIGHT_GRAY);
			} else {
				dayBoxes[i].setBackground(Color.WHITE);
			}
			setDayBox(i);
		}
	}

	// Loads saved calendar
	private void loadCalendar(File loadFile) {
		FileInputStream fisCal;
		try {
			fisCal = new FileInputStream(loadFile);
			ObjectInputStream oisCal = new ObjectInputStream(fisCal);
			CalendarSave loadedCalendar = (CalendarSave) oisCal.readObject();
			theCalendar = loadedCalendar;
			System.out.println("Calendar load success");
			oisCal.close();
		} catch (Exception e) {
			System.out.println("Error loading calendar");
			e.printStackTrace();
		}
	}

	private void initialize() throws IOException {

		// Load existing calendar or save when program opens or closes
		File calendarFile = new File("./calendar/Calendar");
		if (!calendarFile.exists()) {
			theCalendar = new CalendarSave();
		} else {
			loadCalendar(calendarFile);
		}
		getFirstDay();
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				theCalendar.saveThis();
			}
		});

		initDayHeads();
		initDayBoxes();
		grayMonths();

		// Year select box
		JComboBox<Integer> yearSelectBox = new JComboBox<>();
		yearSelectBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cal.set(Calendar.YEAR, (int) yearSelectBox.getSelectedItem());
				getFirstDay();
				daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (boxInit) {
					grayMonths();
				}
			}
		});
		yearSelectBox.setBounds(63, 36, 87, 20);
		yearSelectBox.setModel(new DefaultComboBoxModel<Integer>(years));
		yearSelectBox.setSelectedIndex(1);
		frame.getContentPane().add(yearSelectBox);

		// Month select box
		JComboBox<Object> monthSelectBox = new JComboBox<Object>();
		monthSelectBox.setBounds(203, 36, 97, 20);
		monthSelectBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cal.set(Calendar.MONTH, monthSelectBox.getSelectedIndex());
				getFirstDay();
				daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (boxInit) {
					grayMonths();
				}
				imageLabel.setIcon(new ImageIcon("./images/monthpics/" + 
				Integer.toString(monthSelectBox.getSelectedIndex()+1) + ".png"));
			}
		});
		monthSelectBox.setModel(new DefaultComboBoxModel<Object>(months));
		monthSelectBox.setSelectedIndex(cal.get(Calendar.MONTH)); // current month
		frame.getContentPane().add(monthSelectBox);
		boxInit = true;

		System.out.println(cal.getTime());
		
		// Reverse one month button
		ImageIcon lArrow = new ImageIcon(ImageIO.read(new File("./images/widgets/leftArrow.png")));
		JButton monthDownBut = new JButton(lArrow);
		monthDownBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				monthSelectBox.setSelectedIndex((monthSelectBox.getSelectedIndex() - 1 + 12) % 12);
				if (monthSelectBox.getSelectedIndex() == 11) {
					cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
					yearSelectBox.setSelectedIndex(yearSelectBox.getSelectedIndex() - 1);
				}
			}
		});
		monthDownBut.setBounds(184, 36, 20, 20);
		frame.getContentPane().add(monthDownBut);

		// Advance one month button
		ImageIcon rArrow = new ImageIcon(ImageIO.read(new File("./images/widgets/rightArrow.png")));
		JButton monthUpBut = new JButton(rArrow);
		monthUpBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				monthSelectBox.setSelectedIndex(((monthSelectBox.getSelectedIndex()) + 1) % 12);
				if (monthSelectBox.getSelectedIndex() == 0) {
					cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
					yearSelectBox.setSelectedIndex(yearSelectBox.getSelectedIndex() + 1);
				}
			}
		});
		monthUpBut.setBounds(299, 36, 20, 20);
		frame.getContentPane().add(monthUpBut);
		
		imageLabel.setBounds(730,20, 171, 49);
		frame.add(imageLabel);
	}
}