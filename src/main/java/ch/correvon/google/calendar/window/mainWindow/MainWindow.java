package ch.correvon.google.calendar.window.mainWindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDayChooser;
import com.toedter.components.JValueButton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.GoogleCalendarService;
import ch.correvon.google.calendar.components.TimedLabel;
import ch.correvon.google.calendar.components.date.MyMonthPicker;
import ch.correvon.google.calendar.components.date.MyYearPicker;
import ch.correvon.google.calendar.helper.PreferencesBundle;
import ch.correvon.google.calendar.object.Function;
import ch.correvon.google.calendar.object.Reminder;
import ch.correvon.google.calendar.object.ReminderType;
import ch.correvon.google.calendar.object.Schedule;
import ch.correvon.google.calendar.window.mainWindow.jtable.CalendarModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.ColorChooserEditor;
import ch.correvon.google.calendar.window.mainWindow.jtable.ColorChooserRenderer;
import ch.correvon.google.calendar.window.mainWindow.jtable.FunctionModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.ReminderEditor;
import ch.correvon.google.calendar.window.mainWindow.jtable.ReminderRenderer;
import ch.correvon.google.calendar.window.mainWindow.jtable.ScheduleHeaderRenderer;
import ch.correvon.google.calendar.window.mainWindow.jtable.ScheduleModel;
import ch.correvon.google.calendar.window.mainWindow.jtable.ShortcutEditor;
import ch.correvon.google.calendar.window.mainWindow.jtable.TimeEditor;
import ch.correvon.google.calendar.window.mainWindow.jtable.TimeRenderer;

/**
 * JCalendar : http://toedter.com/jcalendar/
 * @author Loïc Correvon
 */
