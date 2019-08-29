package ru.aakumykov.me.myimageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.telecom.Call;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class MyImageLoader {

    public interface Callbacks {
        void onImageLoadSuccess(Drawable imageDrawable);
        void onImageLoadError();
    }

    private static final String TAG = "MyImageLoader";
    private static ViewGroup.LayoutParams sLayoutParams;


    // вариант 1: только URL
    public static void loadImageToContainer(
            Context context,
            String imageURL,
            ViewGroup container
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                container,
                null,
                null,
                null,
                null
        );
    }

    // вариант 2: URL + заглушки
    public static void loadImageToContainer(
            Context context,
            String imageURL,
            ViewGroup container,
            int loadingPlaceholderId,
            int errorPlaceholderId
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                container,
                loadingPlaceholderId,
                errorPlaceholderId,
                null,
                null
        );
    }

    // вариант 3: URL + коллбеки
    public static void loadImageToContainer(
            Context context,
            String imageURL,
            ViewGroup container,
            Callbacks callbacks
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                container,
                null,
                null,
                null,
                callbacks
        );
    }

    // Базовый метод
    public static void loadImageToContainer(
            final Context context,
            final String imageURL,
            final ViewGroup container,
            @Nullable final Integer loadingPlaceholderId,
            @Nullable final Integer errorPlaceholderId,
            @Nullable final Boolean ignoreCache,
            @Nullable final Callbacks callbacks
    ) {
        if (null == sLayoutParams) {
            sLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        RequestBuilder<Drawable> requestBuilder =
                Glide
                .with(context)
                .load(imageURL);

        if (null != ignoreCache && ignoreCache)
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);

        requestBuilder
                .into(new CustomTarget<Drawable>()
                {
                    @Override public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        displayImage(context, resource, container, errorPlaceholderId);
                        if (null != callbacks)
                            callbacks.onImageLoadSuccess(resource);
                    }

                    @Override public void onLoadCleared(@Nullable Drawable placeholder) {
                        // TODO: для чего это?
                    }

                    @Override public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        showImageThrobber(context, container, loadingPlaceholderId);
                    }

                    @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        showImageError(context, container, errorPlaceholderId);
                        if (null != callbacks)
                            callbacks.onImageLoadError();
                    }
                });
    }


    // Вспомогательные методы
    private static void showImageThrobber(Context context, ViewGroup container, @Nullable Integer loadingPlaceholderId) {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(sLayoutParams);

        container.removeAllViews();
        container.addView(progressBar);
    }

    private static void showImageError(Context context, ViewGroup container, @Nullable Integer errorPlaceholderId) {
        int errorPlaceholder = (null == errorPlaceholderId) ? R.drawable.ic_broken_image : errorPlaceholderId;
        displayImage(context, errorPlaceholder, container, errorPlaceholderId);
    }

    private static <T> void displayImage(Context context, T image, ViewGroup container, @Nullable Integer errorPlaceholderId) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(sLayoutParams);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if (image instanceof Drawable) {
            imageView.setImageDrawable((Drawable)image);
        }
        else if (image instanceof Integer && (Integer)image > 0) {
            imageView.setImageResource((Integer)image);
        }
        else {
            Log.e(TAG, "Illegal type of image: "+image);
            showImageError(context, container, errorPlaceholderId);
            return;
        }

        container.removeAllViews();
        container.addView(imageView);
    }
}
