package se.federspiel.android.game.interfaces;

public interface ISpriteFactory extends IDestroyable
{
    void registerSprite(String id, Class<? extends ISprite> sprite);
    ISprite createSprite(String id);
}
