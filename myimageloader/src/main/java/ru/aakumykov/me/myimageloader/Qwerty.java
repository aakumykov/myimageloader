package ru.aakumykov.me.myimageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class Qwerty {

    public static void loadImageToContainer(
            Context context,
            final ViewGroup imageContainer,
            String imageURL,
            int errorPlaceholderResourceId
//            ,LoadImageToContainerCallbacls callbacls
    ) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);

        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams);
        imageView.setAdjustViewBounds(true);

        final Drawable errorDrawable = context.getResources().getDrawable(errorPlaceholderResourceId);

        imageContainer.addView(progressBar);
        MyUtils.show(imageContainer);

        Glide.with(context)
                .load(imageURL)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        imageContainer.removeViewAt(0);
                        imageContainer.addView(imageView);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        imageView.setImageDrawable(errorDrawable);
                        imageContainer.removeViewAt(0);
                        imageContainer.addView(imageView);
                    }
                });
    }


}
