package com.videoplayer.videoplayer.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
@Dao
public interface VideoInfoDAO {

    @Query("SELECT * FROM video_info ORDER BY videoId ASC")
    List<VideoInfo> getAllVideoInfo();

    @Query("SELECT * FROM video_info WHERE videoId=:video_id")
    VideoInfo getVideoInfo(String video_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertVideoInfo(VideoInfo videoInfo);

    @Update
    int UpdateVideo(VideoInfo videoInfo);

    @Delete
    void deleteVideoInfo(VideoInfo videoInfo);

    @Query("DELETE FROM video_info")
    void deleteAll();
}