public class MainWindow extends JFrame implements KeyListener, WindowListener
{
	/* ------------------------------------------------------------------------ *\
	|* 		  						Constructeur								*|
	\* ------------------------------------------------------------------------ */
	public MainWindow(String title)
	{
		super(title);
		this.addWindowListener(this);

		this.initComponents();
		this.myInit();

		this.loadPref();
		this.loadSchedule();
		this.loadFunction();

		super.setVisible(false);
	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Méthodes publiques								*|
	\* ------------------------------------------------------------------------ */
	public void run()
	{
		super.setVisible(true);
		this.enableCalendar(false); // A appeler une fois que la fenêtre est rendue sinon ça n'a pas d'effet sur le dayChooser.
	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Méthodes privées								*|
	\* ------------------------------------------------------------------------ */
	private void exit()
	{
		this.writePref();
		this.writeSchedule();
		this.writeFunction();
		super.dispose();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Window listener							*|
	\* ------------------------------------------------------------ */
	@Override public void windowActivated(WindowEvent e){}
	@Override public void windowClosed(WindowEvent e){}
	@Override public void windowDeactivated(WindowEvent e){}
	@Override public void windowDeiconified(WindowEvent e){}
	@Override public void windowIconified(WindowEvent e){}
	@Override public void windowOpened(WindowEvent e){}
	@Override public void windowClosing(WindowEvent e)
	{
		this.exit();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Key listener							*|
	\* ------------------------------------------------------------ */
	@Override public void keyReleased(KeyEvent e){}
	@Override public void keyTyped(KeyEvent e){}
	@Override public void keyPressed(KeyEvent event)
	{
		int keycode = event.getKeyCode();
		int modifier = event.getModifiers();
		event.consume();

		Date date = null;
		try
		{
			date = DATE_SDF.parse(this.dayChooser.getDay() + "." + (this.monthChooser.getMonth() + 1) + "." + this.yearChooser.getYear());
		}
		catch(ParseException exception)
		{
			s_logger.error(exception);
			return;
		}

		if(keycode == KeyEvent.VK_SPACE || keycode == KeyEvent.VK_TAB)
			this.nextDay();

		if(keycode == KeyEvent.VK_DELETE)
		{
			this.deleteEvent(date);
			this.nextDay();
			return;
		}

		for(Schedule schedule:this.scheduleModel.getSchedules())
		{
			if(keycode == schedule.getShortcut().getKeycode() && modifier == schedule.getShortcut().getModifier())
			{
				this.createEvent(schedule, date);
				this.nextDay();
				return;
			}
		}

		for(Function function:this.functionModel.getFunctions())
		{
			if(keycode == function.getShortcut().getKeycode() && modifier == function.getShortcut().getModifier())
			{
				this.createFunction(function, date);
				return;
			}
		}
	}

	/* ------------------------------------------------------------ *\
	|* 		  				GUI Events								*|
	\* ------------------------------------------------------------ */
	private void buttonConnectMouseClicked(MouseEvent e)
	{
		if(!this.connected)
			this.connect();
		else
			this.disconnect();
	}

	private void buttonAddScheduleActionPerformed(ActionEvent e)
	{
		Date start = new Date();
		Date end = new Date(start.getTime() + 8 * 3600 * 1000);
		try
		{
			start =  Schedule.SCHEDULE_SDF.parse(Schedule.SCHEDULE_SDF.format(start));
			end =  Schedule.SCHEDULE_SDF.parse(Schedule.SCHEDULE_SDF.format(end));
		}
		catch(ParseException ex)
		{
		}
				
		this.scheduleModel.add(new Schedule("Jx", "", start, end, null, null));
		this.startEditingTable(this.tableSchedule);
	}

	private void buttonAddFunctionActionPerformed(ActionEvent e)
	{
		this.functionModel.add(new Function("Px", "", null, null));
		this.startEditingTable(this.tableFunction);
	}

	private void buttonRemoveScheduleActionPerformed(ActionEvent e)
	{
		int[] rows = this.tableSchedule.getSelectedRows();
		if(rows == null)
			return;
		
		for(int i = rows.length-1; i >= 0; i--)
			this.scheduleModel.remove(rows[i]);
	}

	private void buttonRemoveFunctionActionPerformed(ActionEvent e)
	{
		int[] rows = this.tableFunction.getSelectedRows();
		if(rows == null)
			return;
		
		for(int i = rows.length-1; i >= 0; i--)
			this.functionModel.remove(rows[i]);
	}

	private void comboCalendarsActionPerformed(ActionEvent e)
	{
		if(this.selectedCalendar != null && this.selectedCalendar == this.calendarModel.getSelectedItem())
			return;

		if(this.calendarModel.getSelectedItem() != null)
			this.selectCalendar(this.calendarModel.getSelectedItem().getSummary());
	}

	private void yearChooserStateChanged(ChangeEvent e)
	{
		this.dayChooser.setYear(this.yearChooser.getYear());
		this.getEvents();
	}

	private void monthChooserStateChanged(ChangeEvent e)
	{
		this.dayChooser.setMonth(this.monthChooser.getMonth());
		this.getEvents();
	}

	private void menuFileQuitActionPerformed(ActionEvent e)
	{
		this.exit();
	}
	
	private void startEditingTable(final JTable table)
	{
		TableModel model = table.getModel();
		final int row = model.getRowCount() - 1;
		final int col = 1; // Colone du nom

		SwingUtilities.invokeLater(new Runnable() // Invoke later to let current table event to consume
			{
				@Override public void run()
				{
					// http://www.java2s.com/Code/Java/Swing-JFC/ProgrammaticallyStartingCellEditinginaJTableComponent.htm
					boolean success = table.editCellAt(row, col); // Start editing
					if(!success)
						return;

					MainWindow.this.tableSchedule.changeSelection(row, col, false, false);
					Component editorComponent = table.getEditorComponent();
					if(editorComponent == null)
						return;
					editorComponent.requestFocusInWindow(); // Request focus to show editing cursor

					if(!(editorComponent instanceof JTextComponent)) // Select all text
						return;
					JTextComponent txt = (JTextComponent)editorComponent;
					txt.setSelectionStart(0);
					txt.setSelectionEnd(txt.getText().length());
				}
			});
	}

	/* ------------------------------------------------------------ *\
	|* 		  				File methods							*|
	\* ------------------------------------------------------------ */
	private void loadSchedule()
	{
		s_logger.debug("Chargement des horaires");

		if(!new File(SCHEDULE_FILE_PATH).exists())
			return;

		String line;
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(SCHEDULE_FILE_PATH), "utf-8")))
		{
			while((line = br.readLine()) != null)
			{
				String[] scheduleStr = line.split(":", 2);
				if(scheduleStr.length != 2)
				{
					s_logger.warn("Error reading '" + SCHEDULE_FILE_PATH + "' at line '" + line + "'");
					continue;
				}

				String name = scheduleStr[0];
				String description = "";
				Date start = new Date(0);
				Date end = new Date(0);
				Shortcut shortcut = null;
				Color color = null;
				List<Reminder> reminders = new ArrayList<>();
				
				String[] values = scheduleStr[1].split(";");
				
				if(values.length > 0)
					description = values[0];

				try
				{
					if(values.length > 1)
						start = Schedule.SCHEDULE_SDF.parse(values[1]);
					
					if(values.length > 2)
						end = Schedule.SCHEDULE_SDF.parse(values[2]);
					
					if(values.length > 3)
					{
						String[] shortcutValue = values[3].split("\\+");
						if(shortcutValue.length == 1)
							shortcut = new Shortcut(Integer.parseInt(shortcutValue[0]));
						else if(shortcutValue.length == 2)
							shortcut = new Shortcut(Integer.parseInt(shortcutValue[1]), Integer.parseInt(shortcutValue[0]));
					}
				}
				catch(ParseException | NumberFormatException e)
				{
					s_logger.warn("Error reading '" + SCHEDULE_FILE_PATH + "' at line '" + line + "'");
				}
				
				if(values.length > 4)
				{
					String[] colorValue = values[4].split(",");
					if(colorValue.length == 3)
						color = new Color(Integer.parseInt(colorValue[0]), Integer.parseInt(colorValue[1]), Integer.parseInt(colorValue[2]));
				}

				if(values.length > 5)
				{
					try
					{
						String[] allReminders = values[5].split(",");
						for(String reminder:allReminders)
						{
							String[] reminderValue = reminder.split("\\+");
							reminders.add(new Reminder(ReminderType.valueOf(reminderValue[0]), Integer.parseInt(reminderValue[1])));
						}
					}
					catch(IllegalArgumentException e)
					{
						s_logger.warn("Error reading '" + SCHEDULE_FILE_PATH + "' at line '" + line + "'");
					}
				}

				Schedule schedule = new Schedule(name, description, start, end, shortcut, color);
				schedule.setReminders(reminders);
				this.scheduleModel.add(schedule);
			}
		}
		catch(FileNotFoundException e)
		{
			s_logger.error("Cannot open file '" + SCHEDULE_FILE_PATH + "'", e);
		}
		catch(IOException e)
		{
			s_logger.error("Error reading file '" + SCHEDULE_FILE_PATH + "'", e);
		}
	}

	private void writeSchedule()
	{
		File scheduleFile = new File(SCHEDULE_FILE_PATH);
		if(!scheduleFile.exists())
		{
			try
			{
				if(!scheduleFile.createNewFile())
				{
					s_logger.error("Impossible de créer le fichier '" + SCHEDULE_FILE_PATH + "'");
					return;
				}
			}
			catch(IOException e)
			{
				s_logger.error("Impossible de créer le fichier '" + SCHEDULE_FILE_PATH + "'", e);
				return;
			}
		}

		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scheduleFile, false), "utf-8"));)
		{
			for(Schedule schedule:this.scheduleModel.getSchedules())
				writer.write(schedule.toString() + NEW_LINE);
		}
		catch(IOException e)
		{
			s_logger.error("Error writing file '" + scheduleFile + "'", e);
		}
	}

	private void loadFunction()
	{
		s_logger.debug("Chargement des fonctions");

		if(!new File(FUNCTION_FILE_PATH).exists())
			return;

		String line;
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FUNCTION_FILE_PATH), "utf-8")))
		{
			while((line = br.readLine()) != null)
			{
				String[] functionStr = line.split(":", 2);
				if(functionStr.length != 2)
				{
					s_logger.warn("Error reading '" + FUNCTION_FILE_PATH + "' at line '" + line + "'");
					continue;
				}

				String name = functionStr[0];
				String description = "";
				Shortcut shortcut = null;
				Color color = null;
				List<Reminder> reminders = new ArrayList<>();

				String[] values = functionStr[1].split(";");
				
				if(values.length > 0)
					description = values[0];

				try
				{
					if(values.length > 1)
					{
						String[] shortcutValue = values[1].split("\\+");
						if(shortcutValue.length == 1)
							shortcut = new Shortcut(Integer.parseInt(shortcutValue[0]));
						else if(shortcutValue.length == 2)
							shortcut = new Shortcut(Integer.parseInt(shortcutValue[1]), Integer.parseInt(shortcutValue[0]));
					}
				}
				catch(NumberFormatException e)
				{
					s_logger.warn("Error reading '" + SCHEDULE_FILE_PATH + "' at line '" + line + "'");
				}
				
				if(values.length > 2)
				{
					String[] colorValue = values[2].split(",");
					if(colorValue.length == 3)
						color = new Color(Integer.parseInt(colorValue[0]), Integer.parseInt(colorValue[1]), Integer.parseInt(colorValue[2]));
				}

				if(values.length > 3)
				{
					try
					{
						String[] allReminders = values[3].split(",");
						for(String reminder:allReminders)
						{
							String[] reminderValue = reminder.split("\\+");
							reminders.add(new Reminder(ReminderType.valueOf(reminderValue[0]), Integer.parseInt(reminderValue[1])));
						}
					}
					catch(IllegalArgumentException e)
					{
						s_logger.warn("Error reading '" + SCHEDULE_FILE_PATH + "' at line '" + line + "'");
					}
				}

				Function function = new Function(name, description, shortcut, color);
				function.setReminders(reminders);

				this.functionModel.add(function);
			}
		}
		catch(FileNotFoundException e)
		{
			s_logger.error("Cannot open file '" + FUNCTION_FILE_PATH + "'", e);
		}
		catch(IOException e)
		{
			s_logger.error("Error reading file '" + SCHEDULE_FILE_PATH + "'", e);
		}
	}

	private void writeFunction()
	{
		File functionFile = new File(FUNCTION_FILE_PATH);
		if(!functionFile.exists())
		{
			try
			{
				if(!functionFile.createNewFile())
				{
					s_logger.error("Impossible de créer le fichier '" + FUNCTION_FILE_PATH + "'");
					return;
				}
			}
			catch(IOException e)
			{
				s_logger.error("Impossible de créer le fichier '" + FUNCTION_FILE_PATH + "'", e);
				return;
			}
		}

		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(functionFile, false), "utf-8"));)
		{
			for(Function function:this.functionModel.getFunctions())
				writer.write(function.toString() + NEW_LINE);
		}
		catch(IOException e)
		{
			s_logger.error("Error writing file '" + functionFile + "'", e);
		}
	}

	private void loadPref()
	{
		PreferencesBundle.readPref();
		Map<String, String> metadata = PreferencesBundle.getPref();

		try
		{
			int x = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_X));
			int y = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_Y));
			int w = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_W));
			int h = Integer.parseInt(metadata.get(PREF_MAIN_WINDOW_H));
			super.setLocation(x, y);
			super.setSize(w, h);

			this.selectedCalendarName = metadata.get(PREF_SELECTED_CALENDAR);
		}
		catch(NumberFormatException e)
		{
			s_logger.warn("Error setting window localization preferences", e);
		}
	}

	private void writePref()
	{
		PreferencesBundle.writePref(this.dumpPref());
	}

	private Map<String, String> dumpPref()
	{
		Map<String, String> metadata = new HashMap<String, String>(20);

		metadata.put(PREF_MAIN_WINDOW_X, "" + super.getLocation().x);
		metadata.put(PREF_MAIN_WINDOW_Y, "" + super.getLocation().y);
		metadata.put(PREF_MAIN_WINDOW_W, "" + super.getSize().width);
		metadata.put(PREF_MAIN_WINDOW_H, "" + super.getSize().height);
		if(this.selectedCalendar != null)
			metadata.put(PREF_SELECTED_CALENDAR, "" + this.selectedCalendar.getSummary());

		return metadata;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Google events							*|
	\* ------------------------------------------------------------ */
	private void connect()
	{
		this.connected = false;

		this.buttonConnect.setEnabled(false);		
		this.labelAccountValue.setText("Connexion en cours");
		new SwingWorker<String, CalendarListEntry>()
			{
				@Override protected String doInBackground()
				{
					// Connexion au service google
					MainWindow.this.service = GoogleCalendarService.getService();
					if(MainWindow.this.service == null)
					{
						MainWindow.this.labelAccountValue.setText("Impossible de se connecter à l'agenda, consultez les logs");
						s_logger.error("Impossible de se connecter à l'agenda");
						return null;
					}

					// Récupération de tous les calendriers
					String mainCalendarName = null;
					List<CalendarListEntry> calendars = MainWindow.this.service.getAllCalendar();
					for(CalendarListEntry calendar:calendars)
					{
						if(calendar.getPrimary() != null && calendar.getPrimary() == true)
							mainCalendarName = calendar.getSummary() + " (" + calendar.getId() + ")";
						publish(calendar); // Va appeler la méthode process du SwingWorker (ci-dessous)
					}
					return mainCalendarName;
				}

				@Override protected void process(List<CalendarListEntry> calendars) // Appelé (quand cela est possible) par publish.
				{
					// Comme cette méthode n'est pas appelée systematiquement lors de l'appel à publish
					// il faut s'assurer d'entrer toutes les valeurs (calendars est peuplé correctement).
					// En pratique, pour des si petites quantité de calendrier, elle est appellée peu souvent (1x, en fait...),
					// on peut donc se permettre de tout supprimer et tout ré-importer
					MainWindow.this.calendarModel.removeAll();
					for(CalendarListEntry calendar:calendars)
						MainWindow.this.calendarModel.add(calendar);
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					try
					{
						String mainCalendarName = get(); // Méthode bloquante qui attend la fin de doInBackground et récupère son résultat.
						MainWindow.this.labelAccountValue.setText(mainCalendarName);
						MainWindow.this.selectCalendar(MainWindow.this.selectedCalendarName);
//						MainWindow.this.connected = true; // La connexion sera déterminée comme "établie" une fois le calendrier sélectionné (c'est une nouvelle requête Google)
						MainWindow.this.comboCalendars.setEnabled(true);
						
						MainWindow.this.labelMessage.showWarning("Veuillez sélectionner un calendrier");
					}
					catch(InterruptedException | ExecutionException e)
					{
						s_logger.error(e);
					}
				}
			}.execute();
	}
	
	private void disconnect()
	{
		this.connected = false;
		this.buttonConnect.setText("Connexion");
		this.buttonConnect.setForeground(new Color(0, 134, 175));
		this.calendarModel.removeAll();
		this.comboCalendars.setSelectedIndex(-1);
		this.comboCalendars.setEnabled(false);
		this.clearCalendar();
		this.enableCalendar(false);
	}

	private void getEvents()
	{
		if(!this.connected)
			return;

		if(this.eventSwingWorker != null && this.eventSwingWorker.getState() != SwingWorker.StateValue.DONE) // Si il y a déjà un thread en cours
		{
			synchronized(this)
			{
				this.refreshEvent = true;
			}
			return;
		}

		this.eventSwingWorker = new SwingWorker<Void, Void>()
			{
				@Override protected Void doInBackground()
				{
					Calendar cal = Calendar.getInstance();
					cal.set(MainWindow.this.yearChooser.getYear(), MainWindow.this.monthChooser.getMonth(), 1, 0, 0, 0);
					Date minDate = cal.getTime();
					
					cal.add(Calendar.MONTH, 1); // mois suivant
					cal.add(Calendar.SECOND, -1); // moins une seconde pour arriver au jour précédent à la fin de la journée, pour voir tous les events
					Date maxDate = cal.getTime();

					s_logger.debug("minDate : "+minDate);
					s_logger.debug("maxDate : "+maxDate);
					MainWindow.this.events = MainWindow.this.service.getNextEvents(-1, minDate, maxDate, "");
					return null;
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					synchronized(MainWindow.this)
					{
						if(MainWindow.this.refreshEvent)
						{
							MainWindow.this.refreshEvent = false;
							MainWindow.this.eventSwingWorker = null;
							MainWindow.this.getEvents();
							return;
						}
					}

					if(MainWindow.this.events == null || MainWindow.this.events.isEmpty())
						return;

					for(Event event:MainWindow.this.events)
					{
						Schedule scheduleMatch = null;
						Function functionMatch = null;
						for(Schedule schedule:MainWindow.this.scheduleModel.getSchedules())
						{
							if(event.getSummary().contains(schedule.getLabel()))
							{
								scheduleMatch = schedule;
								break;
							}
						}
						for(Function function:MainWindow.this.functionModel.getFunctions())
						{
							if(event.getSummary().contains(function.getLabel()))
							{
								functionMatch = function;
								break;
							}
						}

						if(scheduleMatch != null || functionMatch != null)
							MainWindow.this.setColorDay(getEventDate(event), scheduleMatch, functionMatch); // Pas de getDateTime, au cas où l'évenement aurait été créé dans Google, sans heure
					}
				}
			};

		this.eventSwingWorker.execute();
	}
	
	private Date getEventDate(Event event)
	{
		if(event == null)
			return null;
		
		EventDateTime start = event.getStart();
		if(start == null)
			return null;
		
		DateTime dateTime = start.getDateTime();
		if(dateTime == null)
			dateTime = start.getDate();
		
		if(dateTime == null)
			return null;
		
		return new Date(dateTime.getValue());
	}

	private void createEvent(final Schedule schedule, final Date date)
	{
		final Date start = new Date(date.getTime() + schedule.getStart().getTime() + 3600 * 1000); // Il faut rajouter 1h, je ne sais pas pourquoi.
		Date endTemp = new Date(date.getTime() + schedule.getEnd().getTime() + 3600 * 1000); // Idem.
		if(endTemp.before(start)) // Si l'heure de fin est avant l'heure de début c'est qu'en fait l'horaire termine le lendemain, il faut donc ajouter une journée à la date.
			endTemp = new Date(endTemp.getTime() + 24 * 3600 * 1000);
		final Date end = endTemp;

		// Désactiver temporairement le bouton
		int eventDay = Integer.parseInt(DAY_SDF.format(date));
		JValueButton dayButton = this.getDayButton(eventDay);
		if(dayButton != null)
			dayButton.setEnabled(false);

		new SwingWorker<Void, Void>()
			{
				@Override protected Void doInBackground()
				{
					// Vérifier si il existe déjà un event
					List<Event> events = MainWindow.this.service.getNextEvents(-1, date, new Date(date.getTime() + 24 * 3600 * 1000 - 1), ""); // Début du jour à minuit, fin à 23:59:59.999
					for(Event event:events)
						if(event.getDescription() != null && event.getDescription().endsWith("(schedule)")) // le supprimer si c'est un schedule (on laisse les functions)
							if(event.getStart().getDateTime().getValue() > date.getTime()) // On ne supprime un événement que si il commence le jour même (pour ne pas supprimer par erreur un Nx qui finirait la nuit du jour en question.
								MainWindow.this.service.deleteEvent(event.getId());

//					List<Event.Reminders> reminders = schedule.getReminders();
//					if(reminders != null && reminders.size() > 0)
//					{
//						Event.Reminders eventReminder = new Event.Reminders();
//						eventReminder.setUseDefault(false);
//						List<EventReminder> eventReminders = new ArrayList<>(reminders.size());
//						for(Event.Reminders reminder:reminders)
//							eventReminders.add(new EventReminder().setMethod(reminder.getType().toString().toLowerCase()).setMinutes(reminder.getTime()));
//						eventReminder.setOverrides(eventReminders);
//						event.setReminders(eventReminder);
//					}

					String eventId = MainWindow.this.service.createEvent(schedule.getLabel(), schedule.getDescription(), "schedule", "", start, end, null, null, null/*, schedule.getReminders()*/); // TODO remettre reminders
					if(eventId == null || eventId.isEmpty())
						MainWindow.this.labelMessage.showError("Impossible de créer l'évenement, veuillez consulter les logs");
					return null;
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					MainWindow.this.setColorDay(date, schedule, null);
				}
			}.execute();
	}

	private void createFunction(final Function function, final Date date)
	{
		final Date start = new Date(date.getTime());
		final Date end = new Date(date.getTime() + 3600 * 1000);

		new SwingWorker<Void, Void>()
			{
				@Override protected Void doInBackground()
				{
					MainWindow.this.service.createEvent(function.getLabel(), function.getDescription(), "function", "", start, end, null, null, null/*, function.getReminders()*/); // Intégrer la fonction dans l'évent ?? // TODO remettre reminders
					return null;
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					MainWindow.this.setColorDay(date, null, function);
				}
			}.execute();
	}

	private void deleteEvent(final Date date)
	{
		new SwingWorker<Void, Void>()
			{
				@Override protected Void doInBackground()
				{
					List<Event> events = MainWindow.this.service.getNextEvents(-1, date, new Date(date.getTime() + 24 * 3600 * 1000 - 1), ""); // Début du jour à minuit, fin à 23:59:59.999
					for(Event event:events)
						MainWindow.this.service.deleteEvent(event.getId());
					return null;
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					MainWindow.this.setColorDay(date, null, null, true);
				}
			}.execute();
	}

	private void selectCalendar(final String calendarName)
	{
		if(calendarName == null || calendarName.isEmpty())
			return;

		new SwingWorker<String, Void>()
			{
				@Override protected String doInBackground()
				{
					return MainWindow.this.service.selectCalendar(calendarName, MainWindow.this.calendarModel.getCalendars());
				}

				@Override protected void done() // Appelé à la fin de doInBackground
				{
					String calendarId = null;
					try
					{
						calendarId = get();
					}
					catch(InterruptedException | ExecutionException e)
					{
						s_logger.error("Impossible de sélectionner l'agenda '"+calendarName+"'", e);
						return;
					}
					
					if(calendarId == null)
					{
						s_logger.error("Aucun agenda '"+calendarName+"' n'a été trouvé");
						return;
					}
					
					boolean connected = false;
					
					CalendarListEntry calendar;
					for(int i = 0; i < MainWindow.this.calendarModel.getSize(); i++)
					{
						calendar = MainWindow.this.calendarModel.getElementAt(i);
						if(calendar.getId().equals(calendarId))
						{
							MainWindow.this.calendarModel.setSelectedItem(calendar);
							MainWindow.this.selectedCalendar = calendar;
							connected = true;
							break;
						}
					}

					if(!connected)
					{
						s_logger.error("Aucun agenda correspondant à '"+calendarName+"' n'a été trouvé");
						return;
					}
					
					MainWindow.this.buttonConnect.setEnabled(true);
					MainWindow.this.buttonConnect.setText("Déconnexion");
					MainWindow.this.buttonConnect.setForeground(new Color(186, 101, 16));
					MainWindow.this.connected = true;
					MainWindow.this.enableCalendar(true);
					MainWindow.this.clearCalendar();
					MainWindow.this.getEvents();
					MainWindow.this.labelMessage.hideMessage();
				}
			}.execute();
	}

	/* ------------------------------------------------------------ *\
	|* 		  				GUI modification						*|
	\* ------------------------------------------------------------ */
	private void setColorDay(Date date, Schedule schedule, Function function)
	{
		this.setColorDay(date, schedule, function, false);
	}

	private void setColorDay(Date date, Schedule schedule, Function function, boolean deleteNull)
	{
		int selectedMonth = this.monthChooser.getMonth() + 1;
		int selectedYear = this.yearChooser.getYear();

		int dateMonth = Integer.parseInt(MONTH_SDF.format(date));
		int dateYear = Integer.parseInt(YEAR_SDF.format(date));

		if(selectedMonth != dateMonth || selectedYear != dateYear)
			return;

		String eventDay = DAY_SDF.format(date);
		JValueButton dayButton = this.getDayButton(Integer.parseInt(eventDay));
		if(dayButton != null)
		{
			dayButton.setEnabled(true); // On réactive le bouton
			if(deleteNull || schedule != null)
				dayButton.setSchedule(schedule);
			if(deleteNull || function != null)
				dayButton.setFunction(function);
		}
	}

	private void nextDay()
	{
		int currentDay = this.dayChooser.getDay();
		this.dayChooser.setDay(currentDay + 1); // Passer au jour suivant.
		if(this.dayChooser.getDay() == currentDay) // Si on n'a pas bougé c'est qu'on est à la fin du mois
		{
			this.monthChooser.nextMonth(); // Mettra l'année à jour si on est en décembre, grâce au listener du monthChooser
			this.dayChooser.setDay(1); // Et on se met au 1er jour.
			JValueButton button = this.getDayButton(1);
			if(button != null)
				button.requestFocus();
		}
	}

	private void clearCalendar()
	{
		for(int i = 7; i < 49; i++)
		{
			JValueButton button = MainWindow.this.dayButtons.get(i);
			button.setSchedule(null);
			button.setFunction(null);
		}
	}

	private void enableCalendar(boolean enable)
	{
		this.yearChooser.setEnabled(enable);
		this.monthChooser.setEnabled(enable);
		this.dayChooser.setEnabled(enable); // A placer en dernier sinon ça bug
	}
	
	private JValueButton getDayButton(int day)
	{
		for(int i = 7; i < 49; i++)
		{
			JValueButton button = MainWindow.this.dayButtons.get(i);
			if(button.getValue() == day)
				return button;
		}
		
		return null;
	}

	/* ------------------------------------------------------------ *\
	|* 		  			GUI Creation & init							*|
	\* ------------------------------------------------------------ */
	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - yggpuduku
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu();
		JMenuItem menuFileQuit = new JMenuItem();
		this.panelConnection = new JPanel();
		this.buttonConnect = new JLabel();
		this.labelAccount = new JLabel();
		this.labelAccountValue = new JLabel();
		this.labelCalendar = new JLabel();
		this.comboCalendars = new JComboBox<>();
		this.monthChooser = new MyMonthPicker();
		this.yearChooser = new MyYearPicker();
		this.dayChooser = new JDayChooser();
		this.panelScheduleFunction = new JPanel();
		JLabel label3 = new JLabel();
		JButton buttonAddSchedule = new JButton();
		JButton buttonRemoveSchedule = new JButton();
		JScrollPane scrollPane2 = new JScrollPane();
		this.tableSchedule = new JTable();
		JLabel label4 = new JLabel();
		JButton buttonAddFunction = new JButton();
		JButton buttonRemoveFunction = new JButton();
		JScrollPane scrollPane3 = new JScrollPane();
		this.tableFunction = new JTable();
		this.labelMessage = new TimedLabel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, right:default, $lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 124dlu, $lcgap, default:grow, $lcgap, 58dlu:grow(0.33), $lcgap, $ugap",
			"fill:30dlu, $lgap, fill:25dlu, $lgap, fill:default:grow, $lgap, default, $lgap, $ugap"));

		//======== menuBar ========

		//======== menuFile ========
		menuFile.setText("Fichier");

		//---- menuFileQuit ----
		menuFileQuit.setText("Quitter");
		menuFileQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuFileQuitActionPerformed(e);
			}
		});
		menuFile.add(menuFileQuit);
		menuBar.add(menuFile);
		setJMenuBar(menuBar);

		//======== panelConnection ========

		// JFormDesigner evaluation mark
		this.panelConnection.setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), this.panelConnection.getBorder())); this.panelConnection.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		this.panelConnection.setLayout(new FormLayout(
			"2*(default, $lcgap), default:grow(0.75), $lcgap, default:grow",
			"3*(default, $lgap), default"));

		//---- buttonConnect ----
		this.buttonConnect.setText("Connexion");
		this.buttonConnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.buttonConnect.setForeground(new Color(0, 134, 175));
		this.buttonConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buttonConnectMouseClicked(e);
			}
		});
		this.panelConnection.add(this.buttonConnect, CC.xy(1, 1));

		//---- labelAccount ----
		this.labelAccount.setText("Compte : ");
		this.panelConnection.add(this.labelAccount, CC.xy(1, 3));

		//---- labelAccountValue ----
		this.labelAccountValue.setText("text");
		this.panelConnection.add(this.labelAccountValue, CC.xywh(3, 3, 5, 1));

		//---- labelCalendar ----
		this.labelCalendar.setText("Agenda : ");
		this.panelConnection.add(this.labelCalendar, CC.xy(1, 5));

		//---- comboCalendars ----
		this.comboCalendars.setMinimumSize(new Dimension(54, 15));
		this.comboCalendars.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboCalendarsActionPerformed(e);
			}
		});
		this.panelConnection.add(this.comboCalendars, CC.xywh(3, 5, 3, 1));
		contentPane.add(this.panelConnection, CC.xywh(15, 1, 3, 3));

		//---- monthChooser ----
		this.monthChooser.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				monthChooserStateChanged(e);
			}
		});
		contentPane.add(this.monthChooser, CC.xy(3, 3));

		//---- yearChooser ----
		this.yearChooser.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				yearChooserStateChanged(e);
			}
		});
		contentPane.add(this.yearChooser, CC.xy(5, 3));
		contentPane.add(this.dayChooser, CC.xywh(3, 5, 11, 3));

		//======== panelScheduleFunction ========
		this.panelScheduleFunction.setLayout(new FormLayout(
			"default, 2*($lcgap, 10dlu), $lcgap, default:grow",
			"default, $lgap, fill:default:grow, $lgap, default, $lgap, fill:default:grow"));
		((FormLayout)this.panelScheduleFunction.getLayout()).setColumnGroups(new int[][] {{3, 5}});

		//---- label3 ----
		label3.setText("Type d'horaires");
		this.panelScheduleFunction.add(label3, CC.xy(1, 1));

		//---- buttonAddSchedule ----
		buttonAddSchedule.setText("+");
		buttonAddSchedule.setMargin(new Insets(0, 2, 0, 2));
		buttonAddSchedule.setToolTipText("Ajouter un horaire");
		buttonAddSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAddScheduleActionPerformed(e);
			}
		});
		this.panelScheduleFunction.add(buttonAddSchedule, CC.xy(3, 1));

		//---- buttonRemoveSchedule ----
		buttonRemoveSchedule.setText("-");
		buttonRemoveSchedule.setMargin(new Insets(0, 2, 0, 2));
		buttonRemoveSchedule.setToolTipText("Supprimer un horaire");
		buttonRemoveSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemoveScheduleActionPerformed(e);
			}
		});
		this.panelScheduleFunction.add(buttonRemoveSchedule, CC.xy(5, 1));

		//======== scrollPane2 ========
		scrollPane2.setViewportView(this.tableSchedule);
		this.panelScheduleFunction.add(scrollPane2, CC.xywh(1, 3, 7, 1));

		//---- label4 ----
		label4.setText("Fonction");
		this.panelScheduleFunction.add(label4, CC.xy(1, 5));

		//---- buttonAddFunction ----
		buttonAddFunction.setText("+");
		buttonAddFunction.setMargin(new Insets(0, 2, 0, 2));
		buttonAddFunction.setToolTipText("Ajouter une fonction");
		buttonAddFunction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAddFunctionActionPerformed(e);
			}
		});
		this.panelScheduleFunction.add(buttonAddFunction, CC.xy(3, 5));

		//---- buttonRemoveFunction ----
		buttonRemoveFunction.setText("-");
		buttonRemoveFunction.setMargin(new Insets(0, 2, 0, 2));
		buttonRemoveFunction.setToolTipText("Supprimer une fonction");
		buttonRemoveFunction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemoveFunctionActionPerformed(e);
			}
		});
		this.panelScheduleFunction.add(buttonRemoveFunction, CC.xy(5, 5));

		//======== scrollPane3 ========
		scrollPane3.setViewportView(this.tableFunction);
		this.panelScheduleFunction.add(scrollPane3, CC.xywh(1, 7, 7, 1));
		contentPane.add(this.panelScheduleFunction, CC.xywh(15, 5, 3, 3));
		contentPane.add(this.labelMessage, CC.xywh(3, 9, 15, 1));
		setSize(825, 440);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void myInit()
	{
		// Vire le fuckin watermark
		for(PropertyChangeListener listener:this.panelConnection.getPropertyChangeListeners())
			this.panelConnection.removePropertyChangeListener(listener);
		this.panelConnection.setBorder(null);
		
		// Cache les labels inutils lorsqu'on n'est pas encore connecté
//		this.labelAccount.setVisible(false);
//		this.labelAccountValue.setVisible(false);
//		this.labelCalendar.setVisible(false);
//		this.comboCalendars.setVisible(false);
		
		this.comboCalendars.setEnabled(false);

		this.eventSwingWorker = null;
		this.refreshEvent = false;

		this.labelAccountValue.setText("Non connecté");
		this.connected = false;
//		this.enableCalendar(false); // Inutile ici, ça n'a pas d'effet tant que la fenêtre n'est pas rendue.

		// Models
		this.calendarModel = new CalendarModel();
		this.comboCalendars.setModel(this.calendarModel);
		this.comboCalendars.setRenderer(new CalendarListEntryRenderer());

		this.scheduleModel = new ScheduleModel();
		this.tableSchedule.setModel(this.scheduleModel);

		this.functionModel = new FunctionModel();
		this.tableFunction.setModel(this.functionModel);

		// Renderers
		DefaultTableCellRenderer leftCellRenderer = new DefaultTableCellRenderer();
		leftCellRenderer.setHorizontalAlignment(JLabel.LEFT);
		
		TableColumnModel scheduleColumnModel = this.tableSchedule.getColumnModel();
		TimeRenderer timeRenderer = new TimeRenderer();
		scheduleColumnModel.getColumn(0).setCellRenderer(new ColorChooserRenderer());
		scheduleColumnModel.getColumn(1).setCellRenderer(new ReminderRenderer());
		scheduleColumnModel.getColumn(4).setCellRenderer(timeRenderer);
		scheduleColumnModel.getColumn(5).setCellRenderer(timeRenderer);
		scheduleColumnModel.getColumn(6).setCellRenderer(leftCellRenderer);

		TableColumnModel functionColumnModel = this.tableFunction.getColumnModel();
		functionColumnModel.getColumn(0).setCellRenderer(new ColorChooserRenderer());
		functionColumnModel.getColumn(1).setCellRenderer(new ReminderRenderer());
		functionColumnModel.getColumn(4).setCellRenderer(leftCellRenderer);

		// Editors
		JTextField txt = new JTextField();
		txt.setBorder(null);
		TimeEditor timeEditor = new TimeEditor(txt);
		scheduleColumnModel.getColumn(0).setCellEditor(new ColorChooserEditor());
		scheduleColumnModel.getColumn(1).setCellEditor(new ReminderEditor("Horaires"));
		scheduleColumnModel.getColumn(4).setCellEditor(timeEditor);
		scheduleColumnModel.getColumn(5).setCellEditor(timeEditor);
		scheduleColumnModel.getColumn(6).setCellEditor(new ShortcutEditor());

		functionColumnModel.getColumn(0).setCellEditor(new ColorChooserEditor());
		functionColumnModel.getColumn(1).setCellEditor(new ReminderEditor("Fonction"));
		functionColumnModel.getColumn(4).setCellEditor(new ShortcutEditor());

		// Taille des colones
		int colorWidth = 18;
		scheduleColumnModel.getColumn(0).setPreferredWidth(colorWidth);
		scheduleColumnModel.getColumn(0).setMinWidth(colorWidth);
		scheduleColumnModel.getColumn(0).setMaxWidth(colorWidth);
		scheduleColumnModel.getColumn(1).setPreferredWidth(colorWidth);
		scheduleColumnModel.getColumn(1).setMinWidth(colorWidth);
		scheduleColumnModel.getColumn(1).setMaxWidth(colorWidth);
		scheduleColumnModel.getColumn(2).setPreferredWidth(45);
		scheduleColumnModel.getColumn(3).setPreferredWidth(55);
		scheduleColumnModel.getColumn(4).setPreferredWidth(47);
		scheduleColumnModel.getColumn(5).setPreferredWidth(47);
//		scheduleColumnModel.getColumn(6).setPreferredWidth(73);
		for(int i = 1; i < scheduleColumnModel.getColumnCount(); i++)
			scheduleColumnModel.getColumn(i).setHeaderRenderer(new ScheduleHeaderRenderer(this.tableSchedule));
		this.tableSchedule.setShowHorizontalLines(false);
		this.tableSchedule.setIntercellSpacing(new Dimension(0, 0));

		functionColumnModel.getColumn(0).setPreferredWidth(colorWidth);
		functionColumnModel.getColumn(0).setMinWidth(colorWidth);
		functionColumnModel.getColumn(0).setMaxWidth(colorWidth);
		functionColumnModel.getColumn(1).setPreferredWidth(colorWidth);
		functionColumnModel.getColumn(1).setMinWidth(colorWidth);
		functionColumnModel.getColumn(1).setMaxWidth(colorWidth);
		for(int i = 1; i < functionColumnModel.getColumnCount(); i++)
			functionColumnModel.getColumn(i).setHeaderRenderer(new ScheduleHeaderRenderer(this.tableSchedule));
		this.tableFunction.setShowHorizontalLines(false);
		this.tableFunction.setIntercellSpacing(new Dimension(0, 0));

		// Listener des boutons Jours
		JPanel dayPanel = this.dayChooser.getDayPanel();
		int nbButton = dayPanel.getComponentCount();

		JValueButton dayButton;
		this.dayButtons = new ArrayList<>(nbButton);
		for(int i = 0; i < nbButton; i++)
		{
			dayButton = (JValueButton)dayPanel.getComponent(i);
			dayButton.addKeyListener(this);
			this.dayButtons.add(dayButton);
		}
		
		this.monthChooser.setYearPicker(this.yearChooser);
		
		Font font = this.buttonConnect.getFont();
		@SuppressWarnings("unchecked") Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>)font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		this.buttonConnect.setFont(font.deriveFont(attributes));
	}

	/* ------------------------------------------------------------------------ *\
	|* 		  					Attributs privés								*|
	\* ------------------------------------------------------------------------ */
	private GoogleCalendarService service;
	private CalendarListEntry selectedCalendar;
	private String selectedCalendarName;
	private boolean connected;
	private SwingWorker<Void, Void> eventSwingWorker;
	private boolean refreshEvent;

	private CalendarModel calendarModel;
	private ScheduleModel scheduleModel;
	private FunctionModel functionModel;

	private List<JValueButton> dayButtons;
	private List<Event> events;

	/* ---------------------------------------- *\
	|* 		  			Static					*|
	\* ---------------------------------------- */
	private static Log s_logger = LogFactory.getLog(MainWindow.class);

	private static final String DATA_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator;
	private static final String SCHEDULE_FILE_PATH = DATA_PATH + "schedule.dat";
	private static final String FUNCTION_FILE_PATH = DATA_PATH + "function.dat";
	public static final String PREF_FILE_PATH = DATA_PATH + "preferences.dat";

	private static final SimpleDateFormat DAY_SDF = new SimpleDateFormat("dd");
	private static final SimpleDateFormat MONTH_SDF = new SimpleDateFormat("MM");
	private static final SimpleDateFormat YEAR_SDF = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_SDF = new SimpleDateFormat("dd.MM.yyyy");

	private static final String PREF_MAIN_WINDOW_X = "MAIN_WINDOW_X";
	private static final String PREF_MAIN_WINDOW_Y = "MAIN_WINDOW_Y";
	private static final String PREF_MAIN_WINDOW_W = "MAIN_WINDOW_W";
	private static final String PREF_MAIN_WINDOW_H = "MAIN_WINDOW_H";
	private static final String PREF_SELECTED_CALENDAR = "PREF_SELECTED_CALENDAR";
	private static final String NEW_LINE = System.getProperty("line.separator");

	/* ---------------------------------------- *\
	|* 		  			GUI						*|
	\* ---------------------------------------- */
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - yggpuduku
	private JPanel panelConnection;
	private JLabel buttonConnect;
	private JLabel labelAccount;
	private JLabel labelAccountValue;
	private JLabel labelCalendar;
	private JComboBox<CalendarListEntry> comboCalendars;
	private MyMonthPicker monthChooser;
	private MyYearPicker yearChooser;
	private JDayChooser dayChooser;
	private JPanel panelScheduleFunction;
	private JTable tableSchedule;
	private JTable tableFunction;
	private TimedLabel labelMessage;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
