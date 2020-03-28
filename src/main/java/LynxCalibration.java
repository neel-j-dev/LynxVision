import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.IntStream;

import static org.bytedeco.javacpp.opencv_calib3d.CV_CALIB_CB_ADAPTIVE_THRESH;
import static org.bytedeco.javacpp.opencv_calib3d.CV_CALIB_CB_FILTER_QUADS;


public class LynxCalibration {
    NetworkTableInstance instance;
    NetworkTable lynxTable;
    ShuffleboardTab calibrationTab;
    Mat cameraFrame;
    Size boardSize;
    MatOfPoint3f obj;
    MatOfPoint2f imageCorners;
    int numSnapshots;

    boolean hasSetWidgets = false;

    List<Mat> imagePoints;
    List<Mat> objectPoints;
    Mat cameraMatrix;
    MatOfDouble distCoeffs;
    Mat savedImage;

    CvSource calibrationStream;

    SimpleWidget takeSnapShot;

    public LynxCalibration(NetworkTableInstance instance, ShuffleboardTab calibrationTab, LynxCameraServer cameraServer){
        this.instance = instance;
        this.lynxTable = instance.getTable("Calibration");
        this.calibrationTab = calibrationTab;
        this.cameraFrame = cameraServer.frames.values().toArray(new Mat[0])[0];
        this.calibrationStream = CameraServer.getInstance().putVideo("Calibration Stream", 640, 480);

        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.distCoeffs = new MatOfDouble();
        this.boardSize = new Size(7, 7);
        this.savedImage = cameraFrame;

        this.obj = new MatOfPoint3f();
        for (int j = 0; j < 49; j++) { obj.push_back(new MatOfPoint3f(new Point3(j / 7, j % 7, 0.0f))); }
        imageCorners = new MatOfPoint2f();

    }

    public void startCalibration(){
        LynxSolvePnP solvePNP = new LynxSolvePnP();
        displayWidgets();
        Thread calibrationThread = new Thread(() ->{

            while(!Thread.interrupted()){
                drawChessboardCorners();
                calibrationStream.putFrame(solvePNP.getVectors(cameraFrame, cameraMatrix, distCoeffs ));

                if(takeSnapShot.getEntry().getBoolean(false)){
                    cameraFrame.copyTo(this.savedImage);
                    takeSnapShot();
                    takeSnapShot.getEntry().setBoolean(false);
                }

            }

        });
        calibrationThread.start();
    }

    public void displayWidgets(){
        if(!hasSetWidgets) {
            takeSnapShot = calibrationTab.add("Take Snapshot", false)
                    .withWidget(BuiltInWidgets.kToggleSwitch);

            this.hasSetWidgets = true;
        }
    }

    public void drawChessboardCorners(){
        //Calib3d.drawChessboardCorners(cameraFrame, boardSize, imageCorners, findChessboard());
    }

    public boolean findChessboard(){
        return Calib3d.findChessboardCorners(cameraFrame, boardSize, imageCorners, Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FILTER_QUADS);
    }

    public void takeSnapShot(){
        if(this.numSnapshots < 5) {
            if(findChessboard()) {
                this.imagePoints.add(this.imageCorners);
                this.objectPoints.add(obj);
                numSnapshots++;
            }
        }else{
            calibrateCamera();

        }
    }

    public void calibrateCamera(){
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
        Mat perViewErrors = new Mat();

        double error = Calib3d.calibrateCameraExtended(objectPoints, imagePoints, savedImage.size(), cameraMatrix, distCoeffs, rvecs, tvecs, new Mat(), new Mat(), perViewErrors);
        System.out.println(error);
        System.out.println(perViewErrors.dump());
    }


}
