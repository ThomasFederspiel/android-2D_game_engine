package se.federspiel.android.game.utils;

import java.util.ArrayList;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.util.ALog;

public class Maze
{
	public enum MazeType
	{
		RecursiveDivisionMaze,
		RecursiveBacktrackingMaze
	}

	private static final int HORIZONTAL = 0;
	private static final int VERTICAL = 1;
	
	private static final int NORTH_MASK = 1;
	private static final int EAST_MASK = 2;
	private static final int SOUTH_MASK = 4;
	private static final int WEST_MASK = 8;
	private static final int PATH_MASK = 128;
	
	private static final int ALL_DIRECTIONS_MASK = (NORTH_MASK | EAST_MASK | SOUTH_MASK | WEST_MASK);

	private enum WallDirection
	{
		NORTH (NORTH_MASK, 0, -1, SOUTH_MASK),
		EAST (EAST_MASK, 1, 0, WEST_MASK),
		SOUTH (SOUTH_MASK, 0, 1, NORTH_MASK),
		WEST (WEST_MASK, -1, 0, EAST_MASK);
		
		private int mDx = 0;
		private int mDy = 0;
		private int mMask = 0;
		private int mOppositeMask = 0;

		WallDirection(int mask, int dx, int dy, int oppositeMask)
		{
			mMask = mask;
			mDx = dx;
			mDy = dy;
			mOppositeMask = oppositeMask;
		}
		
		int getMask()
		{
			return mMask;
		}
		
		int getDxStep()
		{
			return mDx;
		}
		
		int getDyStep()
		{
			return mDy;
		}
		
		int getOppositeMask()
		{
			return mOppositeMask;
		}
		
		public static WallDirection[] all()
		{
			WallDirection[] directions = new WallDirection[]
			{
				NORTH,
				EAST,
				SOUTH,
				WEST
			};
			
			return directions;
		}
		
		public static WallDirection[] randomize()
		{
			return AMath.shuffle(all());
		}
	}
	
	private int[][] mGrid = null;
	private MazePath mMazePath = new MazePath();
 
	private Maze(int width, int height, int intial)
	{
		mGrid = new int[height][width];
		
		for (int y = 0; y < mGrid.length; y++)
		{
			for (int x = 0; x < mGrid[y].length; x++)
			{
				mGrid[y][x] = intial;
			}
		}
	}

	public static Maze generateMaze(int width, int height, MazeType type)
	{
		Maze maze = null;

		switch (type)
		{
			case RecursiveDivisionMaze :
				maze = new Maze(width, height, 0);
				maze.recursiveDivision(0, 0, width, height, maze.chooseOrientation(width, height));
				break;
				
			case RecursiveBacktrackingMaze :
				maze = new Maze(width, height, ALL_DIRECTIONS_MASK);
				
				int x = AMath.randomInt(width);
				int y = AMath.randomInt(height);
				
				maze.recursiveBacktracking(x, y);
				break;
		}
		
		return maze;
	}

	public void displayMaze()
	{
		String str = " ";
		
		for (int x = 0; x < mGrid[0].length; x++)
		{
			str += "__";
		}
			
		ALog.debug(this, str);
			
		for (int y = 0; y < mGrid.length; y++)
		{
			str = "|";
	   
			for (int x = 0; x < mGrid[y].length; x++)
			{
				boolean bottom = y+1 >= mGrid.length;
				boolean south  = ((mGrid[y][x] & SOUTH_MASK) != 0 || bottom);
				boolean south2 = ((x+1 < mGrid[y].length) && ((mGrid[y][x+1] & SOUTH_MASK) != 0) || bottom);
				boolean east   = (((mGrid[y][x] & EAST_MASK) != 0) || (x+1 >= mGrid[y].length));
				boolean path   = ((mGrid[y][x] & PATH_MASK) != 0);
				
				str += south ? (path) ? "X" : "_" : (path) ? "P" : " ";
				str += east ? "|" : ((south && south2) ? "_" : " ");
			}
	   
			ALog.debug(this, str);
		}
	}

