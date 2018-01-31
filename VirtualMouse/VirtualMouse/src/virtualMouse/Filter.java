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
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Filter {
	
	//static integers to calculate movement of the mouse relative to the
	//size of the webcam window and the monitor resolution
	static int wFrame=320;
	static int hFrame=240;
	static int wMonitor=1280;
	static int hMonitor=800;
	public static int t;
	
	//constructor/filtering process for the program
	public static IplImage Filter(IplImage imgOriginal, IplImage imgHSV,IplImage imgBinary,
			CvScalar maxContour, CvScalar minContour, 
			CvSeq contour1,CvSeq contour2, CvMemStorage storage,CvMoments moments,
			int yellow,int red) throws AWTException{
		//moment10 is for the X order, moment01 is for the Y
		//areaMax will be the contour with the largest area
		//areaC is used to help determine the largest area
		double moment10, moment01, areaMax, areaC=0,m_area;
		
		//positions for X and Y coordinates
		int posX=0,posY=0;
		
		//Robot object to send commands to the mouse
		Robot rbt = new Robot();
		
		//contour process to remove the unwanted colors from the webcam feed
		cvCvtColor(imgOriginal,imgHSV,CV_BGR2HSV);
		cvInRangeS(imgHSV,minContour,maxContour,imgBinary);
		
		//since the image contains a lot of noise, a large value such
		//as 1000 was chosen as the areaMax
		areaMax= 1000;
	
		
		cvFindContours(imgBinary,storage,contour1,Loader.sizeof(CvContour.class),
						CV_RETR_LIST,CV_LINK_RUNS,cvPoint(0,0));
	
		//copy of contour1 is made so that it can be used to draw contours
		//and leave the entire contour1 unchanged
		contour2= contour1;
	
		
		//finding the max area to determine the object from background
		while(contour1 != null && !contour1.isNull())
		{
			areaC = cvContourArea(contour1,CV_WHOLE_SEQ,1);
		
			if(areaC>areaMax)
				areaMax = areaC;
		
			contour1 = contour1.h_next();
		
		}
	
		//drawing the contours
		while(contour2 !=null && !contour2.isNull())
		{
			areaC= cvContourArea(contour2,CV_WHOLE_SEQ,1);
		
			if(areaC<areaMax)
			{
				cvDrawContours(imgBinary,contour2,CV_RGB(0,0,0),CV_RGB(0,0,0),
						0,CV_FILLED,8,cvPoint(0,0));
			}
		
			contour2=contour2.h_next();
		}
	
		
				//calculating the moments so that they can be used
				//for obtaining the X and Y coordinates
				cvMoments(imgBinary, moments, 1);
				moment10 = cvGetSpatialMoment(moments, 1, 0);
				moment01 = cvGetSpatialMoment(moments, 0, 1);
				m_area = cvGetCentralMoment(moments, 0, 0);
				
				
				posX = (int) (moment10/m_area);
				posY = (int) (moment01/m_area);
				
				
				
				//if blue object is detected, move the mouse
				if(yellow==1)
					if(posX > 0 && posY > 0) 
					{
						rbt.mouseMove(posX*5, posY*5);
						
						//this is done to be able to access the edges of the
						//screen since below a certain value for X and Y
						//coordinates the area of the object on the webcam
						//is too small to be detected accurately
						if(posX <19){
							rbt.mouseMove((int)(posX/3), posY*5);
						}
						
						if(posY<19){
							rbt.mouseMove(posX*5, (int)(posY/3));
						}
//						if(posX > wFrame && posY > hFrame){
//							rbt.mouseMove((posX-(3*(posX-(wMonitor/2)))), (posY-(3*(posY-(hMonitor/2)))));
//						}
						//rbt.mouseMove((posX-(3*(posX-(wMonitor/2)))), (posY-(3*(posY-(hMonitor/2)))));
					}
				
				//if the green object is detected, the mouse does a left-click
				if(red==1){
					if(posX > 0 && posY > 0)
					
					{
							rbt.mousePress(InputEvent.BUTTON1_MASK);
							t++;
					}
					else if(t>0)
					{
						rbt.mouseRelease(InputEvent.BUTTON1_MASK);
						t=0;
					}
				}
				
				//NOTE: due to certain VERY TIME CONSUMING technical difficulties
				//we didn't have enough time to make this code perfect! Hopefully
				//we'll be more prepared for the next hackathon!
				
				
		
		
				return imgBinary;
		
	}
	
}
