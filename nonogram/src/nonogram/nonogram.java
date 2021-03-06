package nonogram;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

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

    static String serverAddress = "10.0.1.11";
    static int serverPort = 8060;

    static Socket socket = null;
    static DataOutputStream streamOut = null;
    static DataInputStream streamIn = null;
    
    static int size = -1;

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

    static int[][] map15 = {{0,0,0,0,1,1,1,1,1,0,1,1,0,0,0},
					        {0,0,0,1,1,1,1,1,0,1,1,1,0,1,0},
					        {0,0,1,1,1,0,0,0,1,1,1,1,0,1,0},
					        {0,1,1,1,0,0,0,1,1,1,1,1,0,1,1},
					        {0,1,1,0,0,0,0,1,1,0,1,1,0,1,1},
					        {1,1,1,0,0,0,1,1,1,0,1,1,0,1,1},
					        {1,1,0,0,0,0,1,1,0,0,1,1,0,0,1},
					        {1,1,0,0,0,1,1,1,0,0,0,1,0,0,1},
					        {1,1,0,0,0,1,1,1,1,1,1,0,0,1,1},
					        {1,1,1,0,1,1,1,1,1,1,1,1,0,1,1},
					        {0,1,1,0,1,1,0,0,0,0,1,0,0,1,1},
					        {0,1,1,1,1,1,0,0,0,0,0,1,1,1,0},
					        {0,0,1,1,1,0,0,0,0,0,1,1,1,0,0},
					        {0,0,1,1,1,1,1,1,1,1,1,1,0,0,0},
					        {0,0,0,0,1,1,1,1,1,1,1,0,0,0,0}};

    public static void main (String[] args) throws InterruptedException
    {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        Keys keys = ev3.getKeys();
//        color = new EV3ColorSensor(SensorPort.S4);
        lcd = ev3.getTextLCD();

        int[][] usemap = get_data (true);
        size = usemap[0].length;

        /*
        for (int i = 0; i < usemap.length; i++)
        	for (int j = 0; j < usemap[i].length; j++)
        		usemap[i][j] = (i + j) % 2;
        */
        initialize ();
        makeNonogram (usemap, size);
        end ();
    }

    public static int[][] get_data (boolean test)
    {
        if (test)
            return map15;

        try {
            lcd.clear();
            lcd.drawString("Waiting...", 1, 1);
            socket = new Socket(serverAddress, serverPort);

            lcd.clear();
            lcd.drawString ("Connected", 1, 1);

            streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(socket.getOutputStream());
        }
        catch (UnknownHostException uhe) {
            lcd.clear();
        	lcd.drawString("Host unknown: "+uhe.getMessage(), 1, 1);
        } catch (IOException ioe) {
            lcd.clear();
        	lcd.drawString("ioe:", 1, 1);
		}

        String sendM = "";	//Send data
        String recvM = "";	//receive data
        int map[][] = new int[20][20];
        boolean flag = true;

        try {
            recvM = streamIn.readUTF();
            sendM = "EV3 Got Data: " + recvM;
            streamOut.writeUTF(sendM);
            streamOut.flush();

            lcd.clear();
            lcd.drawString ("Connected", 1, 1);
            
            size = Integer.parseInt(recvM);

            lcd.clear();
            for (int i = 0; i < size; i++)
            {
                recvM = streamIn.readUTF();
                for (int j = 0; j < size; j++)
                {
                    if (recvM.charAt(j) == '#')
                        map[i][j] = 1;
                    else if (recvM.charAt(j) == '_')
                        map[i][j] = 0;
                    else
                        map[i][j] = -1;
                }
                lcd.drawString (recvM, 1, i*2);                
            }
            flag = false;
        }
        catch (IOException ioe) {
            lcd.clear();
            lcd.drawString ("SE:\n", 4, 4);
        }
        return map;
    }


    public static void initialize ()
    {
        stampMotor.setSpeed(25);
        verticalMotor.setSpeed(100);
        horizontalMotor.setSpeed(400);
        
        stampMotor.rotate (40);
        verticalMotor.rotate(-740);
	}

	public static void end ()
	{
		horizontalMotor.rotate (-1000);
		horizontalMotor.rotate (600);
	}

    public static void stamp() throws InterruptedException
    {
        stampMotor.rotate (6);
        Thread.sleep(300);
        stampMotor.rotate (-6);
    }

    /* func that move vertical until starting point */
    public static void makeNonogram(int[][] list, int size) throws InterruptedException
    {
        lcd.clear();
//      lcd.drawString ("Make size: " + Integer.toString(size), 1, 1);
        for(int i = 0; i < size; i++)
        {
            horizontalMotor.rotate(-1200);//align left
            horizontalMotor.rotate(100);
//            horizontalMotor.rotate(-30);

            int prev = 0;
            for(int j = 0; j < size; j++)
            {

                if(list[i][j] == 1)
                {
                	horizontalMotor.rotate(+65 * (j - prev));
                    stamp();
                    prev = j;
                }
            }

            verticalMotor.rotate(-46);//move vertical
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
