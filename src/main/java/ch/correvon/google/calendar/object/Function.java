package ch.correvon.google.calendar.object;

import java.awt.Color;
import java.util.List;
import ch.correvon.google.calendar.window.mainWindow.Shortcut;

public class Function
{
	public Function(String label, String description, Shortcut shortcut, Color color)
	{
		this.label = label;
		this.description = description;
		this.shortcut = shortcut;
		this.color = color;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
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

	private Color color;
	private String label;
	private String description;
	private Shortcut shortcut;
	private List<Reminder> reminders;
}