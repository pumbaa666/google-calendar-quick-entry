package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import ch.correvon.google.calendar.object.Function;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.Schedule;
import ch.correvon.google.calendar.window.eventAlertWindow.EventAlertWindow;

/*
 * http://www.java2s.com/Tutorial/Java/0240__Swing/ColorChooserEditor.htm
 */
public class ReminderEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
	public ReminderEditor(String type)
	{
		this.type = type;
		this.delegate = new JButton();
		this.delegate.addActionListener(this);
	}

	@Override public void actionPerformed(ActionEvent actionEvent)
	{
		String desc = "";
		if(this.schedule != null)
			desc = this.schedule.getLabel() + " : " + this.schedule.getDescription() + ", " + Schedule.SCHEDULE_SDF.format(this.schedule.getStart()) + " - " + Schedule.SCHEDULE_SDF.format(this.schedule.getEnd());
		else if(this.function != null)
			desc = this.function.getLabel() + " : " + this.function.getDescription();

		new EventAlertWindow(this, this.type, desc, this.reminders).run();
	}

	@Override public Object getCellEditorValue()
	{
		return this.reminders;
	}

	public void changeReminders(List<Reminder> reminders)
	{
		this.reminders = reminders;
		this.delegate.setIcon(ReminderRenderer.getImageIcon(reminders));
		
		if(this.schedule != null)
			this.schedule.setReminders(reminders);
		else if(this.function != null)
			this.function.setReminders(reminders);
		
		if(this.table != null && this.table.getCellEditor() != null)
			this.table.getCellEditor().stopCellEditing();
	}

	@SuppressWarnings("unchecked")
	@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		this.table = table;
		TableModel model = table.getModel();
		if(model instanceof ScheduleModel)
		{
			ScheduleModel scheduleModel = (ScheduleModel)model;
			this.schedule = scheduleModel.getSchedule(row);
		}
		else if(model instanceof FunctionModel)
		{
			FunctionModel functionModel = (FunctionModel)model;
			this.function = functionModel.getFunction(row);
		}

		this.changeReminders((List<Reminder>)value);
		return this.delegate;
	}

	private Schedule schedule;
	private Function function;
	private String type;
	private JButton delegate;
	private List<Reminder> reminders;
	private JTable table;
}