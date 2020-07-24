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
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MediaStoreAdapter extends RecyclerView.Adapter<MediaStoreAdapter.ViewHolder> implements MediaViewersContract.Adapter {

    private Cursor mMediaStoreCursor;
    private final Activity mActivity;
    private OnClickThumbListener mOnClickThumbListener;
    private Boolean isSelected = true;
    private Boolean isImageSelected = false;
    private List<String> images = new ArrayList<>();
    MediaViewersContract.Presenter presenter;
    private HashSet<String> selectedImages = new HashSet<>();

    public interface OnClickThumbListener {

        void OnClickImage(Uri imageUri);

    }
    public MediaStoreAdapter(Activity activity, MediaViewersContract.Presenter presenter) {
        this.mActivity = activity;
        this.mOnClickThumbListener = (OnClickThumbListener) activity;
        this.presenter = presenter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImageView;

        private final TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.mediaStoreImageView);
            mTextView = itemView.findViewById(R.id.vh_image_selected_indicator);
            mImageView.setOnClickListener(this);


        }


        public ImageView getImageView() {
            return mImageView;
        }

        @Override
        public void onClick(View view) {
            onMultiSelectionClick(mTextView,getAdapterPosition());
            sentUriToPresenter(getAdapterPosition());
            presenter.getBitmap(getBitmapFromMediaStore(getAdapterPosition()));
            getOnClickUri(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_image_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Bitmap bitmap = getBitmapFromMediaStore(position);
//        if (bitmap !=  null) {
//            holder.getImageView().setImageBitmap(bitmap);
//        }
//        if(isImageSelected){
//            onMultiSelectionClick(holder.mTextView,position);
//            isImageSelected = false;
//        }
            Glide.with(mActivity)
                    .load(getUriFromMediaStore(position))
                    .centerCrop()
//                .override(117, 104)
                    .into(holder.getImageView());
    }

    @Override
    public void clearImageList() {
        selectedImages.clear();
    }

    @Override
    public void multiSelectionClicked() {
        isImageSelected = true;
    }

    @Override
    public int getItemCount() {
        return (mMediaStoreCursor == null) ? 0 : mMediaStoreCursor.getCount();
    }

    private Cursor swapCursor(Cursor cursor) {
        if (mMediaStoreCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mMediaStoreCursor;
        this.mMediaStoreCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    private Bitmap getBitmapFromMediaStore(int position) {
        int idIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        mMediaStoreCursor.moveToPosition(position);
        switch (mMediaStoreCursor.getInt(mediaTypeIndex)) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return MediaStore.Images.Thumbnails.getThumbnail(
                        mActivity.getContentResolver(),
                        mMediaStoreCursor.getLong(idIndex),
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        null
                );
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return MediaStore.Video.Thumbnails.getThumbnail(
                        mActivity.getContentResolver(),
                        mMediaStoreCursor.getLong(idIndex),
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        null
                );

            default:
                return null;
        }
    }

    private Uri getUriFromMediaStore(int position) {
        //нужно получить все изображения из текущей папки

        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get path of our image

        mMediaStoreCursor.moveToPosition(position);

        String dataString = mMediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://" + dataString);
        Log.i("Test", mediaUri + "");
        return mediaUri;
    }

    private void getOnClickUri(int position) {
        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);//get type of our file, IMAGE or VIDEO
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get path of our image / location

        mMediaStoreCursor.moveToPosition(position);

        switch (mMediaStoreCursor.getInt(mediaTypeIndex)) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                String dataString = mMediaStoreCursor.getString(dataIndex);
                Uri imageUri = Uri.parse("file://" + dataString);
                mOnClickThumbListener.OnClickImage(imageUri);
                break;
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                break;
            default:
        }
    }

    private void sentUriToPresenter(int position) {
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get path of our image / location

        mMediaStoreCursor.moveToPosition(position);

        String imageDataString = mMediaStoreCursor.getString(dataIndex);
        Uri imageUri = Uri.parse("file://" + imageDataString);
        presenter.getUri(imageUri);
    }

    private void onMultiSelectionClick(TextView textView, int position){

        if (presenter.MultiSelectionIsClicked()) {

            textView.setVisibility(View.VISIBLE);
            selectedImages.add(returnImageUri(position));
            if (isSelected) {
                textView.setBackgroundResource(R.drawable.ic_image_selected);
                textView.setText(" "+selectedImages.size());
                Log.i("Test",""+selectedImages.size());
                isSelected = false;
            } else {
                selectedImages.remove(returnImageUri(position));
                textView.setBackgroundResource(R.drawable.ic_online);
                textView.setText(" ");
                textView.setVisibility(View.GONE);
                isSelected = true;
            }
        }
    }

    private String returnImageUri(int position) {
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get path of our image / location

        mMediaStoreCursor.moveToPosition(position);

        String imageDataString = mMediaStoreCursor.getString(dataIndex);
        Uri imageUri = Uri.parse("file://" + imageDataString);
        return imageUri.toString();
    }

}



