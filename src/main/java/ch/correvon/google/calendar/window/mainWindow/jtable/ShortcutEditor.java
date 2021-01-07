package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import ch.correvon.google.calendar.window.mainWindow.Shortcut;

public class ShortcutEditor extends AbstractCellEditor implements TableCellEditor, KeyListener
{
	public ShortcutEditor()
	{
		this.delegate = new JTextField();
		this.delegate.setBorder(null);
		this.delegate.addKeyListener(this);
	}
	
	@Override public void keyTyped(KeyEvent event){}
	@Override public void keyReleased(KeyEvent event)
	{
		this.delegate.setText(this.savedShortcut.toString()); // Juste pour ne pas écrire le caractère courrant.
	}
	
	@Override public void keyPressed(KeyEvent event)
	{
		this.changeShortcut(new Shortcut(event.getKeyCode(), event.getModifiers()));
	}

	private void changeShortcut(Shortcut shortcut)
	{
		if(shortcut != null)
		{
			this.savedShortcut = shortcut;
			this.delegate.setText(shortcut.toString());
		}
	}

	@Override public Object getCellEditorValue()
	{
		return this.savedShortcut;
	}
	
	@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		this.changeShortcut((Shortcut)value);
		return this.delegate;
	}
	

	@Override public boolean isCellEditable(EventObject e)
	{
		if(e instanceof KeyEvent) // Do not start editing on ctrl-s, ESC, etc... Only on double-click (MouseEvent) or F2-key (ActionEvent)
			return false;
		
		if(e instanceof MouseEvent)
		{
			MouseEvent me = (MouseEvent)e;
			return me.getClickCount() == 2;
		}
		
		return super.isCellEditable(e);
	}
	
	private JTextField delegate;
	private Shortcut savedShortcut;
}
