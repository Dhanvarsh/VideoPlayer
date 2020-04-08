package com.videoplayer.videoplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.videoplayer.videoplayer.data.model.VideoResponseModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<VideoResponseModel> arrVideoResponseModel;



    public RecentListAdapter(Context mContext, ArrayList<VideoResponseModel> arrVideoResponseModel
            ) {
        this.mContext = mContext;
        this.arrVideoResponseModel = arrVideoResponseModel;

    }

    @NonNull
    @Override
    public RecentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.related_video_content, viewGroup, false);
        return new RecentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentListAdapter.ViewHolder viewHolder, final int i) {
        final VideoResponseModel videoResponseModel = arrVideoResponseModel.get(i);

        viewHolder.tvTitle.setText(videoResponseModel.getTitle());
        viewHolder.tvDesc.setText(videoResponseModel.getDescription());
        Picasso.get().load(videoResponseModel.getThumb()).into(viewHolder.ivThumb);

        viewHolder.cvRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder: " + i);
                ((PlayerActivity)mContext).onItemSelected(videoResponseModel.getId());

            }
        });
    }





    @Override
    public int getItemCount() {
        return arrVideoResponseModel.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumb;
        private TextView tvTitle, tvDesc;
        private CardView cvRecent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivVideoThump);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            cvRecent = itemView.findViewById(R.id.cvRecent);
        }
    }
}
