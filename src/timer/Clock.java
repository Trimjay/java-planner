package timer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

public class Clock extends AbsComponent implements Serializable, ComponentIn {

	private static final long serialVersionUID = 1L;
	private String time = "00:00:00";
	private JButton startB;
	private JButton pauseB;
	private Timer timer;
	private JTextField timeF;
	private Clip clip;
	boolean playingSound = false;
	boolean startClicked = false;
	private DocFilter df = new DocFilter();
	
	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeF() {
		return timeF.getText();
	}

	public Clip getClip() {
		return clip;
	}

	public Clock(int position, String name, String time, TimerUI ui) {
		this.position = position;
		this.name = name;
		this.time = time;
		this.tUI = ui;
	}

	// repositions the clock when an above clock is removed
	public void reposition() {
		if (position != 0) {
			position -= 1;
		}
		timeF.setBounds(133, 90 + position * 60, 180, 58);
		pauseB.setBounds(317, 127 + position * 60, 76, 21);
		deleteB.setBounds(405, 111 + position * 60, 19, 23);
		startB.setBounds(317, 93 + position * 60, 76, 23);
		nameF.setBounds(37, 90 + position * 60, 86, 58);
	}

	// returns alarm volume from the volume slider
	private void getVolume() {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		int volume = (int) tUI.getVolume();
		float range = gainControl.getMinimum();
		float result = range * (1 - volume / 100.0f);
		gainControl.setValue(result);
	}

	// plays alarm and changes display to allow muting the alarm
	private void playSound(String filename) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			getVolume();
			clip.start();
			LineListener listener = new LineListener() {
				public void update(LineEvent event) {
					if (event.getFramePosition() == clip.getFrameLength()) {
						startB.setText("Start");
						playingSound = false;
						clip = null;
					}
				}
			};
			clip.addLineListener(listener);
			if (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
				startB.setText("Mute");
				playingSound = true;
			}
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}

	// changes display when sound has finished playing
	private void clipFinished() {
		clip.stop();
		startB.setText("Start");
		playingSound = false;
		startB.setEnabled(true);
		pauseB.setEnabled(false);
	}

	// changes display when count down is finished
	private void clockTimeReached() {
		timer.stop();
		timeF.setForeground(Color.RED);
		playSound(tUI.getAlarmString());
		startB.setEnabled(true);
		pauseB.setEnabled(false);
	}

	// converts the digits shown on the clock into total time in seconds
	private int getTime(JTextField field) {
		String f = field.getText();
		int time = 0;
		time += Character.getNumericValue(f.charAt(0)) * 36000;
		time += Character.getNumericValue(f.charAt(1)) * 3600;
		time += Character.getNumericValue(f.charAt(3)) * 600;
		time += Character.getNumericValue(f.charAt(4)) * 60;
		time += Character.getNumericValue(f.charAt(6)) * 10;
		time += Character.getNumericValue(f.charAt(7));

		return time;
	}

	// converts the total time into hours minutes and seconds
	private int[] getTimeText(int time) {
		int hours = time / 3600;
		int remainder = time - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;

		int[] hms = { hours, mins, secs };
		return hms;
	}

	// removes this clock or all clocks and repositions others
	public void delete(boolean all) {
		if (all == false) {
			tUI.reposition(this);
		}
		frame.remove(nameF);
		frame.remove(timeF);
		frame.remove(pauseB);
		frame.remove(startB);
		frame.remove(deleteB);
		frame.revalidate();
		frame.repaint();
	}	

	public void init() {
		
		super.init();
		
		// displays count down
		timeF = new JTextField();
		timeF.setHorizontalAlignment(SwingConstants.CENTER);
		timeF.setFont(new Font("Tahoma", Font.PLAIN, 30));
		timeF.setBounds(133, 90 + position * 60, 180, 58);
		frame.getContentPane().add(timeF);
		timeF.setText(time);
		timeF.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {}
			public void removeUpdate(DocumentEvent arg0) {}
			public void insertUpdate(DocumentEvent arg0) {
				if (timeF.getText().equals("00:00:00")) {
					startB.setEnabled(false);
				} else if (timer != null && timer.isRunning()) {

				} else {
					startB.setEnabled(true);
					timeF.setForeground(Color.BLACK);
				}
			}
		});

		AbstractDocument document = (AbstractDocument) timeF.getDocument();
		document.setDocumentFilter(df);

		// Start Button and timer action
		ActionListener countdownDisplay = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				df.setFilter(false);
				int curTime = getTime(timeF);
				curTime--;
				int[] displayList = getTimeText(curTime);
				timeF.setText(String.format("%02d", displayList[0]) + ":" + String.format("%02d", displayList[1]) + ":"
						+ String.format("%02d", displayList[2]));
				if (curTime == 0) {
					clockTimeReached();
				}
				df.setFilter(true);
			}
		};

		startB = new JButton("Start");
		startB.setBounds(317, 93 + position * 60, 76, 23);
		if (time.equals("00:00:00")) {
			startB.setEnabled(false);
		}
		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (playingSound == false) {
					startClicked = true;
					timeF.setForeground(Color.BLACK);
					timer = new Timer(1000, countdownDisplay);
					timer.start();
					startB.setEnabled(false);
					pauseB.setEnabled(true);
				} else {
					clipFinished();
					startB.setEnabled(false);
				}
			}
		});
		frame.getContentPane().add(startB);

		// Pauses button - pauses clock timer
		ActionListener pauseListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				startB.setEnabled(true);
				pauseB.setEnabled(false);
			}
		};

		pauseB = new JButton("Pause");
		pauseB.setEnabled(false);
		pauseB.addActionListener(pauseListener);
		pauseB.setBounds(317, 127 + position * 60, 76, 21);
		frame.getContentPane().add(pauseB);
	}

	@Override
	public String[] getSaveFields() {
		String[] saveFields = new String[3];
		saveFields[0] = "clock";
		saveFields[1] = this.getNameF();
		saveFields[2] = this.getTimeF();
		return saveFields;
	}
}