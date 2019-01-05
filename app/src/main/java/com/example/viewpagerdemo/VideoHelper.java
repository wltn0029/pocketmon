package com.example.viewpagerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoHelper extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;
    public VideoHelper(Context context){
        super(context);

    }
    public VideoHelper(Context context,AttributeSet attrs) {
        super(context, attrs);
    }
    public VideoHelper(Context context,AttributeSet attrs,int detStyle){
        super(context,attrs,detStyle);

    }
    public void setVideoSize(int width,int height){
        mVideoWidth=width;
        mVideoHeight=height;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
