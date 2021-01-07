package ch.correvon.google.calendar.window.mainWindow;

import java.awt.event.KeyEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Shortcut
{
	public Shortcut(int keycode)
	{
		this(keycode, 0);
	}

	public Shortcut(int keycode, int modifier)
	{
		this.keycode = keycode;
		this.modifier = modifier;
	}

	public int getKeycode()
	{
		return keycode;
	}

	public void setKeycode(int keycode)
	{
		this.keycode = keycode;
	}

	public int getModifier()
	{
		return modifier;
	}

	public void setModifier(int modifier)
	{
		this.modifier = modifier;
	}

	static public Shortcut parse(String str)
	{
		String[] split = str.split("\\+");
		if(split.length != 2)
			return null;

		try
		{
			int modifier = Integer.parseInt(split[0]);
			int keycode = Integer.parseInt(split[1]);

			return new Shortcut(keycode, modifier);
		}
		catch(NumberFormatException e)
		{
			s_logger.error(e);
			return null;
		}
	}

	@Override public String toString()
	{
		String str = KeyEvent.getKeyText(this.keycode);
		if(this.modifier != 0 && !str.equals(KeyEvent.getKeyModifiersText(this.modifier)))
			str = KeyEvent.getKeyModifiersText(this.modifier) + "+" + str;

		return str;
	}

	public String toIntegerString()
	{
		String str = this.keycode + "";
		if(this.modifier != 0 && this.keycode != this.modifier)
			str = this.modifier + "+" + str;

		return str;
	}

	private int keycode;
	private int modifier;

	private static Log s_logger = LogFactory.getLog(Shortcut.class);
}
