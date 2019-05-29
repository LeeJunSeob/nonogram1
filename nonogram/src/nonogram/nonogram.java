package nonogram;

import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.utility.Delay;
import lejos.hardware.Sound;

import java.lang.String;

public class nonogram {
    //private static EV3ColorSensor color;
    static TextLCD lcd;
    static RegulatedMotor stampMotor = Motor.A;		    // Moter A   forward:  Up stamp frame
                                                        //           backward: Down stamp frame
    static RegulatedMotor verticalMotor = Motor.B;      // Moter B   forward:  Take out sheet toward initial point
                                                        //           backward: Take in sheet ``
    static RegulatedMotor horizontalMotor = Motor.C;	// Moter C   backward:  move stamp frame toward vertical moter
                                                        //           forward: move stamp frame counterwise
    static int[][] map5 = {{1,1,1,1,1},
                          {1,1,1,1,1},
                          {1,1,1,1,1},
                          {1,1,1,1,1},
                          {1,1,1,1,1}};

    static int[][] map7 = {{1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1},
                           {1,1,1,1,1,1,1}};

    static int[][] map10 = {{0,1,1,1,0,0,1,1,1,0},
                            {1,1,0,1,1,1,1,0,1,1},
                            {1,0,0,0,1,1,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,1},
                            {1,1,0,0,0,0,0,0,1,1},
                            {0,1,1,0,0,0,0,1,1,0},
                            {0,0,1,1,0,0,1,1,0,0},
                            {0,0,0,1,1,1,1,0,0,0},
                            {1,1,1,1,1,1,1,1,1,1},
                            {0,1,1,1,1,1,1,1,1,0}};

    public static void main (String[] args)
    {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        Keys keys = ev3.getKeys();
//        color = new EV3ColorSensor(SensorPort.S4);
        lcd = ev3.getTextLCD();
        
        int[][] usemap = map10;
        
        /*
        for (int i = 0; i < usemap.length; i++)
        	for (int j = 0; j < usemap[i].length; j++)
        		usemap[i][j] = (i + j) % 2;
        */
        initialize ();
        makeNonogram (usemap);
        end ();
    }
    
	public static void initialize ()
	{
		stampMotor.setSpeed(25);
		verticalMotor.setSpeed(100);
		horizontalMotor.setSpeed(400);

        //stampMotor.rotate(-10);
        
        verticalMotor.rotate(-200);
	}
	
	public static void end ()
	{
		horizontalMotor.rotate (-1200);
		horizontalMotor.rotate (600);
	}
	
    public static void stamp() throws InterruptedException
    {
        stampMotor.rotate (15);
        Thread.sleep(300);
        stampMotor.rotate (-15);
    }
    
    /* func that move vertical until starting point */
    public static void makeNonogram(int[][] list)
    {
        for(int i = 0; i < list.length; i++)
        {
            horizontalMotor.rotate(1200);//align left
            horizontalMotor.rotate(-140);
            
            int prev = 0;
            for(int j = list[i].length; j >= 0; j--)
            {

                if(list[i][j] == 1)
                {
                	horizontalMotor.rotate(-66 * (j - prev));
                    stamp();
                    prev = j;
                }
            }

            verticalMotor.rotate(-43);//move vertical
        }
    }
    
//    public static int[][] stringtoList(String recv){
//    	for(int i=0;i<10;i++){
//    		for(int j=0;j<10;j++){
//    			recv.charAt(10*i+j);
//    		}
//    	}
//    }
}
