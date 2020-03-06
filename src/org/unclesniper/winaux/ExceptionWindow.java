package org.unclesniper.winaux;

import java.awt.Font;
import java.awt.Window;
import java.awt.Insets;
import javax.swing.Icon;
import java.awt.Container;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import javax.swing.WindowConstants;
import java.util.function.Consumer;
import java.awt.event.WindowAdapter;

public class ExceptionWindow extends JDialog {

	private static class OnCloseAdapter extends WindowAdapter {

		private final Runnable task;

		public OnCloseAdapter(Runnable task) {
			this.task = task;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			task.run();
		}

	}

	private static final String ENDL = System.getProperty("line.separator");

	private final Throwable exception;

	public ExceptionWindow(Throwable exception, Consumer<ExceptionWindow> onClose) {
		super((Window)null, "Error - JWinAux");
		this.exception = exception;
		Container content = getContentPane();
		Icon icon = UIManager.getIcon("OptionPane.errorIcon");
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.insets = new Insets(3, 3, 3, 3);
		content.add(new JLabel("An error occurred!", icon, JLabel.LEADING), c);
		JTextArea area = new JTextArea();
		area.setLineWrap(false);
		area.setEditable(false);
		Font oldFont = area.getFont();
		area.setFont(new Font(Font.MONOSPACED, oldFont.getStyle(), oldFont.getSize()));
		area.setTabSize(4);
		area.setText(ExceptionWindow.throwableToString(exception));
		JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(800, 600));
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		content.add(scroll, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.0;
		JButton clbut = new JButton("Close");
		clbut.addActionListener(env -> {
			if(onClose != null)
				onClose.accept(this);
			else
				dispose();
		});
		content.add(clbut, c);
		pack();
		if(onClose == null)
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		else
			addWindowListener(new OnCloseAdapter(() -> onClose.accept(this)));
	}

	public Throwable getException() {
		return exception;
	}

	public static void showException(Throwable exception, Consumer<ExceptionWindow> onClose) {
		if(!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> ExceptionWindow.showException(exception, onClose));
			return;
		}
		new ExceptionWindow(exception, onClose).setVisible(true);
	}

	private static String throwableToString(Throwable t) {
		StringBuilder build = new StringBuilder();
		String firstsep = null;
		do {
			if(firstsep == null)
				firstsep = "caused by: ";
			else
				build.append(firstsep);
			build.append(t.toString());
			build.append(ExceptionWindow.ENDL);
			for(StackTraceElement frame : t.getStackTrace()) {
				build.append("\tat ");
				build.append(frame.toString());
				build.append(ExceptionWindow.ENDL);
			}
			t = t.getCause();
		} while(t != null);
		return build.toString();
	}

}
