package se.federspiel.android.util;

import java.util.ArrayList;

public class SortingArrayList<T extends Comparable<T>> extends ArrayList<T>
{
	private static final long serialVersionUID = 0L;
	
	public enum Order
	{
		ASCENT,
		DESCENT
	}
	
	private Order mOrder = Order.ASCENT;
	
	public SortingArrayList()
	{
	}
	
	public SortingArrayList(Order order)
	{
		mOrder = order;
	}
	
	@Override
	public boolean add(T element)
	{
		if (size() == 0)
		{
			super.add(element);
		}
		else
		{
			T item = get(size() - 1);
			
			int compare = item.compareTo(element);
		
			if (compare == -1) 
			{
				if (mOrder == Order.ASCENT)
				{
					super.add(element);
					
					return true;
				}
			}
			else if (compare == 1)
			{
				if (mOrder == Order.DESCENT)
				{
					super.add(element);
					
					return true;
				}
			}
			
			int index = 0;
			
			int low = 0;
			int high = size();
			
			while (low <= high)
			{
				index = (low + high) / 2; 
				
				item = get(index);
				
				compare = item.compareTo(element);
				
				if (compare == -1) 
				{
					if (mOrder == Order.ASCENT)
					{
						low = index + 1; 
					}
					else
					{
						high = index - 1; 
					}
				}
				else if (compare == 1) 
				{
					if (mOrder == Order.ASCENT)
					{
						high = index - 1; 
					}
					else
					{
						low = index + 1; 
					}
				}
				else
				{
					break;
				}
			}
			
			super.add(index, element);
		}
		
		return true;
	}
}
