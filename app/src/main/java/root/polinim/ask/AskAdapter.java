package root.polinim.ask;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liefery.android.icon_spinner.ItemSpinnable;

public class AskAdapter implements ItemSpinnable {

    private String name;
    private String expression;
    private int color;
    private Drawable drawable;

    public AskAdapter(String name, String expression, int color, Drawable drawable )
    {
        this.name = name;
        this.expression = expression;
        this.color = color;
        this.drawable = drawable;
    }

    @Override
    @NonNull
    public String getTitle() {
        return name;
    }

    @Override
    @Nullable
    public String getSubtitle() {
        return expression;
    }

    @Override
    @NonNull
    public int getColor() {
        return color;
    }

    @Override
    @NonNull
    public Drawable getIconDrawable() {
        return drawable;
    }

}