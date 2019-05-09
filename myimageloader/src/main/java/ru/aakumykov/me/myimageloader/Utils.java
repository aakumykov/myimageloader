package ru.aakumykov.me.myimageloader;

import android.view.View;

class Utils {
    private Utils(){}

    static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    static void hide(View view) {
        view.setVisibility(View.GONE);
    }

}
