package wlu.cp476;

/*
 * 
Make the robot move its head
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import com.aldebaran.proxy.ALMotionProxy;
import com.aldebaran.proxy.ALTextToSpeechProxy;
import com.aldebaran.proxy.ALVideoDeviceProxy;
import com.aldebaran.proxy.Variant;

public class MoveHead {
	private static String NAOQI_IP = "192.168.1.118";
	private static int NAOQI_PORT = 9559;
	private BufferedImage img;

	// This is required so that we can use the 'Variant' object
	static {
	System.loadLibrary("jnaoqi");
	}

	public static void main(String[] args) {
		System.out.println("Test");
		ALMotionProxy motion = new ALMotionProxy(NAOQI_IP, NAOQI_PORT);
		ALTextToSpeechProxy tts = new ALTextToSpeechProxy(NAOQI_IP, NAOQI_PORT);
		ALVideoDeviceProxy videoDevice = new ALVideoDeviceProxy(NAOQI_IP, NAOQI_PORT);

		videoDevice.subscribe("java", 1, 11, 250);
		Variant ret = videoDevice.getImageRemote("java");
		videoDevice.unsubscribe("java");

		// Video device documentation explain that image is element 6
		Variant imageV = ret.getElement(6);

		// display image from byte array
		byte[] binaryImage = imageV.toBinary();

	    tts.say("Hello");
	    ShowImage myView = new ShowImage(binaryImage);
		
		if (motion == null)
			System.out.println("Error");
		else
			System.out.println("Found Nao");
			
		// This lets you use bound methods that excpects ALValue from Java:
		Variant names  = new Variant(new String[] {"HeadYaw" });
		Variant angles = new Variant(new float[] { -0.5f, 0.5f, 0.0f });
		Variant times  = new Variant(new float[] {	1.0f, 2.0f, 3.0f });

		motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {1.0f}));
		motion.angleInterpolation(names, angles, times, true);
		motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {0.0f}));
		
		// Scan in input from user
    	Scanner scanner = new Scanner(System.in);
    	String inputLine = null;
    	
    	System.out.println("Input speech: ");
    	//Append each line to a vector, press enter (carriage return) once all lines entered
    	while (!(inputLine = scanner.nextLine()).equals(""))
    	{
    		tts.say(inputLine);
    	}
    	
    	tts.say("Peace out brother");
	}
}
