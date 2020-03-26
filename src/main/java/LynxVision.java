
import edu.wpi.first.networktables.NetworkTableInstance;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class LynxVision {

    public static void main(String[] args) throws IOException, URISyntaxException {

        new LynxVision().startLynxVision();
    }

    public void startLynxVision() throws IOException, URISyntaxException {
        //Load DLLs
        System.load(getResource("opencv_java341.dll"));

        //Example "frame" from camera
        Mat frame = Imgcodecs.imread(getResource("chessboard.png"));

        //Start vision thread
        Thread visionThread = new Thread(() ->{
            //Start network tables
            NetworkTableInstance NTinstance = NetworkTableInstance.getDefault();
            NTinstance.startServer();



            //Holds all pipeline outputs to switch between
            LynxCameraServer frames = new LynxCameraServer();
            frames.addFrame("CameraFrame", frame);

            //Get configs from Smartdashboard
            LynxConfig settings = new LynxConfig(NTinstance, frames);

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


    public String getResource(String filename) throws IOException {
        URL url = LynxVision.class.getResource("/" + filename);

        //Creates temp directory
        File tmpDir = Files.createTempDirectory("static-frames").toFile();
        tmpDir.deleteOnExit();
        File nativeLibTmpFile = new File(tmpDir, filename);
        nativeLibTmpFile.deleteOnExit();

        //Writes to temp directory
        try (InputStream in = url.openStream()) {
            Files.copy(in, nativeLibTmpFile.toPath());
        }
        return nativeLibTmpFile.getAbsolutePath();
    }

}
