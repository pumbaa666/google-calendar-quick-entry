package ch.correvon.google.calendar.window.eventAlertWindow.jtable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.ReminderType;


/**
 * @author Loic Correvon
 */
public class ReminderModel extends AbstractTableModel
{
	public ReminderModel(List<Reminder> reminders)
	{
		if(reminders == null)
			this.reminders = new ArrayList<Reminder>();
		else
			this.reminders = reminders;
	}
	
	public List<Reminder> getReminders()
	{
		return this.reminders;
	}
	
	public Reminder getReminder(int row)
	{
		return this.reminders.get(row);
	}
	
	public void add(Reminder reminder)
	{
		this.reminders.add(reminder);
		int currentSize = this.reminders.size() - 1;
		super.fireTableRowsInserted(currentSize, currentSize);
	}
	
	public void remove(int row)
	{
		this.reminders.remove(row);
		super.fireTableRowsDeleted(row, row);
	}

	@Override public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 0 : return ReminderType.class;
			case 1 : return Integer.class;
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
		return this.reminders.size();
	}

	@Override public Object getValueAt(int row, int column)
	{
		Reminder reminder = this.reminders.get(row);
		switch(column)
		{
			case 0 : return reminder.getType();
			case 1 : return reminder.getTime();
		}
		return null;
	}

	@Override public boolean isCellEditable(int row, int column)
	{
		return true;
	}

	@Override public void setValueAt(Object value, int row, int column)
	{
		if(value == null)
			return;
		
		Reminder oldReminder = this.reminders.get(row);
		switch(column)
		{
			case 0 : oldReminder.setType((ReminderType)value); break;
			case 1 : oldReminder.setTime((Integer)value); break;
		}
	}
	
	private List<Reminder> reminders;
	
	private static final String[] columns = {"Alerte", "Minutes"};
}
