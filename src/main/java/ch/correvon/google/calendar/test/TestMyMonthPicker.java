/*
 * Created by JFormDesigner on Thu Nov 26 11:01:00 CET 2015
 */

package ch.correvon.google.calendar.test;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import ch.correvon.google.calendar.components.JComboBoxLight;

public class TestMyMonthPicker extends JPanel
{
	public TestMyMonthPicker()
	{
		this.initComponents();
		this.myInitComponents();
	}

	private void buttonPreviousActionPerformed(ActionEvent e)
	{
		this.setMonth(this.getMonth() - 1);
	}

	private void buttonNextActionPerformed(ActionEvent e)
	{
		this.setMonth(this.getMonth() + 1);
	}

	public void addActionListener(ActionListener listener)
	{
		this.comboMonth.addActionListener(listener);
	}

	public void removeActionListener(ActionListener listener)
	{
		this.comboMonth.removeActionListener(listener);
	}

	@Override public void setEnabled(boolean enabled)
	{
		this.buttonPrevious.setEnabled(enabled);
		this.comboMonth.setEnabled(enabled);
		this.buttonNext.setEnabled(enabled);
	}

	public int getMonth()
	{
		return this.comboMonth.getSelectedIndex();
	}

	public void setMonth(int month)
	{
		if(month < 0 || month >= this.monthModel.getSize())
			return;

		this.comboMonth.setSelectedIndex(month);
	}
	
	@Override public void setFont(Font font)
	{
		if(this.comboMonth != null)
			this.comboMonth.setFont(font);
		else
			super.setFont(font);
	}
	
	@Override public Font getFont()
	{
		if(this.comboMonth != null)
			return this.comboMonth.getFont();
		return super.getFont();
	}

	@SuppressWarnings("all")// A cause du fuckin watermark
	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - abcdeffff ghijkllll
		this.buttonPrevious = new JButton();
		this.comboMonth = new JComboBoxLight<>();
		this.buttonNext = new JButton();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new FormLayout(
			"default, default:grow, default",
			"fill:default:grow"));

		//---- buttonPrevious ----
		this.buttonPrevious.setText("<");
		this.buttonPrevious.setBackground(new Color(238, 238, 238));
		this.buttonPrevious.setMargin(new Insets(0, 4, 0, 4));
		this.buttonPrevious.setRolloverEnabled(false);
		this.buttonPrevious.setRequestFocusEnabled(false);
		this.buttonPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPreviousActionPerformed(e);
			}
		});
		add(this.buttonPrevious, CC.xy(1, 1));
		add(this.comboMonth, CC.xy(2, 1));

		//---- buttonNext ----
		this.buttonNext.setText(">");
		this.buttonNext.setBackground(new Color(238, 238, 238));
		this.buttonNext.setMargin(new Insets(0, 4, 0, 4));
		this.buttonNext.setRolloverEnabled(false);
		this.buttonNext.setRequestFocusEnabled(false);
		this.buttonNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonNextActionPerformed(e);
			}
		});
		add(this.buttonNext, CC.xy(3, 1));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void myInitComponents()
	{
		// Vire le fuckin watermark
		for(PropertyChangeListener listener:this.getPropertyChangeListeners())
			this.removePropertyChangeListener(listener);
		this.setBorder(null);

		// Model
		this.monthModel = new DefaultComboBoxModel<>();
		for(String month:new DateFormatSymbols().getMonths())
			// http://stackoverflow.com/questions/16828176/set-all-months-to-a-list-in-java
			if(month != null && !month.isEmpty()) // Il existe un mois vide après décembre O_o
				this.monthModel.addElement(month);
		this.comboMonth.setModel(this.monthModel);
		this.comboMonth.setMaximumRowCount(this.monthModel.getSize()); // http://stackoverflow.com/questions/5552030/jcombobox-drop-down-sheet-size
		
		// Date du jour
		this.setMonth(Calendar.getInstance().get(Calendar.MONTH));
	}

	public static void main(String[] args)
	{
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(600, 200);
		frame.setSize(200, 200);

		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		panel.setLayout(layout);

		JButton buttonExit = new JButton("Quitter");
		buttonExit.addActionListener(new ActionListener()
			{
				@Override public void actionPerformed(ActionEvent arg0)
				{
					frame.dispose();
				}
			});
		panel.add(buttonExit);

		TestMyMonthPicker monthPicker = new TestMyMonthPicker();
		panel.add(monthPicker);

		frame.setContentPane(panel);

		frame.setVisible(true);
	}

	private DefaultComboBoxModel<String> monthModel;

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - abcdeffff ghijkllll
	private JButton buttonPrevious;
	private JComboBoxLight<String> comboMonth;
	private JButton buttonNext;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
