package se.federspiel.android.game;

import static junit.framework.Assert.assertTrue;

import java.util.Map;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IImageManager;
import se.federspiel.android.game.utils.ObjectCache;
import se.federspiel.android.util.ALog;
import se.federspiel.android.util.ImageTools;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.example.explorerapp.AInstrumentation;

public class ImageManager implements IImageManager
{
    private static final boolean IN_MUTABLE = true;
    private static final Bitmap.Config PREFERRED_CONFIG = Bitmap.Config.RGB_565;
	
	private IGameContext mContext = null;
	private Context mApplContext = null;
	private ImageCache mImageCache = null;
	private StringBuffer chacheKey = new StringBuffer();

	private int mResoureIdGenerator = 0x7f04ffff;

	private boolean mRecycleUnallocatedEnabled = false;

	public ImageManager(IGameContext context)
	{
		mContext = context;
		mApplContext = context.getApplContext();
		
		int memClass = 0;
		
		if (mContext.isApplicationFlagEnabled(ApplicationInfo.FLAG_LARGE_HEAP))
		{
			memClass = ((ActivityManager) mApplContext.getSystemService(
					Context.ACTIVITY_SERVICE)).getLargeMemoryClass();
		}
		else
		{
			memClass = ((ActivityManager) mApplContext.getSystemService(
					Context.ACTIVITY_SERVICE)).getMemoryClass();
		}

		// Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = 1024 * 1024 * memClass / 8;

	    mImageCache = new ImageCache(cacheSize); 
	}

	@Override
	public void destroy()
	{
		mContext = null;
		chacheKey = null;
		
	    mImageCache.clear();
	}
	
    @Override
	public Dimensions getImageSize(String path, Dimensions dim)
	{
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    dim.setDimensions(options.outWidth,options.outHeight);
	    
	    return dim;
	}

    @Override
    public void setRecycleUnallocated(boolean enable)
    {
    	mRecycleUnallocatedEnabled = enable;
    }
    
    @Override
	public void recycleUnallocated()
	{
    	mImageCache.recycle();
	}

    @Override
	public int loadBitmap(String path, int reqWidth, int reqHeight) 
    {
    	return loadBitmap(path, reqWidth, reqHeight, ScaleOperator.Scale);
    }
    
    @Override
	public int loadBitmap(String path, int reqWidth, int reqHeight, ScaleOperator operator) 
	{
		int resId = generateResourceId();
		
		// ;+ resid only
		final String imageKey = buildCacheKey(resId, reqWidth, reqHeight);
		
		Bitmap bitmap = decodeSampledBitmapFromResource(path, reqWidth, reqHeight, operator); 
	    
    	addBitmapToMemoryCache(imageKey, bitmap, false);
	    
	    return resId;
	}
	
    @Override
	public int loadBitmap(String path) 
	{
		int resId = generateResourceId();
		
		// ;+ resid only
		final String imageKey = buildCacheKey(resId, 0, 0);
		
    	Bitmap bitmap = BitmapFactory.decodeFile(path);
    	
		// ;+ resid only
    	addBitmapToMemoryCache(imageKey, bitmap, false);
	    
	    return resId;
	}

    // ;+
    @Override
	public Bitmap allocateBitmap(int resourceId, int reqWidth, int reqHeight) 
    {
    	return allocateBitmap(resourceId, reqWidth, reqHeight, ScaleOperator.Scale);
    }
    
    // ;+
    @Override
	public Bitmap allocateBitmap(int resourceId, int reqWidth, int reqHeight, ScaleOperator operator) 
    {
    	return loadBitmap(resourceId, reqWidth, reqHeight, operator, true);
    }
    
    @Override
	public Bitmap allocateBitmap(int resourceId) 
    {
    	return loadBitmap(resourceId, true);
    }

    @Override
	public int allocateBitmap(Bitmap bitmap)
	{
		int resId = generateResourceId();
		
		final String imageKey = buildCacheKey(resId, bitmap.getWidth(), bitmap.getHeight());

    	addBitmapToMemoryCache(imageKey, bitmap, true);
    	
    	return resId;
	}
    
