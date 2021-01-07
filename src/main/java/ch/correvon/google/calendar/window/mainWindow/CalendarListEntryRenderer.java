package ch.correvon.google.calendar.window.mainWindow;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.google.api.services.calendar.model.CalendarListEntry;

public class CalendarListEntryRenderer implements ListCellRenderer<CalendarListEntry>
{
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override public Component getListCellRendererComponent(JList<? extends CalendarListEntry> list, CalendarListEntry value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel renderer = (JLabel)this.defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if(value != null)
			renderer.setText(value.getSummary());
		return renderer;
	}
}