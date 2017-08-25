package se.federspiel.android.util;

public class SortTools
{
	public static <T> void shuffle(T[] array)
	{
		int nofItems = array.length;
		
		for (int i = 0; i < nofItems; i++)
		{
			int rndIndex = Math.round((float) (Math.random() * (nofItems - 1)));
			
			T tmp = array[i];
			
			array[i] = array[rndIndex];
			array[rndIndex] = tmp;
		}
	}
}
