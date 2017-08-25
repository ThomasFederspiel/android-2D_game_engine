package se.federspiel.android.util;

import java.util.ArrayList;

import se.federspiel.android.game.geometry.Rectangle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageTools
{
	private static Matrix sMatrix = new Matrix();

	private static final int[][] X_MOORE_NEIGHBOR = new int[][]
	{
		{ 1,  1,  0 },
		{ 0,  0,  0 },
		{ 0, -1, -1 }
	};
		 
	private static final int[][] Y_MOORE_NEIGHBOR = new int[][]
	{
		{  0, 0, 1 },
		{ -1, 0, 1 },
		{ -1, 0, 0 }
	};
	
	public static Bitmap extractBitmap(Bitmap source, Rectangle bounds)
	{
		return Bitmap.createBitmap(source, (int) bounds.getLeft(), (int) bounds.getTop(), bounds.getWidth(),
				bounds.getHeight());
	}

	public static Bitmap rotateBitmap(Bitmap source, float degrees)
	{
		sMatrix.reset();
		sMatrix.postRotate(degrees);
		
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), sMatrix, false);
	}
	
	public static Bitmap[][] splitBitmap(int rows, int cols, Bitmap source)
	{
		Bitmap[][] maps = new Bitmap[rows][cols];

		int bHeight = source.getHeight() / rows;
		int bWidth = source.getWidth() / cols;
		
		int y = 0;
		
		for (int row = 0; row < rows; row++)
		{
			int x = 0;
			
			for (int col = 0; col < cols; col++)
			{
				maps[row][col] = Bitmap.createBitmap(source, x, y, bWidth, bHeight);
				
				x += bWidth; 
			}
			
			y += bHeight; 
		}
		
		return maps;
	}
	
	public static Bitmap[][] splitBitmap(int rows, int cols, int height, int width, Bitmap source)
	{
		Bitmap[][] maps = new Bitmap[rows][cols];

		int xOffset = (source.getWidth() - cols * width) / 2; 
		int yOffset = (source.getHeight() - rows * height) / 2; 
		
		int y = yOffset;
		
		for (int row = 0; row < rows; row++)
		{
			int x = xOffset;
			
			for (int col = 0; col < cols; col++)
			{
				maps[row][col] = Bitmap.createBitmap(source, x, y, width, height);
				
				x += width; 
			}
			
			y += height; 
		}
		
		return maps;
	}
	
	public static Bitmap createBitmapFromTile(Bitmap tileBitmap, int width, int height)
	{
		Bitmap createdBitmap = null;
		
		int tileHeight = tileBitmap.getHeight();
		int tileWidth = tileBitmap.getWidth();
		
		if (tileHeight > height)
		{
			tileHeight = height;
		}
		
		if (tileWidth > width)
		{
			tileWidth = width;
		}
		
		Canvas canvas = new Canvas();
		
		createdBitmap = Bitmap.createBitmap(width, height, tileBitmap.getConfig());

		canvas.setBitmap(createdBitmap);
		
		Rect srcRect = new Rect();
		RectF dstRect = new RectF();
		
		for (int x = 0; x < width;)
		{
			int copyWidth = (width - x) > tileWidth ? tileWidth : (width - x);
			
			for (int y = 0; y < height;)
			{
				int copyHeight = (height - y) > tileHeight ? tileHeight : (height - y);
				
				srcRect.set(0, 0, copyWidth, copyHeight);
				dstRect.set(x, y, x + copyWidth, y + copyHeight);
				
				canvas.drawBitmap(tileBitmap, srcRect, dstRect, null);
				
				y += copyHeight;
			}
			
			x += copyWidth;
		}
		
		return createdBitmap;
	}

	public static Bitmap extendBitmap(Bitmap sourceBitmap, int width, int height)
	{
		Bitmap createdBitmap = null;
		
		int sourceHeight = sourceBitmap.getHeight();
		int sourceWidth = sourceBitmap.getWidth();
		
		int targetHeight = sourceHeight;
		int targetWidth = sourceWidth;

		int extentWidth = 1;
		int extentHeight = 1;
		
		if (sourceHeight < height)
		{
			extentHeight = (int) Math.ceil(height / (float) sourceHeight);
			
			targetHeight = extentHeight * sourceHeight;
		}
		
		if (sourceWidth < width)
		{
			extentWidth = (int) Math.ceil(width / (float) sourceWidth);
			
			targetWidth = extentWidth * sourceWidth;
		}

		if ((extentWidth > 1) || (extentHeight > 1))
		{
			Canvas canvas = new Canvas();
			
			createdBitmap = Bitmap.createBitmap(targetWidth, targetHeight, sourceBitmap.getConfig());
	
			canvas.setBitmap(createdBitmap);
			
			Rect srcRect = new Rect(0, 0, sourceWidth, sourceHeight);
			RectF dstRect = new RectF();
			
			for (int w = 0; w < extentWidth; w++)
			{
				for (int h = 0; h < extentHeight; h++)
				{
					float x = w * sourceWidth;
					float y = h * sourceHeight;
					
					dstRect.set(x, y, x + sourceWidth, y + sourceHeight);
					
					canvas.drawBitmap(sourceBitmap, srcRect, dstRect, null);
				}
			}
		}
		
		return createdBitmap;
	}

	/*
	Input: A square tessellation, T, containing a connected component P of black cells. 
	Output: A sequence B (b1, b2 ,..., bk) of boundary pixels i.e. the contour. 
	Define M(a) to be the Moore neighborhood of pixel a. 

	Let p denote the current boundary pixel. 
	Let c denote the current pixel under consideration i.e. c is in M(p). 
	
	Begin 
	Set B to be empty. 
	From bottom to top and left to right scan the cells of T until a black pixel, s, of P is found. 
	Insert s in B. 
	Set the current boundary point p to s i.e. p=s 
	Backtrack i.e. move to the pixel from which s was entered. 
	Set c to be the next clockwise pixel in M(p). 
	While c not equal to s do 
	   If c is black 
	insert c in B 
	set p=c 
	backtrack (move the current pixel c to the pixel from which p was entered) 
	   else 
	advance the current pixel c to the next clockwise pixel in M(p) 
	end While
	End 
*/
	public static ArrayList<Rectangle> getImageObjectBounds(Bitmap image, IColorMatcher colorMatcher)
	{
		ArrayList<Rectangle> imageObjectBounds = new ArrayList<Rectangle>();
		 
		int width = image.getWidth();
		int height = image.getHeight();

		Point e = new Point(0, -1);
		Point s = new Point(-1, -1);
		Point p = new Point(-1, -1);
		Point c = new Point(-1, -1);
		
		for (int x = 0; x < width; x++)
		{
			e.x = x;
			e.y = -1;
			
			for (int y = 0; y < height; y++)
			{
				int color = image.getPixel(x, y);
				
				if (colorMatcher.match(color) && !isWithinImageObjectBounds(imageObjectBounds, x, y))
				{
					s.x = x;
					s.y = y;
					
					p.x = s.x;
					p.y = s.y;

					Rectangle objectBounds = new Rectangle(p.x, p.y, 0, 0);
					imageObjectBounds.add(objectBounds);

					do
					{
						c = MooreNeighbor(p, e, c);
						
						if ((c.x == e.x) && (c.y == e.y))
						{
							break;
						}
						else if ((c.x < 0) || (c.x >= width) || (c.y < 0) || (c.y >= height))
						{
							e.x = c.x;
							e.y = c.y;
						}
						else
						{
							color = image.getPixel((int) c.x, (int) c.y);
							
							if (colorMatcher.match(color))
							{
								p.x = c.x;
								p.y = c.y;
								
								objectBounds.union(p.x, p.y);
							}
							else
							{
								e.x = c.x;
								e.y = c.y;
							}
						}
					}
					while ((s.x != c.x) || (s.y != c.y));
				}
			
				e.y = y;
			}
		}
		 
		return imageObjectBounds;
	}
	
	private static Point MooreNeighbor(Point center, Point current, Point result)
	{
		int xIndex = (current.x + 1) - center.x;
		int yIndex = (current.y + 1) - center.y;
		 
		result.set(current.x, current.y); 

		result.offset(X_MOORE_NEIGHBOR[yIndex][xIndex], Y_MOORE_NEIGHBOR[yIndex][xIndex]);
		
		return result;
	}

	private static boolean isWithinImageObjectBounds(ArrayList<Rectangle> objectBounds, int x, int y)
	{
		boolean inside = false;
		
		for (int i = 0; i < objectBounds.size(); i++)
		{
			if (objectBounds.get(i).contains(x, y))
			{
				inside = true;
				
				break;
			}
			
		}
		
		return inside;
	}

	public interface IColorMatcher
	{
		public boolean match(int color);
	}
}
