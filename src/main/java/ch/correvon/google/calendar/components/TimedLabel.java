package ch.correvon.google.calendar.components;

import java.awt.Color;

import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimedLabel extends JLabel
{
	public TimedLabel()
	{
		this.thread = null;
		this.showingTime = 10000;
	}
	
	public int getShowingTime()
	{
		return this.showingTime;
	}

	public void setShowingTime(int showingTime)
	{
		this.showingTime = showingTime;
	}
	
	public void showError(String text)
	{
		super.setForeground(ERROR_COLOR);
		super.setText(text);
		this.start();
	}

	public void showWarning(String text)
	{
		super.setForeground(WARNING_COLOR);
		super.setText(text);
		this.start();
	}

	public void showMessage(String text)
	{
		super.setForeground(MESSAGE_COLOR);
		super.setText(text);
		this.start();
	}
	
	private void start()
	{
		if(this.thread != null)
			this.thread.exit();
		
		super.setVisible(true);
		this.thread = new ShowingThread(this, this.showingTime);
		this.thread.start();
	}
	
	public void hideMessage()
	{
		if(this.thread != null)
			this.thread.exit();
	}

	private ShowingThread thread;
	private int showingTime;
	private static final Color ERROR_COLOR = Color.red;
	private static final Color WARNING_COLOR = new Color(255, 141, 35);
	private static final Color MESSAGE_COLOR = Color.black;
}

class ShowingThread extends Thread
{
	public ShowingThread(JLabel label, int showingTime)
	{
		this.label = label;
		this.showingTime = showingTime;
//		this.exit = false;
	}
	
	@Override public void run()
	{
		try
		{
			super.sleep(this.showingTime);
		}
		catch(InterruptedException e)
		{
			s_logger.error(e);
		}
		
		this.exit();
	}
	
	public void exit()
	{
//		this.exit = true;
		this.label.setVisible(false);
	}
	
	private JLabel label;
	private int showingTime;
//	private boolean exit;
	
	private static Log s_logger = LogFactory.getLog(ShowingThread.class);
}
