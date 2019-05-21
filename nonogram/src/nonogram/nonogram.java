package nonogram;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.robotics.SampleProvider;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;
import lejos.hardware.Sound;

import java.lang.String;

public class nonogram {
	   private static EV3ColorSensor color;
	   static TextLCD lcd;
	   static RegulatedMotor stampMotor = Motor.A;		// Moter A   forward:  Up stamp frame
														//           backward: Down stamp frame
	   static RegulatedMotor verticalMotor = Motor.B;   // Moter B   forward:  Take out sheet toward initial point
														//           backward: Take in sheet ``
	   static RegulatedMotor horizontalMotor = Motor.C;	// Moter C   backward:  move stamp frame toward vertical moter
														//           forward: move stamp frame counterwise
	   static int[][] map = {{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1}};
	   static int[][] map7 = {{1,1,1,1,1,1,1},{1,1,1,1,1,1,1},{1,1,1,1,1,1,1},{1,1,1,1,1,1,1},{1,1,1,1,1,1,1},{1,1,1,1,1,1,1},{1,1,1,1,1,1,1}};

	  	public static void main (String[] args)
	  	{
	        EV3 ev3 = (EV3) BrickFinder.getLocal();
	        Keys keys = ev3.getKeys();
	        color = new EV3ColorSensor(SensorPort.S4);
	        lcd = ev3.getTextLCD();

	        stampMotor.setSpeed(800);
	        stampMotor.rotate(200);
	        horizontalMotor.setSpeed(400);
	        makeNonogram(map7);

	  	}
	  	
	  	
	  	public static void stamp()
	  	{
	  		stampMotor.setSpeed(800);
	  		stampMotor.rotate (+200);
	        stampMotor.rotate(-400);
	        stampMotor.rotate(200);
	  	}
			
	  	public static void makeNonogram(int[][] list)
	  	{
	  		horizontalMotor.setSpeed(400);
	  		verticalMotor.rotate(150);
	  		
	  		//func that move vertical until starting point
	  		//stamping that list index is 1
	  		for(int i=0;i<7;i++){
		  		horizontalMotor.rotate(1000);//align left
		  		for(int j=0;j<7;j++){
		  			horizontalMotor.rotate(-100);//move horizontal
		  			if(list[i][j]==1){
		  				stamp();
		  			}
		  		}
		  		verticalMotor.rotate(100);//move vertical
	  		}
	  	}
	  	
//	  	public static void initialize ()
//	    {
//	      stampMoter.setSpeed(100); 	  		        	// * ARGUMENT MUST BE TESTED *
//	      verticalMoter.setSpeed(50); 				        // * ARGUMENT MUST BE TESTED *
//	      horizontalMoter.setSpeed(50); 			        // * ARGUMENT MUST BE TESTED *
//	      
//	      horizontalMoter.forward();
//	      horizontalMoter.rotate (720); 				    // * ARGUMENT MUST BE TESTED *
//	      horizontalMoter.rotate (-360); 			        // * ARGUMENT MUST BE TESTED *
//	      verticalMoter.backward();
//	      verticalMoter.move();
//
//	      while (color.getcolorID() != Color.BLACK);
//	      
//	      verticalMoter.stop();
//	      
//	      /* Following codes make stampMoter initialized.
//	       * For avoid making dirty in blanks of sheet, Sheet must be taken out a few, 
//	       * then check stampMoter and get back to original point. */
//	      verticalMoter.rotate (180);
//	      stampMoter.rotate (-180);
//	      stampMoter.rotate (90);
//	      verticalMoter.rotate (-180);
//
//	      Thread.sleep (1000);
//	    }

	   


}