	public ArrayList<Point> getWallCoordinates(float left, float top, int cellWidth, int cellHeight)
	{
		ArrayList<Point> wallCoordinates = new ArrayList<Point>();
		
		Point start = null;
		Point end = null;

		float yPos = top + cellHeight;
		
		for (int y = 0; y < (mGrid.length - 1); y++)
		{
			float xPos = left;
			
			for (int x = 0; x < mGrid[y].length; x++)
			{
				if ((mGrid[y][x] & SOUTH_MASK) != 0)
				{
					if (start == null)
					{
						start = new Point(xPos, yPos);
						end = new Point(xPos + cellWidth, yPos);
					}
					else
					{
						end.X = xPos + cellWidth;
					}
				}
				else
				{
					if (start != null)
					{
						wallCoordinates.add(start);
						wallCoordinates.add(end);
						
						start = null;
						end = null;
					}
				}
				
				xPos += cellWidth;
			}
			
			if (start != null)
			{
				wallCoordinates.add(start);
				wallCoordinates.add(end);
				
				start = null;
				end = null;
			}
			
			yPos += cellHeight;
		}
		
		float xPos = left + cellWidth;
		
		for (int x = 0; x < (mGrid[0].length - 1); x++)
		{
			yPos = top;
			
			for (int y = 0; y < mGrid.length; y++)
			{
				if ((mGrid[y][x] & EAST_MASK) != 0)
				{
					if (start == null)
					{
						start = new Point(xPos, yPos);
						end = new Point(xPos, yPos + cellHeight);
					}
					else
					{
						end.Y = yPos + cellHeight;
					}
				}
				else
				{
					if (start != null)
					{
						wallCoordinates.add(start);
						wallCoordinates.add(end);
						
						start = null;
						end = null;
					}
				}
				
				yPos += cellHeight;
			}
			
			if (start != null)
			{
				wallCoordinates.add(start);
				wallCoordinates.add(end);
				
				start = null;
				end = null;
			}
			
			xPos += cellWidth;
		}
		
		return wallCoordinates;
	}
	
	public ArrayList<Point> getPathCoordinates(float left, float top, int cellWidth, int cellHeight)
	{
		ArrayList<Point> pathCoordinates = new ArrayList<Point>();
		
		if (mMazePath.mSet)
		{
			int cx = mMazePath.mStartX;
			int cy = mMazePath.mStartY;
		
			Point point = new Point(left + cellWidth / 2 + cx * cellWidth,	top + cellHeight / 2 + cy * cellHeight);
			
			pathCoordinates.add(point);
			
			WallDirection[] directions = WallDirection.all();
			
			boolean endOfPath = false;
			
			while (!endOfPath)
			{
				endOfPath = true;
				
				for (int i = 0; i < directions.length; i++)
				{
					WallDirection direction = directions[i];
					
					if ((mGrid[cy][cx] & direction.getMask()) == 0)
					{
						int nx = cx + direction.getDxStep();
						int ny = cy + direction.getDyStep();
						
						if ((ny >= 0) && (ny <= (mGrid.length - 1)) 
								&& (nx >= 0) && (nx <= (mGrid[ny].length - 1)) 
								&& ((mGrid[ny][nx] & PATH_MASK) != 0))
						{
							mGrid[cy][cx] &= ~PATH_MASK;
						
							point = new Point(point);
							
							point.X += direction.getDxStep() * cellWidth;
							point.Y += direction.getDyStep() * cellHeight;
			
							cx = nx;	
							cy = ny;	
						
							pathCoordinates.add(point);
							
							endOfPath = false;
							
							break;
						}
					}
				}
				
				if ((cx == mMazePath.mEndX) && (cy == mMazePath.mEndY))
				{
					endOfPath = true;
				}
			}
		}
		
		return pathCoordinates;
	}
	
	private int chooseOrientation(int width, int height)
	{
		int orientation = HORIZONTAL; 

		if (width < height)
		{
			orientation = HORIZONTAL;
		}
		else if (height < width)
		{
			orientation = VERTICAL;
		}
		else
		{
			if (AMath.randomBool())
			{
				orientation = HORIZONTAL;
			}
			else
			{
				orientation = VERTICAL;
			}
		}
		
		return orientation;
	}
	
	public void solveMaze(int sx, int sy, int ex, int ey)
	{
		if (mMazePath.mSet)
		{
			clearSolverPath();
		}
		
		recursiveBacktrackingSolver(sx, sy, ex, ey);
		
		mMazePath.mSet = true;
		
		mMazePath.mStartX = sx;
		mMazePath.mStartY = sy;
		mMazePath.mEndX = ex;
		mMazePath.mEndY = ey;
	}
	
