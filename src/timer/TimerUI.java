package timer;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.JComboBox;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JSlider;

public class TimerUI {

	protected JFrame frame;
	private int componentCount = 0;
	protected ArrayList<ComponentIn> components = new ArrayList<ComponentIn>();
	private JButton addClockBut;
	private JButton addCheckBut;
	private JButton saveButton;
	private JTextField saveText;
	private String alarmString = "./alarms/alarm1.wav";
	private JComboBox<Object> loadBox;
	private JComboBox<Object> alarmBox;
	private JButton resetButton;
	private JSlider volumeSlider;
	boolean saveClicked = false;
	boolean loadClicked = false;
	boolean alarmClicked = false;

	ArrayList<String> configs = getFiles("./configs/", 0);
	ArrayList<String> alarms = getFiles("./alarms/", 4);

	public TimerUI() {
		initialize();
	}
	
	// saves current state when closing window and opens latest state when reopening
	/**
	 * @wbp.parser.constructor
	 */
	public TimerUI(boolean standard) {
		initialize();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveConfig(null);
			}
			public void windowOpened(WindowEvent e) {
				File lastCfgFile = new File("./configs/Last");
				if (!lastCfgFile.exists()) {
					addClock();
				} else {
					loadConfig(lastCfgFile);
					configs.add(1, "Last");
					loadBox.setModel(new DefaultComboBoxModel<Object>(configs.toArray()));
				}
			}
		});
	} 

	public JFrame getFrame() {
		return frame;
	}

	// returns file path of selected alarm
	public String getAlarmString() {
		return alarmString;
	}

	public long getVolume() {
		return volumeSlider.getValue();
	}

	// called when adding clocks from a saved configuration
	public void addClock(String name, String time) {
		Clock newClock = new Clock(componentCount, name, time, this);
		newClock.setFrame(frame);
		newClock.init();
		componentCount++;
		components.add(newClock);
		frame.setBounds(100, 100, 450, 300 + (componentCount - 1) * 60);
		addClockBut.setBounds(335, 187 + (componentCount - 1) * 60, 28, 22);
		addCheckBut.setBounds(396, 187 + (componentCount - 1) * 60, 28, 22);
	}

	// blank clock called when initiating the window or adding a new clock
	public void addClock() {
		addClock("", "00:00:00");
	}
	
	// called when adding clocks from a saved configuration
	public void addCheck(String name, String complete) {
		Taskbox newTask = new Taskbox(componentCount, name, (complete.equals("n") ? false : true), this);
		newTask.setFrame(frame);
		newTask.init();
		componentCount++;
		components.add(newTask);
		frame.setBounds(100, 100, 450, 300 + (componentCount - 1) * 60);
		addClockBut.setBounds(335, 187 + (componentCount - 1) * 60, 28, 22);
		addCheckBut.setBounds(396, 187 + (componentCount - 1) * 60, 28, 22);
	}

	// blank clock called when initiating the window or adding a new clock
	public void addCheck() {
		addCheck("", "n");
	}
	
	// repositions all clocks below the one being removed
	public void reposition(ComponentIn comp) {
		for (int i = comp.getPosition() + 1; i < components.size(); i++) {
			components.get(i).reposition();
		}
		componentCount--;
		components.remove(comp);
		frame.setBounds(100, 100, 450, 300 + (componentCount - 1) * 60);
		addClockBut.setBounds(335, 187 + (componentCount - 1) * 60, 28, 22);
		addCheckBut.setBounds(396, 187 + (componentCount - 1) * 60, 28, 22);
	}

	// deletes all clocks
	protected void deleteComponents() {
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getClass() == Clock.class) {
				Clock deleteClock = (Clock) components.get(i);
				if (deleteClock.getClip() != null && deleteClock.getClip().isActive()) {
					deleteClock.getClip().close();
				}
			}
			components.get(i).delete(true);
			components.remove(i);
			i--;
		}
		components.clear();
		componentCount = 0;
	}

	// returns a list of all file paths for alarms and configs
	private ArrayList<String> getFiles(String filePath, int extensionChop) {
		ArrayList<String> results = new ArrayList<String>();
		File[] files = new File(filePath).listFiles();
		for (File file : files) {
			if (file.isFile() && !file.getName().equals("Last")) {
				results.add(file.getName().substring(0, file.getName().length() - extensionChop));
			}
		}
		return results;
	}

	// shows text box to name saved file
	private void showSaveText() {
		saveText.setBackground(Color.WHITE);
		saveText.setVisible(true);
		saveClicked = true;
	}

	// shows a message for 3 seconds when a config is saved
	private void showSavedMsg() {
		Border defaultBorder = saveText.getBorder();
		saveText.setText("Config saved");
		saveText.setBackground(null);
		saveText.setBorder(null);
		saveText.setHorizontalAlignment(SwingConstants.CENTER);
		saveButton.setEnabled(false);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				saveText.setText(null);
				saveText.setBorder(defaultBorder);
				saveText.setBackground(Color.WHITE);
				saveText.setVisible(false);
				saveText.setHorizontalAlignment(SwingConstants.LEFT);
				saveClicked = false;
				saveButton.setEnabled(true);
			}
		}, 3000);
	}

	// saves all clocks and their details to a file
	private void saveConfig(String saveText) {
		SavedConfig newConfig = new SavedConfig();
		for (int i = 0; i < components.size(); i++) {
			newConfig.addComp(components.get(i).getSaveFields());
		}
		if (saveText == null) {
			newConfig.saveThis("Last");
		} else {
			newConfig.saveThis(saveText);
		}
	}

	// loads a saved config
	protected void loadConfig(File loadFile) {
		FileInputStream fisCon;
		try {
			fisCon = new FileInputStream(loadFile);
			ObjectInputStream oisCon = new ObjectInputStream(fisCon);
			SavedConfig loadedConfig = (SavedConfig) oisCon.readObject();
			deleteComponents();
			for (int i = 0; i < loadedConfig.getComps().size(); i++) {
				if (loadedConfig.getComps().get(i)[0].equals("clock")) {
					addClock(loadedConfig.getComps().get(i)[1], loadedConfig.getComps().get(i)[2]);
				}
				else {
					addCheck(loadedConfig.getComps().get(i)[1], loadedConfig.getComps().get(i)[2]);
				}
			}
			oisCon.close();
		} catch (Exception e) {
			System.out.println("Error loading config");
			e.printStackTrace();
		}
	}

	private void initialize() {

		frame = new JFrame("Timer");
		frame.setResizable(true);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// adds a new blank clock
		addClockBut = new JButton("Add Clock");
		addClockBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addClock();
			}
		});
		addClockBut.setBounds(335, 187, 28, 22);
		frame.getContentPane().add(addClockBut);


		addCheckBut = new JButton("Add Check");
		addCheckBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addCheck();
			}
		});
		addCheckBut.setBounds(396, 187, 28, 22);
		frame.getContentPane().add(addCheckBut);


		// saves all current clocks to a file
		saveButton = new JButton("Save");
		saveButton.setToolTipText("Save current setup");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (saveClicked == true) {
					TimerUI temp = components.get(0).gettUI();
					saveConfig(saveText.getText());
					for (int i = 0; i < components.size(); i++) {
						components.get(i).settUI(temp);
						components.get(i).getNameBox().setBackground(frame.getBackground());
					}
					configs.add(saveText.getText());
					loadBox.setModel(new DefaultComboBoxModel<Object>(configs.toArray()));
					showSavedMsg();

				} else {
					showSaveText();
				}
			}
		});
		saveButton.setBounds(335, 23, 89, 23);
		frame.getContentPane().add(saveButton);
		
		// text box to enter save name
		saveText = new JTextField();
		saveText.setBounds(335, 51, 89, 23);
		frame.getContentPane().add(saveText);
		saveText.setColumns(10);
		saveText.setVisible(false);

		// slider for alarm volume
		volumeSlider = new JSlider();
		volumeSlider.setBounds(183, 52, 86, 22);
		frame.getContentPane().add(volumeSlider);
		volumeSlider.setValue(100);

		// loads a saved configuration of clocks
		loadBox = new JComboBox<Object>();
		loadBox.setToolTipText("Load a saved setup");
		loadBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (loadClicked == false) {
					loadClicked = true;
					loadBox.removeItemAt(0);
				}
				Object selected = loadBox.getSelectedItem();
				File loadPath = new File("./configs/" + selected.toString());
				loadConfig(loadPath);
			}
		});
		configs.add(0, "Load");
		loadBox.setModel(new DefaultComboBoxModel<Object>(configs.toArray()));
		loadBox.setBounds(41, 23, 86, 22);
		frame.getContentPane().add(loadBox);

		// chooses an alarm
		alarmBox = new JComboBox<Object>();
		alarmBox.setToolTipText("Set alarm to play");
		alarmBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (alarmClicked == false) {
					alarmClicked = true;
					alarms.remove(0);
					alarmBox.setModel(new DefaultComboBoxModel<Object>(alarms.toArray()));
				}
				Object selected = alarmBox.getSelectedItem();
				alarmString = "./alarms/" + selected.toString() + ".wav";
			}
		});
		alarms.add(0, "Alarms");
		alarmBox.setModel(new DefaultComboBoxModel<Object>(alarms.toArray()));
		alarmBox.setBounds(183, 23, 86, 22);
		frame.getContentPane().add(alarmBox);

		// removes all clocks and adds a blank one
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteComponents();
				addClock();
			}
		});
		resetButton.setBounds(41, 51, 86, 23);
		frame.getContentPane().add(resetButton);
	}
}