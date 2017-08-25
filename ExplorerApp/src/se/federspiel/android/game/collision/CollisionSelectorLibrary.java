package se.federspiel.android.game.collision;

import java.util.ArrayList;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.ICollisionManager.ICollisionSelector;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ISpritePositionChangedListener;
import se.federspiel.android.util.ALog;

public class CollisionSelectorLibrary
{
    public static class ListCollisionSelector implements ICollisionSelector 
    {
        private ArrayList<ISpriteCollisionObject> mSpriteCollisionObjects = new ArrayList<ISpriteCollisionObject>();

		@Override
		public ArrayList<ISpriteCollisionObject> select(ArrayList<ISpriteCollisionObject> objects,
				ISpriteCollisionObject collisioner)
		{
			objects.addAll(mSpriteCollisionObjects);
			objects.remove(collisioner);
			
			return objects;
		}

		@Override
		public void add(ISpriteCollisionObject collisionObject)
		{
			mSpriteCollisionObjects.add(collisionObject);
		}

		@Override
		public void remove(ISpriteCollisionObject collisionObject)
		{
			mSpriteCollisionObjects.remove(collisionObject);
		}

		@Override
		public void clear()
		{
			mSpriteCollisionObjects.clear();
		}
    }
    
    public static class GridCollisionSelector implements ICollisionSelector, ISpritePositionChangedListener 
    {
    	private Rectangle mTmpBounds = Rectangle.Zero.clone();
    	private Point mGridTopLeft = Point.Zero.clone();
        private float mGridHeight = 0;
        private float mGridWidth = 0;
        private int mNofRows = 0;
        private int mNofCols = 0;

    	private ArrayList<ISpriteCollisionObject> mNoneGridItems = new ArrayList<ISpriteCollisionObject>();
        private GridItem[][] mGrid = null;
        
        private boolean mLog = false;
        
        public GridCollisionSelector()
        {
        }
        
        public GridCollisionSelector(int x, int y, int cellWidth, int cellHeight, int rows, int columns)
        {
        	setGridProperties(x, y, cellWidth, cellHeight, rows, columns);
        }
        
		@Override
		public ArrayList<ISpriteCollisionObject> select(ArrayList<ISpriteCollisionObject> objects,
				ISpriteCollisionObject collisioner)
		{
			// find objects
			addObjectsInPath(objects, collisioner);

//			ALog.debug(this, "objects.size() = " + objects.size());
//			ALog.debug(this, "mNoneGridItems.size() = " + mNoneGridItems.size());
			
			objects.addAll(mNoneGridItems);
			
//			ALog.debug(this, "objects.size() = " + objects.size());
			
			objects.remove(collisioner);
			
//			ALog.debug(this, "objects.size() = " + objects.size());
			
			return objects;
		}

		@Override
		public void add(ISpriteCollisionObject collisionObject)
		{
			collisionObject.setOnSpritePositionChangedListener(this);

			Point position = collisionObject.getLastUpdateMovementRay().getStartPosition();
			
			addObject(position.X, position.Y, collisionObject);
		}

		@Override
		public void remove(ISpriteCollisionObject collisionObject)
		{
//			ALog.debug(this, "remove() ");
			
			mLog = false;

			collisionObject.removeOnSpritePositionChangedListener(this);

			Point position = collisionObject.getLastUpdateMovementRay().getStartPosition();
			
			boolean removeStart = removeObject(position.X, position.Y, collisionObject);
			
			position = collisionObject.getLastUpdateMovementRay().getEndPosition();
			
			boolean removeEnd = removeObject(position.X, position.Y, collisionObject);
			
			assert ((removeStart || removeEnd) == true);
			
			if (!(removeStart || removeEnd))
			{
				ALog.debug(this, "remove() -- REMOVE FAILED ==========================");
			}
			
			mLog = false;
		}

		@Override
		public void clear()
		{
			mNoneGridItems.clear();
			mGrid = null;
		}

		@Override
		public void onSpritePositionChanged(ISpriteCollisionObject collisionObject,
				Point oldPosition, Point newPosition)
		{
//			ALog.debug(this, "onSpritePositionChanged() ");
			
			boolean removed = removeObject(oldPosition.X, oldPosition.Y, collisionObject);
			
			assert removed == true;
			
			if (!removed)
			{
				ALog.debug(this, "REMOVE FAILED ==========================");
			}
			
			addObject(newPosition.X, newPosition.Y, collisionObject);
		}

		public void setGridProperties(int x, int y, int cellWidth, int cellHeight, int rows, int columns)
		{
			clear();
			
        	mGridTopLeft.set(x, y);
            
            mNofRows = rows;
            mNofCols = columns;
            
        	mGridWidth = cellWidth;
        	mGridHeight = cellHeight;
        	
        	mGrid = new GridItem[rows][columns];
		}
		
		private boolean removeObject(float x, float y, ISpriteCollisionObject collisionObject)
		{
			boolean removed = false;

//			ALog.debug(this, "removeObject, x = " + x + ", y = " + y);
			
			GridItem item = getGridItem(x, y, collisionObject);

			if (item != null)
			{
//				ALog.debug(this, "removeObject - gridItem");
				
				removed = item.mObjects.remove(collisionObject);
			}
			else
			{
//				ALog.debug(this, "removeObject - noneGridItem");
				
				removed = mNoneGridItems.remove(collisionObject);
			}
			
			return removed;
		}
		
