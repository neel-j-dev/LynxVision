import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;
import edu.wpi.cscore.CvSource;

public class LynxCameraServer {
    Mat frame;
    CvSource frameOutput;



    public LynxCameraServer(String name){
        frameOutput = CameraServer.getInstance().putVideo(name, 640, 480);
    }

    public void putFrame(Mat frame){
        frameOutput.putFrame(frame);
    }

}
