package ch.correvon.google.calendar.components.spinner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class LeftRightSpinner extends JSpinner
{
	public LeftRightSpinner()
	{
		this(new SpinnerNumberModel());
	}

	public LeftRightSpinner(SpinnerModel model)
	{
		super(model);
		super.setUI(new LeftRightSpinnerUI());
	}
}

class LeftRightSpinnerUI extends BasicSpinnerUI
{

	public static ComponentUI createUI(JComponent c)
	{
		return new LeftRightSpinnerUI();
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
		JComponent editor = super.spinner.getEditor();
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