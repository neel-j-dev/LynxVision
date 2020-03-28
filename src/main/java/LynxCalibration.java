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
    Mat intrinsic;
    Mat distCoeffs;
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
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.boardSize = new Size(7, 7);
        this.savedImage = cameraFrame;

        this.obj = new MatOfPoint3f();
        for (int j = 0; j < 49; j++) { obj.push_back(new MatOfPoint3f(new Point3(j / 7, j % 7, 0.0f))); }
        imageCorners = new MatOfPoint2f();

    }

    public void startCalibration(){

        displayWidgets();
        Thread calibrationThread = new Thread(() ->{

            while(!Thread.interrupted()){
                drawChessboardCorners();
                calibrationStream.putFrame(cameraFrame);

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
        Calib3d.drawChessboardCorners(cameraFrame, boardSize, imageCorners, findChessboard());
    }

    public boolean findChessboard(){
        boolean found = Calib3d.findChessboardCorners(cameraFrame, boardSize, imageCorners);
        return found;
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
        Mat cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        System.out.println(objectPoints.size() + " " + imagePoints.size());

        double error = Calib3d.calibrateCamera(objectPoints, imagePoints, savedImage.size(), cameraMatrix, distCoeffs, rvecs, tvecs);
        System.out.println(error);
        getSnapShotErrors(objectPoints, rvecs, tvecs, cameraMatrix, distCoeffs, imagePoints);
    }

    public void getSnapShotErrors(List<Mat> objectPoints, List<Mat> rvecs, List<Mat> tvecs, Mat cameraMatrix, Mat distCoeffs, List<Mat> imagePoints){
        double total_error = 0;
        double total_points = 0;
        MatOfPoint2f reprojectedPoints = new MatOfPoint2f();
        MatOfPoint3f currentObjectPoint = new MatOfPoint3f();
        MatOfDouble distortionCoeffs = new MatOfDouble();
        for(int i = 0; i < objectPoints.size(); i++){

            objectPoints.get(i).convertTo(currentObjectPoint, CvType.CV_32FC2);
            distCoeffs.convertTo(distortionCoeffs,CvType.CV_32FC2 );

            /*Calib3d.projectPoints(currentObjectPoint, rvecs.get(i), tvecs.get(i), cameraMatrix, distortionCoeffs, reprojectedPoints);
            total_error += Math.pow(Math.abs(Core.norm(imagePoints.get(i), reprojectedPoints)), 2);
            System.out.println(Math.pow(Math.abs(Core.norm(imagePoints.get(i), reprojectedPoints)), 2));
            total_points += objectPoints.get(i).size().area();*/
            List<Mat> currMat = new ArrayList<>();
            currMat.add(objectPoints.get(i));
            List<Mat> currPoint = new ArrayList<>();
            currPoint.add(imagePoints.get(i));
            List<Mat> rvecs2 = new ArrayList<>();
            List<Mat> tvecs2 = new ArrayList<>();

            double r  = Calib3d.calibrateCamera(currMat, currPoint, new Size(1, 1), new Mat(), new MatOfDouble(), rvecs2, tvecs2);
            System.out.println(r);
        }
        //System.out.println(total_error / total_points);
    }

}
