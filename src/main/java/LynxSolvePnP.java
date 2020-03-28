import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Collections;

public class LynxSolvePnP {

    // Vectors from camera to center of target
    public Mat rvec;
    public Mat tvec;
    private  final Scalar BLUE = new Scalar(255, 0, 0),
            GREEN = new Scalar(0, 255, 0),

            MAGENTA = new Scalar(255, 0, 255);

    public  Mat getVectors(Mat frame, Mat cameraMatrix, MatOfDouble distCoeffs) {
            MatOfPoint2f target;

            Mat dst = new Mat();
            frame.copyTo(dst);
            // All camera intrinsics are in pixel values
            final double principalOffsetX = dst.width() / 2;
            final double principalOffsetY = dst.height() / 2;

            // 3D axes is same as 2D image axes, right is positive x, down is positive y,
            // foward is positive z (a clockwise axes system)
            // Points start with bottom left corner and run counter-clockwise
            // Bottom of the line as (0, 0, 0)
            Point3[] worldSpaceArr = new Point3[4];
            worldSpaceArr[0] = new Point3(-7.313, -0.5008, 0);
            worldSpaceArr[1] = new Point3(-5.9363, -5.826, 0);
            worldSpaceArr[2] = new Point3(5.9363, -5.826, 0);
            worldSpaceArr[3] = new Point3(7.313, -0.5008, 0);
            MatOfPoint3f worldSpacePts = new MatOfPoint3f(worldSpaceArr);

            rvec = new Mat();
            tvec = new Mat();
            target =  new MatOfPoint2f(new Point(1, 1), new Point(1, 6), new Point(-1, -6), new Point(-1 , -1));
            Calib3d.solvePnP(worldSpacePts, target, cameraMatrix, distCoeffs, rvec, tvec);

            // 3D box with the corners on the outside of the target
            Point3[] boxBottomWorldSpaceArr = new Point3[4];
            boxBottomWorldSpaceArr[0] = new Point3(-7.313, 0, 0);
            boxBottomWorldSpaceArr[1] = new Point3(-7.313, -5.826, 0);
            boxBottomWorldSpaceArr[2] = new Point3(7.313, -5.826, 0);
            boxBottomWorldSpaceArr[3] = new Point3(7.313, 0, 0);
            MatOfPoint3f boxBottomWorldSpacePts = new MatOfPoint3f(boxBottomWorldSpaceArr);
            MatOfPoint2f boxBottomImgPts = new MatOfPoint2f();
            Calib3d.projectPoints(boxBottomWorldSpacePts, rvec, tvec, cameraMatrix, distCoeffs, boxBottomImgPts);

            Point3[] boxTopWorldSpaceArr = new Point3[4];
            boxTopWorldSpaceArr[0] = new Point3(-7.313, 0, -3);
            boxTopWorldSpaceArr[1] = new Point3(-7.313, -5.826, -3);
            boxTopWorldSpaceArr[2] = new Point3(7.313, -5.826, -3);
            boxTopWorldSpaceArr[3] = new Point3(7.313, 0, -3);
            MatOfPoint3f boxTopWorldSpacePts = new MatOfPoint3f(boxTopWorldSpaceArr);
            MatOfPoint2f boxTopImgPts = new MatOfPoint2f();
            Calib3d.projectPoints(boxTopWorldSpacePts, rvec, tvec, cameraMatrix, distCoeffs, boxTopImgPts);

            return drawBox(boxBottomImgPts, boxTopImgPts, dst);
    }

    public  Mat drawBox(MatOfPoint2f imagePoints, MatOfPoint2f shiftedImagePoints, Mat dst) {
        Imgproc.drawContours(dst, Collections.singletonList(new MatOfPoint(imagePoints.toArray())), -1, GREEN, 2);

        for (int i = 0; i < imagePoints.rows(); i++) {
            Imgproc.line(dst, new Point(imagePoints.get(i, 0)), new Point(shiftedImagePoints.get(i, 0)), BLUE, 2);
        }

        Imgproc.drawContours(dst, Collections.singletonList(new MatOfPoint(shiftedImagePoints.toArray())), -1, MAGENTA, 2);
        return dst;
    }

}
