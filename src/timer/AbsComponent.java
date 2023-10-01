package timer;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public abstract class AbsComponent extends JComponent {
	
	private static final long serialVersionUID = 1L;
	protected int position;
	protected String name;
	protected JTextField nameF;
	protected TimerUI tUI;
	protected JButton deleteB;
	protected JFrame frame = new JFrame();
	
	public TimerUI gettUI() {
		return tUI;
	}

	public void settUI(TimerUI tUI) {
		this.tUI = tUI;
	}
	
	public int getPosition() {
		return position;
	}
	
	protected void setFrame(JFrame frame) {
		this.frame = frame;
	}
	

	protected String getNameF() {
		return nameF.getText();
	}
	
	public JTextField getNameBox() {
		return nameF;
	}
	
	protected void reposition() {}
	
	public void delete(boolean all) {}
	
	Component[] subComponents() {
		return null;
	} 
	
	// Component name field
	protected void initNameBox() {
		nameF = new JTextField();
		nameF.setBounds(37, 90 + position * 60, 86, 58);
		nameF.setBorder(BorderFactory.createEmptyBorder());
		nameF.setBackground(frame.getBackground());
		nameF.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(nameF);
		nameF.setColumns(10);
		nameF.setText(name);
	}
	
	protected void initDeleteBox() {
		// Delete clock button
		deleteB = new JButton("-");
		deleteB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete(false);
			}
		});
		deleteB.setFont(new Font("Tahoma", Font.PLAIN, 6));
		deleteB.setBounds(405, 111 + position * 60, 19, 23);
		frame.getContentPane().add(deleteB);
	}
	
	protected void init() {
		initNameBox();
		initDeleteBox();
	}
}
