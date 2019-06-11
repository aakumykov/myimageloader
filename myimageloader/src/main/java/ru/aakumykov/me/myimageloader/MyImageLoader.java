package ru.aakumykov.me.myimageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class MyImageLoader {

    private static final String TAG = "MyImageLoader";
    private static ViewGroup.LayoutParams sLayoutParams;
    private static Integer sImageErrorResourceId;

    public static void loadImageToContainer(
            Context context,
            String imageURL,
            ViewGroup container
    ) {
        loadImageToContainer(context, container, imageURL, null);
    }

    public static void loadImageToContainer(
            final Context context,
            final ViewGroup container,
            String imageURL,
            @Nullable Integer imageErrorResourceId
    ) {
        if (null == sLayoutParams) {
            sLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        if (null != imageErrorResourceId)
            sImageErrorResourceId = imageErrorResourceId;

        Glide.with(context)
                .load(imageURL)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Drawable>()
                {
                    @Override public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        displayImage(context, resource, container);
                    }

                    @Override public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        showImageThrobber(context, container);
                    }

                    @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        showImageError(context, container);
                    }
                });
    }

    private static void showImageThrobber(Context context, ViewGroup container) {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(sLayoutParams);

        container.removeAllViews();
        container.addView(progressBar);
    }

    private static void showImageError(Context context, ViewGroup container) {
        int resourceId = (null == sImageErrorResourceId) ? R.drawable.ic_broken_image : sImageErrorResourceId;
        displayImage(context, resourceId, container);
    }

    private static <T> void displayImage(Context context, T image, ViewGroup container) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(sLayoutParams);
        imageView.setAdjustViewBounds(true);

        if (image instanceof Drawable) {
            imageView.setImageDrawable((Drawable)image);
        }
        else if (image instanceof Integer && (Integer)image > 0) {
            imageView.setImageResource((Integer)image);
        }
        else {
            Log.e(TAG, "Illegal type of image: "+image);
            showImageError(context, container);
            return;
        }

        container.removeAllViews();
        container.addView(imageView);
    }
}
