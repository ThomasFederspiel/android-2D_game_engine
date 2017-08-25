package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IUIWindow;
import se.federspiel.android.game.interfaces.IUserInputManager;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnLongClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnTouchListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchZOrder;
import se.federspiel.android.game.ui.UIPanel.PanelLayout;

public abstract class UIAbstractWindow extends UIInputComponent implements IUIWindow
{
	protected Point mPosition = Point.Zero.clone();
	
	private boolean mEnabled = true;
	
	protected TouchZOrder mTouchLayer = TouchZOrder.UI_MAIN_WINDOW;	
	
	protected UIPanel mPanel = null;

	public UIAbstractWindow(float x, float y, int width, int height)
	{
		mPosition.set(x, y);
		
		mPanel = new UIPanel(width, height);
		
		mPanel.setUIWindow(this);
		mPanel.setPosition(x, y);
	}
	
	@Override
	protected IBounds getComponentBounds()
	{
		return mPanel.getComponentBounds();
	}

	@Override
	protected UIAbstractWindow getWindow()
	{
		return this;
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
	
	public void addComponent(UIAbstractComponent component)
	{
		mPanel.addComponent(component);
	}

	public void removeComponent(UIAbstractComponent component)
	{
		mPanel.removeComponent(component);
	}

	public int nofComponents()
	{
		return mPanel.getNofComponents();
	}
	
	public Dimensions getPreferredDimensions()
	{
		return mPanel.mPreferredDimensions;
	}
	
	public Dimensions getDimensions()
	{
		return mPanel.getDimensions();
	}
	
	public void setPosition(float x, float y)
	{
		mPosition.set(x, y);
		
		mPanel.setPosition(x, y);
	}
	
	public void setDimensions(Dimensions dim)
	{
		mPanel.setDimensions(dim);
	}

	public void setPreferredDimensions(Dimensions dim)
	{
		mPanel.setPreferredDimensions(dim);
	}
	
	public void setBackgroundColor(int color)
	{
		mPanel.setBackgroundColor(color);
	}
	
	public void setTransparancy(int alpha)
	{
		mPanel.setTransparancy(alpha);
	}
	
	public void setLayout(PanelLayout layout)
	{
		mPanel.setLayout(layout);
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();
		
		mPanel.loadContent();
	}

	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		mPanel.unloadContent();
	}
	
	@Override
	public void draw(GameRenderer renderer)
	{
		if (mPanel.getNofComponents() > 0)
		{
			mPanel.draw(mPosition, renderer.getCanvas());
		}
	}
	
	void setOnClickListener(IOnClickListener listener, IBounds bounds)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.setBoundedOnClickListener(listener, bounds, mTouchLayer);
	}
	
	void setOnLongClickListener(IOnLongClickListener listener, IBounds bounds)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.setBoundedOnLongClickListener(listener, bounds, mTouchLayer);
	}

	void setOnTouchListener(IOnTouchListener listener, IBounds bounds)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.setBoundedOnTouchListener(listener, bounds, mTouchLayer);
	}

	void removeRegisterdOnClickListener(IOnClickListener listener)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.removeBoundedOnClickListener(listener);
	}
	
	void removeRegisterdOnLongClickListener(IOnLongClickListener listener)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.removeBoundedOnLongClickListener(listener);
	}

	void removeRegisterdOnTouchListener(IOnTouchListener listener)
	{
		IUserInputManager inputManager = GameApplication.getGameContext().getUserInputManager();
		
		inputManager.removeBoundedOnTouchListener(listener);
	}
}
