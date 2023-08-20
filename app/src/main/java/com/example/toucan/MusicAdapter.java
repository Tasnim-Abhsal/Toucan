package com.example.toucan;


import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_item,parent,false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder( MyVieHolder holder,int  position) {
        holder.file_name.setText(mFiles.get(holder.getAdapterPosition()).getTitle());
        byte[] image=getAlbumArt(mFiles.get(holder.getAdapterPosition()).getPath());
        if(image !=null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }
        else {
            Glide.with(mContext).load(R.drawable.album).into(holder.album_art);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,PlayerActivity.class);
                intent.putExtra("position",holder.getAdapterPosition());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount()  {
        return mFiles.size();
    }


    public class MyVieHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        ImageView album_art;
        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);
            album_art=itemView.findViewById(R.id.music_img);
        }
    }
    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art= retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;

    }
    void updateList(ArrayList<MusicFiles>musicFilesArrayList)
    {
        mFiles=new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
