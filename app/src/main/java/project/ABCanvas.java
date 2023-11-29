package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import coords_calc.DistToPoint;
import coords_calc.GPS;

public class ABCanvas extends View {

    Paint paint;
    float[] B_coord,A_coord;
    DataProjectSingleton dataProject;
    public ABCanvas(Context context) {
        super(context);
        paint = new Paint();
        dataProject = DataProjectSingleton.getInstance();
        translateTouch();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);

        float size = 50;
        float sizedot=20;
        if(dataProject.mScaleFactor>1){
            sizedot=20;
        }else {
            sizedot = sizedot / dataProject.mScaleFactor;
        }

        float half_width = getWidth() / 2f;

        float half_height = getHeight() / 2f;

        canvas.save();
        canvas.scale(dataProject.getmScaleFactor(), dataProject.getmScaleFactor(), half_width, half_height);
        canvas.translate(dataProject.getOffsetX(), dataProject.offsetY);




        if(dataProject.getSize() == 1){
            paint.setColor(Color.RED);

            canvas.drawCircle(half_width, half_height, sizedot, paint);

        }

        if(dataProject.getSize() == 2 || dataProject.getSize() == 4 || dataProject.getSize() == 6){

            paint.setColor(Color.RED);

            GPS a = dataProject.getPoints().get("A");

            GPS b = dataProject.getPoints().get("B");

            double distance =  new DistToPoint(a.getX(), a.getY(), 0, b.getX(), b.getY(), 0).getDist_to_point() * dataProject.getScale();
            A_coord=new float[]{half_width, half_height + (float)(distance / 2f)};
            canvas.drawCircle(half_width, half_height + (float)(distance / 2f), sizedot, paint);



            paint.setStrokeWidth(5 / dataProject.getmScaleFactor());

            canvas.drawLine(half_width, half_height + (float)(distance / 2f), half_width, half_height - (float)(distance / 2f), paint);

            B_coord=new float[]{half_width, half_height - (float)(distance / 2f)};
            canvas.drawCircle(B_coord[0],B_coord[1],sizedot, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(50/dataProject.mScaleFactor);
            canvas.drawText("A", half_width+50, half_height + (float)(distance / 2f) + 350, paint);
            canvas.drawText("B", half_width+50, half_height - (float)(distance / 2f) - 150, paint);
        }



        if(dataProject.getSize() == 6){

            double leftA=dataProject.getLtLength()*Math.sin(Math.toRadians(dataProject.getLtSlope()));
            double rightA=dataProject.getRtLength()*Math.sin(Math.toRadians(dataProject.getRtSlope()));
            double sideBLeft=Math.sqrt((dataProject.getLtLength()*dataProject.getLtLength())-(leftA*leftA));
            double sideBright=Math.sqrt((dataProject.getRtLength()*dataProject.getRtLength())-(rightA*rightA));

            float [] C_coord=new float[]{(float) (B_coord[0]+sideBright*dataProject.getScale()),B_coord[1]};
            float [] D_coord=new float[]{(float) (A_coord[0]+sideBright*dataProject.getScale()),A_coord[1]};
            float [] E_coord=new float[]{(float) (B_coord[0]-sideBLeft*dataProject.getScale()),B_coord[1]};
            float [] F_coord=new float[]{(float) (A_coord[0]-sideBLeft*dataProject.getScale()),A_coord[1]};
            paint.setColor(Color.RED);

            canvas.drawCircle(C_coord[0], C_coord[1], sizedot, paint);
            canvas.drawCircle(D_coord[0], D_coord[1], sizedot, paint);
            canvas.drawCircle(E_coord[0], E_coord[1], sizedot, paint);
            canvas.drawCircle(F_coord[0], F_coord[1], sizedot, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("C", C_coord[0]+50f, C_coord[1] - 150f, paint);
            canvas.drawText("D", D_coord[0]+50f, D_coord[1] + 350f, paint);
            canvas.drawText("E", E_coord[0]-250f, E_coord[1] - 150f, paint);
            canvas.drawText("F", F_coord[0]-250f, F_coord[1] + 350f, paint);



            paint.setStrokeWidth(5 / dataProject.getmScaleFactor());

            canvas.drawLine(B_coord[0],B_coord[1],C_coord[0],C_coord[1], paint);
            canvas.drawLine(C_coord[0],C_coord[1],D_coord[0],D_coord[1], paint);
            canvas.drawLine(D_coord[0],D_coord[1],A_coord[0],A_coord[1], paint);
            canvas.drawLine(B_coord[0],B_coord[1],E_coord[0],E_coord[1], paint);
            canvas.drawLine(E_coord[0],E_coord[1],F_coord[0],F_coord[1], paint);
            canvas.drawLine(F_coord[0],F_coord[1],A_coord[0],A_coord[1], paint);

        }
    }



    private void translateTouch(){
        dataProject.setOffsetX(0);
        dataProject.setOffsetY(0);
        setOnTouchListener(new OnTouchListener() {
            float lastTouchX, lastTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                float x = event.getX() / dataProject.getmScaleFactor();
                float y = event.getY() / dataProject.getmScaleFactor();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = x;
                        lastTouchY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dataProject.offsetX += x - lastTouchX;
                        dataProject.offsetY += y - lastTouchY;
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
