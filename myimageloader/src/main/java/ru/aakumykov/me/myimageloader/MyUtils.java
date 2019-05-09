package ru.aakumykov.me.myimageloader;

import android.view.View;

public class MyUtils {
    private MyUtils(){}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

}
