package se.federspiel.android.game.ui;

import java.util.ArrayList;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Margins;
import se.federspiel.android.game.geometry.Point;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class UIPanel extends UIAbstractComponent
{
	public enum PanelLayout
	{
		LAYOUT_XY,
		LAYOUT_LINEAR_X,
		LAYOUT_LINEAR_Y
	}
	
	public static final int TRANSPARENT = 0;
	public static final int OPACQUE = 255;
	
	private static final int DEFAULT_MARGIN = 10;
	private static final int SEPARATION_MARGIN = 10;
	
	private Margins mBorderMargin = Margins.Zero.clone();
	
	private boolean mTransparent = false;
	private Paint mBackground = null;

	private int mBackgroundResourceId = -1;
	private Bitmap mBackgroundBitmap = null;

	private Rect mImageDrawRect = new Rect();
	
	private PanelLayout mLayout = PanelLayout.LAYOUT_XY;
	
	private boolean mIsActive = false;
	
	private Point mTmpDrawPosition = Point.Zero.clone();
	
	private ArrayList<UIAbstractComponent> mComponents = new ArrayList<UIAbstractComponent>();
	
	public UIPanel(int width, int height)
	{
		super(width, height);

		init();
	}
	
	public UIPanel(int x, int y, int width, int height)
	{
		super(x, y, width, height);

		init();
	}

	public void addComponent(UIAbstractComponent component)
	{
		component.setParent(this);
		component.setUIWindow(mParentWindow);
	
		component.setPosition(mBounds.getPosition());
		
		component.setInvalidate(true);
		
		mComponents.add(component);
		
		if (mIsActive)
		{
			component.loadContent();
		}
	}

	public void removeComponent(UIAbstractComponent component)
	{
		mComponents.remove(component);
		
		component.setInvalidate(true);
		
		if (mIsActive)
		{
			component.unloadContent();
		}
	}
	
	public int getNofComponents()
	{
		return mComponents.size();
	}
	
	public void setLayout(PanelLayout layout)
	{
		mLayout = layout;
	}
	
	public void setBackgroundColor(int color)
	{
		mBackground.setColor(color);
	}

	public void setBackgroundBitmap(int resourceId)
	{
		mBackgroundResourceId = resourceId;
		
		loadBackgroundBitmap();
	}

	public void setTransparancy(int alpha)
	{
		mTransparent = (alpha <= TRANSPARENT);
		
		mBackground.setAlpha(alpha);
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();

		for (int i = 0; i < mComponents.size(); i++)
		{
			mComponents.get(i).loadContent();
		}
		
		loadBackgroundBitmap();
		
		mIsActive = true;
	}
	
	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		for (int i = 0; i < mComponents.size(); i++)
		{
			mComponents.get(i).unloadContent();
		}

		unloadBackgroundBitmap();
		
		mIsActive = false;
	}
	
	@Override
	public void draw(Point parentPosition, Canvas canvas)
	{
		if (isInvalidated())
		{
			doLayout();
		}

		mTmpDrawPosition.set(parentPosition);
		mTmpDrawPosition.addToThis(mParentOffset);

		if (mBackgroundBitmap != null)
		{
			mImageDrawRect.set((int) mTmpDrawPosition.X, (int) mTmpDrawPosition.Y, (int) (mTmpDrawPosition.X + mBounds.getWidth()), (int) (mTmpDrawPosition.Y + mBounds.getHeight()));
			
			canvas.drawBitmap(mBackgroundBitmap, null, mImageDrawRect, null);
		}
		else
		{
			if (!mTransparent)
			{
				canvas.drawRect(mTmpDrawPosition.X, mTmpDrawPosition.Y, mTmpDrawPosition.X + mBounds.getWidth(), mTmpDrawPosition.Y + mBounds.getHeight(), mBackground);
			}
		}
		
		for (int i = 0; i < mComponents.size(); i++)
		{
			mComponents.get(i).draw(mTmpDrawPosition, canvas);
		}
	}

	protected boolean isResourceLoadEnabled()
	{
		return mIsActive;
	}
	
	void update(GameTime gameTime)
	{
		for (int i = 0; i < mComponents.size(); i++)
		{
			mComponents.get(i).update(gameTime);
		}
	}
	
	private void loadBackgroundBitmap()
	{
		if (mBackgroundResourceId != -1)
		{
			if (mBackgroundBitmap == null)
			{
				if ((mPreferredDimensions.getWidth() == -1) || (mPreferredDimensions.getHeight() == -1))
				{
					mBackgroundBitmap = GameApplication.getGameContext().getImageManager().allocateBitmap(mBackgroundResourceId);
					
					mPreferredDimensions.setDimensions(mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
				}
				else
				{
					mBackgroundBitmap = GameApplication.getGameContext().getImageManager().allocateBitmap(mBackgroundResourceId, mPreferredDimensions.getWidth(), mPreferredDimensions.getHeight());
				}
				
				setInvalidate(true);
			}
		}
	}
	
	private void unloadBackgroundBitmap()
	{
		if (mBackgroundResourceId != -1)
		{
			if (mBackgroundBitmap != null)
			{
				if ((mPreferredDimensions.getWidth() == -1) || (mPreferredDimensions.getHeight() == -1))
				{
					GameApplication.getGameContext().getImageManager().deallocateBitmap(mBackgroundResourceId);
				}
				else
				{
					GameApplication.getGameContext().getImageManager().deallocateBitmap(mBackgroundResourceId, mPreferredDimensions.getWidth(),
		    				mPreferredDimensions.getHeight());
				}

				mBackgroundBitmap = null;
			}
		}
	}
	
	private void init()
	{
		mBackground = new Paint();
		mBackground.setColor(Color.LTGRAY);
		mBackground.setStyle(Style.FILL);
		mBackground.setStrokeWidth(1);

		mBorderMargin.setMargins(DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN);
	}
	
	private void doLayout()
	{
		mBounds.setDimensions(mPreferredDimensions);
		
		switch (mLayout)
		{
			case LAYOUT_XY :
				doLayoutXY();
				break;
				
			case LAYOUT_LINEAR_X :
				doLayoutLinearX();
				break;
				
			case LAYOUT_LINEAR_Y :
				doLayoutLinearY();
				break;
				
			default :
				assert false;
				break;
		}
	}
	
	private void doLayoutXY()
	{
		if (mComponents.size() > 0)
		{
			UIAbstractComponent component = null;
			
			for (int i = 0; i < mComponents.size(); i++)
			{
				component = mComponents.get(i);
				
//	;+			component.setParentOffset(component.getPosition());
				
				component.setInvalidate(false);
			}
		}
		
		setInvalidate(false);
	}
	
	private void doLayoutLinearX()
	{
		if (mComponents.size() > 0)
		{
			int width = 0;
			
			UIAbstractComponent component = null;
			
			for (int i = 0; i < mComponents.size(); i++)
			{
				width += mComponents.get(i).mPreferredDimensions.getWidth();
			}
			
			int remain = (mBounds.getWidth() 
					- mBorderMargin.getLeft() 
					- mBorderMargin.getRight() 
					- SEPARATION_MARGIN * (mComponents.size() - 1)
					- width);

			int xPos = mBorderMargin.getLeft();
			
			if (remain >= 0)
			{
				xPos += remain / 2;
				
				remain = 0;
			}
			else
			{
				remain /= mComponents.size();
			}
			
			int yMiddle = mBorderMargin.getTop() + (mBounds.getHeight() - mBorderMargin.getTop() - mBorderMargin.getBottom()) / 2;
			
			UILayoutProperties layoutProperties = null;
			
			for (int i = 0; i < mComponents.size(); i++)
			{
				component = mComponents.get(i);
				
				layoutProperties = component.getLayoutProperties();
				
				width = component.mPreferredDimensions.getWidth() + remain;
			
				component.setDimensions(width, component.mPreferredDimensions.getHeight());

				int yPos = 0;
				
				if (layoutProperties.isTop())
				{
					yPos = mBorderMargin.getTop();
				}
				else if (layoutProperties.isBottom())
				{
					yPos = mBounds.getHeight() - mBorderMargin.getBottom() - component.mPreferredDimensions.getHeight();
				}
				else if (layoutProperties.isCenterY())
				{
					yPos = yMiddle - component.mPreferredDimensions.getHeight() / 2;
				}
				
				component.setParentOffset(xPos, yPos);
				
				component.setInvalidate(false);
				
				xPos += (width + SEPARATION_MARGIN);
			}
		}
		
		setInvalidate(false);
	}
	
	private void doLayoutLinearY()
	{
		if (mComponents.size() > 0)
		{
			int height = 0;
			
			UIAbstractComponent component = null;
			
			for (int i = 0; i < mComponents.size(); i++)
			{
				height += mComponents.get(i).mPreferredDimensions.getHeight();
			}
			
			int remain = (mBounds.getHeight() 
					- mBorderMargin.getTop() 
					- mBorderMargin.getBottom() 
					- SEPARATION_MARGIN * (mComponents.size() - 1)
					- height);

			int heightCorr = 0;
			
			if (remain < 0)
			{
				heightCorr /= mComponents.size();
			}
			
			UILayoutProperties layoutProperties = null;
			
			int xMiddle = mBorderMargin.getLeft() + (mBounds.getWidth() - mBorderMargin.getLeft() - mBorderMargin.getRight()) / 2;
			
			int yPos = mBorderMargin.getTop();
			
			for (int i = 0; i < mComponents.size(); i++)
			{
				component = mComponents.get(i);

				layoutProperties = component.getLayoutProperties();
				
				height = component.mPreferredDimensions.getHeight() + heightCorr;
			
				component.setDimensions(component.mPreferredDimensions.getWidth(), height);

				int xPos = 0;
				
				if (layoutProperties.isLeft())
				{
					xPos = mBorderMargin.getLeft();
				}
				else if (layoutProperties.isRight())
				{
					xPos = mBounds.getWidth() - mBorderMargin.getRight() - component.mPreferredDimensions.getWidth();
				}
				else if (layoutProperties.isCenterX())
				{
					xPos = xMiddle - component.mPreferredDimensions.getWidth() / 2;
				}

				int nextYPos = 0;
				
				if (layoutProperties.isTop())
				{
			//		remain -= (height + SEPARATION_MARGIN);
					
					nextYPos = yPos + (height + SEPARATION_MARGIN);
				}
				else if (layoutProperties.isBottom())
				{
					yPos += remain;
					
					remain = 0;
					
					nextYPos = yPos + (height + SEPARATION_MARGIN);
				}
				else if (layoutProperties.isCenterY())
				{
					yPos += remain / 2;
					
					remain /= 2;
					
					nextYPos = yPos + (height + SEPARATION_MARGIN);
				}
				
				component.setParentOffset(xPos, yPos);

				component.setInvalidate(false);
				
				yPos = nextYPos;
			}
		}
		
		setInvalidate(false);
	}
}
