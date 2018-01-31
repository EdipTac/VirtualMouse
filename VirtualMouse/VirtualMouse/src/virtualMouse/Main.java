package virtualMouse;

/***************************************************************************************************
 * Authors: Akshat Bisht and Edip Tac
 * Version: 1.8
 * 
 * Acknowledgments: 
 * We taught ourselves to code in openCV using tutorials provided by
 * Manoj Manjunatha of EngineerVisions, along with several different
 * open source tutorials.
 */
import java.awt.AWTException;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
public class Main {

	public static void main(String[] args) throws AWTException {
		
		IplImage imgOriginal,imgBinaryRed, imgBinaryYellow;
		IplImage imgHSV;
		
		CvScalar yellowMinContour = cvScalar(20,100,100,0), yellowMaxContour = cvScalar(30,255,255,0);
		CvScalar redMinContour = cvScalar(160,150,75,0), redMaxContour = cvScalar(180,255,255,0);
		
		CvArr mask;
		int w=320,h=240;
		imgHSV = cvCreateImage(cvSize(w,h),8,3);
		imgBinaryRed = cvCreateImage(cvSize(w,h),8,1);
		imgBinaryYellow = cvCreateImage(cvSize(w,h),8,1);
		IplImage imgC = cvCreateImage(cvSize(w,h),8,1);
		//contour1 is for assessing contours and contour2 is to act
		//as a storage for the data so that it can be analyzed
		//in a second loop to remove the colors
		CvSeq contour1 = new CvSeq(), contour2=null;
		CvMemStorage storage = CvMemStorage.create();
		CvMoments moments = new CvMoments(Loader.sizeof(CvMoments.class));
		
		//capture the default webcam of the device
		CvCapture capture = cvCreateCameraCapture(CV_CAP_ANY);
		cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_WIDTH,w);
		cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_HEIGHT,h);
		
		//infinite loop to maintain the animation until exit key is pressed
		while(true)
		{
			
			//Insert the webcam into the frame to display content
			imgOriginal = cvQueryFrame(capture);
			//mirror flip the webcam so that tracking hand gestures
			//allows mouse to move towards the direction of hand
			//movement instead of going the opposite direction
			cvFlip(imgOriginal,imgOriginal,1);
			
			//if the webcam was not detected terminate the infinite loop
			if(imgOriginal == null){
				System.err.println("No Web-cam could be accessed");
				break;
				}
				
			//pass the images into the filter class for contour filtering
			imgBinaryYellow = Filter.Filter(imgOriginal,imgHSV,imgBinaryYellow,yellowMaxContour, yellowMinContour, contour1, contour2, storage,moments,1,0);
			imgBinaryRed = Filter.Filter(imgOriginal,imgHSV,imgBinaryRed,redMaxContour, redMinContour, contour1, contour2, storage,moments,0,1);
					
			
			cvOr(imgBinaryYellow,imgBinaryRed,imgC,mask=null);
			cvShowImage("Contour Filtered Display",imgC);	
			cvShowImage("Default Webcam",imgOriginal);
			char c = (char)cvWaitKey(15);
			//escape key terminates the loop
			if(c==27) break;
					
		}
		//de-allocating the memory
		cvReleaseImage(imgHSV);
		cvReleaseImage(imgBinaryRed);
		cvReleaseImage(imgBinaryYellow);
		cvReleaseImage(imgHSV);
		cvReleaseMemStorage(storage);
		cvReleaseCapture(capture);
				
	}

}

