package timer;


import javax.swing.JTextField;

public interface ComponentIn {

	public TimerUI gettUI();
	
	// repositions the clock when an above clock is removed
	void reposition();

	// removes this clock or all clocks and repositions others
	public void delete(boolean all);

	void init();
	
	String[] getSaveFields();

	public void settUI(TimerUI temp);

	public JTextField getNameBox();

	public int getPosition();
	
}