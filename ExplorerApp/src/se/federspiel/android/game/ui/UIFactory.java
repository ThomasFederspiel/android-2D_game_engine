package se.federspiel.android.game.ui;

public class UIFactory
{
	private static UITapDialog sTapDialog = null;
	
	
	public static UITapDialog getTapDialog()
	{
		if (sTapDialog == null)
		{
			sTapDialog = new UITapDialog();
		}
		
		return sTapDialog;
	}

	public static class UITapDialog extends UIDialog
	{
		private UILabel mInfoLabel = new UILabel();
		private UILabel mTapLabel = new UILabel();

		public UITapDialog()
		{
			init();
		}

		public void setInfoText(int resourceId)
		{
			mInfoLabel.setText(resourceId);
		}
		
		public void setTapText(int resourceId)
		{
			mTapLabel.setText(resourceId);
		}

		private void init()
		{
			mInfoLabel.getLayoutProperties().set(UILayoutProperties.CENTER_X | UILayoutProperties.CENTER_Y);
			mTapLabel.getLayoutProperties().set(UILayoutProperties.CENTER_X | UILayoutProperties.TOP);
			
			addComponent(mInfoLabel);
			addComponent(mTapLabel);
		}
	}
}
