package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * http://www.java2s.com/Tutorial/Java/0240__Swing/ColorChooserEditor.htm
 */
public class ColorChooserRenderer extends DefaultTableCellRenderer
{
	@Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column); // Passer null à la place de value pour ne pas avoir la string de Color affichée.
		c.setBackground((Color)value);
		return c;
	}
}