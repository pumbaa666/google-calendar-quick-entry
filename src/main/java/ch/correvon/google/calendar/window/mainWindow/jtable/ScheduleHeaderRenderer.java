package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

// http://stackoverflow.com/questions/7493369/jtable-right-align-header/7494597#7494597
public class ScheduleHeaderRenderer implements TableCellRenderer
{
	public ScheduleHeaderRenderer(JTable table)
	{
		this.renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
		this.renderer.setHorizontalAlignment(JLabel.LEFT);
	}

	@Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Component c = this.renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		if(!(c instanceof DefaultTableCellRenderer))
			return c;
		
		return c;
	}
	
	private DefaultTableCellRenderer renderer;
}