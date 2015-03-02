package com.funnycorps.we.xuv500;



import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SongsManager {
    // SDCard Path
    File MEDIA_PATH ;//= new String("/sdcard/");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    // Constructor
    public SongsManager(){}
    public SongsManager(File S){
        MEDIA_PATH=S;
    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){

        File home;
        try {
            home = MEDIA_PATH;

        }catch(Exception e)
        {
            return songsList;
        }
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }

        Collections.sort(songsList, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> lhs,
                               HashMap<String, String> rhs) {
                // Do your comparison logic here and retrn accordingly.
                return lhs.get("songTitle").compareTo(rhs.get("songTitle"));
                // return 0;
            }
        });
        // return songs list array
        return songsList;
    }
    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }

}
