package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;

import java.util.ArrayList;
import java.util.Map;

import coords_calc.GPS;
import gnss.Nmea_In;

public class ProjectCanvas extends View {
    Paint paint;
    private final DataProjectSingleton dataProject;

    public ProjectCanvas(Context context) {
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

        float half_width = getWidth() / 2f;
        float half_height = getHeight() / 2f;

        canvas.save();
        canvas.scale(dataProject.getmScaleFactor(), dataProject.getmScaleFactor(), half_width, half_height);
        canvas.translate(dataProject.getOffsetX(), dataProject.offsetY);

        paint.setColor(Color.BLACK);
        canvas.drawCircle(half_width, half_height, 10 / dataProject.getmScaleFactor(), paint);

        float size = 50;

        paint.setColor(Color.BLUE);
        RectF rover = new RectF(half_width - size, half_height - size, half_width + size, half_height + size);
        canvas.drawArc(rover, 0, 360, true, paint);

        paint.setColor(Color.WHITE);
        rover = new RectF(half_width - (size / 2f), half_height - (size / 2f), half_width + (size / 2f), half_height + (size / 2f));
        canvas.drawArc(rover, 0, 360, true, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2 / dataProject.getmScaleFactor());
        RectF rectF = new RectF(half_width - dataProject.getRadius(), half_height - dataProject.getRadius(), half_width + dataProject.getRadius(), half_height + dataProject.getRadius());
        canvas.drawArc(rectF, 0, 360, true, paint);

        paint.setStyle(Paint.Style.FILL);
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
            if (LoadProject.auto) {
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
            paint.setTextSize(50);
            canvas.drawText(id, endX + 25f, endY - 25f, paint);

            coordinates[counter] = new Coordinate(endX, endY, 0);
            counter++;
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
            Coordinate start;
            Coordinate dest;

            for (int i = 0; i < triangoli.length; i++) {
                start = new Coordinate(triangoli[i].x, triangoli[i].y);

                if ((i + 1) % 3 != 0)
                    dest = new Coordinate(triangoli[i + 1].x, triangoli[i + 1].y);
                else
                    dest = new Coordinate(triangoli[i - 2].x, triangoli[i - 2].y);

                canvas.drawLine((float) start.x, (float) start.y, (float) dest.x, (float) dest.y, paint);
            }

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

        if (indexLine != -1) {
            paint.setColor(Color.BLUE);
            canvas.drawLine(half_width, half_height, (float) coordinates[indexLine].x, (float) coordinates[indexLine].y, paint);

            paint.setColor(Color.GREEN);

            canvas.drawCircle((float) coordinates[indexLine].x, (float) coordinates[indexLine].y, size / 2.5f, paint);
        }

        canvas.restore();
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
