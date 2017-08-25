package se.federspiel.android.game.utils;

public class ArrayDisjointSet
{
	private int[] set;
	private int[] sizes;
	private int size;

	/**
	 * Creates a new disjoint set, in which each item is in its own group.
	 * 
	 * @param size The initial number of elements in the set.
	 */
	public ArrayDisjointSet(int size) 
	{
		this.set = new int[size];
		
		for (int i = 0; i < size; i++) 
		{
			this.set[i] = i;
		}
		
		this.sizes = new int[size];

		for (int i = 0; i < size; i++) 
		{
			this.sizes[i] = 1;
		}
		this.size = size;
	}
	
	/**
	 * Finds the group in which the given item is found.
	 * 
	 * Data structure optimization:
	 * while searching, makes all nodes in the search path to point directly at
	 * the root of the group, which will be returned.
	 * 
	 * @param item the item for which group to search.
	 * @return The item at the root of the found group.
	 */
	public int find(int item) 
	{
		int root = item;
		
		// find the root
		while (set[root] != root) 
		{
			root = set[root];
		}
		
		// now shorten the paths
		int curr = item;
		
		while (set[curr] != root) 
		{
			set[curr] = root;
		}
		
		return root;
	}

	/**
	 * Joins the two groups together.
	 * 
	 * Data structure optimization:
	 * joins the smaller group to the larger to avoid long sequences.
	 * 
	 * @return the root of the joint group.
	 */
	public int join(int item1, int item2) 
	{
		int group1 = find(item1);
		int group2 = find(item2);
		
		--size;
		
		if (sizes[group1] > sizes[group2]) 
		{
			set[group2] = group1;
			sizes[group1] += sizes[group2];
			return group1;
		} 
		else 
		{
			set[group1] = group2;
			sizes[group2] += sizes[group1];			
			return group2;
		}
	}
	
	public int size() 
	{
		return size;
	}
}
