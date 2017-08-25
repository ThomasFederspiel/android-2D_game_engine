package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Dimensions;
import android.graphics.Bitmap;

public interface IImageManager extends IDestroyable
{
	public enum ScaleOperator
	{
		Scale,
		Tile
	}
	
	public Dimensions getImageSize(String path, Dimensions dim);

	public Bitmap allocateBitmap(int resourceId, int reqWidth, int reqHeight);
	public Bitmap allocateBitmap(int resourceId, int reqWidth, int reqHeight, ScaleOperator operator);
	public Bitmap allocateBitmap(int resourceId); 
	public int allocateBitmap(Bitmap bitmap);

	public void deallocateBitmap(int resourceId, int reqWidth, int reqHeight);
	public void deallocateBitmap(int resourceId); 
	
	public Bitmap loadBitmap(int resourceId, int reqWidth, int reqHeight);
	public Bitmap loadBitmap(int resourceId, int reqWidth, int reqHeight, ScaleOperator operator);
	public Bitmap loadBitmap(int resourceId); 

	public int loadBitmap(String path, int reqWidth, int reqHeight);
	public int loadBitmap(String path, int reqWidth, int reqHeight, ScaleOperator operator);
	public int loadBitmap(String path); 

	public void unloadBitmap(int resourceId);
	public void unloadBitmap(int resourceId, int width, int height);
	
	public Bitmap getBitmap(int resourceId);
	public int addBitmap(Bitmap bitmap);
	
    public void setRecycleUnallocated(boolean enable);
	public void recycleUnallocated();
}
