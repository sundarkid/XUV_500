package com.funnycorps.we.xuv500;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class FragmentMusic extends Fragment implements MediaPlayer.OnCompletionListener,SeekBar.OnSeekBarChangeListener{

    int f;
    Context context;
    ImageView repeat,shuffle,play,next,previous,source;
    TextView songTitleLabel;
    SeekBar songProgressBar;
    Utilities utils;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    SongsManager songManager;
    LinearLayout linearLayoutMusicControls;
    Handler mHandler = new Handler();
    MediaPlayer mp;
    int currentSongIndex=0;
    ArrayAdapter adapter;

    public FragmentMusic() {
        // Required empty public constructor
    }
    ListView currentPlayList;
    List currentTracks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        context = getActivity().getApplicationContext();
        repeat = (ImageView) view.findViewById(R.id.imageViewRepeat);
        shuffle = (ImageView) view.findViewById(R.id.imageViewRandom);
        play = (ImageView) view.findViewById(R.id.imageViewPlay);
        next = (ImageView) view.findViewById(R.id.imageViewNext);
        previous = (ImageView) view.findViewById(R.id.imageViewPrevious);
        source = (ImageView) view.findViewById(R.id.imageViewSelectSource);
        linearLayoutMusicControls = (LinearLayout) view.findViewById(R.id.linearLayoutMusicControls);

        songTitleLabel = (TextView) view.findViewById(R.id.textViewSongName);
        context = getActivity().getApplicationContext();
   /*     currentTracks = new ArrayList();

        currentPlayList = (ListView) view.findViewById(R.id.listViewCurrentPlayList);
        ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1,currentTracks){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view1 = super.getView(position, convertView, parent);
                TextView textView = (TextView) view1.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view1;
            }
        };
        currentPlayList.setAdapter(adapter);*/

        mp = new MediaPlayer();
        utils = new Utilities();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            songManager = new SongsManager(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        else
            songManager = new SongsManager(Environment.getDataDirectory());
        songsList = songManager.getPlayList();
        if (songsList == null || songsList.size() == 0) { f=0;
        }
        else
        { f=1;
            String[] names = new String[songsList.size()];
            for (int i = 0; i < songsList.size(); i++) {
                names[i] = songsList.get(i).get("songTitle");
            }
            currentPlayList = (ListView) view.findViewById(R.id.listViewCurrentPlayList);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view1 = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view1.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    return view1;
                }
            };

            currentPlayList.setAdapter(adapter);
            currentPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    playSong(position);
                    // Passing the selected music from the list.
                }
            });}



        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (songsList == null || songsList.size() == 0) { f=0;
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    // Setting Dialog Title
                    alertDialog.setTitle("Songs Unavailable in sdcard/");
                    // Setting Dialog Message
                    alertDialog.setMessage("Add songs to that directory" +
                            ".\nsdcard/Music");
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }

                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        play.setImageResource(R.drawable.image_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        mHandler.post(mUpdateTimeTask);
                        // Changing button image to pause button
                        play.setImageResource(R.drawable.image_pause);
                    }
                }

            }
        });

        // Selecting Source Dialog
    /*    source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View SrcDialog;
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                SrcDialog = layoutInflater.inflate(R.layout.music_source,null);
                RadioGroup radioGroup = (RadioGroup)SrcDialog.findViewById(R.id.radioGroupSrcSelect);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i){
                            case R.id.radioButtonCD:
                                Toast.makeText(getActivity(),"Cd selected",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.radioButtonUSB:
                                Toast.makeText(getActivity(),"USB Selected",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.radioButtonIpod:
                                Toast.makeText(getActivity(),"Ipod Selected",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.radioButtonBTMusic:
                                Toast.makeText(getActivity(),"BT Music selected",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.radioButtonAux:
                                Toast.makeText(getActivity(),"Aux selected",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                alert.setView(SrcDialog);
                alert.show();
            }
        });
        */


        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(!isShuffle) {
                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
                else {
                    int idxx=(int)Math.floor(Math.random()*(songsList.size()-1));
                    playSong(idxx);
                }
            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!isShuffle) {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        // play last song
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }
                }
                else
                {
                    int idxx=(int)Math.floor(Math.random()*(songsList.size()-1));
                    playSong(idxx);
                }
            }
        });

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    //Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.image_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    //Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    //repeat.setImageResource(R.drawable.btn_repeat_focused);
                    // btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    // Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    shuffle.setImageResource(R.drawable.image_random);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    //Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat=false;
                    //btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    //btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        songProgressBar = new SeekBar(context);
        linearLayoutMusicControls.addView(songProgressBar);
        songProgressBar.setOnSeekBarChangeListener(this);
        mHandler.post(mUpdateTimeTask);
        super.onResume();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        linearLayoutMusicControls.removeView(songProgressBar);
        mp.stop();
        super.onPause();
    }

    public void  playSong(int songIndex){
        // Play song
        if (f==1){
            try {
                mp.reset();
                mp.setDataSource(songsList.get(songIndex).get("songPath"));
                mp.prepare();
                mp.start();
                // Displaying Song title
                String songTitle = songsList.get(songIndex).get("songTitle");
                songTitleLabel.setText(songTitle);
                // totalduration.setText(mp.getDuration());
                // Changing Button Image to pause image
                play.setImageResource(R.drawable.image_pause);
                // set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                // Updating progress bar
                updateProgressBar();
            }  catch (IllegalArgumentException e) {
                e.printStackTrace();}
            catch (IllegalStateException e) {
                e.printStackTrace();}
            catch (IOException e) {
                e.printStackTrace();
            }}
    }

    public void updateArticleView(int position) {
        playSong(position);

    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    @Override

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        }
        else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }
        else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }
    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    public void stop()
    {
        mp.stop();
    }
}