    @Override
	public Bitmap loadBitmap(int resourceId, int reqWidth, int reqHeight) 
	{
    	return loadBitmap(resourceId, reqWidth, reqHeight, ScaleOperator.Scale);
	}
    
    @Override
	public Bitmap loadBitmap(int resourceId, int reqWidth, int reqHeight, ScaleOperator operator) 
	{
    	return loadBitmap(resourceId, reqWidth, reqHeight, operator, false);
	}
	
    @Override
	public Bitmap loadBitmap(int resourceId) 
	{
	    return loadBitmap(resourceId, false);
	}

    @Override
	public Bitmap getBitmap(int resourceId) 
	{
		final String imageKey = buildCacheKey(resourceId, 0, 0);

	    return getBitmapFromMemCache(imageKey, false);
	}
	
    @Override
	public int addBitmap(Bitmap bitmap)
	{
		int resId = generateResourceId();
		
		final String imageKey = buildCacheKey(resId, bitmap.getWidth(), bitmap.getHeight());

    	addBitmapToMemoryCache(imageKey, bitmap, false);
    	
    	return resId;
	}
	
    @Override
	public void deallocateBitmap(int resourceId) 
	{
		final String imageKey = buildCacheKey(resourceId, 0, 0);
	    
	    deallocateBitmapInMemCache(imageKey);
	}
	
    @Override
	public void deallocateBitmap(int resourceId, int width, int height) 
	{
		final String imageKey = buildCacheKey(resourceId, width, height);
	    
	    deallocateBitmapInMemCache(imageKey);
	}

    @Override
	public void unloadBitmap(int resourceId) 
	{
		final String imageKey = buildCacheKey(resourceId, 0, 0);
	    
	    removeBitmapFromMemCache(imageKey);
	}
	
    @Override
	public void unloadBitmap(int resourceId, int width, int height) 
	{
		final String imageKey = buildCacheKey(resourceId, width, height);
	    
	    removeBitmapFromMemCache(imageKey);
	}

    private String buildCacheKey(int resourceId, int width, int height)
    {
		chacheKey.delete(0, chacheKey.length());
		
		String key = String.valueOf(resourceId);
		
		chacheKey.append(key);
		
		if (resourceId < mResoureIdGenerator)
		{
			chacheKey.append(":");
			chacheKey.append(width);
			chacheKey.append(":");
			chacheKey.append(height);
		}

		return chacheKey.toString();
    }
    
	private Bitmap loadBitmap(int resourceId, int reqWidth, int reqHeight, ScaleOperator operator, boolean allocate) 
	{
		final String imageKey = buildCacheKey(resourceId, reqWidth, reqHeight);

	    Bitmap bitmap = getBitmapFromMemCache(imageKey, allocate);
	    
	    if (bitmap == null) 
	    {
	    	bitmap = decodeSampledBitmapFromResource(mApplContext.getResources(), resourceId, reqWidth, reqHeight, operator); 
	    	
	    	addBitmapToMemoryCache(imageKey, bitmap, allocate);
	    }
	    
	    return bitmap;
	}
    
	private Bitmap loadBitmap(int resourceId, boolean allocate) 
	{
		final String imageKey = buildCacheKey(resourceId, 0, 0);

	    Bitmap bitmap = getBitmapFromMemCache(imageKey, allocate);
	    
	    if (bitmap == null) 
	    {
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inScaled = false;
		    options.inMutable = IN_MUTABLE;
		    options.inPreferredConfig = PREFERRED_CONFIG;
		    
	    	bitmap = BitmapFactory.decodeResource(mApplContext.getResources(), resourceId, options);
	    	
	    	addBitmapToMemoryCache(imageKey, bitmap, allocate);
	    }
	    
	    return bitmap;
	}

    private void addBitmapToMemoryCache(String key, Bitmap bitmap, boolean allocate) 
	{
		assertTrue(getBitmapFromMemCache(key, false) == null);

		ImageCacheItem item = mImageCache.createItem();
		
		item.mBitmap = bitmap;
		
		if (allocate)
		{
			item.mAllocationCount = 1;
		}
		
		mImageCache.put(key, item);
		
		if (AInstrumentation.LOG_IMAGE_MANAGER_STATISTICS)
		{
			logStatistics();			
		}
	}

