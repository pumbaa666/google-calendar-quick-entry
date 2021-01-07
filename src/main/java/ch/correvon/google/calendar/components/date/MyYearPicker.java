/*
 * Created by JFormDesigner on Thu Nov 26 11:37:07 CET 2015
 */

package ch.correvon.google.calendar.components.date;

import java.util.Calendar;

import javax.swing.JSpinner;
import ch.correvon.google.calendar.components.spinner.LeftRightSpinner;

public class MyYearPicker extends LeftRightSpinner
{
	public MyYearPicker()
	{
		this(Calendar.getInstance().get(Calendar.YEAR));
	}

	public MyYearPicker(int year)
	{
		this.setYear(year);
		super.setEditor(new JSpinner.NumberEditor(this, "#")); // http://stackoverflow.com/questions/8440754/remove-comma-from-jspinner
	}

	public void setYear(int year)
	{
		super.setValue(year);
	}

	public int getYear()
	{
		return (Integer)super.getValue();
	}
}