package timer;

import java.io.Serializable;
import javax.swing.JCheckBox;

public class Taskbox extends AbsComponent implements Serializable, ComponentIn {

	private static final long serialVersionUID = 1L;
	private JCheckBox checkbox;
	private boolean complete;
	
	public JCheckBox getCheckbox() {
		return checkbox;
	}
	public void setCheckbox(JCheckBox checkbox) {
		this.checkbox = checkbox;
	}
	
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public Taskbox(String name, TimerUI tUi) {
		this.name = name;
		this.tUI = tUi;
	}
	
	public Taskbox(int position, String name, boolean completed, TimerUI ui) {
		this.position = position;
		this.name = name;
		this.complete = completed;
		this.tUI = ui;
	}
	
	public void reposition() {
		if (position != 0) {
			position -= 1;
		}
		deleteB.setBounds(405, 111 + position * 60, 19, 23);
		checkbox.setBounds(133, 90 + position * 60, 180, 58);
		nameF.setBounds(37, 90 + position * 60, 86, 58);
	}
	
	// removes this clock or all clocks and repositions others
	public void delete(boolean all) {
		if (all == false) {
			tUI.reposition(this);
		}
		frame.remove(nameF);
		frame.remove(checkbox);
		frame.remove(deleteB);
		frame.revalidate();
		frame.repaint();
	} 
	
	public void init() {
		
		super.init();
		
		checkbox = new JCheckBox();
		if (complete) {
			checkbox.setSelected(true);
		}
		checkbox.setBounds(133, 90 + position * 60, 180, 58);
		frame.getContentPane().add(checkbox);		
	}
	@Override
	public String[] getSaveFields() {
		String[] saveFields = new String[3];
		saveFields[0] = "timer";
		saveFields[1] = this.getNameF();
		saveFields[2] = (checkbox.isSelected() ? "y" : "n");
		return saveFields;
	}
}
