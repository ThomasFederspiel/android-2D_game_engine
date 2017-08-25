package se.federspiel.android.data;

import java.util.Hashtable;

public class GlobalDataStorage
{
	private static GlobalDataStorage mInstance = null;
	
	private Hashtable<String, Hashtable<String, Object>> mGlobalObjectStorage = 
			new Hashtable<String, Hashtable<String, Object>>();
	
	public static GlobalDataStorage instance()
	{
		if (mInstance == null)
		{
			mInstance = new GlobalDataStorage();
		}
		
		return mInstance;
	}

	public <T> void addObject(String className, String key, T value)
	{
		Hashtable<String, Object> dataTable = null;
		
		if (mGlobalObjectStorage.containsKey(className))
		{
			dataTable = mGlobalObjectStorage.get(className);
		}
		else
		{
			dataTable = new Hashtable<String, Object>();
			
			mGlobalObjectStorage.put(className, dataTable);
		}
		
		dataTable.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(String className, String key)
	{
		T dataObject = null;
		
		if (mGlobalObjectStorage.containsKey(className))
		{
			Hashtable<String, Object> dataTable = mGlobalObjectStorage.get(className);

			if (dataTable.containsKey(key))
			{
				Object data = dataTable.remove(key);

				dataObject = (T) data;
			}
			
			if (dataTable.size() == 0)
			{
				mGlobalObjectStorage.remove(className);
			}
		}
		
		return dataObject;
	}
}
