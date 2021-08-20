package com.example.tetris;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tetris.views.CustomView;
import com.example.tetris.views.NextView;
import com.example.tetris.views.SavedView;

import java.util.ArrayList;

public class Game_Activity extends AppCompatActivity implements View.OnClickListener{
    private CustomView mCustomView;
    private NextView nNextView;
    private SavedView savedView;
    private GestureDetectorCompat mDetector;

    public MediaPlayer musicPlayer, sfx0, sfx1,sfx2,sfx3,sfx4;
    public ArrayList<MediaPlayer> sfxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_);

        /*Custom View declarations*/
        mCustomView = findViewById(R.id.customView);
        nNextView = findViewById(R.id.NextView);
        savedView = findViewById(R.id.SavedView);
        nNextView.mCustomView = mCustomView;
        savedView.mCustomView = mCustomView;

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        new Thread(){
            @Override
            public void run(){
                while (true) {
                    try {
                        mCustomView.dropDown();
                        if(mCustomView.lines_cleared==4){
                            if(sfxList.size()!=0){ //if sfx is on
                                sfxList.get(3).start();
                                mCustomView.lines_cleared = 0;
                            }
                        }else if(mCustomView.lines_cleared>0){
                            if(sfxList.size()!=0){ //if sfx is on
                                sfxList.get(2).start();
                                mCustomView.lines_cleared = 0;
                            }
                        }
                        Thread.sleep(mCustomView.time);//SMALLER FOR HIGHER DIFFICULTY

                        TextChange(); //changing the score

                    } catch (InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        /*begins the game with the audio on*/
        Sound();
        musicPlayer.start();
        musicPlayer.setLooping(true);

        /*button declarations*/
        ImageButton right_button = findViewById(R.id.right_button);
        right_button.setOnClickListener(this); //calling onClick() method;
        ImageButton left_button = findViewById(R.id.left_button);
        left_button.setOnClickListener(this);
        ImageButton down_button = findViewById(R.id.down_button);
        down_button.setOnClickListener(this);
        ImageButton rotate_button = findViewById(R.id.rotate);
        rotate_button.setOnClickListener(this);
        Button Hold_Button = findViewById(R.id.Hold_Button);
        Hold_Button.setOnClickListener(this);
        Switch music_switch = findViewById(R.id.music_switch);
        music_switch.setOnClickListener(this);
        Switch sfx_switch = findViewById(R.id.sfx_switch);
        sfx_switch.setOnClickListener(this);

    }

    public void Sound(){
        //initializes the music/audio
        musicPlayer = MediaPlayer.create(Game_Activity.this, R.raw.tetris_main_theme);
        sfx0 = MediaPlayer.create(Game_Activity.this, R.raw.jojo_tbc);
        sfx1 = MediaPlayer.create(Game_Activity.this, R.raw.hard_drop);
        sfx2 = MediaPlayer.create(Game_Activity.this, R.raw.line_clear);
        sfx3 = MediaPlayer.create(Game_Activity.this, R.raw.tetris_clear);
        sfx4 = MediaPlayer.create(Game_Activity.this, R.raw.rotate_switch);
        sfxList.add(sfx0); //end game sfx
        sfxList.add(sfx1); //hard drop sfx
        sfxList.add(sfx2); //regular line clear
        sfxList.add(sfx3); //tetris clear sfx
        sfxList.add(sfx4); //rotate sfx
    }
    private void stopPlaying(){
        //stops the music
        if(musicPlayer!=null){
            musicPlayer.stop();
            musicPlayer.reset();
            musicPlayer.release();
            musicPlayer = null;
        }
    }
    @Override
    public void onClick(View v){
        //handling onClick events
        //this is where all the buttons will be tapped, from here call tetris_main to do the right intent
        switch (v.getId()){
            case R.id.right_button:
                mCustomView.move(+1);
                break;
            case R.id.left_button:
                mCustomView.move(-1);
                break;
            case R.id.down_button:
                mCustomView.dropDown();
                mCustomView.score += 1;
                break;
            case R.id.rotate:
                mCustomView.rotate(-1);
                if(sfxList.size()!=0){ //if sfx is on
                    sfxList.get(4).start();
                }
                break;
            case R.id.Hold_Button:
                mCustomView.swapPiece();
                break;
            case R.id.music_switch:
                Switch music_switch = findViewById(R.id.music_switch);
                AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
                if (music_switch.isChecked()) { //switch is on
                    if (!manager.isMusicActive()) { //if music is not active
                        Sound();
                        musicPlayer.start();
                        musicPlayer.setLooping(true);
                    }
                } else { //switch is off
                    stopPlaying();
                }
                break;
            case R.id.sfx_switch:
                Switch sfx_switch = findViewById(R.id.sfx_switch);
                if (sfx_switch.isChecked()) {
                    if (sfxList.size() == 0) {
                        sfxList.add(sfx0); //end game sfx
                        sfxList.add(sfx1); //hard drop sfx
                        sfxList.add(sfx2); //regular line clear
                        sfxList.add(sfx3); //tetris clear sfx
                        sfxList.add(sfx4); //rotate sfx
                    }
                } else {
                    if (sfxList.size() != 0) {
                        sfxList.removeAll(sfxList);
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent event){
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            // Get swipe delta value in x axis.
            float deltaX = event1.getX() - event2.getX();
            // Get swipe delta value in y axis.
            float deltaY = event1.getY() - event2.getY();
            // Get absolute value.
            float deltaXAbs = Math.abs(deltaX);
            float deltaYAbs = Math.abs(deltaY);

            // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
            if((deltaXAbs >= 200) && (deltaXAbs <= 2000))
            {
                if(deltaX > 0) //left swipe
                {
                    mCustomView.move(-1);
                }else //right swipe
                {
                    mCustomView.move(+1);
                }
            }

            if((deltaYAbs >= 350) && (deltaYAbs <= 2000))
            {
                if(deltaY > 0)//up swipe
                {
                    mCustomView.rotate(-1);
                }else //down swipe
                {
                    mCustomView.hardDrop();
                    if(sfxList.size()!=0){ //if sfx is on
                        sfxList.get(1).start();
                    }
                }
            }
            return true;
        }
    }

    public void TextChange(){ //changes the text in the game
        //Score text
        TextView score_value = findViewById(R.id.score_text);
        String string_score = Long.toString(mCustomView.score);
        score_value.setText(string_score);

        //level text
        TextView level_value = findViewById(R.id.level_text);
        String string_level = Long.toString(mCustomView.level);
        level_value.setText(getResources().getString(R.string.Level_Status, string_level));

        //line text
        TextView line_value = findViewById(R.id.line_text);
        String string_line = Long.toString(mCustomView.line);
        line_value.setText(getResources().getString(R.string.Lines_Cleared, string_line));
    }
}
