package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import ch.correvon.google.calendar.object.Function;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.window.mainWindow.Shortcut;


/**
 * @author Loic Correvon
 */
public class FunctionModel extends AbstractTableModel
{
	public FunctionModel()
	{
		this.functions = new ArrayList<>();
	}
	
	public List<Function> getFunctions()
	{
		return this.functions;
	}
	
	public Function getFunction(int row)
	{
		return this.functions.get(row);
	}
	
	public void add(Function function)
	{
		this.functions.add(function);
		int currentSize = this.functions.size() - 1;
		super.fireTableRowsInserted(currentSize, currentSize);
	}

	public void remove(int row)
	{
		this.functions.remove(row);
		super.fireTableRowsDeleted(row, row);
	}

	@Override public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 0 : return Color.class;
			case 1 : return List.class;
			case 2 : return String.class;
			case 3 : return String.class;
			case 4 : return Integer.class;
		}
		return null;
	}

	@Override public int getColumnCount()
	{
		return columns.length;
	}

	@Override public String getColumnName(int column)
	{
		return columns[column];
	}

	@Override public int getRowCount()
	{
		return this.functions.size();
	}

	@Override public Object getValueAt(int row, int column)
	{
		Function function = this.functions.get(row);
		switch(column)
		{
			case 0 : return function.getColor();
			case 1 : return function.getReminders();
			case 2 : return function.getLabel();
			case 3 : return function.getDescription();
			case 4 : return function.getShortcut();
		}
		return null;
	}

	@Override public boolean isCellEditable(int row, int column)
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override public void setValueAt(Object value, int row, int column)
	{
		if(value == null)
			return;
		
		Function oldFunction = this.functions.get(row);
		switch(column)
		{
			case 0 : oldFunction.setColor((Color)value); break;
			case 1 : oldFunction.setReminders((List<Reminder>)value); break;
			case 2 : oldFunction.setLabel((String)value); break;
			case 3 : oldFunction.setDescription((String)value); break;
			case 4 : oldFunction.setShortcut((Shortcut)value); break;
		}
	}
	
	private List<Function> functions;
	
	private static final String[] columns = {"", "R", "Label", "Description", "Raccourci"};
}