
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LynxConfig {
    LynxConsole lynxConsole;

    NetworkTableEntry blurSettings;
    NetworkTableEntry cameraOutput;
    NetworkTableEntry hsvSettings;
    NetworkTableEntry diagonalFOV;


    //Stores all settings
    double[] threshHoldSettings;
    double blurAmount;
    int cameraIndex;
    double imageWidth;
    double imageHeight;
    double FOV;
    double focalLength;


    public LynxConfig(NetworkTableInstance instance){
        lynxConsole = new LynxConsole(instance);

        updateNT();

        //grab initial settings
        grabSettings();

    }

    public void updateNT(){
        //Grab data set in widgets
        blurSettings = lynxConsole.blurSettings.getEntry();
        hsvSettings = lynxConsole.hsvWidget.getEntry();
        cameraOutput = lynxConsole.cameraOutput.getEntry();
        diagonalFOV = lynxConsole.FOV.getEntry();
    }

    public void grabSettings(){
        updateNT();
        threshHoldSettings = hsvSettings.getDoubleArray(new double[]{0});
        blurAmount = blurSettings.getDouble(0.0);
        cameraIndex = cameraOutput.getNumber(100).intValue();
        FOV  = diagonalFOV.getNumber(0).intValue();
        imageWidth = 640;
        imageHeight = 480;
        focalLength = imageWidth / ( 2.0 *Math.tan(FOV/2.0));
    }


}
