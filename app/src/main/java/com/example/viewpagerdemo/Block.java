package com.example.q.ondraw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Block {

    public int x;
    public int y;
    int width=50;
    int height = 50;
    private Paint paint;
    boolean isFilled = false ;
    private Rect rect;
    int boundaryColor = Color.BLACK;
    int filledColor;
    boolean isBoundary=false;

    public Block (int x, int y, int color){
        this.x = x;
        this.y = y;
        paint =  new Paint();
        filledColor = color;
        paint.setAntiAlias(true);
        paint.setColor(boundaryColor);
        paint.setStyle(Paint.Style.STROKE);
        rect = new Rect();
        if(x==0||y==0||x==22||y==22){
            isBoundary=true;
        }
    }

    public boolean isFilled(){ return isFilled; }
    public void setFilled(boolean filled){ isFilled = filled; }
    public void setPaint(Paint mpaint){paint = mpaint;}
    public void setPosition(int x,int y){ this.x = x; this.y = y;}
    public boolean isBoundary(){return isBoundary;}

    public void drawBlock(Canvas c){
    if(isFilled){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(filledColor);
    }
        c.drawRect(x*width+5,y*width+5,x*width+55,y*width+55,paint);
    }

}
