package ch.correvon.google.calendar.object;

public class Reminder
{
	public Reminder(ReminderType type, int timeInMin)
	{
		this.type = type;
		this.time = timeInMin;
	}

	public ReminderType getType()
	{
		return type;
	}
	
	public void setType(ReminderType type)
	{
		this.type = type;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}
	
	@Override public String toString()
	{
		return this.type.toString() + "+" + this.time + ",";
	}

	private ReminderType type;
	private int time;
}