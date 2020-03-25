
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import java.util.Map;


public class LynxConfig {
    //Get Startup settings

    //Custom Widget entries
    SimpleWidget hsvWidget;

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

        hsvWidget = lynxTab.addPersistent("HSV Settings", new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0})
                .withWidget("HSV Settings");

        hsvSettings = hsvWidget.getEntry();
        double[] settings = hsvSettings.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0});

        hsvWidget.withProperties(Map.of(
                "HueSlider1", settings[0],
                "HueSlider2",settings[1],
                "SatSlider1", settings[2],
                "SatSlider2", settings[3],
                "ValueSlider1", settings[4],
                "ValueSlider2", settings[5]
        ));




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
