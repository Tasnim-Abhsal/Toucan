package com.example.toucan;

import static com.example.toucan.MusicMediaPlayer.mediaPlayer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {
    public static List<MusicClass> audioUrls;
    private Context context;

    public AudioListAdapter(List<MusicClass> audioUrls,Context context) {
        this.audioUrls = audioUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicClass music = audioUrls.get(position);
        holder.bind(music);
    }

    @Override
    public int getItemCount() {
        return audioUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private ImageView musicImg;
        private LinearLayout musicItem;
        private ImageView playOnline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_file_name);
            musicImg = itemView.findViewById(R.id.music_img);
            musicItem = itemView.findViewById(R.id.musicItem);
            playOnline = itemView.findViewById(R.id.playOnline);
        }

        public void bind(MusicClass audioUrl) {
            titleTextView.setText(audioUrl.getName());
            Glide.with(context)
                    .load(audioUrl.getThumbUri())
                    .apply(new RequestOptions().placeholder(R.drawable.profile))
                    .into(musicImg);
            musicItem.setOnClickListener(view -> {
                playAudioFromUrl(audioUrl.getUri());
            });
            playOnline.setOnClickListener(view->{
                if(mediaPlayer.isPlaying()){
                    playOnline.setImageResource(R.drawable.baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else{
                    playOnline.setImageResource(R.drawable.baseline_pause_24);
                    mediaPlayer.start();
                }
            });
        }
        private void playAudioFromUrl(String audioUrlString) {
            if(mediaPlayer==null){
                mediaPlayer = new MediaPlayer();
            }
            try{
                mediaPlayer.setDataSource(audioUrlString);
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                });
                mediaPlayer.prepareAsync();
                playOnline.setImageResource(R.drawable.baseline_pause_24);
            } catch(Exception ignored){
            }
        }
    }
}