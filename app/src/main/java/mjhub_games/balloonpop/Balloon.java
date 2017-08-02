package mjhub_games.balloonpop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by mmnet on 2017-08-01.
 */
public class Balloon extends ImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mPopped;



    Letter letter;
    Random rand = new Random();
    String[] letters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","0","P","Q","R","S","T","U","V","W","X","Y","Z"};
    int h;
    int currLetter;

    public Balloon(Context context, int height, int color) {
        super(context);


        mListener  = (BalloonListener) context;

        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        int width = height/2;
        h = height;

        int dpHeight = PixelHelper.pixelsToDp(height,context);
        int dpWidth = PixelHelper.pixelsToDp(width,context);
        currLetter = rand.nextInt(23);

        letter = new Letter(context,letters[currLetter],height);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth,dpHeight);

       setLayoutParams(params);
    }

    /*
   Let us create the animation logic
    */
    public void releaseBalloon(int screenHeight, int duration){
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }




    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!mPopped){
            mListener.popBalloon(this, false, "");
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setY((Float) animation.getAnimatedValue());
        letter.setY((Float) animation.getAnimatedValue());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mPopped && event.getAction() == MotionEvent.ACTION_DOWN){
           currLetter++;
            letter.setText(letters[currLetter %= 26]);
            mListener.popBalloon(this, true, this.letter.getText().toString());
        }
        return super.onTouchEvent(event);
    }

    public interface BalloonListener{
        void popBalloon(Balloon balloon, boolean userTouch, String s);
    }


    public void setPopped(boolean b){
        if(b){
            mPopped = true;
            mAnimator.cancel();
        }
    }
}
