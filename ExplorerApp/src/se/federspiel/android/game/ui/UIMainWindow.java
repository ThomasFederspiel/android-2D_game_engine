package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IDrawableComponent;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.ui.UIPanel.PanelLayout;
import se.federspiel.android.util.ALog;

public class UIMainWindow extends UIAbstractWindow implements IDrawableComponent
{
	private UIDialog mDialog = null;

	private DialogTouchShield mTouchShield = new DialogTouchShield();
	
	private DrawableZOrder mZOrder = DrawableZOrder.UI_LAYER;

	public UIMainWindow(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		mPanel.setLayout(PanelLayout.LAYOUT_XY);
		mPanel.setTransparancy(UIPanel.TRANSPARENT);
	}
	
	public void showDialog(UIDialog dialog)
	{
		ALog.debug(this, "show");
		
		if (mDialog != null)
		{
			hideDialog(mDialog);
		}
		
		if (mPanel.isResourceLoadEnabled())
		{
			dialog.loadContent();
		}
	
		alignDialog(dialog);
		
		mTouchShield.activate();
		
		mDialog = dialog;
	}
	
	public void hideDialog(UIDialog dialog)
	{
		assert mDialog == dialog;
		
		mDialog.unloadContent();

		mTouchShield.deactivate();
		
		mDialog = null;
	}

	@Override
	public DrawableZOrder getZOrder()
	{
		return mZOrder;
	}
	
	@Override
	public void setZOrder(DrawableZOrder level)
	{
		mZOrder = level;
	}
	
	@Override
	public IBounds getBounds()
	{
		return getComponentBounds();
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();
		
		if (mDialog != null)
		{
			mDialog.loadContent();
		}
	}

	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		if (mDialog != null)
		{
			mDialog.unloadContent();
		}
	}

	@Override
	public void update(GameTime gameTime)
	{
		mPanel.update(gameTime);
	}

	@Override
	public void draw(GameRenderer renderer)
	{
		super.draw(renderer);
		
		if (mDialog != null)
		{
			mDialog.draw(renderer);
		}
	}

	private void alignDialog(UIDialog dialog)
	{
		switch (dialog.getAlignment())
		{
			case DIALOG_CENTERED :
				centerDialog(dialog);
				break;

			case DIALOG_ABSOLUTE :
				// nothing to change
				break;
				
			case DIALOG_FILL :
				fillDialog(dialog);
				break;
				
			default :
				assert false;
		}
	}
	
	private void centerDialog(UIDialog dialog)
	{
		Dimensions winDim = getDimensions();
		Dimensions dlgDim = dialog.getPreferredDimensions();
		
		float x = mPosition.X + (winDim.getWidth() - dlgDim.getWidth()) / 2;
		float y = mPosition.Y + (winDim.getHeight() - dlgDim.getHeight()) / 2;
		
		dialog.setPosition(x, y);
	}
	
	private void fillDialog(UIDialog dialog)
	{
		dialog.setPosition(0, 0);
		dialog.setPreferredDimensions(getDimensions());
		dialog.setDimensions(getDimensions());
	}
	
	private class DialogTouchShield implements UIIOnTouchListener
	{
		private boolean mIsActive = false;
		
		public void activate()
		{
			if (!mIsActive)
			{
				setOnTouchListener(this);
				
				mIsActive = true;
			}
		}
		
		public void deactivate()
		{
			if (mIsActive)
			{
				removeOnTouchListener(this);
				
				mIsActive = false;
			}
		}

		@Override
		public boolean onTouch(UIInputComponent component, TouchEvent event)
		{
			return true;
		}
	}
}
