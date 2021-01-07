/*
 * This file is part of 4DXliffEditor.
 * 4DXliffEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * 4DXliffEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with 4DXliffEditor.  If not, see <http://www.gnu.org/licenses/>.   
 */
package ch.correvon.google.calendar.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.window.mainWindow.MainWindow;

public class PreferencesBundle
{
	public static Map<String, String> getPref()
	{
		return metadata;
	}

	public static Map<String, String> readPref()
	{
		String line;
		metadata = new HashMap<String, String>(20);

		if(!new File(PREF_FILE_PATH).exists())
			return metadata;

		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(PREF_FILE_PATH), "utf-8")))
		{
			while((line = br.readLine()) != null)
			{
				String[] keyValue = line.split(":", 2);
				if(keyValue.length == 2)
					metadata.put(keyValue[0], keyValue[1]);
				else
					s_logger.error("Error reading '" + PREF_FILE_PATH + "' at line '" + line + "'");
			}
		}
		catch(FileNotFoundException e)
		{
			s_logger.error("Cannot open file '" + PREF_FILE_PATH + "'", e);
		}
		catch(IOException e)
		{
			s_logger.error("Error reading file '" + PREF_FILE_PATH + "'", e);
		}

		return metadata;
	}

	public static void writePref(Map<String, String> metadata)
	{
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PREF_FILE_PATH, false), "utf-8")))
		{
			for(Entry<String, String> entry:metadata.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();

				writer.write(key + ":" + value + NEW_LINE);
			}
		}
		catch(IOException e)
		{
			s_logger.error("Error writing preference file '" + PREF_FILE_PATH + "'", e);
		}
	}

	private static Map<String, String> metadata = null;
	private static final String PREF_FILE_PATH = MainWindow.PREF_FILE_PATH;
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static Log s_logger = LogFactory.getLog(PreferencesBundle.class);
}
