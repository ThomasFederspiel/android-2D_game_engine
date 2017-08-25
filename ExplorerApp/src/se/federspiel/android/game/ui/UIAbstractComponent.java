package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IUIComponent;

public abstract class UIAbstractComponent extends UIInputComponent implements IUIComponent
{
	
	protected UIAbstractComponent mParent = null;
	protected UIAbstractWindow mParentWindow = null;
	
	protected Dimensions mPreferredDimensions = Dimensions.Zero.clone();

	protected Point mParentOffset = Point.Zero.clone();
	
	protected Rectangle mBounds = Rectangle.Zero.clone();

	private UILayoutProperties mLayoutProperties = new UILayoutProperties(UILayoutProperties.CENTER_X | UILayoutProperties.CENTER_Y);
	
	private boolean mInvalidated = false;

	private boolean mEnabled = true;
	
	public UIAbstractComponent(int width, int height)
	{
		super();
		
		setupDimensions(width, height);
	}
	
	public UIAbstractComponent(float x, float y, int width, int height)
	{
		super();
		
		mParentOffset.set(x, y);
		mBounds.setPosition(x, y);
		
		setupDimensions(width, height);
	}
	
	@Override
	public Dimensions getDimensions()
	{
		return mBounds.getDimensions();
	}

	@Override
	public void setDimensions(int width, int height)
	{
		mBounds.setDimensions(width, height);
	}

	public void setDimensions(Dimensions dim)
	{
		mBounds.setDimensions(dim);
	}
	
	public void setPreferredDimensions(Dimensions dim)
	{
		mPreferredDimensions.set(dim);
	}

	@Override
	public Point getPosition()
	{
		return mBounds.getPosition();
	}

	@Override
	public void setPosition(Point position)
	{
		mBounds.setPosition(position);
		mBounds.getPosition().addToThis(mParentOffset);
	}
	
	@Override
	public void setPosition(float x, float y)
	{
		mBounds.setPosition(x, y);
		mBounds.getPosition().addToThis(mParentOffset);
	}
	
	@Override
	public void setEnable(boolean enable)
	{
		mEnabled = enable;
	}
	
	@Override
	public boolean isEnabled()
	{
		return mEnabled;
	}
	
	@Override
	public Point getParentOffset()
	{
		return mParentOffset;
	}
	
	@Override
	public void setParentOffset(float x, float y)
	{
		mBounds.getPosition().subtractFromThis(mParentOffset);
		mParentOffset.set(x, y);
		mBounds.getPosition().addToThis(mParentOffset);
	}
	
	public void setParentOffset(Point position)
	{
		mBounds.getPosition().subtractFromThis(mParentOffset);
		mParentOffset.set(position);
		mBounds.getPosition().addToThis(mParentOffset);
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();
	}
	
	@Override
	public void unloadContent()
	{
		super.unloadContent();
	}

	public UILayoutProperties getLayoutProperties()
	{
		return mLayoutProperties;
	}
	
	@Override
	protected IBounds getComponentBounds()
	{
		return mBounds;
	}

	@Override
	protected UIAbstractWindow getWindow()
	{
		return mParentWindow;
	}

	protected void setParent(UIAbstractComponent parent)
	{
		mParent = parent;
	}
	
	protected boolean isInvalidated()
	{
		return mInvalidated;
	}
	
	protected void setInvalidate(boolean value)
	{
		mInvalidated = value;
		
		if (mParent != null)
		{
			mParent.setInvalidate(true);
		}
	}
	
	void setUIWindow(UIAbstractWindow window)
	{
		mParentWindow = window;
	}
	
	void update(GameTime gameTime)
	{
	}
	
	private void setupDimensions(int width, int height)
	{
		mPreferredDimensions.setDimensions(width, height);
		
		mBounds.setDimensions(width, height);
	}
}
