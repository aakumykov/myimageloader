package ru.aakumykov.me.myimageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class MyImageLoader {

    public static void loadImageToContainer(
            Context context,
            final ViewGroup imageContainer,
            String imageURL,
            int errorPlaceholderResourceId
    ) {
        // Проверяю аргументы, кроме imageURL.
        if (null == context)
            throw new IllegalArgumentException("There is no context supplied.");

        if (null == imageContainer)
            throw new IllegalArgumentException("There is no image container supplied.");

        if (errorPlaceholderResourceId <= 0) {
            throw new IllegalArgumentException("Illegal errorPlaceholder resourceId");
        }

        // Готовлю программный ImageView и errorDrawable
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        final ImageView imageView = new ImageView(context);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);

        final Drawable errorDrawable = context.getResources().getDrawable(errorPlaceholderResourceId);

        // Проверяю imageURL
        if (TextUtils.isEmpty(imageURL)) {
            dispalyImageResource(imageContainer, imageView, errorDrawable);
            return;
        }

        // Показываю крутилку ожидания изображения
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);
        imageContainer.addView(progressBar);
        Utils.show(imageContainer);

        // Загружаю картинку
        Glide.with(context)
                .load(imageURL)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        dispalyImageResource(imageContainer, imageView, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        dispalyImageResource(imageContainer, imageView, errorDrawable);
                    }
                });
    }

    private static void dispalyImageResource(ViewGroup imageContainer, ImageView imageView, Drawable imageResource) {
        imageView.setImageDrawable(imageResource);

//        int childCount = imageContainer.getChildCount();
//        if (childCount)
        imageContainer.removeAllViews();
//        imageContainer.removeViewAt(0);
        imageContainer.addView(imageView);
    }
}
