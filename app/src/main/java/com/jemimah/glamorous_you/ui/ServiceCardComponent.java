package com.jemimah.glamorous_you.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jemimah.glamorous_you.R;

public class ServiceCardComponent extends RelativeLayout {

    protected int imageSrc;
    protected String title;
    private ImageView imageView;
    private TextView tvTitle;

    private static final int DEFAULT_WIDTH = 145;
    private static final int DEFAULT_HEIGHT = 105;
    private int mWidth = DEFAULT_WIDTH, mHeight = DEFAULT_HEIGHT;

    public ServiceCardComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.component_service_card, this);

        imageView= findViewById(R.id.ivImage);
        tvTitle = findViewById(R.id.tvTitle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ServiceCardComponent);
        try{
            for (int i = 0; i < typedArray.getIndexCount(); i++){
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.ServiceCardComponent_title){
                    setTitle(typedArray.getString(attr));
                }
                if (attr == R.styleable.ServiceCardComponent_imageSrc){
                    setImageSrc(typedArray.getResourceId(attr, R.drawable.barber));
                }
                if (attr == R.styleable.ServiceCardComponent_mWidth){
                    setmWidth(typedArray.getInt(attr, DEFAULT_WIDTH));
                }
                if (attr == R.styleable.ServiceCardComponent_mHeight){
                    setmHeight(typedArray.getInt(attr, DEFAULT_HEIGHT));
                }

            }
        } catch (Exception ignored){

        }

    }

    public void setImageSrc(int imageSrc) {
        this.imageSrc = imageSrc;
        imageView.setImageResource(imageSrc);
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
        setMinimumWidth(mWidth);
        imageView.setMaxWidth((int)(0.55 * mWidth));
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
        setMinimumHeight(mHeight);
        imageView.setMaxHeight((int)(0.6 * mHeight));
    }
}
