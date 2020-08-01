package unc.live.d42n81.assignment3;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int MIN_SWIPPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    BrowseImageActivity browseImageActivity;

    public GestureListener(BrowseImageActivity b) {
        this.browseImageActivity = b;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (e1.getX() - e2.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
        {
//            Toast.makeText(Context, "You have swipped left side", Toast.LENGTH_SHORT).show();
            /* Code that you want to do on swiping left side*/
            //  Next entry

            browseImageActivity.increaseArrayListIndex();
            return true;
        }
        else if (e2.getX() - e1.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
        {
//            Toast.makeText(getApplicationContext(), "You have swipped right side", Toast.LENGTH_SHORT).show();
            /* Code that you want to do on swiping right side*/

            browseImageActivity.decreaseArrayListIndex();
            return true;
        }
        return false;
    }
}