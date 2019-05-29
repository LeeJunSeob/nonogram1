package bluetooth;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

public class bluetooth {
	public static void main(String args[]) throws IOException, InterruptedException, NotBoundException{
		
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		

		String serverAddress = "10.0.1.11";
		int serverPort = 8060;
		
		Socket socket = null;
		DataOutputStream streamOut = null;
		DataInputStream streamIn = null;
		try {
			lcd.clear();
			lcd.drawString("Waiting...", 1, 1);		
		
			socket = new Socket(serverAddress, serverPort);			
			lcd.clear();
			lcd.drawString("Connected", 1, 1);
			
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));		
			streamOut = new DataOutputStream(socket.getOutputStream());
			
		}catch(UnknownHostException uhe) {
			lcd.drawString("Host unknown: "+uhe.getMessage(), 1, 1);
		}
		
		String sendM = "";	//Send data
		String recvM = "";	//receive data
		int cnt = 0;
		while(keys.getButtons() != Keys.ID_ESCAPE) {
			try {
				recvM = streamIn.readUTF();
				for(int i=0;i<3;i++){
					for(int j=0;j<3;j++){
						//if(recvM.charAt(3*i+j) == '1'){
							lcd.drawChar(recvM.charAt(3*i+j), i+1, j+1);
						//}
						//else{
						//	lcd.drawString(".", i+1, j+1);
						//}
					}
				}
//				cnt += 1;
//				sendM = "test"+cnt;
//				streamOut.writeUTF(sendM);
//				streamOut.flush();	
//			
//				recvM = streamIn.readUTF();
//				lcd.drawString(recvM, 1, 3);
//							
//				Thread.sleep(1000);
			}catch(IOException ioe) {
				lcd.drawString("SE:"+ioe.getMessage(), 4, 4);	
			}
		}
				
		//release resources
		if (socket != null) socket.close();
		if (streamOut != null) streamOut.close();
		if (streamIn != null) streamIn.close();
	}
}
