package mjhub_games.balloonpop;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {


    //To player audio
    SoundHelper soundHelper;


    //Let us make sure the ballons are not in the same position
    Random rand = new Random();
    ArrayList<Integer> pos = new ArrayList<Integer>();
    String[] letters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","0","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private boolean perfect = false;


    private int[] mBalloonColors = new int[3];
    private int mNextColor, mScreenWidth, mScreenHeight = 0;

    public static final int MIN_ANIMATION_DELAY = 500;
    public static final int MAX_ANIMATION_DELAY = 1500;
    public static final int MIN_ANIMATION_DURATION = 1000;
    public static final int MAX_ANIMATION_DURATION = 8000;
    private int mLevel = 0;
    private int mScore = 0;
    private List<Balloon> mBalloons = new ArrayList<Balloon>();

    private boolean isPlaying = false;
    private int balloonsRemoved = 0;
    private int lives = 100;


    TextView mScoreDisplay, mLevelDisplay, mHighScore;
    Button playButton;
    TextView levelText;
    TextView livesDisplay;
    String levelTextString = "ABC";

    private ViewGroup mMainScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundHelper = new SoundHelper(this);

        mBalloonColors[0] = Color.argb(255,255,0,0);
        mBalloonColors[1] = Color.argb(255,0,255,0);
        mBalloonColors[2] = Color.argb(255,0,0,255);

        mMainScreen = (ViewGroup)findViewById(R.id.main_screen);
        setFullScreen();

        ViewTreeObserver viewTreeObserver = mMainScreen.getViewTreeObserver();
        if(viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mMainScreen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenHeight = mMainScreen.getHeight();
                    mScreenWidth = mMainScreen.getWidth();
                }
            });
        }

        mScoreDisplay = (TextView) findViewById(R.id.score_value);
        mLevelDisplay = (TextView) findViewById(R.id.level_value);
        playButton = (Button) findViewById(R.id.go_button);
        mHighScore = (TextView) findViewById(R.id.high_score_value);
        levelText = (TextView) findViewById(R.id.currSeq);
        livesDisplay = (TextView)findViewById(R.id.lives_value);
        levelText.setText(levelTextString);

        updateDisplay();



    }

    @Override
    protected void onStart() {
        super.onStart();
        setFullScreen();
        updateDisplay();
        soundHelper.playMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setFullScreen();
        updateDisplay();
        soundHelper.playMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
        soundHelper.playMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundHelper.pauseMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundHelper.pauseMusic();
    }

    public void setFullScreen(){
        ViewGroup root = (ViewGroup) findViewById(R.id.main_screen);
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public void startLevel(View v){



        if(!isPlaying){
            mLevel = 1;
            mScore = 0;
            levelTextString = "ABC";
            lives = 100;
            isPlaying = true;
            balloonsRemoved = 0;
            BalloonLauncher launcher = new BalloonLauncher();
            launcher.execute(mLevel);
            updateDisplay();
            playButton.setText("PLAYING....");
        }

    }

    public void startAnotherLevel(){
        mLevel += 1;
        if(levelTextString.length() < 6){
            levelTextString = generateRandomString(levelTextString.length()+1);
        }else{
            levelTextString = generateRandomString(3);
        }

        isPlaying = true;
        BalloonLauncher launcher = new BalloonLauncher();
        launcher.execute(mLevel);
        updateDisplay();
        playButton.setText("PLAYING....");
    }

    private String generateRandomString(int r){
        String output = "";
        for(int y = 0; y < r; y++){
            output += letters[rand.nextInt(100) % 26];
        }
        return output.toUpperCase();
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch, String s) {



        if(userTouch){

            if(levelTextString.toLowerCase().contains(s.toLowerCase())){
                Toast.makeText(this,s, Toast.LENGTH_LONG).show();
               levelTextString =  levelTextString.replace(s.charAt(0),"-".charAt(0));
                mMainScreen.removeView(balloon.letter);
                mMainScreen.removeView(balloon);
                balloon.setPopped(true);

                mBalloons.remove(balloon);


                soundHelper.pop();


               perfect = true;
                for(int yu = 0; yu < levelTextString.length(); yu++){
                    if(levelTextString.charAt(yu) != '-'){
                        perfect = false;
                    }
                }
                if(perfect){
                    lives += 10;
                    Toast.makeText(this,"PERFECT!",Toast.LENGTH_LONG).show();
                }




                balloonsRemoved++;
                mScore++;
            }else{
           //     Toast.makeText(this,"Does not contain that..", Toast.LENGTH_LONG).show();
            }


        }else{

            mMainScreen.removeView(balloon.letter);
            mMainScreen.removeView(balloon);
             balloon.setPopped(true);
            mBalloons.remove(balloon);


            soundHelper.pop();



                if(isPlaying){
                    lives -= 10;
                    Toast.makeText(this,"Missed that one!", Toast.LENGTH_LONG).show();
                }


        }

        if(mBalloons.size() == 0 && lives > 0){
            startAnotherLevel();
        }

        if(lives <= 0){
            gameOver(true);
        }


        updateDisplay();
    }

    private boolean gameOver(boolean b){
        if(mScore > getHighScore()){
            saveCurrHighScore(mScore);
        }
        updateDisplay();
        isPlaying = false;
        playButton.setText("PLAY");
        Toast.makeText(this,"Game Over!",Toast.LENGTH_LONG).show();
        if(!isPlaying){
            for(int y = 0; y < mBalloons.size(); y++){
                mMainScreen.removeView(mBalloons.get(y));
                mMainScreen.removeView(mBalloons.get(y).letter);
                mBalloons.get(y).setPopped(true);
                mBalloons.get(y).setPopped(true);
            }

        }
        mBalloons.clear();

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateDisplay(){
        //update the display


        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {Color.argb(255,10*mLevel % 255,15*mLevel % 255,20*mLevel % 255),Color.argb(255,20*mLevel % 255,30*mLevel % 255,40*mLevel % 255)});
        gd.setCornerRadius(0f);
        mMainScreen.setBackground(gd);

        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
        mHighScore.setText(String.valueOf(getHighScore()));
        levelText.setText(levelTextString);
        livesDisplay.setText(String.valueOf(lives));
    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {



        @Override

        protected Void doInBackground(Integer... params) {



            if (params.length != 1) {

                throw new AssertionError(

                        "Expected 1 param for current level");
            }



            int level = params[0];

            int maxDelay = Math.max(MIN_ANIMATION_DELAY,

                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));

            int minDelay = maxDelay / 2;



            int balloonsLaunched = 1;

            while (isPlaying && balloonsLaunched <= levelTextString.length()) {



//              Get a random horizontal position for the next balloon

                Random random = new Random(new Date().getTime());

                int xPosition = random.nextInt(mScreenWidth - 200);

                publishProgress(xPosition);

                balloonsLaunched++;



//              Wait a random number of milliseconds before looping

                int delay = random.nextInt(minDelay) + minDelay;

                try {

                    Thread.sleep(delay);

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }

            }



            return null;



        }



        @Override

        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            int xPosition = values[0];

            launchBalloon(xPosition);

        }



    }



    private void launchBalloon(int x) {


        Balloon balloon = new Balloon(this, 150, Color.argb(255,rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));


        x = (int)(mBalloons.size() * 300);

        if(isPlaying){
            mBalloons.add(balloon);
        }





        if (mNextColor + 1 == mBalloonColors.length) {

            mNextColor = 0;

        } else {

            mNextColor++;

        }



//      Set balloon vertical position and dimensions, add to container





        balloon.setX(x);

        balloon.setY(mScreenHeight + balloon.getHeight());

        /*
        Make sure the letters do not match
         */
        while(levelTextString.contains(balloon.letter.getText())){
            balloon.currLetter += 1;
            balloon.letter.setText(balloon.letters[balloon.currLetter]);
        }

        balloon.letter.setX(x + 55);
        balloon.letter.setY(mScreenHeight + balloon.getHeight());

        if(isPlaying){
            mMainScreen.addView(balloon);
            mMainScreen.addView(balloon.letter);
        }




//      Let 'er fly

        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel*100));

        balloon.releaseBalloon(mScreenHeight, duration+10000);


    }

    void saveCurrHighScore(int score){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("high_score",score);
        editor.commit();
    }


    int getHighScore(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains("high_score")){
            int highScore = sharedPref.getInt("high_score", 0);
            return highScore;
        }else{
            return 0;
        }
    }








}
