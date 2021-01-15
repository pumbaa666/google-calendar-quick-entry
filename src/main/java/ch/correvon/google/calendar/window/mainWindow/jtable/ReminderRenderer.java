package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.ReminderType;

/*
 * http://www.java2s.com/Tutorial/Java/0240__Swing/ColorChooserEditor.htm
 */
public class ReminderRenderer extends DefaultTableCellRenderer
{

	private static Log s_logger = LogFactory.getLog(ReminderRenderer.class);
	static final ImageIcon MAIL_ICON = readImageIcon("icon-mail.png");
	static final ImageIcon POPUP_ICON = readImageIcon("icon-popup.png");
	static final ImageIcon MAIL_POPUP_ICON = readImageIcon("icon-mail-popup.png");
	static final ImageIcon EMPTY_ICON = readImageIcon("icon-empty.png");

	@Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		JButton button = new JButton();

		if(value == null)
		{
			button.setIcon(EMPTY_ICON);
			return button;
		}

		@SuppressWarnings("unchecked")
		List<Reminder> reminders = (List<Reminder>)value;
		button.setIcon(getImageIcon(reminders));
		return button;
	}

	private static ImageIcon readImageIcon(String fileName)
	{
		return new ImageIcon(ReminderRenderer.class.getClassLoader().getResource("images/" + fileName));
	}

	public static ImageIcon getImageIcon(List<Reminder> reminders)
	{
		if(reminders == null)
			return EMPTY_ICON;

		boolean hasMail = false;
		boolean hasPopup = false;

		for(Reminder reminder:reminders)
		{
			if(reminder.getType() == ReminderType.EMAIL)
				hasMail = true;
			if(reminder.getType() == ReminderType.POPUP)
				hasPopup = true;
		}

		if(hasMail && hasPopup)
			return MAIL_POPUP_ICON;

		if(hasMail)
			return MAIL_ICON;

		if(hasPopup)
			return POPUP_ICON;

		return EMPTY_ICON;
	}
}