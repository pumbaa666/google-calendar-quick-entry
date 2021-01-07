package com.toedter.components;

import java.awt.Color;

import javax.swing.JButton;
import ch.correvon.google.calendar.object.Function;
import ch.correvon.google.calendar.object.Schedule;

public class JValueButton extends JButton
{
	public JValueButton()
	{
		this("");
	}
	
	public JValueButton(String text)
	{
		super(text);
		this.defaultColor = super.getBackground();
	}
	
	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
		this.refreshText();
	}

	public Schedule getSchedule()
	{
		return schedule;
	}

	public void setSchedule(Schedule schedule)
	{
		this.schedule = schedule;
		this.refreshText();
		this.refreshBackground();
	}

	public Function getFunction()
	{
		return function;
	}

	public void setFunction(Function function)
	{
		this.function = function;
		this.refreshText();
	}
	
	public void reset()
	{
		this.schedule = null;
		this.function = null;
		this.value = 0;
		super.setText("");
	}
	
	private void refreshText()
	{
		int width = (int)(super.getWidth() * 0.7);
		
		String css =	"<style>" +

							"div.full {" +
								//"border-style: solid;"+
								//"display: block;" + // ça ne marche pas :( Pourtant ça devrait : http://stackoverflow.com/questions/5251048/div-width-100
								//"display: inline;" + // ne marche pas non plus. Et <span> non plus : http://stackoverflow.com/questions/7311235/java-html-css-in-swing-display-inline-not-working
								"width: "+width+"px;" +
							"}"+

							".date {" +
								"font-style: italic;"+
								"font-size: 16;"+
								"text-align: left;"+
								"color: black;"+
								//"border-style: solid;"+
								//"width: "+(width/5)+"px;" +
								//"display: inline !important;" +
							"}"+

							".schedule {" +
								"font-style: normal;"+
								"font-size: 20;"+
								"text-align: center;"+
								"height: 23px;"+
								//"border-style: dashed;"+
								//"width: "+(3*width/5)+"px;" +
								//"display: inline !important;" +
							"}"+

							".function {" +
								"font-style: normal;"+
								"color: " + this.getFunctionCssColor() + ";"+
								//"text-align: right;"+
								//"border-style: solid;"+
								//"width: "+(width/5)+"px;" +
								//"display: inline !important;" +
							"}"+
						"</style>" +
						"";
		
		String fullStr = "";
		if(this.schedule != null)
			fullStr = this.schedule.getLabel();

		if(this.function != null)
		{
			if(!fullStr.isEmpty())
				fullStr = fullStr + "/";
			fullStr = fullStr + "<font class=function>" + this.function.getLabel() + "</font>";
		}
		
		super.setText("<html><head>" + css + "</head><body><div class=full><div class=date>" + this.getValue() + "</div>" + "<div class=schedule>" + fullStr + "</div></div></body></html>");
//		super.setText("<html><head>" + css + "</head><body><div class=full><div class=date>" + this.getValue() + "</div><div class=schedule>" + scheduleStr + "</div><div class=function>" + functionStr + "</div></div></body></html>");

//		this.refreshBackground();
	}
	
	public void refreshBackground()
	{
		if(this.schedule != null)
			super.setBackground(this.schedule.getColor());
		else
			super.setBackground(this.defaultColor);			
	}
	
	private String getFunctionCssColor()
	{
		if (this.function == null)
			return "";
		
		Color color = this.function.getColor();
		if(color == null)
			return "";
		
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()); // http://stackoverflow.com/questions/3607858/how-to-convert-a-rgb-color-value-to-an-hexadecimal-value-in-java
	}
	
	private int value;
	private Schedule schedule;
	private Function function;
	private Color defaultColor;
}