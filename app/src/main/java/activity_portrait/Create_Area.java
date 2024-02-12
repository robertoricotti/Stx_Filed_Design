package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.icu.number.LocalizedNumberFormatter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.stx_field_design.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import coords_calc.GPS;
import dialogs.CoordsGNSSInfo;
import dialogs.CustomToast;
import dialogs.Dialog_Edit_Zeta;
import dialogs.SaveFileDialog;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import services_and_bluetooth.DataSaved;

public class Create_Area extends AppCompatActivity {
    public static double quotaPiano=-32768;
    private static boolean showP = false;
    public static boolean shoeButton=false;
    CoordsGNSSInfo coordsGNSSInfo;
    private boolean mRunning = true;
    private Handler handler;
    SaveFileDialog saveFileDialog;
    DataProjectSingleton dataProject;
    public int pickIndex;
    ImageButton zoomC, zoomIn, zoomOut, rotateL, rotateR;
    static float layoutWidth, layoutHeight, lastX, lastY, deltaX, deltaY;
    private boolean zommaIn, zommaOut, rotLeft, rotRight, isDragging, isStat = false;
    ConstraintLayout drawingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSaved.offset_Z_antenna = 0;
        shoeButton=false;
        findView();
        onClick();


    }

    private void findView() {
        zoomC = findViewById(R.id.myCenterNav);
        zoomIn = findViewById(R.id.myZoomIn);
        zoomOut = findViewById(R.id.myZoomOut);
        rotateL = findViewById(R.id.myRotateL);
        rotateR = findViewById(R.id.myRotateR);
        pickIndex = 0;
        dataProject = DataProjectSingleton.getInstance();
        saveFileDialog = new SaveFileDialog(this, "AREA", String.valueOf(DataSaved.offset_Z_antenna));
        dataProject.mScaleFactor = 30f;
        drawingLayout = findViewById(R.id.container_draw);

        (new Handler()).postDelayed(this::setNavig, 500);


    }

    private void setNavig() {
        dataProject.navigatorX = drawingLayout.getWidth() / 2f;
        dataProject.navigatorY = drawingLayout.getHeight() / 2f;
        updateUI();


    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        zoomC.setOnClickListener(view -> {
            dataProject.mScaleFactor = 30f;


        });
        zoomIn.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                zommaIn = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                zommaIn = false;
            }
            return true;
        });
        zoomOut.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                zommaOut = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                zommaOut = false;
            }
            return true;
        });
        rotateR.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                rotRight = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                rotRight = false;
            }

            return false;
        });
        rotateL.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                rotLeft = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                rotLeft = false;
            }

            return false;
        });
    }

    private void updateUI() {
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // Ottenere il riferimento al layout dove vuoi disegnare i punti
                                Log.d("ZOOMMA", dataProject.mScaleFactor + "");

                                layoutWidth = drawingLayout.getWidth();
                                layoutHeight = drawingLayout.getHeight();
                                // Creare un oggetto Bitmap per disegnare
                                Bitmap bitmap = Bitmap.createBitmap(drawingLayout.getWidth(), drawingLayout.getHeight(), Bitmap.Config.ARGB_8888);
                                // Creare un oggetto Canvas per disegnare sulla bitmap
                                Canvas canvas = new Canvas(bitmap);
                                canvas.drawColor(Color.LTGRAY);
                                canvas.rotate(dataProject.rotate);
                                // Creare un oggetto Paint per definire lo stile del disegno
                                Paint paint = new Paint();


                                // Ottenere la mappa dei punti salvati dal DataProjectSingleton
                                LinkedHashMap<String, GPS> pointsMap = DataProjectSingleton.getInstance().getPoints();
                                ArrayList<GPS> points = new ArrayList<>(pointsMap.values());

                                // Disegnare i punti salvati e le linee che li collegano
                                for (int i = 0; i < points.size(); i++) {
                                    paint.setColor(Color.BLUE);
                                    paint.setStrokeWidth(5/dataProject.mScaleFactor);
                                    GPS point = points.get(i);
                                    double x = calculateXCoordinate((float) point.getX());
                                    double y = calculateYCoordinate((float) point.getY());
                                    canvas.drawCircle((float) x, (float) y, 10, paint);
                                    paint.setTextSize(25f);
                                    canvas.drawText("P"+pickIndex,(float) x, (float) y,paint);
                                    paint.setColor(Color.BLACK);
                                    paint.setStrokeWidth(4/dataProject.mScaleFactor);
                                    if (i > 0) {
                                        GPS prevPoint = points.get(i - 1);
                                        double prevX = calculateXCoordinate((float) prevPoint.getX());
                                        double prevY = calculateYCoordinate((float) prevPoint.getY());
                                        canvas.drawLine((float) prevX, (float) prevY, (float) x, (float) y, paint);

                                    }
                                }

                                paint.setColor(Color.BLACK);
                                paint.setStrokeWidth(4);

                                    drawNavigator(paint, canvas);


                                drawingLayout.setBackground(new BitmapDrawable(getResources(), bitmap));


                                if (zommaOut) {
                                    zommaIn = false;

                                        dataProject.mScaleFactor +=1f;



                                }
                                if (zommaIn) {
                                    zommaOut = false;

                                        dataProject.mScaleFactor -= 1f;



                                }
                                if (rotRight) {
                                    rotLeft = false;
                                    if (dataProject.rotate <= 360f) {
                                        dataProject.rotate += 2f;

                                    } else {
                                        dataProject.rotate = 0f;
                                    }
                                }
                                if (rotLeft) {
                                    rotRight = false;
                                    if (dataProject.rotate >= 1f) {
                                        dataProject.rotate -= 2f;

                                    } else {
                                        dataProject.rotate = 360f;

                                    }

                                }


                                // Gestione del pan
                            /*    drawingLayout.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                lastX = event.getX();
                                                lastY = event.getY();
                                                isDragging = true;
                                                break;
                                            case MotionEvent.ACTION_MOVE:
                                                if (isDragging) {
                                                    deltaX = event.getX() - lastX;
                                                    deltaY = event.getY() - lastY;
                                                    lastX = event.getX();
                                                    lastY = event.getY();
                                                    // Trasla tutti i punti e il navigator
                                                    drawNavigator(paint, canvas);
                                                    translatePointsAndNavigator(deltaX / dataProject.mScaleFactor, deltaY / dataProject.mScaleFactor);
                                                }
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                isDragging = false;
                                                break;
                                        }
                                        return true;
                                    }
                                });*/
                                if(quotaPiano>-32768) {
                                    shoeButton=true;
                                }


                            } catch (Exception e) {
                                System.out.println(e.toString());
                            }
                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    private void translatePointsAndNavigator(float deltaX, float deltaY) {
        // Trasla tutti i punti
        for (GPS point : DataProjectSingleton.getInstance().getPoints().values()) {
            point.setX(point.getX() + deltaX);
            point.setY(point.getY() + deltaY);
        }
        // Trasla il navigator
        dataProject.navigatorX += deltaX * dataProject.mScaleFactor;
        dataProject.navigatorY += deltaY * dataProject.mScaleFactor;
    }

    public void addPoint() {

        showP = false;
        if ((Nmea_In.Crs_Est==0&&Nmea_In.Crs_Nord==0)|| (!Nmea_In.ggaQuality.equals("4")&&DataSaved.useDemo==0)) {
            Log.d("PuntiF", String.valueOf(dataProject.getPoints()) + " " + dataProject.getSize());
            Toast.makeText(this, "Invalid GPS Position", Toast.LENGTH_LONG).show();
        } else {
            pickIndex++;
            if(pickIndex==1&&quotaPiano==-32768){
                new Dialog_Edit_Zeta(Create_Area.this,0).show();

                pickIndex--;
            }
            if(quotaPiano>-32768) {
                shoeButton=true;
                GPS gps = new GPS(null, Nmea_In.Crs_Est, Nmea_In.Crs_Nord,quotaPiano , Nmea_In.Band, Nmea_In.Zone);
                dataProject.addCoordinate("P" + pickIndex, gps);
            }
        }
    }

    public void clearPoint() {
        if (pickIndex > 0) {
            String s = "P" + pickIndex;
            dataProject.deleteCoordinate(s);
            pickIndex--;
        } else {
            Toast.makeText(this, "No More Points to Delete", Toast.LENGTH_LONG).show();
        }

    }

    public void showList() {
        if (!showP) {
            showP = true;
            coordsGNSSInfo = new CoordsGNSSInfo(this);
        }
        if (!coordsGNSSInfo.dialog.isShowing()) {

            coordsGNSSInfo.show();
        }

    }

    public void saveProj() {
        if (dataProject.getSize() >= 3) {

            if (!saveFileDialog.dialog.isShowing())
                saveFileDialog.show();
        } else {
            Toast.makeText(this, "Pick at least 3 points", Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataProject.clearData();
        mRunning = false;
        showP = false;
        quotaPiano=-32768;
    }


    private float calculateXCoordinate(float utmX) {
        // Calcola la coordinata X in base alla differenza tra la posizione attuale e quella dell'UTM
        // Ad esempio, se la posizione attuale è al centro del layout e l'UTM è a destra della posizione attuale,
        // la coordinata X sarà positiva; se l'UTM è a sinistra della posizione attuale, sarà negativa
        // Puoi adattare questo calcolo in base alle tue esigenze specifiche
        float currentX = layoutWidth / 2; // Posizione X attuale al centro del layout
        float xCoordinate = (float) (currentX + (utmX - Nmea_In.Crs_Est) * dataProject.mScaleFactor); // scaleFactor può essere utilizzato per regolare la velocità di spostamento

        return xCoordinate;
    }

    private float calculateYCoordinate(float utmY) {
        // Calcola la coordinata Y in modo simile alla coordinata X
        float currentY = layoutHeight / 2; // Posizione Y attuale al centro del layout
        float yCoordinate = (float) (currentY + (utmY - Nmea_In.Crs_Nord) * dataProject.mScaleFactor);

        return yCoordinate;
    }

    private void drawNavigator(Paint paint, Canvas canvas) {
        float centerX = dataProject.navigatorX;
        float centerY = dataProject.navigatorY;

        // Applica le traslazioni per il panning
        centerX += deltaX;
        centerY += deltaY;
        float triangleSize = 50; //
        if (dataProject.mScaleFactor > 1) {
            triangleSize = 50;
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

    /*
    private void drawPalina(Paint paint,Canvas canvas,float half_width,float half_height) {
        float size=20;
        if (dataProject.mScaleFactor > 1) {
            size = 20;
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

    }*/
}