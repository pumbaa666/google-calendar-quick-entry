package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/*
 * http://www.java2s.com/Tutorial/Java/0240__Swing/ColorChooserEditor.htm
 */
public class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
	public ColorChooserEditor()
	{
		this.delegate = new JButton();
		this.delegate.addActionListener(this);
	}

	@Override public void actionPerformed(ActionEvent actionEvent)
	{
		Color color = JColorChooser.showDialog(ColorChooserEditor.this.delegate, "Color Chooser", ColorChooserEditor.this.savedColor);
		this.changeColor(color);
		if(color != null && this.table != null)
			this.table.getCellEditor().stopCellEditing(); // Valide la couleur dès qu'on sort du JColorChooser
	}

	@Override public Object getCellEditorValue()
	{
		return this.savedColor;
	}

	private void changeColor(Color color)
	{
		if(color != null)
		{
			this.savedColor = color;
			this.delegate.setBackground(color);
		}
	}

	@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		this.table = table;
		this.changeColor((Color)value);
		return this.delegate;
	}

	private JButton delegate;
	private Color savedColor;
	private JTable table;
}