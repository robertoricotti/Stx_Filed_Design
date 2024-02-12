package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.stx_field_design.R;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import activity_portrait.AB_WorkActivity;
import coords_calc.GPS;
import gnss.Nmea_In;
import services_and_bluetooth.DataSaved;

public class ProjectCanvas extends View {

    private float lastTouchX, lastTouchY;
    private static final float TOUCH_MOVE_THRESHOLD = 100.0f; // Puoi regolare questo valore
    Paint paint;
    Canvas canvas;
    float half_width;
    float half_height;
    float size;
    private final DataProjectSingleton dataProject;
    private ScaleGestureDetector scaleGestureDetector;

    public ProjectCanvas(Context context) {
        super(context);
        paint = new Paint();
        dataProject = DataProjectSingleton.getInstance();
        //translateTouch();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        this.canvas = canvas;
        super.onDraw(canvas);

        paint.setAntiAlias(true);

        half_width = getWidth() / 2f;
        half_height = getHeight() / 2f;

        canvas.save();
        canvas.scale(dataProject.getmScaleFactor(), dataProject.getmScaleFactor(), half_width, half_height);
        canvas.translate(dataProject.getOffsetX(), dataProject.offsetY);


        size = 50;


        double myLat = Nmea_In.mLat_1;
        double myLong = Nmea_In.mLon_1;

        double pointLat, pointLong;

        String id;

        float[] result = new float[3];

        double meters;

        double angolo;

        Coordinate[] coordinates = new Coordinate[dataProject.getSize()];

        int indexLine = -1;

        int counter = 0;
        float[] arrx = new float[dataProject.getSize()];
        float[] arry = new float[dataProject.getSize()];
        for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
            GPS value = entry.getValue();
            id = entry.getKey();
            pointLat = value.getLatitude();
            pointLong = value.getLongitude();

            if (id.equals(dataProject.getDistanceID())) {
                indexLine = counter;
            }

            Location.distanceBetween(myLat, myLong, pointLat, pointLong, result);
            meters = result[0] * dataProject.getScale();

            double resultAngolo = 0;
            if (AB_WorkActivity.auto) {
                resultAngolo = result[1] - (Nmea_In.tractorBearing);
                resultAngolo = resultAngolo - 90;
            } else {
                resultAngolo = result[1] + dataProject.rotate;
            }

            if (resultAngolo < 0)
                resultAngolo += 360;

            angolo = resultAngolo;

            double angleRadians = Math.toRadians(angolo);


            float endX = getWidth() / 2f + (float) (meters * Math.cos(angleRadians));
            float endY = getHeight() / 2f + (float) (meters * Math.sin(angleRadians));

            paint.setColor(Color.RED);
            canvas.drawCircle(endX, endY, size / 2.5f, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(30 / dataProject.mScaleFactor);
            canvas.drawText(id, endX + 25f, endY - 25f, paint);

            coordinates[counter] = new Coordinate(endX, endY, 0);
            arrx[counter] = endX;
            arry[counter] = endY;
            counter++;

        }


        if (dataProject.getProjectTag().equals("AREA")) {
            Log.d("ArrX", Arrays.toString(arrx));
            Log.d("ArrY", Arrays.toString(arry));
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5f);

            // Disegna le linee tra i punti consecutivi
            for (int i = 0; i < arrx.length - 1; i++) {
                float startX = arrx[i];
                float startY = arry[i];
                float endX = arrx[i + 1];
                float endY = arry[i + 1];
                canvas.drawLine(startX, startY, endX, endY, paint);
            }
            canvas.drawLine(arrx[arrx.length - 1], arry[arry.length - 1], arrx[0], arry[0], paint);
            // Disegna le linee che collegano i punti
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5f);
            Path path = new Path();
            path.moveTo(arrx[0], arry[0]); // Muovi il percorso al primo punto
            for (int i = 1; i < arrx.length; i++) {
                path.lineTo(arrx[i], arry[i]); // Aggiungi una linea dal punto precedente al punto attuale
            }
            path.close(); // Chiudi il percorso

            // Riempimento del percorso con un colore blu trasparente
            paint.setColor(Color.parseColor("#800F00FF"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, paint);
        }



        if (dataProject.isDelaunay()) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Geometry inputGeometry = geometryFactory.createMultiPointFromCoords(coordinates);

            DelaunayTriangulationBuilder delaunayBuilder = new DelaunayTriangulationBuilder();
            delaunayBuilder.setSites(inputGeometry);

            QuadEdgeSubdivision subdivision = delaunayBuilder.getSubdivision();

            Geometry outputGeometry = subdivision.getTriangles(geometryFactory);
            ArrayList<Coordinate> tmp = new ArrayList<>();

            for (int i = 0; i < outputGeometry.getNumGeometries(); i++) {
                Coordinate[] c = outputGeometry.getGeometryN(i).getCoordinates();
                for (int j = 0; j < c.length - 1; j++) {
                    tmp.add(new Coordinate(c[j].x, c[j].y, c[j].z));
                }
            }
            Coordinate[] triangoli = tmp.toArray(new Coordinate[0]);
            paint.setColor(Color.BLACK);

