
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import java.util.Arrays;
import java.util.Map;


public class LynxConfig {
    //NT entries
    NetworkTableInstance instance;
    NetworkTable table;
    NetworkTableEntry hsvSettings;
    NetworkTableEntry blurSettings;
    NetworkTableEntry cameraOutput;

    //Declare shuffleboard tab
    ShuffleboardTab lynxTab;

    //Stores all settings
    double[] threshHoldSettings;
    double blurAmount;
    int cameraIndex;


    public LynxConfig(NetworkTableInstance instance){
        this.instance = instance;
        table = instance.getTable("Lynx Vision");

        //Shuffleboard tab
        lynxTab = Shuffleboard.getTab("Lynx Vision");

        //Get blur radius from shuffleboard
        blurSettings = lynxTab.addPersistent("Blur Settings", 0).
                withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", 0, "max", 100))
                .getEntry();

        //Get HSV  settings from shuffleboard
        double[] defaultValue = new double[]{0.0, 150, 0.0, 150, 0.0, 250};
        hsvSettings = lynxTab.addPersistent("HSV Settings", defaultValue)
                .withWidget("HSV Settings")
                .getEntry();

        //Get which output user wants outputted to the CameraServer, if they want all the frames to be published, the number provided would be 100
        cameraOutput = lynxTab.addPersistent("OutputStream Index", 30)
                .withWidget(BuiltInWidgets.kTextView)
                .getEntry();


        Shuffleboard.update();

        //grab initial settings
        grabSettings();

    }

    public void grabSettings(){
        threshHoldSettings = hsvSettings.getDoubleArray(new double[]{0});
        blurAmount = blurSettings.getDouble(0.0);
        cameraIndex = cameraOutput.getNumber(100).intValue();
    }


}
