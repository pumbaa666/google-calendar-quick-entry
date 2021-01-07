package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.Schedule;
import ch.correvon.google.calendar.window.mainWindow.Shortcut;


/**
 * @author Loic Correvon
 */
public class ScheduleModel extends AbstractTableModel
{
	public ScheduleModel()
	{
		this.schedules = new ArrayList<>();
	}
	
	public List<Schedule> getSchedules()
	{
		return this.schedules;
	}
	
	public Schedule getSchedule(int row)
	{
		return this.schedules.get(row);
	}
	
	public void add(Schedule schedule)
	{
		this.schedules.add(schedule);
		int currentSize = this.schedules.size() - 1;
		super.fireTableRowsInserted(currentSize, currentSize);
	}
	
	public void remove(int row)
	{
		this.schedules.remove(row);
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
			case 4 : return Date.class;
			case 5 : return Date.class;
			case 6 : return Shortcut.class;
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
		return this.schedules.size();
	}

	@Override public Object getValueAt(int row, int column)
	{
		Schedule schedule = this.schedules.get(row);
		switch(column)
		{
			case 0 : return schedule.getColor();
			case 1 : return schedule.getReminders();
			case 2 : return schedule.getLabel();
			case 3 : return schedule.getDescription();
			case 4 : return schedule.getStart();
			case 5 : return schedule.getEnd();
			case 6 : return schedule.getShortcut();
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
		
		Schedule oldSchedule = this.schedules.get(row);
		switch(column)
		{
			case 0 : oldSchedule.setColor((Color)value); break;
			case 1 : oldSchedule.setReminders((List<Reminder>)value); break;
			case 2 : oldSchedule.setLabel((String)value); break;
			case 3 : oldSchedule.setDescription((String)value); break;
			
			case 4 :
				try
				{
					oldSchedule.setStart(Schedule.SCHEDULE_SDF.parse((String)value));
				}
				catch(ParseException e)
				{
				}
				break;
			
			case 5 :
				try
				{
					oldSchedule.setEnd(Schedule.SCHEDULE_SDF.parse((String)value));
				}
				catch(ParseException e)
				{
				}
				break;
			
			case 6 : oldSchedule.setShortcut((Shortcut)value); break;
		}
	}
	
	private List<Schedule> schedules;
	
	private static final String[] columns = {"", "R", "Label", "Desc.", "Début", "Fin", "Racc."};
}