	private Bitmap getBitmapFromMemCache(String key, boolean allocate) 
	{
		Bitmap bitmap = null;
		
		ImageCacheItem item = mImageCache.get(key);

		if (item != null)
		{
			bitmap = item.mBitmap;
			
			if (allocate)
			{
				item.mAllocationCount++;
			}
		}
		
		return bitmap;
	}
	
	private void removeBitmapFromMemCache(String key) 
	{
		ImageCacheItem item = mImageCache.remove(key);

		// Object may no longer be in the cache if evicted
		if (item != null)
		{
			assertTrue(item.mAllocationCount == 0);
 			assertTrue(item.mBitmap != null);
	    
		    item.mBitmap.recycle();
		    
		    mImageCache.returnItem(item);
		}
		
		if (AInstrumentation.LOG_IMAGE_MANAGER_STATISTICS)
		{
			logStatistics();			
		}
	}

	private void deallocateBitmapInMemCache(String key) 
	{
		ImageCacheItem item = mImageCache.get(key);

		// Object may no longer be in the cache if evicted
		if (item != null)
		{
			assertTrue(item.mAllocationCount > 0);
	
			item.mAllocationCount--;
			
			if (mRecycleUnallocatedEnabled)
			{
				ALog.debug(this, "item.mAllocationCount = " + item.mAllocationCount);
				
				if (item.mAllocationCount == 0)
				{
					ALog.debug(this, "mImageCache.remove(key)");

					removeBitmapFromMemCache(key);
				}
			}
		}
	}
		
	private Bitmap decodeSampledBitmapFromResource(Resources resource, int resourceId, int reqWidth, int reqHeight, ScaleOperator operator) 
	{
	    Bitmap bitmap = null;
	    
	    Bitmap loadedBitmap = null;
	    
    	switch (operator)
    	{
	    	case Scale :
			    // First decode with inJustDecodeBounds=true to check dimensions
			    final BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
			    scaleOptions.inJustDecodeBounds = true;
			    scaleOptions.inPreferredConfig = PREFERRED_CONFIG;
			    
			    BitmapFactory.decodeResource(resource, resourceId, scaleOptions);
		
			    // Calculate inSampleSize
			    scaleOptions.inSampleSize = calculateInSampleSize(scaleOptions, reqWidth, reqHeight);
		
			    // Decode bitmap with inSampleSize set
			    scaleOptions.inJustDecodeBounds = false;
	
			    loadedBitmap = BitmapFactory.decodeResource(resource, resourceId, scaleOptions);
			    
			    bitmap = loadedBitmap;
			    
			    if ((loadedBitmap.getWidth() != reqWidth) || (loadedBitmap.getHeight() != reqHeight))
			    {
			    	bitmap = Bitmap.createScaledBitmap(loadedBitmap, reqWidth, reqHeight, true);
			    	
			    	loadedBitmap.recycle();
			    }
			    
			    break;
			    
	    	case Tile :
			    final BitmapFactory.Options tileOptions = new BitmapFactory.Options();
			    tileOptions.inScaled = false;
			    
			    tileOptions.inPreferredConfig = PREFERRED_CONFIG;
			    
	    		loadedBitmap = BitmapFactory.decodeResource(resource, resourceId, tileOptions);
			    
	    		bitmap = ImageTools.createBitmapFromTile(loadedBitmap, reqWidth, reqHeight);
	    		
		    	loadedBitmap.recycle();
				break;
    	
    		default :
    			assertTrue(false);
	    }
	    
	    return bitmap;
	}
	
