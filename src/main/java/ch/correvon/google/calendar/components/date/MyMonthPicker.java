/*
 * Created by JFormDesigner on Thu Nov 26 11:37:07 CET 2015
 */

package ch.correvon.google.calendar.components.date;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import ch.correvon.google.calendar.components.spinner.LeftRightComboSpinner;
import ch.correvon.google.calendar.components.spinner.LoopListener;
import ch.correvon.google.calendar.components.spinner.SpinnerCircularListModel;

public class MyMonthPicker extends LeftRightComboSpinner implements LoopListener
{
	private static final List<String> months = new ArrayList<>(12);
	static
	{
		for(String month:new DateFormatSymbols().getMonths())
			if(month != null && !month.isEmpty()) // Il y a un mois vide après décembre O_o
				months.add(month);
	}

	public MyMonthPicker()
	{
		this(Calendar.getInstance().get(Calendar.MONTH));
	}

	public MyMonthPicker(int month)
	{
		super(new SpinnerCircularListModel(months));
		this.model = (SpinnerCircularListModel)super.getModel();
		this.model.addLoopListener(this);
		this.setMonth(month);
		this.yearPicker = null;
	}

	public void setMonth(int month)
	{
		super.setValue(month);
	}

	public int getMonth()
	{
		return (Integer)super.getValue();
	}
	
	public void nextMonth()
	{
		this.model.setValue(this.model.getNextValue());
	}
	
	public void setYearPicker(MyYearPicker yearPicker)
	{
		this.yearPicker = yearPicker;
	}
	
	@Override public void looped(int sens)
	{
		if(this.yearPicker != null)
			this.yearPicker.setYear(this.yearPicker.getYear() + sens);
	}
	
	private MyYearPicker yearPicker;
	private SpinnerCircularListModel model;
}