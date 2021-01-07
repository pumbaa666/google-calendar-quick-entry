package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import ch.correvon.google.calendar.object.Schedule;

public class TimeRenderer extends DefaultTableCellRenderer
{
	// http://stackoverflow.com/questions/2412007/format-date-in-jtable-resultset
	@Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		String valueStr = Schedule.SCHEDULE_SDF.format((Date)value);
		return super.getTableCellRendererComponent(table, valueStr, isSelected, hasFocus, row, column);
	}
}
