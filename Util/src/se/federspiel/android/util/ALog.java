package se.federspiel.android.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class ALog
{
	private static String sAppName = "ALog"; 
	
	private static final boolean DEBUG_ENABLE = true;
	
	public ALog()
	{
	}

	public static void setAppName(String name)
	{
		sAppName = name;
	}
	
	public static String getAppName()
	{
		return sAppName;
	}

	public static String getStackTrace()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		PrintStream ps = new PrintStream(baos);
		
		new Throwable().printStackTrace(ps);
		
		return baos.toString();
	}
	
	public static void info(Object logger, String message)
	{
		Log.i(getTag(logger), message);
	}
	
	public static void debug(Object logger, String message)
	{
		if (DEBUG_ENABLE)
		{
			Log.d(getTag(logger), message);
		}
	}

	public static void error(Object logger, String message)
	{
		Log.e(getTag(logger), message);
	}

	public static void error(Object logger, String message, Exception ex)
	{
		Log.e(getTag(logger), message + " Exception : " + ex.getMessage());
	}

	public static void error(String message, StackTraceElement[] trace)
	{
		Object logger = new Object();
		
		Log.e(getTag(logger), message);
		
		writeStackTraceLog(logger, trace);
	}
	
	public static void error(Object logger, Exception ex)
	{
		Log.e(getTag(logger), ex.getMessage());
	}
	
    private static void writeStackTraceLog(Object logger, StackTraceElement[] trace)
    {
        if ((trace != null) && (trace.length > 0))
        {
            for (StackTraceElement frame : trace)
            {
            	Log.e(getTag(logger), frame.toString());
            }
        }
    }

	private static String getTag(Object logger)
	{
		return sAppName + "::" + logger.getClass().getName();
	}
	
	public static ALogRecordDatabase readApplicationLog(ALogRecordDatabase.LogCatFormatEnum format, String search, boolean regExp)
	{
		ALogRecordDatabase records = new ALogRecordDatabase();

		records.readLogs(format, search, regExp);
		
		return records;
	}

	public static void clearApplicationLog()
	{
		ALogRecordDatabase.clearLog();
	}

	static public class ALogRecordDatabase
	{
		private ArrayList<ALogRecord> mRecords = new ArrayList<ALogRecord>();

		public enum LogCatFormatEnum
		{
			BRIEF("brief"),
			PROCESS("process"),
			TAG("tag"),
			RAW("raw"),
			TIME("time"),
			THREAD_TIME("threadtime"),
			LONG("long");

			private String mFormat = null;
			
			private LogCatFormatEnum(String format)
			{
				mFormat = format;
			}

			public String getFormat()
			{
				return mFormat;
			}
			
			public String toString()
			{
				return getFormat();
			}
		}
		
		public ALogRecordDatabase()
		{
		}

		public static void clearLog()
		{
			try 
			{
				Runtime.getRuntime().exec("logcat -c");
			} catch (IOException e) {
			}
		}
		
		public void readLogs(LogCatFormatEnum format, String search, boolean regExp)
		{
			try 
			{
				Process process = Runtime.getRuntime().exec("logcat -d -v " + format.getFormat());

				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));

				try
				{
					String line;
		
					while ((line = bufferedReader.readLine()) != null) 
					{
						addRecord(line, search, regExp);
					}
				} 
				finally
				{
					bufferedReader.close();
				}
			} catch (IOException e) {
			}
		}

		private String filterLine(String line, String search, boolean regExp)
		{
			String fLine = null;
		
			if (!search.isEmpty())
			{
				if (regExp)
				{
					Pattern pattern = Pattern.compile(search);
			
					Matcher matcher = pattern.matcher(line);
			
					if (matcher.matches())
					{
						fLine = line;
					}
				}
				else if (line.contains(search))
				{
					fLine = line;
				}
			}
			else
			{
				fLine = line;
			}
			
			return fLine;
		}
		
		private void addRecord(String line, String search, boolean regExp)
		{
			String fLine = filterLine(line, search, regExp);

			if (fLine!= null)
			{
				ALogRecord record = new ALogRecord(fLine);
			
				mRecords.add(record);
			}
		}

		public ArrayList<ALogRecord> getAllRecords()
		{
			return mRecords;
		}

		public class ALogRecord
		{
			private String mRecord = null;

			public ALogRecord(String record)
			{
				mRecord = record;
			}
			
			public String toString()
			{
				return mRecord;
			}
		}
	}
}

