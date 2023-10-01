package timer;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocFilter extends DocumentFilter {

	private boolean filter = true;

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)
			throws BadLocationException {
		if (filter) {
			if (str.matches("[0-9]") && str.length() == 1) {

				String docString = fb.getDocument().getText(0, fb.getDocument().getLength());

				offset = Math.min(5, offset - (offset + 1) / 3);

				StringBuilder sb = new StringBuilder();
				sb.append(docString.substring(0, 2));
				sb.append(docString.substring(3, 5));
				sb.append(docString.substring(6, 8));

				for (int i = 0; i < offset; i++) {
					sb.replace(i, i + 1, Character.toString(sb.charAt(i + 1)));
				}

				sb.replace(Math.min(offset, 5), Math.min(6, offset + 1), str);

				sb.insert(2, ':');
				sb.insert(5, ':');

				super.replace(fb, 0, 8, sb.toString(), a);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} else {
			super.replace(fb, 0, 8, str, a);
		}
	}

	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		if (text.charAt(offset) == ':') {
			return;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) != ':') {
					sb.append(text.charAt(i));
				}
			}
		}
	}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		System.out.println("insert");
	}
}