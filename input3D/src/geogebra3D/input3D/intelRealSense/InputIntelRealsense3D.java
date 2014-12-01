package geogebra3D.input3D.intelRealSense;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian3D.Input3D;
import geogebra.common.euclidian3D.Input3D.DeviceType;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.main.App;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class InputIntelRealsense3D implements Input3D {

	
	private Socket socket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double screenHalfWidth;
	
	
	private double[] glassesPosition;
	
	private double eyeSeparation;
	
	/**
	 * constructor
	 */
	public InputIntelRealsense3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// glasses position
		glassesPosition = new double[3];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		socket = new Socket();
	}
	
	
	public boolean update(){
	
		boolean updateOccured = false;
		
		// update from last message
		if (socket.gotMessage){
			
			// mouse position
			mousePosition[0] = socket.birdX * screenHalfWidth;
			mousePosition[1] = socket.birdY * screenHalfWidth;
			mousePosition[2] = socket.birdZ * screenHalfWidth;
			
			/*
			App.debug("\norientation"
			+"\nx="+leoSocket.birdOrientationX
			+"\ny="+leoSocket.birdOrientationY
			+"\nz="+leoSocket.birdOrientationZ
			+"\nw="+leoSocket.birdOrientationW
			+"\nagnle="+(2*Math.acos(leoSocket.birdOrientationW)*180/Math.PI)+"�");
			*/
			
			// mouse position
			mouseOrientation[0] = socket.birdOrientationX;
			mouseOrientation[1] = socket.birdOrientationY;
			mouseOrientation[2] = socket.birdOrientationZ;
			mouseOrientation[3] = socket.birdOrientationW;

			
			// right button
			isRightPressed = socket.rightButton;
			
			// left button
			isLeftPressed = socket.leftButton;
			
			
			
			// eye separation
			eyeSeparation = (socket.leftEyeX - socket.rightEyeX) * screenHalfWidth;

			// glasses position
			glassesPosition[0] = socket.leftEyeX * screenHalfWidth + eyeSeparation/2;
			glassesPosition[1] = socket.leftEyeY * screenHalfWidth;
			glassesPosition[2] = socket.leftEyeZ * screenHalfWidth;

			/*
			App.debug("\nleft eye"
					+"\nx="+leftEyePosition[0]
					+"\ny="+leftEyePosition[1]
					+"\nz="+leftEyePosition[2]
				    +
					"\nright eye"
					+"\nx="+rightEyePosition[0]
					+"\ny="+rightEyePosition[1]
					+"\nz="+rightEyePosition[2]);
					
			App.debug("\nleft-right="+(rightEyePosition[0]-leftEyePosition[0])+"\nheight="+GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
			*/
			
			/*
			App.debug("\nbuttons"
					+"\nbig = "+leoSocket.bigButton
					+"\nright = "+isRightPressed
					);
					*/
			
			updateOccured = true;
		}
		
		// request next message
		socket.getData();		
		
		return updateOccured;
		
	}
	
	public double[] getMouse3DPosition(){
		return mousePosition;
	}
	
	public double[] getMouse3DOrientation(){
		return mouseOrientation;
	}
	
	public boolean isRightPressed(){
		return isRightPressed;
	}
	
	public boolean isLeftPressed(){
		return isLeftPressed;
	}

	public double[] getGlassesPosition(){
		return glassesPosition;
	}
	
	public double getEyeSeparation(){
		return eyeSeparation;
	}
	
	public boolean useInputDepthForHitting(){
		return false;
	}
	
	public boolean useMouseRobot(){
		return false;
	}


//	@Override
//	public float getMouse2DX(){
//		return socket.hand2Dx;
//	}
//	
//	@Override
//	public float getMouse2DY(){
//		return socket.hand2Dy;
//	}
//	
//	@Override
//	public float getMouse2DFactor(){
//		return socket.hand2Dfactor;
//	}
	
	public DeviceType getDeviceType(){
		return DeviceType.HAND;
	}
	
	public boolean hasMouse(EuclidianView3D view3D){
		return socket.hasTrackedHand();
	}
	
	public boolean currentlyUseMouse2D(){
		return !socket.hasTrackedHand();
	}
	
	public void setLeftButtonPressed(boolean flag){
		socket.setLeftButtonPressed(flag);
	}
	
	public boolean getLeftButton(){
		return socket.leftButton;
	}
	
}
