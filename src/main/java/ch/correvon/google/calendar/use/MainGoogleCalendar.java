package ch.correvon.google.calendar.use;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.window.mainWindow.MainWindow;

public class MainGoogleCalendar
{
	/*
	 * https://console.developers.google.com/apis/credentials?project=medical-calendar-1125
	 */
	public static void main(String[] args) throws ParseException, InterruptedException
	{
		s_logger.info("Démarrage du programme");
		new MainWindow("Medical Google Calendar (" + VERSION + ")").run();
	}

	private static Log s_logger = LogFactory.getLog(MainGoogleCalendar.class);
	private static final String VERSION = "0.4";
	
	/*
	 * TODO
	 * ne pas mettre le jour (de la date du jour) en surbrillance si on n'est pas sur le bon mois 
	 */
}