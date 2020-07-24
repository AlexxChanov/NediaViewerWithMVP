package com.example.nediaviewerwithmvp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MediaViewersContract.View, MediaStoreAdapter.OnClickThumbListener, AdapterView.OnItemSelectedListener {

    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;

    private RecyclerView mThumbnailRecyclerView;
    private MediaViewersContract.Presenter presenter;
    private ImageView mFullScreen;
    private Spinner spinner;
    private ConstraintLayout fullScreenContainer;
    private ImageView fullSizeBtn;
    private Button btnBack;
    private ImageView multiSelectionBtn;
    private Boolean isMultiSelectionBtnClicked = false;
    private Boolean isFullImageSizeBtnClicked = true;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MediaViewerPresenter(this);
        fullScreenContainer = findViewById(R.id.full_image_view_container);
        fullSizeBtn = findViewById(R.id.full_image_size_btn);
        spinner = findViewById(R.id.spinner);
        btnBack = findViewById(R.id.btn_back);
        multiSelectionBtn = findViewById(R.id.multi_selection_btn);
        mFullScreen = findViewById(R.id.full_image_view);
        mThumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView);
        scrollView = findViewById(R.id.scrollView);

        presenter.init();
       // onLoadImages();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreenContainer.setVisibility(View.GONE);
            }
        });

        fullSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFullImageSizeBtnClicked) {
                    presenter.onFullSizeBtnClick();
                    fullSizeBtn.setBackgroundResource(R.color.colorPrimaryDark);
                    isFullImageSizeBtnClicked = false;
                } else {
                    presenter.onImageClick();
                    fullSizeBtn.setBackgroundResource(R.color.colorTransparent);
                    isFullImageSizeBtnClicked = true;
                }
            }
        });

        multiSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mThumbnailRecyclerView.setAdapter(mMediaStoreAdapter);
                presenter.multiBtnClicked();
                presenter.clearImageList();

                if (isMultiSelectionBtnClicked) {
                    multiSelectionBtn.setBackgroundResource(R.color.colorTransparent);
                    isMultiSelectionBtnClicked = false;
                    presenter.isBtnMultiSelectionClicked(isMultiSelectionBtnClicked);
                } else {
                    multiSelectionBtn.setBackgroundResource(R.color.colorPrimaryDark);
                    isMultiSelectionBtnClicked = true;
                    presenter.isBtnMultiSelectionClicked(isMultiSelectionBtnClicked);
                }
            }
        });

        checkReadExternalStoragePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Call cursor loader
                    // Toast.makeText(this, "Now have access to view thumbs", Toast.LENGTH_SHORT).show();
                    getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);


            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App need to view thumbnails", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        return new CursorLoader(
                this,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        presenter.getAdapter().changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
       presenter.getAdapter().changeCursor(null);
    }

    @Override
    public void OnClickImage(Uri imageUri) {
        fullSizeBtn.setBackgroundResource(R.color.colorTransparent);
        isFullImageSizeBtnClicked = true;
        scrollView.scrollTo(0,100);
        presenter.onImageClick();
        fullScreenContainer.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Image uri = " + imageUri.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Boolean btnZoomImageIsClicked() {
        return null;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mThumbnailRecyclerView;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setFullScreenImage(Bitmap bitmap, Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(mFullScreen);
//                .override(300, 300)
//        if (bitmap != null && mFullScreen != null) {
//            mFullScreen.setVisibility(View.VISIBLE);
//            mFullScreen.setImageBitmap(bitmap);
//        }
//        mFullScreen.setVisibility(View.VISIBLE);
//        Toast.makeText(MainActivity.this,"Image uri = " + uri.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setFullImageSize(Bitmap bitmap, Uri uri) {

        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(mFullScreen);
    }

    @Override
    public void openComplainDialog(String targetId, String targetType) {

    }

    @Override
    public void showMessage(String text) {

    }

    @Override
    public void showMessage(int resString) {

    }
    private Map<String, List<String>> folderMap;
    private List<String> folders = new ArrayList<>();

    void onLoadImages() {
        folderMap = getPhotoFolders();

        for (String item : folderMap.keySet()) {
            if (item != null && !item.equals("null")) {
                folders.add(item);
            }
        }

        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, folders));
        spinner.setOnItemSelectedListener(this);


//        new LoaderImagesTask(presenter).execute();
    }

    public Map<String, List<String>> getPhotoFolders() {
        List<String> folderNames = new ArrayList<>();
        Map<String, List<String>> map = new LinkedHashMap<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Images.Media.DATE_ADDED;

        Cursor cursor = this.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                orderBy + " DESC"
        );

        if (cursor != null) {
            int indexColumnImage = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED);
            int indexBucketsNames = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
            int indexColumnImagePath = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                String folderId = cursor.getString(indexColumnImage);
                String folderName = cursor.getString(indexBucketsNames);
                String path = cursor.getString(indexColumnImagePath);

                if (folderName != null && !folderNames.contains(folderName) && !folderName.equals("null")) {
                    folderNames.add(folderName);
                }

                List<String> images = map.get(folderName);
                if (images == null) {
                    images = new ArrayList<>();
                }

                images.add(path);
                map.put(folderName, images);
            }
            cursor.close();
        }

        return map;
    }

    private void loadPhotoFromFolder() {
        List<String> photos = folderMap.get(spinner.getSelectedItem());
        presenter.loadImagesResponse(photos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        loadPhotoFromFolder();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}