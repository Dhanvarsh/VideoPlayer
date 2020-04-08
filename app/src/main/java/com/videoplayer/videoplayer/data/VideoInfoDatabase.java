package com.videoplayer.videoplayer.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {VideoInfo.class}, version = 1)

public abstract class VideoInfoDatabase extends RoomDatabase {

    private static VideoInfoDatabase INSTANCE;

    public abstract VideoInfoDAO videoInfoModel();

    public static VideoInfoDatabase getVideoInfoDatabase(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(mContext.getApplicationContext(), VideoInfoDatabase.class, "video_info")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}
