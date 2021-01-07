package ch.correvon.google.calendar.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

// http://stackoverflow.com/questions/2000478/extend-from-generic-supertype
public class JComboBoxLight<E> extends JComboBox<E>
{
	public JComboBoxLight()
	{
		this(new DefaultComboBoxModel<E>());
	}
	
	public JComboBoxLight(ComboBoxModel<E> model)
	{
		super(model);
		
		// http://stackoverflow.com/questions/7485832/hide-jcombox-box-arrow
		super.setUI(new BasicComboBoxUI()
			{
				@Override protected JButton createArrowButton()
				{
					return new JButton()
						{
							@Override public int getWidth()
							{
								return 0;
							}
						};
				}
			});

		super.setRenderer(new JComboBoxLightRenderer<>());
	}
}

class JComboBoxLightRenderer<E> implements ListCellRenderer<E>
{
	@Override public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel renderer = (JLabel)this.defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		renderer.setHorizontalAlignment(SwingConstants.CENTER); // http://stackoverflow.com/questions/11008431/how-to-center-items-in-a-java-combobox
		renderer.setBorder(new EmptyBorder(0, 0, 0, 0)); // http://stackoverflow.com/questions/9838509/how-to-add-padding-to-jcombobox

		// http://stackoverflow.com/questions/20588225/delete-highlighting-in-jcombobox
		if(isSelected)
		{
			renderer.setBackground(selectingColor);
			list.setSelectionBackground(selectedColor);
		}

		return renderer;
	}
	
	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private static final Color selectedColor = new Color(238, 238, 238);
	private static final Color selectingColor = new Color(200, 200, 200);
}