package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import ch.correvon.google.calendar.object.Schedule;

public class TimeEditor extends DefaultCellEditor implements KeyListener
{
	public TimeEditor(JTextField textField)
	{
		super(textField);
	}

	@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if(value instanceof Date)
		{
			Date date = (Date)value;
			String str = Schedule.SCHEDULE_SDF.format(date);
			return super.getTableCellEditorComponent(table, str, isSelected, row, column);
		}
		
		Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		if (c instanceof JTextComponent)
		{
			// Source : http://stackoverflow.com/questions/4611363/how-to-select-all-text-in-jtable-cell-when-editing
			final JTextComponent jtc = (JTextComponent)c;
			jtc.requestFocus();
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					jtc.selectAll();
				}
			});
		}
		c.addKeyListener(this);
		return c;
	}
	
	@Override public void keyPressed(KeyEvent e){}
	@Override public void keyReleased(KeyEvent e){}
	@Override public void keyTyped(KeyEvent e){}

	@Override public boolean isCellEditable(EventObject e)
	{
		if(e instanceof KeyEvent) // Do not start editing on ctrl-s, ESC, etc... Only on double-click (MouseEvent) or F2-key (ActionEvent)
			return false;
		return super.isCellEditable(e);
	}
}
