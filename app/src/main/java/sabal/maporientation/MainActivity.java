package sabal.maporientation;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    public static final int MAP_LENGTH = 7;
    public static final int MAP_HEIGHT = 3;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mIntermediateMat;
    public Robot2WD Controller;
    boolean BTconnected = false;
    boolean click = false;
    boolean known = false;
    ArrayList<Point> moves = new ArrayList<Point>();
    //UP, RIGHT, DOWN, LEFT
    String map[][] =
            {{"0110", "0101", "0111", "0111", "0101", "0011", "0110", "0011"},
                    {"1110", "0101", "1101", "1111", "0101", "1111", "1011", "1010"},
                    {"1110", "0101", "0011", "1010", "0110", "1111", "1111", "1011"},
                    {"1100", "0101", "1101", "1101", "1001", "1100", "1101", "1001"}};
    String robotsMap[][] = new String[MAP_LENGTH][MAP_HEIGHT];
    int robotsCurY = 2;
    int robotsCurX = 4;
    String Y = "1";
    String N = "0";
    Point can1 = new Point(0, 0);
    Point can2 = new Point(0, 0);
    Point can3 = new Point(0, 0);
    int equalsN = 0;
    ArrayList<Point> equalses = new ArrayList<Point>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setMaxFrameSize(800, 480);
        ToScreen(Integer.toString(mOpenCvCameraView.getHeight()));
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        Controller = new EV3();

        String Device = getIntent().getExtras().getString("Device Name", "null");
        try {
            BTconnected = Controller.Connect(Device);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BTconnected) {
            mOpenCvCameraView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (click) {
                        click = false;
                        try {
                            Controller.MotorsPowerOff();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        click = true;
                        try {
                            Controller.MotorsPowerOn();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ToScreen("\nNo Bluetooth connection with " + Device + "\n");
            MainActivity.this.finish();
        }

        //крутимся и сканируем пока не знаем где мы
        for (int n = 0; !known; n++) {
            switch (n) {
                case 0:
                    robotsMap[robotsCurX][robotsCurY] = cellScan();
                    //проверка по карте
                    for (int i = 0; i < MAP_LENGTH; i++) {
                        for (int j = 0; j < MAP_HEIGHT; j++) {
                            if (robotsMap[robotsCurX][robotsCurY].equals(map[i][j])) {
                                equalsN++;
                                equalses.add(new Point(i, j));
                            }
                        }
                    }
                    if (equalsN == 1) {
                        known = true;
                        robotsCurX = equalses.get(0).x;
                        robotsCurY = equalses.get(0).y;
                    } else {
                        //поворачиваем и проезжаем на следующую клетку (случайно выбранную или до какого поворота ближе)
                        //по соответствующему направлению делаем коорд curCell.charAt() == "Y"
                        robotsCurX++;
                        robotsCurY++;
                        //присваиваем в moves точки движения противоположные изменению коорд
                        moves.add(new Point(-1, -1));
                    }
                    equalses.clear();
                    equalsN = 0;
                    break;
                case 1:
                    robotsMap[robotsCurX][robotsCurY] = cellScan();
                    for (int i = 0; i < MAP_LENGTH; i++) {
                        for (int j = 0; j < MAP_HEIGHT; j++) {
                            if (robotsMap[robotsCurX][robotsCurY].equals(map[i][j]) &&
                                    robotsMap[robotsCurX + moves.get(1).x][robotsCurY + moves.get(1).y].equals(map[i + moves.get(1).x][j + moves.get(1).y])) {
                                equalsN++;
                                equalses.add(new Point(i, j));
                            }
                        }
                    }
                    if (equalsN == 1) {
                        known = true;
                        robotsCurX = equalses.get(0).x;
                        robotsCurY = equalses.get(0).y;
                    } else {
                        //поворачиваем и проезжаем на следующую клетку (случайно выбранную или до какого поворота ближе)
                        //по соответствующему направлению делаем коорд curCell.charAt(0123) == "Y"
                        robotsCurX++;
                        robotsCurY++;
                        //присваиваем в moves точки движения
                        moves.add(new Point(-1, -1));
                    }
                    equalses.clear();
                    equalsN = 0;
                    break;
                case 2:
                    robotsMap[robotsCurX][robotsCurY] = cellScan();
                    for (int i = 0; i < MAP_LENGTH; i++) {
                        for (int j = 0; j < MAP_HEIGHT; j++) {
                            if (robotsMap[robotsCurX][robotsCurY].equals(map[i][j]) &&
                                    robotsMap[robotsCurX + moves.get(1).x][robotsCurY + moves.get(1).y].equals(map[i + moves.get(1).x][j + moves.get(1).y]) &&
                                    robotsMap[robotsCurX + moves.get(2).x][robotsCurY + moves.get(2).y].equals(map[i + moves.get(2).x][j + moves.get(2).y])) {
                                equalsN++;
                                equalses.add(new Point(i, j));
                            }
                        }
                    }
                    if (equalsN == 1) {
                        known = true;
                        robotsCurX = equalses.get(0).x;
                        robotsCurY = equalses.get(0).y;
                    }
                    equalses.clear();
                    break;
            }
        }
        //поиск пути до ближайшей банки волновым способом

    }

    private String cellScan() {
        String curCell = "";
        for (int i = 0; i < 4; i++) {
            //получаем кадр
            //определяем есть ли путь в эту сторону
            boolean haveWay = false;
            //записываем в массив робота свойства клетки
            if (haveWay) {
                curCell += Y;
            } else {
                curCell += N;
            }
            //поворот на 90 градусов вправо
        }
        return curCell;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Mat grey = inputFrame.gray();
        Size sizeRgba = rgba.size();
        Mat rgbaInnerWindow;
        Mat greyInnerWindow;
        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;
        int width2 = cols / 5;
        int width = cols * 1 / 3;
        greyInnerWindow = grey.submat(0, rows, width2, width2 + width);
        rgbaInnerWindow = rgba.submat(0, rows, width2, width2 + width);


        rgbaInnerWindow.release();
        return rgba;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if (BTconnected) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);

    }

    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat();
    }

    public void onCameraViewStopped() {
        if (mIntermediateMat != null)
            mIntermediateMat.release();
        mIntermediateMat = null;
    }

    public void ToScreen(String stroka) {
        Toast.makeText(getApplicationContext(), stroka, Toast.LENGTH_SHORT).show();
    }
}