package com.videoplayer.videoplayer.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_info")
public class VideoInfo {
    @PrimaryKey
    @NonNull
    private String videoId;
    private long lastPositionMillis;
    private boolean isEnded;

    public VideoInfo(@NonNull String videoId, long lastPositionMillis, boolean isEnded) {
        this.videoId = videoId;
        this.lastPositionMillis = lastPositionMillis;
        this.isEnded = isEnded;
    }

    @NonNull String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public long getLastPositionMillis() {
        return lastPositionMillis;
    }

    public void setLastPositionMillis(long lastPositionMillis) {
        this.lastPositionMillis = lastPositionMillis;
    }

    public boolean getIsEnded() {
        return isEnded;
    }

    public void setIsEnded(boolean isEnded) {
        this.isEnded = isEnded;
    }
}
