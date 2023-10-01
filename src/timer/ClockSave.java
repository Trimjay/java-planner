package timer;

import java.io.Serializable;

public class ClockSave implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String time;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public ClockSave(String name, String time) {
		this.name = name;
		this.time = time;
	}
	
}