		private void addObject(float x, float y, ISpriteCollisionObject collisionObject)
		{
//			ALog.debug(this, "addObject, x = " + x + ", y = " + y);
			
			GridItem item = getGridItem(x, y, collisionObject);
			
			if (item != null)
			{
//				ALog.debug(this, "addObject - gridItem");
				
				item.mObjects.add(collisionObject);
				
				if (item.mObjects.size() > 1)
				{
					ALog.debug(this, "TOO MANY GRID ITEMS ----------------------");
				}
			}
			else
			{
//				ALog.debug(this, "addObject - noneGridItem");
				
				mNoneGridItems.add(collisionObject);
			}
		}

		private GridItem getGridItem(float x, float y, ISpriteCollisionObject collisionObject)
        {
			collisionObject.getBounds().copy(mTmpBounds, x, y);
			
        	int upperLeftRow = (int) ((mTmpBounds.getTop() - mGridTopLeft.Y) / mGridHeight);
        	int upperLeftCol = (int) ((mTmpBounds.getLeft() - mGridTopLeft.X) / mGridWidth);
        	
        	int lowerRightRow = (int) ((mTmpBounds.getBottom() - mGridTopLeft.Y) / mGridHeight);
        	int lowerRightCol = (int) ((mTmpBounds.getRight() - mGridTopLeft.X) / mGridWidth);

        	GridItem item = null;

//			ALog.debug(this, "getGridItem, x = " + x + ", y = " + y);
//			ALog.debug(this, "getGridItem, dims = " + dims.toString());
//			ALog.debug(this, "getGridItem, grid = " + mGridTopLeft.toString());
//			ALog.debug(this, "getGridItem, gridHeight = " + mGridHeight);
//			ALog.debug(this, "getGridItem, gridWidth = " + mGridWidth);
//			ALog.debug(this, "upperLeftRow = " + upperLeftRow + ", upperLeftCol = " + upperLeftCol);
//			ALog.debug(this, "lowerRightRow = " + lowerRightRow + ", lowerRightCol = " + lowerRightCol);
        	
        	if (((upperLeftRow >= 0) && (upperLeftRow < mNofRows))
	        		&& ((upperLeftCol >= 0) && (upperLeftCol < mNofCols)))
        	{
	        	if ((upperLeftRow == lowerRightRow) && (upperLeftCol == lowerRightCol))
	        	{
	            	item = mGrid[upperLeftRow][upperLeftCol];

	            	if (mLog)
	            	{
	            		ALog.debug(this, "getGridItem = item(" + upperLeftRow + ", "+ upperLeftCol + ")");
	            	}
	            	
	            	if (item == null)
	            	{
	            		item = new GridItem();
	            		
	            		mGrid[upperLeftRow][upperLeftCol] = item; 
	            	}
	        	}
        	}

        	return item;
        }

		private void addObjectsInPath(ArrayList<ISpriteCollisionObject> objects,
				ISpriteCollisionObject collisioner)
		{
			Ray ray = collisioner.getLastUpdateMovementRay();
			
			Point startPosition = ray.getStartPosition();
			Point endPosition = ray.getEndPosition();
			
//			ALog.debug(this, "startPosition = " + startPosition);
//			ALog.debug(this, "endPosition = " + endPosition);
			
			float maxX = Math.max(endPosition.X, startPosition.X);
			float minX = Math.min(endPosition.X, startPosition.X);
			
			float maxY = Math.max(endPosition.Y, startPosition.Y);
			float minY = Math.min(endPosition.Y, startPosition.Y);
			
			collisioner.getBounds().copy(mTmpBounds, minX, minY);

			minX = mTmpBounds.getLeft();
			minY = mTmpBounds.getTop();
			
			collisioner.getBounds().copy(mTmpBounds, maxX, maxY);

			maxX = mTmpBounds.getRight();
			maxY = mTmpBounds.getBottom();
			
        	int minRow = (int) ((minY - mGridTopLeft.Y) / mGridHeight);
        	int minCol = (int) ((minX - mGridTopLeft.X) / mGridWidth);
        	
        	int maxRow = (int) ((maxY - mGridTopLeft.Y) / mGridHeight);
        	int maxCol = (int) ((maxX - mGridTopLeft.X) / mGridWidth);
        	
        	for (int i = minRow; i <= maxRow; i++)
        	{
            	for (int j = minCol; j <= maxCol; j++)
            	{
                	if (((i >= 0) && (i < mNofRows))
                		&& ((j >= 0) && (j < mNofCols)))
                	{
                    	GridItem item = mGrid[i][j];
                    	
                    	if (item != null)
                    	{
                    		if (item.mObjects.size() > 0)
                    		{
//	                			ALog.debug(this, "Add path item (" + i + ", " + j + ")");
                    		}
                			
                    		objects.addAll(item.mObjects);
                    	}
                	}
            	}
        	}
		}

		private static class GridItem
        {
        	public ArrayList<ISpriteCollisionObject> mObjects = new ArrayList<ISpriteCollisionObject>();
        }
    }
}
