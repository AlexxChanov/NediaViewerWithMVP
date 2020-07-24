package com.example.nediaviewerwithmvp;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter implements MediaViewersContract.Adapter {

    private MediaViewersContract.Presenter presenter;
    private final List<String> images;
    private HashSet<String> selectedImages = new HashSet<>();
    private Activity mActivity;
    private Boolean isImageSelected = false;
    private Cursor mMediaStoreCursor;
    private MediaStoreAdapter.OnClickThumbListener mOnClickThumbListener;
    private Boolean isSelected = true;


    public ImagesAdapter(Activity activity,MediaViewersContract.Presenter presenter, List<String> images) {
        this.presenter = presenter;
        this.images = images;
        this.mOnClickThumbListener = (MediaStoreAdapter.OnClickThumbListener) activity;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_image_view, parent, false);
        return new ImageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindImageHolder((ImageHolder) holder);
    }

    private void onBindImageHolder(ImageHolder holder) {
        String path = images.get(holder.getAdapterPosition());

        Glide.with(holder.itemView)
                .load(path)
                .into(holder.mImageView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void clearImageList() {

    }

    @Override
    public void multiSelectionClicked() {
        isImageSelected = true;
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImageView;

        private final TextView mTextView;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.mediaStoreImageView);
            mTextView = itemView.findViewById(R.id.vh_image_selected_indicator);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (presenter.MultiSelectionIsClicked()) {

                mTextView.setVisibility(View.VISIBLE);
                selectedImages.add(returnImageUri(getAdapterPosition()));
                if (isSelected) {
                    mTextView.setBackgroundResource(R.drawable.ic_image_selected);
                    mTextView.setText(" "+selectedImages.size());
                    Log.i("Test",""+selectedImages.size());
                    isSelected = false;
                } else {
                    selectedImages.remove(returnImageUri(getAdapterPosition()));
                    mTextView.setBackgroundResource(R.drawable.ic_online);
                    mTextView.setText(" ");
                    mTextView.setVisibility(View.GONE);
                    isSelected = true;
                }
            }
//            onMultiSelectionClick(mTextView, getAdapterPosition());
//            presenter.getBitmap(getBitmapFromMediaStore(getAdapterPosition()));
        }
    }
//
//    private Bitmap getBitmapFromMediaStore(int position) {
//        int idIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
//        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
//
//        mMediaStoreCursor.moveToPosition(position);
//        switch (mMediaStoreCursor.getInt(mediaTypeIndex)) {
//            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
//                return MediaStore.Images.Thumbnails.getThumbnail(
//                        mActivity.getContentResolver(),
//                        mMediaStoreCursor.getLong(idIndex),
//                        MediaStore.Images.Thumbnails.MICRO_KIND,
//                        null
//                );
//            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
//                return MediaStore.Video.Thumbnails.getThumbnail(
//                        mActivity.getContentResolver(),
//                        mMediaStoreCursor.getLong(idIndex),
//                        MediaStore.Video.Thumbnails.MICRO_KIND,
//                        null
//                );
//
//            default:
//                return null;
//
//        }
//    }
//    private Cursor swapCursor(Cursor cursor) {
//        if (mMediaStoreCursor == cursor) {
//            return null;
//        }
//        Cursor oldCursor = mMediaStoreCursor;
//        this.mMediaStoreCursor = cursor;
//        if (cursor != null) {
//            this.notifyDataSetChanged();
//        }
//        return oldCursor;
//    }
//
//    public void changeCursor(Cursor cursor) {
//        Cursor oldCursor = swapCursor(cursor);
//        if (oldCursor != null) {
//            oldCursor.close();
//        }
//    }
//    private void onMultiSelectionClick(TextView textView, int position){
//
//        if (presenter.MultiSelectionIsClicked()) {
//
//            textView.setVisibility(View.VISIBLE);
//            selectedImages.add(returnImageUri(position));
//            if (isSelected) {
//                textView.setBackgroundResource(R.drawable.ic_image_selected);
//                textView.setText(" "+selectedImages.size());
//                Log.i("Test",""+selectedImages.size());
//                isSelected = false;
//            } else {
//                selectedImages.remove(returnImageUri(position));
//                textView.setBackgroundResource(R.drawable.ic_online);
//                textView.setText(" ");
//                textView.setVisibility(View.GONE);
//                isSelected = true;
//            }
//        }
//    }
    private String returnImageUri(int position) {
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get path of our image / location

        mMediaStoreCursor.moveToPosition(position);

        String imageDataString = mMediaStoreCursor.getString(dataIndex);
        Uri imageUri = Uri.parse("file://" + imageDataString);
        return imageUri.toString();
    }
}
