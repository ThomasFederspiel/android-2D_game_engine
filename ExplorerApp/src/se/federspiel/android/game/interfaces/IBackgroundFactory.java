package se.federspiel.android.game.interfaces;

public interface IBackgroundFactory extends IDestroyable
{
    void registerBackground(String id, Class<? extends IBackground> background);

    IBackground createBackground(String id);
}
