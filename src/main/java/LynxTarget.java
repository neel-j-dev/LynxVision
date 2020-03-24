import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class LynxTarget {
    //Holds raw contour
    MatOfPoint target;

    //Holds bounding rect of contour
    Rect boundingRect;

    //Holds minAreaRect of target
    RotatedRect rRect;
    //RotatedRect vertices
    Point[] vertices = new Point[4];

    //Target properties
    double area;




    public LynxTarget(MatOfPoint target){
        this.target = target;

        this.boundingRect = Imgproc.boundingRect(target);

        this.area = Imgproc.contourArea(target);

        this.rRect = Imgproc.minAreaRect( new MatOfPoint2f(target.toArray()));
        this.rRect.points(this.vertices);
    }

    //returns area of the target
    public double getArea(){
        return area;
    }

}
