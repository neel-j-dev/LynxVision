import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;


public class LynxCalibration {
    NetworkTableInstance instance;
    NetworkTable lynxTable;
    ShuffleboardTab calibrationTab;
    Mat cameraFrame;
    Size boardSize;
    MatOfPoint3f obj;
    MatOfPoint2f imageCorners;

    CvSource calibrationStream;

    ComplexWidget camera;

    public LynxCalibration(NetworkTableInstance instance, ShuffleboardTab calibrationTab, LynxCameraServer cameraServer){
        this.instance = instance;
        this.lynxTable = instance.getTable("Calibration");
        this.calibrationTab = calibrationTab;
        this.cameraFrame = cameraServer.frames.values().toArray(new Mat[0])[0];
        this.calibrationStream = CameraServer.getInstance().putVideo("Calibration Stream", 640, 480);
        this.boardSize = new Size(7, 7);

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
            }

        });
        calibrationThread.start();
    }

    public void displayWidgets(){
        if(!calibrationTab.getComponents().contains(camera)) {
            camera = calibrationTab.add(calibrationStream).withWidget(BuiltInWidgets.kCameraStream);
        }
    }

    public void drawChessboardCorners(){
        System.out.println(findChessboard());
        Calib3d.drawChessboardCorners(cameraFrame, boardSize, imageCorners, findChessboard());
    }

    public boolean findChessboard(){
        return Calib3d.findChessboardCorners(cameraFrame, boardSize, imageCorners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
    }
}
