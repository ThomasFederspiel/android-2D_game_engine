package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchZOrder;
import se.federspiel.android.game.ui.UIPanel.PanelLayout;

public class UIDialog extends UIAbstractWindow
{
	public enum DialogAlignement
	{
		DIALOG_CENTERED,
		DIALOG_ABSOLUTE,
		DIALOG_FILL
	}

	private DialogAlignement mAlignment = DialogAlignement.DIALOG_CENTERED;
	
	public UIDialog()
	{
		this(-1, -1);
	}
	
	public UIDialog(int width, int height)
	{
		this(0, 0, width, height);
	}

	public UIDialog(int x, int y, int width, int height)
	{
		super(x, y, width, height);

		mTouchLayer = TouchZOrder.UI_DIALOG;
		
		mPanel.setLayout(PanelLayout.LAYOUT_LINEAR_Y);
		mPanel.setTransparancy(UIPanel.OPACQUE);
	}
	
	public void show()
	{
		GameApplication.getMainWindow().showDialog(this);
	}
	
	public void close()
	{
		removeAllListeners();
		
		hide();
	}
	
	public DialogAlignement getAlignment()
	{
		return mAlignment;
	}
	
	public void setAlignment(DialogAlignement alignment)
	{
		mAlignment = alignment;
	}
	
	public void setBackgroundBitmap(int resourceId)
	{
		mPanel.setBackgroundBitmap(resourceId);
	}
	
	private void hide()
	{
		GameApplication.getMainWindow().hideDialog(this);
	}
}
