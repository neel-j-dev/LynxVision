
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PersistentException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


import java.io.IOException;

public class LynxVision {

    public static void main(String[] args) throws IOException{
        //Load DLLs
        loadDLLs();

        //Example "frame" from camera
        Mat frame = Imgcodecs.imread("test_images\\LoadingBay.jpg");


        //Start vision thread
        Thread visionThread = new Thread(() ->{
            //Start network tables
            NetworkTableInstance NTinstance = NetworkTableInstance.getDefault();
            NTinstance.startServer();

            //Get configs from Smartdashboard
            LynxConfig settings = new LynxConfig(NTinstance);


            //Holds all pipeline outputs to switch between
            LynxCameraServer frames = new LynxCameraServer();


            //LynxPipeline is responsible for all image processing
            LynxPipeline pipeline = new LynxPipeline(settings, frames, NTinstance);

            //Continue while thread is not interrupted
            while(!Thread.interrupted()){

                //Grab settings again if they have changed
                settings.grabSettings();

                //Process Frame
                pipeline.process(frame);

                //output frame to camera server
                frames.publishFrame( (settings.cameraIndex + frames.frames.size()) % frames.frames.size());

            }

        });
        visionThread.start();
    }


    public static void loadDLLs() throws IOException {
        //Load dependent libraries
        System.loadLibrary("wpiHaljni");
        System.loadLibrary("wpiHal");
        System.loadLibrary("ntcore");
        System.loadLibrary("ntcorejni");
        System.loadLibrary("opencv_java401");
    }


}
