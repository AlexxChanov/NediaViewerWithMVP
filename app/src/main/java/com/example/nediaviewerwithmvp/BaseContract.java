package com.example.nediaviewerwithmvp;

public interface BaseContract {
    interface View {
        void openComplainDialog(String targetId, String targetType);

        void showMessage(String text);

        void showMessage(int resString);
    }

    interface Presenter<V extends View> {
        void attachView(V view);

        void viewIsReady();

        void detachView();

        void onError(int code, String errorMsg);
    }

    interface Model<P extends Presenter> {

    }
}