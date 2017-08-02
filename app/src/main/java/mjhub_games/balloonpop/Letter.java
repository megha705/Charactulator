package mjhub_games.balloonpop;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by mmnet on 2017-08-01.
 */
public class Letter extends TextView {

    public Letter(Context context, String s, int x) {
        super(context);

        this.setText(s);
        this.setTextSize(50);
        this.setTextColor(Color.argb(255,255,255,255));

        int width = x;
        int height = x;

        int dpHeight = PixelHelper.pixelsToDp(height,context);
        int dpWidth = PixelHelper.pixelsToDp(width,context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth,dpHeight);

        setLayoutParams(params);
    }
}