            if (dataProject.projectTag.equals("AB")) {
                canvas.drawLine((float) coordinates[0].x, (float) coordinates[0].y, (float) coordinates[4].x, (float) coordinates[4].y, paint);
                canvas.drawLine((float) coordinates[3].x, (float) coordinates[3].y, (float) coordinates[1].x, (float) coordinates[1].y, paint);
                // Codice per riempire l'area chiusa
                paint.setColor(Color.parseColor("#800F00FF")); // Colore rosso semitrasparente
                paint.setStyle(Paint.Style.FILL);

                for (int i = 0; i < triangoli.length; i += 3) {
                    Path trianglePath = new Path();
                    trianglePath.moveTo((float) triangoli[i].x, (float) triangoli[i].y);
                    trianglePath.lineTo((float) triangoli[i + 1].x, (float) triangoli[i + 1].y);
                    trianglePath.lineTo((float) triangoli[i + 2].x, (float) triangoli[i + 2].y);
                    trianglePath.close();
                    canvas.drawPath(trianglePath, paint);
                }

            }


        }

        if (indexLine != -1) {
            paint.setColor(Color.RED);
            if (dataProject.projectTag.equals("AB")) {
                canvas.drawLine(half_width, half_height, (float) coordinates[indexLine].x, (float) coordinates[indexLine].y, paint);
            }
            paint.setColor(Color.GREEN);

            canvas.drawCircle((float) coordinates[indexLine].x, (float) coordinates[indexLine].y, size / 2.5f, paint);

        }
        if (dataProject.projectTag.equals("AB")) {
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(9f);
            canvas.drawLine((float) coordinates[0].x, (float) coordinates[0].y, (float) coordinates[1].x, (float) coordinates[1].y, paint);
            //  paint.setColor(Color.BLACK);
            // canvas.drawCircle(half_width, half_height, 10 / dataProject.getmScaleFactor(), paint);
        }
        if (DataSaved.imgMode == 0) {
            drawPalina();
        } else {
            drawNavigator();
        }
        canvas.restore();
    }

    private void drawPalina() {
        float size = 40;
        if (dataProject.mScaleFactor > 1) {
            size = 40;
        } else {
            size = size / dataProject.mScaleFactor;
        }

        paint.setColor(getResources().getColor(R.color.black));
        paint.setStyle(Paint.Style.STROKE);
        RectF rover1 = new RectF(half_width - size, half_height - size, half_width + size, half_height + size);
        canvas.drawArc(rover1, 0, 360, true, paint);
        paint.setColor(getResources().getColor(R.color.bg_sfsred));
        paint.setStyle(Paint.Style.FILL);
        RectF rover = new RectF(half_width - size, half_height - size, half_width + size, half_height + size);
        canvas.drawArc(rover, 0, 360, true, paint);

        paint.setColor(Color.parseColor("#A0FFFFFF"));
        rover = new RectF(half_width - (size / 2f), half_height - (size / 2f), half_width + (size / 2f), half_height + (size / 2f));
        canvas.drawArc(rover, 0, 360, true, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3 / dataProject.getmScaleFactor());
        RectF rectF = new RectF(half_width - dataProject.getRadius(), half_height - dataProject.getRadius(), half_width + dataProject.getRadius(), half_height + dataProject.getRadius());
        canvas.drawArc(rectF, 0, 360, true, paint);

        paint.setStyle(Paint.Style.FILL);

    }

    private void drawNavigator() {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float triangleSize = 100; //
        if (dataProject.mScaleFactor > 1) {
            triangleSize = 100;
        } else {
            triangleSize = triangleSize / dataProject.mScaleFactor;
        }

        float x1 = centerX - triangleSize / 2;
        float y1 = centerY + triangleSize / (2 * (float) Math.tan(Math.toRadians(30)));
        float x2 = centerX + triangleSize / 2;
        float y2 = y1;
        float x3 = centerX;
        float y3 = centerY;


        paint.setColor(getResources().getColor(R.color.black));
        paint.setStyle(Paint.Style.STROKE);
        Path trianglePath1 = new Path();
        trianglePath1.moveTo(x1, y1);
        trianglePath1.lineTo(x2, y2);
        trianglePath1.lineTo(x3, y3);
        trianglePath1.close();
        canvas.drawPath(trianglePath1, paint);

        paint.setColor(getResources().getColor(R.color.bg_sfsred));
        paint.setStyle(Paint.Style.FILL);

        trianglePath1.moveTo(x1, y1);
        trianglePath1.lineTo(x2, y2);
        trianglePath1.lineTo(x3, y3);
        trianglePath1.close();
        canvas.drawPath(trianglePath1, paint);


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


    // GestureDetector for pinch-to-zoom
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
}
