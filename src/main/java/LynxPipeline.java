import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class LynxPipeline {

    LynxConfig settings;
    List<LynxCameraServer> frames;

    //Holds output mats after hsv threshing
    LynxCameraServer hsvOutput;


    //HSV settings
    double[] hueValues;
    double[] saturationValues;
    double[] valueValues;

    //This holds the threshed Mat
    Mat hsvThresholdOutput;

    public LynxPipeline(LynxConfig settings, List<LynxCameraServer> frames){
        //This allows us to access the settings entered on the shuffleboard and output them to the camera server
        this.settings = settings;
        this.frames = frames;

        //Start camera server
        hsvOutput = new LynxCameraServer("HSV output");
        frames.add(hsvOutput);
    }

    public void process(Mat frame){
        //Use hsv settings from range sliders on shuffleboard to threshold frame
        hsvThresholdOutput = new Mat();
        hueValues = new double[]{settings.threshHoldSettings[0], settings.threshHoldSettings[1]};
        saturationValues = new double[]{settings.threshHoldSettings[2], settings.threshHoldSettings[3]};
        valueValues =  new double[]{settings.threshHoldSettings[4], settings.threshHoldSettings[5]};
        hsvThreshold(frame, hueValues, saturationValues, valueValues, hsvThresholdOutput);

        //Output to camera server
        hsvOutput.putFrame(hsvThresholdOutput);
    }

    /**
     * Segment an image based on hue, saturation, and value ranges.
     *
     * @param input The image on which to perform the HSL threshold.
     * @param hue The min and max hue
     * @param sat The min and max saturation
     * @param val The min and max value
     * @param out The image in which to store the output.
     */
    private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
                              Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
        Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
                new Scalar(hue[1], sat[1], val[1]), out);
    }

}
