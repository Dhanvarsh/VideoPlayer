package com.videoplayer.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.videoplayer.videoplayer.data.model.VideoResponseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Callback;

public class VideoHomeAdapter extends RecyclerView.Adapter<VideoHomeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VideoResponseModel> arrVideoResponseModel;
    public static final String TRACK_LIST = "track_list";
    public static final String VIDEO_LIST_MASTER = "video_list_master";
    public static final String SELECTED_VIDEO_ID = "selected_video";



    public VideoHomeAdapter(Context context, ArrayList<VideoResponseModel> videoResponseModels) {
        this.mContext = context;
        this.arrVideoResponseModel = videoResponseModels;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_content_layout_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final VideoResponseModel videoResponseModel = arrVideoResponseModel.get(i);

        viewHolder.tvTitle.setText(videoResponseModel.getTitle());
        viewHolder.tvDesc.setText(videoResponseModel.getDescription());
        Picasso.get().load(videoResponseModel.getThumb()).into(viewHolder.ivVideoThump);
       // viewHolder.cvVideo.setOnClickListener(view -> homePresenter.itemClicked(i, videoResponseModel));
        viewHolder.cvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.swap(arrVideoResponseModel, 0, i);

                HashMap<String, Integer> trackAndPositionMap = new HashMap<>();

                for (int trackPosition = 0; trackPosition <arrVideoResponseModel.size(); trackPosition++) {
                    trackAndPositionMap.put(arrVideoResponseModel.get(trackPosition).getId(), trackPosition);
                }
                Intent intent = new Intent(mContext, PlayerActivity.class);

                intent.putExtra(SELECTED_VIDEO_ID, videoResponseModel.getId());
                intent.putExtra(TRACK_LIST, trackAndPositionMap);
                intent.putExtra(VIDEO_LIST_MASTER, arrVideoResponseModel);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrVideoResponseModel.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivVideoThump;
        private TextView tvTitle, tvDesc;
        private CardView cvVideo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVideoThump = itemView.findViewById(R.id.ivVideoThump);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            cvVideo = itemView.findViewById(R.id.cvVideo);
        }
    }
}

