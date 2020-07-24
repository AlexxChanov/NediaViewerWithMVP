package com.example.nediaviewerwithmvp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration;

import java.util.List;

public class MediaViewerPresenter implements MediaViewersContract.Presenter {

    private MediaViewersContract.Model model;
    private MediaViewersContract.Adapter adapter;
    private MediaViewersContract.View view;
    private MediaViewersContract.Adapter imagesAdapter;
    private Boolean isMultiSelectionBtnClicked = false;
    Uri uri;
    Bitmap bitmap;

    public MediaViewerPresenter(MediaViewersContract.View view) {
        this.view = view;
        model = new MediaStoreModel();
        attachView(view);
    }

    @Override
    public void multiBtnClicked() {
        view.getRecyclerView().setAdapter((RecyclerView.Adapter) adapter);
    }

    @Override
    public void clearImageList() {
        adapter.clearImageList();
    }

    public void init() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getActivity(), 3);
        view.getRecyclerView().setLayoutManager(gridLayoutManager);
        adapter = new MediaStoreAdapter(view.getActivity(), this);
        this.view.getRecyclerView().setAdapter((RecyclerView.Adapter) adapter);
        int spacingInPixels = 4;
        view.getRecyclerView().addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    @Override
    public void isBtnMultiSelectionClicked(Boolean flag) {
        isMultiSelectionBtnClicked = flag;
    }

    @Override
    public Boolean MultiSelectionIsClicked() {
        return isMultiSelectionBtnClicked;
    }


    @Override
    public void onImageClick() {
        adapter.multiSelectionClicked();
//        imagesAdapter.multiSelectionClicked();
        view.setFullScreenImage(bitmap, uri);
    }

    @Override
    public void getUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void getBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public MediaStoreAdapter getAdapter() {

        return (MediaStoreAdapter) adapter;
    }

    @Override
    public void onFullSizeBtnClick() {
        view.setFullImageSize(bitmap,uri);
    }

    @Override
    public void loadImagesResponse(List<String> images) {


        imagesAdapter = new ImagesAdapter(view.getActivity(),this,images);
        this.view.getRecyclerView().setAdapter((RecyclerView.Adapter) imagesAdapter);
        ((RecyclerView.Adapter) imagesAdapter).notifyDataSetChanged();
    }

    @Override
    public void attachView(BaseContract.View view) {


    }

    @Override
    public void viewIsReady() {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onError(int code, String errorMsg) {

    }
}
