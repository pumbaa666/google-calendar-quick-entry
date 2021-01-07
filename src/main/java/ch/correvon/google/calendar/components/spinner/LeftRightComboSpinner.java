package ch.correvon.google.calendar.components.spinner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;
import ch.correvon.google.calendar.components.JComboBoxLight;

public class LeftRightComboSpinner extends JSpinner implements ChangeListener, ActionListener, PropertyChangeListener
{
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
		panel.add(new LeftRightComboSpinner(new SpinnerCircularListModel(new DateFormatSymbols().getMonths())));
		frame.setContentPane(panel);
		frame.setVisible(true);
	}

//	public LeftRightComboSpinner()
//	{
//		this(new SpinnerListModel());
//	}

	public LeftRightComboSpinner(SpinnerModel model)
	{
		super(model);
		super.setUI(new LeftRightComboSpinnerUI());
		this.addPropertyChangeListener(this);
		this.addChangeListener(this);
		this.installEditor();
	}
	
	@Override public void addChangeListener(ChangeListener listener)
	{
		if(listener instanceof LeftRightComboSpinner)
			super.addChangeListener(listener);
		else
			this.listener = listener;
	}

	@Override public void propertyChange(PropertyChangeEvent evt)
	{
		this.installEditor();
	}

	@SuppressWarnings("unchecked") // Peux pas faire mieux :( : http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
	private void installEditor()
	{
		JComponent editor = super.getEditor();
		if(editor == null)
			return;
		
		if(!(editor instanceof JComboBoxLight))
			return;
		
		this.jcomboBoxEditor = (JComboBoxLight<String>)editor;
	}
	
	@Override public void stateChanged(ChangeEvent e)
	{
		if(this.jcomboBoxEditor == null)
			return;
		
		this.jcomboBoxEditor.setSelectedItem(super.getValue());
		
		if(this.listener != null)
//			try
//			{
				this.listener.stateChanged(e);
//			}
//			catch(IllegalMonitorStateException ex)
//			{
//				ex.printStackTrace();
//			}
//		System.out.println("stateChanged new value : " + super.getValue());
	}

	@Override public void actionPerformed(ActionEvent e)
	{
		if(this.jcomboBoxEditor == null)
			return;
		
		super.setValue(this.jcomboBoxEditor.getSelectedItem());
//		System.out.println("actionPerformed new value : " + super.getValue());
	}
	
	@Override public void setValue(Object value)
	{
		if(this.jcomboBoxEditor == null)
			return;
		
		if(value instanceof Integer)
			this.jcomboBoxEditor.setSelectedIndex((Integer)value);
		else
			super.setValue(value);
//		System.out.println("setValue : "+value.toString());
	}
	
	@Override public Object getValue()
	{
		if(this.jcomboBoxEditor == null)
			return null;
		
		Object value = super.getValue();
//		if(value instanceof Integer)
//			return this.jcomboBoxEditor.getItemAt((Integer)value);
//		
//		return super.getValue();
		
		if(!(value instanceof Integer))
			return this.jcomboBoxEditor.getSelectedIndex();
		
		return super.getValue();
	}

	private JComboBoxLight<String> jcomboBoxEditor;
	private ChangeListener listener; // TODO en faire une liste
}

class LeftRightComboSpinnerUI extends BasicSpinnerUI
{
	public static ComponentUI createUI(JComponent c)
	{
		return new LeftRightComboSpinnerUI();
	}

	@Override protected Component createNextButton()
	{
		Component c = createArrowButton(SwingConstants.EAST);
		c.setName("Spinner.nextButton");
		super.installNextButtonListeners(c);
		return c;
	}

	@Override protected Component createPreviousButton()
	{
		Component c = createArrowButton(SwingConstants.WEST);
		c.setName("Spinner.previousButton");
		super.installPreviousButtonListeners(c);
		return c;
	}

	// copied from BasicSpinnerUI
	private Component createArrowButton(int direction)
	{
//		JButton b = new BasicArrowButton(direction);

		String str = (direction == SwingConstants.EAST) ? ">" : "<";
		JButton b = new JButton(str);

		b.setBackground(new Color(238, 238, 238));
		b.setFocusPainted(false); // http://stackoverflow.com/questions/9361658/disable-jbutton-focus-border

//		Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
		Border buttonBorder = new EmptyBorder(2, 15, 2, 15);
		if(buttonBorder instanceof UIResource)
			b.setBorder(new CompoundBorder(buttonBorder, null));
		else
			b.setBorder(buttonBorder);
		b.setInheritsPopupMenu(true);

		return b;
	}

	// http://boinc.gorlaeus.net/ClassicalBuilder/lookandfeel/com/jgoodies/looks/windows/WindowsSpinnerUI.java
	@Override public JComponent createEditor()
	{
		JComponent editor;
		SpinnerModel spinnerModel = super.spinner.getModel();
		if(spinnerModel instanceof SpinnerListModel)
		{
			SpinnerListModel spinnerListModel = (SpinnerListModel)spinnerModel;
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			for(Object value:spinnerListModel.getList())
				if(value != null && !value.toString().isEmpty())
					comboBoxModel.addElement(value.toString());
			editor = new JComboBoxLight<String>(comboBoxModel);

			@SuppressWarnings("unchecked")
			JComboBoxLight<String> jcomboBoxEditor = (JComboBoxLight<String>)editor; // Peux pas faire mieux :( : http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
			jcomboBoxEditor.setMaximumRowCount(spinnerListModel.getList().size());
			jcomboBoxEditor.addActionListener((LeftRightComboSpinner)super.spinner);

			super.spinner.setEditor(editor);
		}
		else
			editor = super.spinner.getEditor();

//		JComponent editor = super.spinner.getEditor();
		configureEditorBorder(editor);
		return editor;
	}

	@Override protected void replaceEditor(JComponent oldEditor, JComponent newEditor)
	{
		super.spinner.remove(oldEditor);
		configureEditorBorder(newEditor);
		super.spinner.add(newEditor, "Editor");
	}

	
	/**
	 * Sets an empty border with consistent insets.
	 */
	private void configureEditorBorder(JComponent editor)
	{
		Insets inset = new Insets(0, 5, 0, 5);

		if((editor instanceof JSpinner.DefaultEditor))
		{
			JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)editor;
			JTextField editorField = defaultEditor.getTextField();
			editorField.setColumns(4);
			editorField.setHorizontalAlignment(SwingConstants.CENTER);

			editorField.setBorder(new EmptyBorder(inset));
//			editorField.setBorder(new MatteBorder(new Insets(0, 5, 0, 5), Color.red));
			editorField.setBackground(new Color(238, 238, 238));
		}
		else if((editor instanceof JPanel) && (editor.getBorder() == null) && (editor.getComponentCount() > 0))
		{
			JComponent editorField = (JComponent)editor.getComponent(0);
			editorField.setBorder(new EmptyBorder(inset));
		}
	}

	@Override public void installUI(JComponent c)
	{
		super.installUI(c);
		c.removeAll();

		// Pour qu'on puisse affecter un JSpinner.NumberEditor
		// http://www.coderanch.com/t/607817/GUI/java/problems-custom-JSpinner-UI
		c.setLayout(new BorderLayout()
			{
				@Override public void addLayoutComponent(Component comp, Object constraints)
				{
					if(constraints.equals("Editor"))
						constraints = CENTER;
					super.addLayoutComponent(comp, constraints);
				}
			});

		c.add(createNextButton(), BorderLayout.EAST);
		c.add(createEditor(), BorderLayout.CENTER);
		c.add(createPreviousButton(), BorderLayout.WEST);
	}
}