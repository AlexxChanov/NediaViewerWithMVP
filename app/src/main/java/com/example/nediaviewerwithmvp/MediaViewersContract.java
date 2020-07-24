package com.example.nediaviewerwithmvp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public interface MediaViewersContract {

    interface View extends BaseContract.View {

        Boolean btnZoomImageIsClicked();
        RecyclerView getRecyclerView();
        Activity getActivity();
        void setFullScreenImage(Bitmap bitmap, Uri uri);
        void setFullImageSize(Bitmap bitmap, Uri uri);
    }

    interface Presenter extends BaseContract.Presenter {
        void multiBtnClicked();
        void clearImageList();
        void init();
        void isBtnMultiSelectionClicked(Boolean flag);
        Boolean MultiSelectionIsClicked();
        void onImageClick();
        void getUri(Uri uri);
        void getBitmap(Bitmap bitmap);
        MediaStoreAdapter getAdapter();
        void onFullSizeBtnClick();
        void loadImagesResponse(List<String> images);
    }

    interface Model extends BaseContract.Model {

    }

    interface Adapter {
        void clearImageList();
        void multiSelectionClicked();

    }

}
