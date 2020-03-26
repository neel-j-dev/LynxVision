import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

import java.util.Map;

public class LynxConsole {
    ShuffleboardTab lynxTab;
    ShuffleboardTab calibrationTab;

    NetworkTableInstance instance;
    NetworkTable lynxTable;
    NetworkTableEntry hsvSettings;

    SimpleWidget hsvWidget;
    SimpleWidget blurSettings;
    SimpleWidget cameraOutput;
    SimpleWidget FOV;
    SimpleWidget calibrateCamera;


    public LynxConsole(NetworkTableInstance instance){
        this.instance = instance;
        this.lynxTable = instance.getTable("Lynx Vision");

        //Shuffleboard tab
        this.lynxTab = Shuffleboard.getTab("Lynx Vision");
        this.calibrationTab = Shuffleboard.getTab("Lynx3D");

        //Get blur radius from shuffleboard
        blurSettings = lynxTab.addPersistent("Blur Settings", 0).
                withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", 0, "max", 100));

        this.hsvWidget = lynxTab.addPersistent("HSV Settings", new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0})
                .withWidget("HSV Settings");

        this.hsvSettings = hsvWidget.getEntry();
        double[] settings = hsvSettings.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0});
        setHSVSliders(hsvWidget, settings);


        //Get which output user wants outputted to the CameraServer.
        this.cameraOutput = lynxTab.addPersistent("Stream Index", 0)
                .withWidget(BuiltInWidgets.kTextView);

        //Gets FOV of camera
        this.FOV = lynxTab.addPersistent("Diagonal FOV", 0)
                .withWidget(BuiltInWidgets.kTextView);

        //Toggle for starting camera calibration
        this.calibrateCamera = calibrationTab.add("Start", false)
                .withWidget(BuiltInWidgets.kToggleButton);
        Shuffleboard.update();

    }

    public void setHSVSliders(SimpleWidget hsvWidget, double[] settings){
        hsvWidget.withProperties(Map.of(
                "HueSlider1", settings[0],
                "HueSlider2",settings[1],
                "SatSlider1", settings[2],
                "SatSlider2", settings[3],
                "ValueSlider1", settings[4],
                "ValueSlider2", settings[5]
        ));
    }


}
