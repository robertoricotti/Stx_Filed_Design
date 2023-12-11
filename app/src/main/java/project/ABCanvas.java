package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.stx_field_design.R;

import coords_calc.DistToPoint;
import coords_calc.GPS;

public class ABCanvas extends View {

    Paint paint;
    Canvas canvas;
    float[] B_coord,A_coord;
    DataProjectSingleton dataProject;
    ///
    private static final float TOUCH_MOVE_THRESHOLD = 100.0f; // Puoi regolare questo valore
    private float lastTouchX, lastTouchY;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    ///
    public ABCanvas(Context context) {
        super(context);
        paint = new Paint();
        dataProject = DataProjectSingleton.getInstance();
        // Initialize gesture detectors
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());


    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);

        paint.setAntiAlias(true);


        float sizedot=10;
        if(dataProject.mScaleFactor>1){
            sizedot=10;
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
            paint.setColor(Color.BLUE);
            paint.setTextSize(50/dataProject.mScaleFactor);
            canvas.drawText("A", half_width+50, A_coord[1] + 100, paint);
            canvas.drawText("B", half_width+50, B_coord[1] - 50, paint);
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

            paint.setColor(Color.BLUE);
            canvas.drawText("C", C_coord[0]+50f, B_coord[1] - 50, paint);
            canvas.drawText("D", D_coord[0]+50f, A_coord[1] + 100, paint);
            canvas.drawText("E", E_coord[0]-250f, B_coord[1] - 50, paint);
            canvas.drawText("F", F_coord[0]-250f, A_coord[1] + 100, paint);


            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5 / dataProject.getmScaleFactor());

            canvas.drawLine(B_coord[0],B_coord[1],C_coord[0],C_coord[1], paint);
            canvas.drawLine(C_coord[0],C_coord[1],D_coord[0],D_coord[1], paint);
            canvas.drawLine(D_coord[0],D_coord[1],A_coord[0],A_coord[1], paint);
            canvas.drawLine(B_coord[0],B_coord[1],E_coord[0],E_coord[1], paint);
            canvas.drawLine(E_coord[0],E_coord[1],F_coord[0],F_coord[1], paint);
            canvas.drawLine(F_coord[0],F_coord[1],A_coord[0],A_coord[1], paint);

        }
    }






    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Gestisci il pinch-to-zoom
        scaleGestureDetector.onTouchEvent(event);

        // Verifica se si sta attualmente gestendo uno zoom
        boolean isScaling = scaleGestureDetector.isInProgress();

        // Gestisci il trascinamento solo se non si sta zoomando
        if (!isScaling) {
            float x = event.getX() / dataProject.getmScaleFactor();
            float y = event.getY() / dataProject.getmScaleFactor();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = x;
                    lastTouchY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - lastTouchX;
                    float deltaY = y - lastTouchY;

                    // Verifica la distanza totale percorsa prima di considerare il trascinamento
                    if (Math.abs(deltaX) > TOUCH_MOVE_THRESHOLD || Math.abs(deltaY) > TOUCH_MOVE_THRESHOLD) {
                        dataProject.offsetX += deltaX;
                        dataProject.offsetY += deltaY;
                        lastTouchX = x;
                        lastTouchY = y;
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Ripristina lastTouchX e lastTouchY
                    lastTouchX = x;
                    lastTouchY = y;
                    break;
            }
        }

        return true;
    }



    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Adjust the scale factor and invalidate the view
            dataProject.mScaleFactor *= detector.getScaleFactor();
            dataProject.mScaleFactor = Math.max(0.04f, Math.min(dataProject.mScaleFactor, 2.0f));
            invalidate();
            return true;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // override methods for other gestures if needed
    }



}
