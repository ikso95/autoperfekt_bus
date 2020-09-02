package com.autoperfekt.autoperfekt_bus.Steps.PhotoStep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autoperfekt.autoperfekt_bus.R;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<String> storageFilesPathsList;
    private Context context;
    private PhotoStep photoStep;


    public GalleryAdapter(List<String> storageFilesPathsList, Context context, PhotoStep photoStep) {
        this.storageFilesPathsList = storageFilesPathsList;
        this.context = context;
        this.photoStep=photoStep;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        //final int THUMBSIZE = 64;
        //Bitmap myBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(storageFilesPathsList.get(position)),THUMBSIZE, THUMBSIZE);
        Log.d("photos", "onBindViewHolder: " + storageFilesPathsList.get(position));
        Bitmap myBitmap = BitmapFactory.decodeFile(storageFilesPathsList.get(position));

        Matrix matrix = new Matrix();           //obrocenie zdjecia o 90 stopni
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 864, 486, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


        ((ViewHolder) holder).imageView.setImageBitmap(rotatedBitmap);


        ((ViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("delete", String.valueOf(storageFilesPathsList.get(position)));

                //usuniecie pliku z pamieci telefonu
                File file  = new File(storageFilesPathsList.get(position));
                boolean deleted  = file.delete();

                //usuniecie sciezki pliku z listy
                storageFilesPathsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, storageFilesPathsList.size());
                //jezeli nie ma na liscie zadnego pliku ukryj recyclerview
                if(storageFilesPathsList.size()==0)
                {
                    photoStep.recyclerView.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return storageFilesPathsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_galleryItem);
            button = itemView.findViewById(R.id.delete_imageButton_GalleryItem);

        }
    }

}
