package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import activity_portrait.MyApp;
import coords_calc.DistToPoint;
import gnss.My_LocationCalc;
import utils.Utils;

public class UOM_Canvas extends View {
    Paint paint;
    public float offsetX, offsetY;
    float half_width, half_height, size;
    public  double[] B_coord, A_coord;
    public float mScaleFactor = 0.5f;
    public float scala = 100f;
    public static int pNumber;
    double distance = 0, orientation = 0;

    public UOM_Canvas(Context context) {
        super(context);
        paint = new Paint();
        translateTouch();

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        size = 50;
        half_width = getWidth() / 2f;
        half_height = getHeight() / 2f;

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, half_width, half_height);
        canvas.translate(offsetX, offsetY);

        paint.setColor(Color.BLUE);
        if (pNumber > 0) {

            canvas.drawCircle(half_width, half_height, size * mScaleFactor, paint);
            paint.setColor(Color.BLUE);
            paint.setTextSize(size/1.5f/ mScaleFactor);
            String s1 = "E:" + Utils.readUnitOfMeasure(String.valueOf(A_coord[0]), MyApp.visibleActivity);
            String s2 = " N:" + Utils.readUnitOfMeasure(String.valueOf(A_coord[1]), MyApp.visibleActivity);
            String s3 = " Z:" + Utils.readUnitOfMeasure(String.valueOf(A_coord[2]), MyApp.visibleActivity);
            canvas.drawText(s1+s2+s3, half_width + 50f, half_height + 50f, paint);
           // canvas.drawText(s2, half_width + 50f, half_height + 120f, paint);
           // canvas.drawText(s3, half_width + 50f, half_height + 190f, paint);

            if (pNumber == 2) {
                distance = new DistToPoint(A_coord[0], A_coord[1], 0, B_coord[0], B_coord[1], 0).getDist_to_point() * scala;
                orientation = My_LocationCalc.calcBearingXY(A_coord[0], A_coord[1], B_coord[0], B_coord[1]);
                orientation=orientation+270;

                if (orientation<0){orientation+=360;}
                if(orientation>360){
                    orientation-=360;
                }
                double angleRadians = Math.toRadians(-orientation);
                double endX = half_width + distance * Math.cos(angleRadians);
                double endY = half_height - distance * Math.sin(angleRadians);  // Modifica il segno qui

                paint.setColor(Color.RED);
                canvas.drawCircle((float) endX, (float) endY, size * mScaleFactor, paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(size/1.5f / mScaleFactor);
                String s4 = "E:" + Utils.readUnitOfMeasure(String.valueOf(B_coord[0]), MyApp.visibleActivity);
                String s5 = " N:" + Utils.readUnitOfMeasure(String.valueOf(B_coord[1]), MyApp.visibleActivity);
                String s6 = " Z:" + Utils.readUnitOfMeasure(String.valueOf(B_coord[2]), MyApp.visibleActivity);
                canvas.drawText(s4+s5+s6, (float) (endX + 50f), (float) (endY + 50f), paint);
                //canvas.drawText(s5, (float) (endX + 50f), (float) (endY + 120f), paint);
                //canvas.drawText(s6, (float) (endX + 50f), (float) (endY + 190f), paint);

                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(5f);
                canvas.drawLine(half_width, half_height, (float) endX, (float) endY, paint);
            }
        }
        canvas.restore();
    }

    private void translateTouch() {

        setOnTouchListener(new OnTouchListener() {
            float lastTouchX, lastTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                float x = event.getX() / mScaleFactor;
                float y = event.getY() / mScaleFactor;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = x;
                        lastTouchY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        offsetX += x - lastTouchX;
                        offsetY += y - lastTouchY;
                        lastTouchX = x;
                        lastTouchY = y;
                        invalidate();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
