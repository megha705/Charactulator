package mjhub_games.balloonpop;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by mmnet on 2017-07-27.
 */
public class SoundHelper {

    MediaPlayer musicPlayer;
    MediaPlayer popPlayer;
    boolean mute = false;

    public SoundHelper(Context context){
        prepareMusicPlayer(context);
    }

    void prepareMusicPlayer(Context context){
        musicPlayer = MediaPlayer.create(context.getApplicationContext(),R.raw.pleasant_music);
        popPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.balloon_pop);
        popPlayer.setVolume(.5f,.5f);
        popPlayer.setLooping(false);
        musicPlayer.setVolume(0.5f,0.5f);
        musicPlayer.setLooping(true);
    }

    void pop(){
        popPlayer.start();
    }

    void playMusic(){
        if(musicPlayer != null && !mute){
            musicPlayer.start();
        }
    }

    void pauseMusic(){
        if(musicPlayer != null){
            musicPlayer.pause();
        }
    }


}