	private void clearSolverPath()
	{
		for (int y = 0; y < mGrid.length; y++)
		{
			int clearMask = ~PATH_MASK;
			
			for (int x = 0; x < mGrid[y].length; x++)
			{
				mGrid[y][x] &= clearMask;
			}
		}
			
		mMazePath.mSet = false;
	}
	
	private boolean recursiveBacktrackingSolver(int sx, int sy, int ex, int ey)
	{
		boolean foundGoal = false;
		
		mGrid[sy][sx] |= PATH_MASK;
		
		if ((sx == ex) && (sy == ey))
		{
			foundGoal = true;
		}
		else
		{
			WallDirection[] directions = WallDirection.all();
		  
			for (int i = 0; i < directions.length; i++)
			{
				WallDirection direction = directions[i];
				
				int nx = sx + direction.getDxStep();
				int ny = sy + direction.getDyStep();
	
				if ((ny >= 0) && (ny <= (mGrid.length - 1)) 
						&& (nx >= 0) && (nx <= (mGrid[ny].length - 1)) 
						&& ((mGrid[sy][sx] & direction.getMask()) == 0)
						&& ((mGrid[ny][nx] & PATH_MASK) == 0))
				{
				    foundGoal = recursiveBacktrackingSolver(nx, ny, ex, ey);
  
					if (foundGoal)
					{
						return foundGoal;
					}
				}
			}
			
			mGrid[sy][sx] &= ~PATH_MASK;
		}
		
		return foundGoal;
	}
	
	private void recursiveBacktracking(int cx, int cy)
	{
		WallDirection[] directions = WallDirection.randomize();
	  
		for (int i = 0; i < directions.length; i++)
		{
			WallDirection direction = directions[i];
			
			int nx = cx + direction.getDxStep();
			int ny = cy + direction.getDyStep();

			if ((ny >= 0) && (ny <= (mGrid.length - 1)) && (nx >= 0) && (nx <= (mGrid[ny].length - 1)) && (mGrid[ny][nx] == ALL_DIRECTIONS_MASK))
			{
			  mGrid[cy][cx] &= ~direction.getMask();
			  mGrid[ny][nx] &= ~direction.getOppositeMask();
			  
			  recursiveBacktracking(nx, ny);
			}
		}
	}

	private void recursiveDivision(int x, int y, int width, int height, int orientation)
	{
		if ((width > 2) || (height > 2))
		{
			boolean horizontal = orientation == HORIZONTAL;
			
			// where will the wall be drawn from?
			int wx = x + (horizontal ? 0 : AMath.randomInt(width - 1));
			int wy = y + (horizontal ? AMath.randomInt(height - 1) : 0);

			// where will the passage through the wall exist?
			int px = wx + (horizontal ? AMath.randomInt(width) : 0);
			int py = wy + (horizontal ? 0 : AMath.randomInt(height));
			
			// what direction will the wall be drawn?
			int dx = horizontal ? 1 : 0;
			int dy = horizontal ? 0 : 1;
					
			// how long will the wall be?
			int length = horizontal ? width : height;
			
			// what direction is perpendicular to the wall?
			int dir = horizontal ? SOUTH_MASK : EAST_MASK;
					
			for (int i = 0; i < length; i++)
			{
				if ((wx != px) || (wy != py))
				{
					mGrid[wy][wx] |= dir; 
				}
			    
				wx += dx;
				wy += dy;
			}

			int nx = x;
			int ny = y;
			   
			int w = horizontal ? width : wx-x+1;
			int h = horizontal ? wy-y+1 : height;
			  
			recursiveDivision(nx, ny, w, h, chooseOrientation(w, h));
			
			nx = horizontal ? x : wx+1;
			ny = horizontal ? wy+1 : y;
			   
			w = horizontal ? width : x+width-wx-1;
			h = horizontal ? y+height-wy-1 : height;
			   
			recursiveDivision(nx, ny, w, h, chooseOrientation(w, h));
		}
	}
	
	private static class MazePath
	{
		public boolean mSet = false;
		public int mStartX = 0;
		public int mStartY = 0;
		public int mEndX = 0;
		public int mEndY = 0;
	}
}
