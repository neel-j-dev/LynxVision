
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import java.util.Arrays;
import java.util.Map;


public class LynxConfig {
    //NT instance
    NetworkTableInstance instance;
    NetworkTable table;
    NetworkTableEntry hsvSettings;
    NetworkTableEntry blurSettings;

    //Declare shuffleboard tab
    ShuffleboardTab lynxTab;

    //Stores all settings
    double[] threshHoldSettings;
    double blurAmount;


    public LynxConfig(NetworkTableInstance instance){
        this.instance = instance;
        table = instance.getTable("Lynx Vision");
        //hueSettings = table.getEntry("Hue Settings");

        //Shuffleboard tab
        lynxTab = Shuffleboard.getTab("Lynx Vision");

        //Get blur amount from shuffleboard
        blurSettings = lynxTab.addPersistent("Blur Settings", 0).
                withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", 0, "max", 100))
                .getEntry();

        //Get HSV  settings from shuffleboard
        double[] defaultValue = new double[]{0.0, 150, 0.0, 150, 0.0, 250};
        hsvSettings = lynxTab.addPersistent("HSV Settings", defaultValue)
                .withWidget("HSV Settings")
                .getEntry();

        Shuffleboard.update();

        //grab initial settings
        grabSettings();

    }

    public void grabSettings(){
        threshHoldSettings = hsvSettings.getDoubleArray(new double[]{0});
        blurAmount = hsvSettings.getDouble(0.0);
    }


}
