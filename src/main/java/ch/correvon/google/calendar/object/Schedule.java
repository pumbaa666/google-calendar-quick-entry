package ch.correvon.google.calendar.object;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import ch.correvon.google.calendar.window.mainWindow.Shortcut;

public class Schedule
{
	public Schedule(String label, String description, Date start, Date end, Shortcut shortcut, Color color)
	{
		this.label = label;
		this.description = description;
		this.start = start;
		this.end = end;
		this.shortcut = shortcut;
		this.color = color;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getStart()
	{
		return start;
	}

	public void setStart(Date start)
	{
		this.start = start;
	}

	public Date getEnd()
	{
		return end;
	}

	public void setEnd(Date end)
	{
		this.end = end;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public Shortcut getShortcut()
	{
		return shortcut;
	}

	public void setShortcut(Shortcut shortcut)
	{
		this.shortcut = shortcut;
	}

	public List<Reminder> getReminders()
	{
		return reminders;
	}

	public void setReminders(List<Reminder> reminders)
	{
		this.reminders = reminders;
	}

	@Override public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.label + ":");
		sb.append(this.description + ";");
		sb.append(SCHEDULE_SDF.format(this.start) + ";");
		sb.append(SCHEDULE_SDF.format(this.end) + ";");

		if(this.shortcut != null)
			sb.append(this.shortcut.toIntegerString());
		sb.append(";");
		
		if(this.color != null)
		{
			sb.append(this.color.getRed() + ",");
			sb.append(this.color.getGreen() + ",");
			sb.append(this.color.getBlue());
		}
		sb.append(";");
		
		if(this.reminders != null && this.reminders.size() > 0)
		{
			for(Reminder reminder:this.reminders)
				sb.append(reminder.toString());
			sb.append(";");
		}
		
		return sb.toString();
	}

	private String label;
	private String description;
	private Date start;
	private Date end;
	private Color color;
	private Shortcut shortcut;
	private List<Reminder> reminders;
	
	public static final SimpleDateFormat SCHEDULE_SDF = new SimpleDateFormat("HH:mm");
}
