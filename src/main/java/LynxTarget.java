import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class LynxTarget {
    //Holds raw contour
    MatOfPoint target;

    //Holds bounding rect of contour
    Rect boundingRect;

    //Holds Area of contour
    double area;



    public LynxTarget(MatOfPoint target){
        this.target = target;
        this.boundingRect = Imgproc.boundingRect(target);
        this.area = Imgproc.contourArea(target);
    }

    //returns area of the target
    public double getArea(){
        return area;
    }

}
