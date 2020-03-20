import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;
import edu.wpi.cscore.CvSource;


import java.util.LinkedHashMap;

public class LynxCameraServer {
    LinkedHashMap<String, Mat> frames = new LinkedHashMap<>();
    CvSource LynxVision;


    public LynxCameraServer(){
        LynxVision = CameraServer.getInstance().putVideo("LynxVisionOutput", 640, 480);

    }

    public void addFrame(String name, Mat frame){
        frames.put(name, frame) ;
    }

    public void publishFrame(int index){
        LynxVision.putFrame(frames.values().toArray(new Mat[0])[index]);
    }

    public void publishAll(){
        frames.forEach((name,frame) -> {
            CameraServer.getInstance().putVideo(name, 640, 480).putFrame(frame);
        });
    }

}
