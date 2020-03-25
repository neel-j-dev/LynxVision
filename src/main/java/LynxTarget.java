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

    //center point of coordinates
    double[] coordinates;
    Point centerPoint;

    //Target properties
    double area;

    public LynxTarget(MatOfPoint target){

        this.target = target;

        this.boundingRect = Imgproc.boundingRect(target);

        this.area = Imgproc.contourArea(target);

        this.rRect = Imgproc.minAreaRect( new MatOfPoint2f(target.toArray()));
        this.rRect.points(this.vertices);

        this.coordinates = new double[]{this.boundingRect.x + 0.5 * this.boundingRect.width, this.boundingRect.y + 0.5 * this.boundingRect.height};
        this.centerPoint = new Point(this.coordinates[0], this.coordinates[1]);

    }

    //returns area of the target
    public double getArea(){
        return area;
    }

}
