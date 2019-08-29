package ru.aakumykov.me.myimageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

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
            ImageView targetImageView
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                targetImageView,
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
            ImageView targetImageView,
            int loadingPlaceholderId,
            int errorPlaceholderId
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                targetImageView,
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
            ImageView targetImageView,
            Callbacks callbacks
    ) {
        MyImageLoader.loadImageToContainer(
                context,
                imageURL,
                targetImageView,
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
            final ImageView targetImageView,
            @Nullable final Integer loadingPlaceholderId,
            @Nullable final Integer errorPlaceholderId,
            @Nullable final Boolean ignoreCache,
            @Nullable final Callbacks callbacks
    ) {
        if (null == sLayoutParams) {
            sLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
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
                        displayImage(context, resource, targetImageView, errorPlaceholderId);

                        if (null != callbacks)
                            callbacks.onImageLoadSuccess(resource);
                    }

                    @Override public void onLoadCleared(@Nullable Drawable placeholder) {
                        // TODO: для чего это?
                    }

                    @Override public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        showImageLoading(context, targetImageView, loadingPlaceholderId);
                    }

                    @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        showImageError(context, targetImageView, errorPlaceholderId);

                        if (null != callbacks)
                            callbacks.onImageLoadError();
                    }
                });
    }

    // Отмена загрузки
    public static void cancelLoading(final Context context, final ImageView imageView) {
        Glide.with(context).clear(imageView);
    }


    // Вспомогательные методы
    private static <T> void displayImage(Context context, T image, ImageView targetImageView, @Nullable Integer errorPlaceholderId) {

        if (image instanceof Drawable) {
            targetImageView.setImageDrawable((Drawable)image);
        }
        else if (image instanceof Integer && (Integer)image > 0) {
            targetImageView.setImageResource((Integer)image);
        }
        else {
            Log.e(TAG, "Illegal type of image: "+image);
            showImageError(context, targetImageView, errorPlaceholderId);
        }
    }

    private static void showImageLoading(Context context, ImageView targetImageView, @Nullable Integer loadingPlaceholderId) {
        Drawable drawable = context.getResources().getDrawable(
                (null != loadingPlaceholderId) ? loadingPlaceholderId : R.drawable.ic_broken_image
        );
        targetImageView.setImageDrawable(drawable);
    }

    private static void showImageError(Context context, ImageView targetImageView, @Nullable Integer errorPlaceholderId) {
        Drawable errorPlaceholder = context.getResources().getDrawable(
                (null == errorPlaceholderId) ? R.drawable.ic_broken_image : errorPlaceholderId
        );

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(sLayoutParams);
        imageView.setImageDrawable(errorPlaceholder);

        targetImageView.setImageDrawable(errorPlaceholder);
    }
}
