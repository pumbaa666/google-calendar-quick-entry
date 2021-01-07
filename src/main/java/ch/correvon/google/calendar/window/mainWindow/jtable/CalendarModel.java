package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import com.google.api.services.calendar.model.CalendarListEntry;

public class CalendarModel extends AbstractListModel<CalendarListEntry> implements ComboBoxModel<CalendarListEntry>
{
	public CalendarModel()
	{
		this.calendars = new ArrayList<>();
	}
	
	public List<CalendarListEntry> getCalendars()
	{
		return this.calendars;
	}
	
	public void removeAll()
	{
		int size = this.calendars.size();
		this.calendars.clear();
		super.fireIntervalRemoved(this.calendars, 0, size);
	}
	
	public void add(CalendarListEntry calendar)
	{
		this.calendars.add(calendar);
		
		int size = this.calendars.size();
		super.fireIntervalAdded(this.calendars, size, size);
	}

	@Override public CalendarListEntry getSelectedItem()
	{
		return this.selectedItem;
	}

	@Override public void setSelectedItem(Object newValue)
	{
		this.selectedItem = (CalendarListEntry)newValue;
		if(this.selectedItem == null)
			return;
		super.fireContentsChanged(this.selectedItem, 0, this.getSize());
	}

	@Override public int getSize()
	{
		return this.calendars.size();
	}

	@Override public CalendarListEntry getElementAt(int i)
	{
		return this.calendars.get(i);
	}
	
	private List<CalendarListEntry> calendars;
	private CalendarListEntry selectedItem;
}
