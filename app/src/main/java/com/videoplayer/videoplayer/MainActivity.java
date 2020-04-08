package com.videoplayer.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.videoplayer.videoplayer.data.Retrofit.ApiInterface;
import com.videoplayer.videoplayer.data.Retrofit.ApiServiceGenerator;
import com.videoplayer.videoplayer.data.model.VideoResponseModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvVideoList;
    ViewDialog viewDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        viewDialog=new ViewDialog(this);
        TextView tvUserName = findViewById(R.id.tvUserName);
        rvVideoList = findViewById(R.id.rvVideoList);
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        RetrofitCall();
    }

    private void RetrofitCall() {
        viewDialog.showDialog();
        ApiInterface apiInterface = ApiServiceGenerator.createService().create(ApiInterface.class);
        Call<ArrayList<VideoResponseModel>> call = apiInterface.callVideoListApi();
        call.enqueue(new Callback<ArrayList<VideoResponseModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<VideoResponseModel>> call, @NonNull Response<ArrayList<VideoResponseModel>> response) {

                viewDialog.hideDialog();
                if (response.code() == 200 && response.body() != null) {
                    Log.i("request_url", "onResponse: " + response.raw().request().url());
                    String requestUrl = response.raw().request().url().toString();
                    Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
                    ArrayList<VideoResponseModel> videoResponseModels = response.body();
                   // videoResponseModelArrayList = videoResponseModels;
                    VideoHomeAdapter videoHomeAdapter = new VideoHomeAdapter(context, videoResponseModels);
                    rvVideoList.setAdapter(videoHomeAdapter);
                } else {
                    Toast.makeText(context , "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<VideoResponseModel>> call,@NonNull Throwable t) {
                viewDialog.hideDialog();
                Toast.makeText(context , "Something went wrong. please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