	private Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight, ScaleOperator operator) 
	{
	    Bitmap bitmap = null;
	    
	    Bitmap loadedBitmap = null;
	    
    	switch (operator)
    	{
	    	case Scale :
			    // First decode with inJustDecodeBounds=true to check dimensions
			    final BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
			    scaleOptions.inJustDecodeBounds = true;
			    
			    scaleOptions.inPreferredConfig = PREFERRED_CONFIG;
			    
			    BitmapFactory.decodeFile(path, scaleOptions);
		
			    // Calculate inSampleSize
			    scaleOptions.inSampleSize = calculateInSampleSize(scaleOptions, reqWidth, reqHeight);
		
			    // Decode bitmap with inSampleSize set
			    scaleOptions.inJustDecodeBounds = false;
			    
			    loadedBitmap = BitmapFactory.decodeFile(path, scaleOptions);
			    
			    bitmap = loadedBitmap;
			    
			    if ((loadedBitmap.getWidth() != reqWidth) || (loadedBitmap.getHeight() != reqHeight))
			    {
	    	    	bitmap = Bitmap.createScaledBitmap(loadedBitmap, reqWidth, reqHeight, true);
	    	    	
	    	    	loadedBitmap.recycle();
		    	}
			    
			    break;
			    
	    	case Tile :
			    final BitmapFactory.Options tileOptions = new BitmapFactory.Options();
			    tileOptions.inScaled = false;
			    
			    tileOptions.inPreferredConfig = PREFERRED_CONFIG;
			    
			    loadedBitmap = BitmapFactory.decodeFile(path, tileOptions);
			    
	    		bitmap = ImageTools.createBitmapFromTile(loadedBitmap, reqWidth, reqHeight);
	    		
		    	loadedBitmap.recycle();
				break;
	    }
	    
	    return bitmap;
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) 
	    {
	        if (width > height) 
	        {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } 
	        else 
	        {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	
	    return inSampleSize;
	}
	
	private int generateResourceId()
	{
	    int id = mResoureIdGenerator;

	    mResoureIdGenerator--;
	    
	    return id;
	}

	private void logStatistics()
	{
		ALog.debug(this, "ImageManager statistics -------------");
		ALog.debug(this, "Max size (kB) : " + mImageCache.maxSize() / 1024);
		ALog.debug(this, "In use (kB) : " + mImageCache.size() / 1024);
		ALog.debug(this, "# active : " + mImageCache.getActiveItemCount());
		ALog.debug(this, "# released : " + mImageCache.getReleasedItemCount());
		ALog.debug(this, "# evicted : " + mImageCache.evictionCount());
	}
	
	private static class ImageCacheItem
	{
		public Bitmap mBitmap = null;
		public int mAllocationCount = 0;
		
		public void reset()
		{
			mBitmap = null;
			mAllocationCount = 0;
		}
	}

	private static class ImageCacheItemCache extends ObjectCache<ImageCacheItem>
	{
		@Override
		protected ImageCacheItem createObject()
		{
			return new ImageCacheItem();
		}

		@Override
		protected void initObject(ImageCacheItem object)
		{
			object.reset();
		}
	}
	
	private static class ImageCache extends LruCache<String, ImageCacheItem>
	{
		private ImageCacheItemCache mImageCacheItemCache = new ImageCacheItemCache();

		private int mActiveItemsCount = 0;
		private int mReleasedItemsCount = 0;
		
		public ImageCache(int maxSize)
		{
			super(maxSize);
		}

		public void recycle()
		{
			Map<String, ImageCacheItem> map = snapshot();
			
			for (Map.Entry<String, ImageCacheItem> entry : map.entrySet()) 
			{
				if (entry.getValue().mAllocationCount == 0)
				{
					remove(entry.getKey());
				}
			}
		}
		
		public void clear()
		{
			evictAll();
			
			mImageCacheItemCache.clear();
		}
		
		public int getActiveItemCount()
		{
			return mActiveItemsCount;
		}
		
		public int getReleasedItemCount()
		{
			return mReleasedItemsCount;
		}

		public ImageCacheItem createItem()
		{
			mActiveItemsCount++;
			
			return mImageCacheItemCache.get();
		}
		
		public void returnItem(ImageCacheItem item)
		{
			mActiveItemsCount--;
			mReleasedItemsCount++;
			
			mImageCacheItemCache.add(item);
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				ImageCacheItem oldValue, ImageCacheItem newValue)
		{
			super.entryRemoved(evicted, key, oldValue, newValue);
			
			if (evicted)
			{
				assertTrue(oldValue.mAllocationCount == 0);

				oldValue.mBitmap.recycle();
				
				mImageCacheItemCache.add(oldValue);
			}
		}

        @Override
        protected int sizeOf(String key, ImageCacheItem item) 
        {
            // The cache size will be measured in bytes rather than number of items.
            return item.mBitmap.getByteCount();
        }
	}
}
