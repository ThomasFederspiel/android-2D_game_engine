package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.interfaces.ISprite;

public class SpriteNonAdjustAction extends AbstractSpriteAction
{
	@Override
	public boolean isYielding(ISprite sprite)
	{
		return false;
	}
}
