package ch.correvon.google.calendar.components.spinner;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerListModel;

// http://www.java2s.com/Tutorial/Java/0240__Swing/CreatingaSpinnerListModelThatLoopsThroughItsValues.htm
public class SpinnerCircularListModel extends SpinnerListModel
{
	public SpinnerCircularListModel(Object[] items)
	{
		super(items);
		this.listeners = new ArrayList<>();
	}

	public SpinnerCircularListModel(List<?> items)
	{
		super(items);
		this.listeners = new ArrayList<>();
	}

	@Override public Object getNextValue()
	{
		List<?> list = super.getList();
		int index = list.indexOf(super.getValue());

		if(index >= list.size() - 1)
		{
			index = 0;
			notifyListeners(1);
		}
		else
			index++;
		
		//index = (index >= list.size() - 1) ? 0 : index + 1;
		return list.get(index);
	}

	@Override public Object getPreviousValue()
	{
		List<?> list = super.getList();
		int index = list.indexOf(super.getValue());

		if(index <= 0)
		{
			index = list.size() - 1;
			notifyListeners(-1);
		}
		else
			index--;

//		index = (index <= 0) ? list.size() - 1 : index - 1;
		return list.get(index);
	}
	
	public void addLoopListener(LoopListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeLoopListener(LoopListener listener)
	{
		for(LoopListener l:this.listeners)
			if(listener == l)
				this.listeners.remove(listener);
	}
	
	private void notifyListeners(int sens)
	{
		for(LoopListener listener:this.listeners)
			listener.looped(sens);
	}
	
	private List<LoopListener> listeners;
}
