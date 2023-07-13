package com.example.finalmedia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    TextView title;
    TextView timestamp;
    ImageButton upload;
    ImageButton play;
    ImageButton stop;
    SeekBar seekbar1;
    Intent intent;
    Button info,us;
    Uri song;
    String duration;
    MediaPlayer mediaPlayer;
    Runnable seekBarProgressUpdater;
    Handler handler;
    public static final int PICK_FILE =99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload = findViewById(R.id.button1);
        play = findViewById(R.id.button2);
        stop = findViewById(R.id.button3);
        title = findViewById(R.id.textView2);
        timestamp = findViewById(R.id.textView3);
        seekbar1 = findViewById(R.id.seekbar1);
        info = findViewById(R.id.info);
        us=findViewById(R.id.us);
        Handler handler = new Handler();


        String about="We   are   from   6th   sem   AIML department, developed this app  Music Player.\n\nKaushik             1BY20AI024\n\nManish             1BY20AI027";
        String proj="The   project    is    developed    to   implement   the  functions  of  a  media  player.  The  application  fetches  the   songs   from    the  local storage  and  plays it.  When any  song  is  clicked  it  starts  to  play . The song can be played , paused , seekbar,stopped.\n" +
                "\n" +
                "For  the  main  page  where the all the interface to select the song and control it .\n" +
                "Two buttons which leads to another activity which contains the project details and student details are present\n" +
                "To  display the  image ImageView is used, to display title of the song, TextView is used, to display the controls ImageButtons are used and Seekbar is used.";


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, PICK_FILE);


            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        play.setImageResource(R.drawable.play);
                        handler.removeCallbacks(seekBarProgressUpdater);
                    } else {

                        mediaPlayer.start();
                        play.setImageResource(R.drawable.pause);

                        Runnable seekBarProgressUpdater = new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null) {
                                    if (!seekbar1.isPressed()) {
                                        seekbar1.setProgress(mediaPlayer.getCurrentPosition());
                                    }
                                }
                                handler.postDelayed(this, 0);
                            }
                        };


                        handler.postDelayed(seekBarProgressUpdater, 10);
                    }
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    releaseMediaPlayer();
                    play.setImageResource(R.drawable.play);
                    createMediaPlayer(song);
                    seekbar1.setProgress(0);
                }
            }
        });

        us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this, Displaying.class);
                intent.putExtra("head","About Us");
                intent.putExtra("body",about);
                startActivity(intent);

            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this, Displaying.class);
                intent.putExtra("head","About Project");
                intent.putExtra("body",proj);
                startActivity(intent);

            }

        });

        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null){
                    int millis = mediaPlayer.getCurrentPosition();
                    long total_secs = millis/1000;
                    long mins = total_secs / 60;
                    long secs = total_secs - (mins*60);
                    timestamp.setText(mins + ":" + secs + " / " + duration);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekbar1.getProgress());
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                createMediaPlayer(uri);
                song=uri;
            }
        }
    }

    public void createMediaPlayer(Uri uri){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();

            title.setText(getNameFromUri(uri));


            int millis = mediaPlayer.getDuration();
            long total_secs = millis/1000;
            long mins = total_secs / 60;
            long secs = total_secs - (mins*60);
            duration = mins + ":" + secs;
            timestamp.setText("00:00 / " + duration);
            seekbar1.setMax(millis);
            seekbar1.setProgress(0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                    play.setImageResource(R.drawable.play);
                    createMediaPlayer(song);
                    seekbar1.setProgress(0);
                }
            });
        } catch (IOException e){
            title.setText(e.toString());
        }
    }


    public String getNameFromUri(Uri uri){
        String fileName = "";
        DocumentFile documentFile = DocumentFile.fromSingleUri(getApplicationContext(), uri);
        if (documentFile != null) {
            fileName = documentFile.getName();
        }
        return fileName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    public void releaseMediaPlayer(){

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        title.setText("");
        timestamp.setText("00:00 / 00:00");
        seekbar1.setMax(100);
        seekbar1.setProgress(0);
}

}