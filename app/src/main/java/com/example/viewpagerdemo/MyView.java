package com.example.viewpagerdemo;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MyView extends View {
    Canvas mcanvas;
    Boolean isTouch = false;
    Path path;
    Paint m_Paint;
    int userColor;
    int positionX=0;
    int positionY=0;
    public static String direction;
    float iconX;
    float iconY;
    int originalXuser;
    int originalYuser;
    Context context;
    int startX;
    int startY;
    int endX;
    int endY;
    boolean isStart=false;
    boolean isEnd = false;
    //---------------------------------------------------------------------------------------------
    //constructor
    //---------------------------------------------------------------------------------------------

    public MyView(Context mcontext){
        super(mcontext);
        context =mcontext;
        initMyView();
    }

    public MyView(Context mcontext, AttributeSet attrs){
        super(mcontext,attrs);
        context = mcontext;
        initMyView();
    }
    public MyView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initMyView();
    }
    //---------------------------------------------------------------------------------------------
    //override
    //---------------------------------------------------------------------------------------------
    public void initMyView(){
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(Color.BLUE);
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(4);
        iconX = GameActivity.heartX;
        iconY = GameActivity.heartY;
        direction = "DOWN";
        path = new Path();
//        startX=5;
//        startY=5;
        path.moveTo(0,0);
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mcanvas = canvas;
        //set rectangle size as 50
        int leftStart = 5;
        int topStart = 5;
        for(int j=0;j<23;j++) {
            for (int i = 0; i < 23; i++) {
                GameActivity.Board.get(i + 23 * j).drawBlock(canvas);
            }
        }
        if(GameActivity.curXuser == GameActivity.initXuser && GameActivity.curYuser == GameActivity.initYuser){
            GameActivity.heart.setVisibility(VISIBLE);
        }
        path.moveTo(originalXuser*50.0f,originalYuser*50.0f);
        path.lineTo(GameActivity.curXuser*50.0f,GameActivity.curYother*50.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(GameActivity.heart,View.X,View.Y,path);
        objectAnimator.start();
        originalXuser=GameActivity.curXuser;
        originalYuser = GameActivity.curYuser;
//        super.onDraw(canvas);
//        int prevX = startX;
//        int prevY=startY;
//        switch(direction){
//            case "UP":
//                if(startY>=50)
//                    startY-=50;
//                break;
//            case "DOWN":
//                if(startY<=1000)
//                    startY+=50;
//                break;
//            case "LEFT":
//                if(startX>=50)
//                    startX-=50;
//                break;
//            case"RIGHT":
//                if(startX<=1000)
//                    startX+=50;
//
//        }
//       // path.moveTo(0,0);
//        path.lineTo(startX,startY);

        //path.lineTo(180,418);
        /**
         *  p = new Path();
         *         p.moveTo(0, 0);
         *         p.lineTo(180, 0);
         *         p.lineTo(180,180);
         *         p.lineTo(0,180);
         *        // p.lineTo(0,0);
         *         p.moveTo(180, 300);
         *        // p.cubicTo(44, 261, 166, 332, 90, 339);
         *        // p.cubicTo(14, 332, 136, 261, 0, 300);
         *         p.moveTo(0, 300);
         *         p.lineTo(0, 0);
         */
//        canvas.drawPath(path, m_Paint);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float xPos = event.getX();
        float yPos = event.getY();
        int y=5;
        if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE) {
            if (yPos < 500 && (((xPos > yPos) && (xPos < 500)) || ((xPos + yPos < 1000) && (xPos > 500)))) {
                //up
                if (positionY > 0) {
                    positionY -= 1;
                    direction ="UP";
                }
            }
            //down
            else if (yPos > 500 && (((xPos < yPos) && (xPos > 500)) || ((xPos + yPos > 1000) && (xPos < 500)))) {
                if (positionY < 22) {
                    positionY += 1;
                    direction = "DOWN";
                }
            }
            //left
            else if (xPos < 500 && (((yPos < 500) && (xPos < yPos)) || ((yPos > 500) && (xPos + yPos < 1000)))) {
                //Toast.makeText(context,"left",Toast.LENGTH_SHORT).show();
                if (positionX > 0) {
                    positionX -= 1;
                    direction = "LEFT";
                }
            }
            //right
            else if ((xPos > 500) && ((xPos > yPos) && (yPos > 500) || ((xPos + yPos > 1000) && (yPos < 500)))) {
                //Toast.makeText(context,"right",Toast.LENGTH_SHORT).show();
                if (positionX < 22) {
                    positionX += 1;
                    direction="RIGHT";
                }
            }
       // invalidate();
        }
        return true;
    }

}
