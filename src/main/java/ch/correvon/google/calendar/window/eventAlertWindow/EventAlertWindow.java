/*
 * Created by JFormDesigner on Fri Dec 18 10:35:46 CET 2015
 */

package ch.correvon.google.calendar.window.eventAlertWindow;

import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.ReminderType;
import ch.correvon.google.calendar.window.eventAlertWindow.jtable.ReminderModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.ReminderEditor;

/**
 * @author merdea lafin
 */
public class EventAlertWindow extends JDialog
{
	public EventAlertWindow(ReminderEditor editor, String type, String desc, List<Reminder> reminders)
	{
		this.editor = editor;

		this.initComponents();
		this.myInit(type, desc, reminders);
		
		super.setVisible(false);
	}
	
	public void run()
	{
		super.setVisible(true);
	}

	private void buttonOkActionPerformed(ActionEvent e)
	{
		this.editor.changeReminders(this.reminderModel.getReminders());
		super.dispose();
	}

	private void buttonCancelActionPerformed(ActionEvent e)
	{
		super.dispose();
	}

	private void buttonAddReminderActionPerformed(ActionEvent e)
	{
		this.reminderModel.add(new Reminder(ReminderType.EMAIL, 60));
	}

	private void buttonRemoveReminderActionPerformed(ActionEvent e)
	{
		int[] rows = this.table.getSelectedRows();
		if(rows == null)
			return;
		
		for(int i = rows.length-1; i >= 0; i--)
			this.reminderModel.remove(rows[i]);
	}

	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - merdea lafin
		this.labelType = new JLabel();
		JButton buttonAddReminder = new JButton();
		JButton buttonRemoveReminder = new JButton();
		this.labelDesc = new JLabel();
		this.scrollPane1 = new JScrollPane();
		this.table = new JTable();
		JButton buttonOk = new JButton();
		JButton buttonCancel = new JButton();

		//======== this ========
		setTitle("Alertes de rendez-vous");
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, default:grow, $lcgap, 22dlu, 2*($lcgap, default), $lcgap, $ugap",
			"$ugap, 2*($lgap, default), $lgap, fill:default:grow, $lgap, default, $lgap, $ugap"));
		((FormLayout)contentPane.getLayout()).setColumnGroups(new int[][] {{9, 11}});

		//---- labelType ----
		this.labelType.setText("Horaires");
		this.labelType.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(this.labelType, CC.xywh(3, 3, 3, 1));

		//---- buttonAddReminder ----
		buttonAddReminder.setText("+");
		buttonAddReminder.setMargin(new Insets(0, 2, 0, 2));
		buttonAddReminder.setToolTipText("Ajouter un horaire");
		buttonAddReminder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAddReminderActionPerformed(e);
			}
		});
		contentPane.add(buttonAddReminder, CC.xy(9, 3));

		//---- buttonRemoveReminder ----
		buttonRemoveReminder.setText("-");
		buttonRemoveReminder.setMargin(new Insets(0, 2, 0, 2));
		buttonRemoveReminder.setToolTipText("Supprimer un horaire");
		buttonRemoveReminder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemoveReminderActionPerformed(e);
			}
		});
		contentPane.add(buttonRemoveReminder, CC.xy(11, 3));

		//---- labelDesc ----
		this.labelDesc.setText("description");
		contentPane.add(this.labelDesc, CC.xywh(3, 5, 9, 1));

		//======== scrollPane1 ========
		{
			this.scrollPane1.setViewportView(this.table);
		}
		contentPane.add(this.scrollPane1, CC.xywh(3, 7, 9, 1));

		//---- buttonOk ----
		buttonOk.setText("Ok");
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOkActionPerformed(e);
			}
		});
		contentPane.add(buttonOk, CC.xywh(3, 9, 3, 1));

		//---- buttonCancel ----
		buttonCancel.setText("Annuler");
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCancelActionPerformed(e);
			}
		});
		contentPane.add(buttonCancel, CC.xywh(7, 9, 5, 1));
		setSize(200, 275);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	private void myInit(String type, String desc, List<Reminder> reminders)
	{
		this.labelType.setText(type);
		this.labelDesc.setText(desc);
		
		this.table.setRowHeight(20);
		
		this.reminderModel = new ReminderModel(reminders);
		this.table.setModel(this.reminderModel);
		
		JComboBox<ReminderType> combo = new JComboBox<>();
		combo.addItem(ReminderType.EMAIL);
		combo.addItem(ReminderType.POPUP);
		this.table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo));
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - merdea lafin
	private JLabel labelType;
	private JLabel labelDesc;
	private JScrollPane scrollPane1;
	private JTable table;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	private ReminderModel reminderModel;
	private ReminderEditor editor;
}
