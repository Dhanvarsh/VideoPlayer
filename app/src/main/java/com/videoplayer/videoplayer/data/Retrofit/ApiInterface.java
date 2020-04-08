package com.videoplayer.videoplayer.data.Retrofit;

import com.videoplayer.videoplayer.data.model.VideoResponseModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("media.json?print=pretty")
    Call<ArrayList<VideoResponseModel>> callVideoListApi();
}
