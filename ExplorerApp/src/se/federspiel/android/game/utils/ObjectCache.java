package se.federspiel.android.game.utils;

import java.util.ArrayList;

public abstract class ObjectCache<T>
{
	private ArrayList<T> mCache = new ArrayList<T>();
	
	public T get()
	{
		T object = null;
		
		if (mCache.size() > 0)
		{
			object = mCache.remove(0);
			
			initObject(object);
		}
		else
		{
			object = createObject();
		}
		
		return object;
	}
	
	public void add(T object)
	{
		mCache.add(object);
	}

	public void clear()
	{
		mCache.clear();
	}
	
	protected void initObject(T object)
	{
	}
	
	protected abstract T createObject();
}
