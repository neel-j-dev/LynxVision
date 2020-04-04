
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class LynxPipeline {

    LynxConfig settings;
    LynxCameraServer frames;

    //Target data NT entries
    NetworkTableInstance instance;
    NetworkTableEntry targetArea;
    NetworkTableEntry targetWidth;
    NetworkTableEntry targetHeight;
    NetworkTableEntry targetBoundingWidth;
    NetworkTableEntry targetBoundingHeight;
    NetworkTableEntry targetYaw;
    NetworkTableEntry targetPitch;


    //Holds all settings from NT
    double[] hueValues;
    double[] saturationValues;
    double[] valueValues;
    double blurRadius;

    //Holds all Targets found
    List<LynxTarget> targets = new ArrayList<>();
    LynxTarget target;

    //All output/input Mats
    Mat hsvThresholdOutput = new Mat();
    Mat blurOutput = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    //Mat to draw contours on
    Mat contoursOutput;

    public LynxPipeline(LynxConfig settings, LynxCameraServer frames, NetworkTableInstance instance){
        //This allows us to access the settings entered on the shuffleboard and output them to the camera server
        this.settings = settings;
        this.frames = frames;
        this.instance = instance;

        //Initialize NT entries
        targetArea = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("area");
        targetWidth = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("width");
        targetHeight = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("height");
        targetBoundingWidth = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("boundingWidth");
        targetBoundingHeight = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("boundingHeight");
        targetYaw = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("yaw");
        targetPitch = instance.getTable("Lynx Vision").getSubTable("TargetData").getEntry("pitch");

    }

    public void process(Mat frame){
        //Blur frame
        blurRadius = settings.blurAmount;
        blur(frame, blurRadius, blurOutput);


        //Use hsv settings from range sliders on shuffleboard to threshold frame
        hueValues = new double[]{settings.threshHoldSettings[0], settings.threshHoldSettings[1]};
        saturationValues = new double[]{settings.threshHoldSettings[2], settings.threshHoldSettings[3]};
        valueValues =  new double[]{settings.threshHoldSettings[4], settings.threshHoldSettings[5]};

        //Use the blur output, then perform the HSV thresholds
        hsvThreshold(blurOutput, hueValues, saturationValues, valueValues, hsvThresholdOutput);

        //Remove all previously stored contours
        targets.clear();

        //Find Contours after performing HSV thresholds
        findContours(hsvThresholdOutput, false, contours);

        //Add each contour to Target Array
        contours.forEach(contour -> targets.add( new LynxTarget(contour)));
        //Sort target array by area so largest contour is at the front of the array
        Collections.sort(targets, Comparator.comparing(LynxTarget::getArea).reversed());

        //drawContours(targets);

        //Output to camera server
        frames.addFrame("CameraFrame", frame);
        frames.addFrame("HSV Output", hsvThresholdOutput);
        frames.addFrame("Blur Output", blurOutput);
        frames.addFrame("Contour Output", contoursOutput);
    }



    /**
     * Softens an image using one of several filters.
     * @param input The image on which to perform the blur.
     * @param doubleRadius The radius for the blur.
     * @param output The image in which to store the output.
     */
    private void blur(Mat input, double doubleRadius, Mat output) {
        int radius = (int)(doubleRadius + 0.5);
        int kernelSize;
        kernelSize = 6 * radius + 1;
        Imgproc.GaussianBlur(input,output, new Size(kernelSize, kernelSize), radius);
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
    private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val, Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
        Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
                new Scalar(hue[1], sat[1], val[1]), out);
    }


    /**
     * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
     * @param input The image on which to perform the Distance Transform.
     * @param contours The image in which to store the output.
     */
    private void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {
        Mat hierarchy = new Mat();
        contours.clear();
        int mode;
        if (externalOnly) {
            mode = Imgproc.RETR_EXTERNAL;
        }
        else {
            mode = Imgproc.RETR_LIST;
        }
        int method = Imgproc.CHAIN_APPROX_SIMPLE;
        Imgproc.findContours(input, contours, hierarchy, mode, method);
    }

    public void drawContours(List<LynxTarget> targets){
        if(!targets.isEmpty()) {
            target = targets.get(0);
            //Draw contours onto Mat
            contoursOutput = hsvThresholdOutput;

            //Converts mat from grayscale to BGR so we can draw bounding and rotated rect in colour
            Imgproc.cvtColor(hsvThresholdOutput, contoursOutput, Imgproc.COLOR_GRAY2BGR);

            //Draws bounding rect
            Imgproc.rectangle(contoursOutput, target.boundingRect.tl(), target.boundingRect.br(), new Scalar(255, 0, 0), 3);

            //Draws rotated rect
            IntStream.range(0, target.vertices.length).forEach( point ->
                    Imgproc.line(contoursOutput, target.vertices[point], target.vertices[(point+1)%4], new Scalar(0,0,255))
            );

            //Draws center point
            Imgproc.circle(contoursOutput, target.centerPoint, 4, new Scalar(0, 255, 0) );

            //Publish target data
            publishTargetData(target);
        }
    }

    public void publishTargetData(LynxTarget target){
        targetArea.setDouble(target.area);

        targetWidth.setDouble(target.rRect.size.width);
        targetHeight.setDouble(target.rRect.size.height);

        targetBoundingWidth.setDouble(target.boundingRect.width);
        targetBoundingHeight.setDouble(target.boundingRect.height);

        targetYaw.setDouble(getYaw(target.coordinates[0], settings.imageWidth, settings.focalLength));
        targetPitch.setDouble(getPitch(target.coordinates[1], settings.imageHeight, settings.focalLength));
    }

    public double getYaw(double x, double width, double focalLength){
        return Math.atan(  (x - (width/2 - 0.5) )  / focalLength     );
    }

    public double getPitch(double y, double height, double focalLength){
        return Math.atan(  (y - (height/2 - 0.5) )  / focalLength     );
    }
}
