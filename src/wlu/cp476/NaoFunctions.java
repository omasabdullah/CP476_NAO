package wlu.cp476;

import java.awt.image.BufferedImage;
import java.util.Scanner;

import com.aldebaran.proxy.ALMemoryProxy;
import com.aldebaran.proxy.ALMotionProxy;
import com.aldebaran.proxy.ALTextToSpeechProxy;
import com.aldebaran.proxy.ALVideoDeviceProxy;
import com.aldebaran.proxy.Variant;

public class NaoFunctions
{
	private String NAOQI_IP = "nao.local";
	private int NAOQI_PORT = 9559;
	
	static final int RESULT_OK = 0;
	static final int RESULT_FAILED = 1;
	
	int result;
	
	NaoFunctions(String ipAddress, int port)
	{
		this.NAOQI_IP = ipAddress;
		this.NAOQI_PORT = port;
	}

	// This is required so that we can use the 'Variant' object
	static
	{
		System.loadLibrary("jnaoqi");
	}
	
	@SuppressWarnings("unused")
	int MoveTo(float x, float y, float z)
	{
		ALMotionProxy motion = new ALMotionProxy(NAOQI_IP, NAOQI_PORT);
		result = RESULT_FAILED;
		
		if (motion == null)	System.out.println("Error Motion Proxy not found");
		else
		{
			//MOVEMENT
			motion.wakeUp();
			motion.moveInit();
			motion.moveTo(x, y, z);
			motion.rest();
			result = RESULT_OK;
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	int HeadTurn(float x, float y, float z)
	{
		ALMotionProxy motion = new ALMotionProxy(NAOQI_IP, NAOQI_PORT);
		result = RESULT_FAILED;
		
		if (motion == null)	System.out.println("Error: Motion Proxy not found");
		else
		{
			// This lets you use bound methods that excpects ALValue from Java:
			Variant names  = new Variant(new String[] {"HeadYaw" });
			Variant angles = new Variant(new float[] { -0.5f, 0.5f, 0.0f });
			Variant times  = new Variant(new float[] {	1.0f, 2.0f, 3.0f });
	
			motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {1.0f}));
			motion.angleInterpolation(names, angles, times, true);
			motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {0.0f}));
			result = RESULT_OK;
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	void OverrideSpeech()
	{
		ALTextToSpeechProxy tts = new ALTextToSpeechProxy(NAOQI_IP, NAOQI_PORT);
		
		if (tts == null) System.out.println("Error: Talk Proxy not found");
		else
		{	
			// Scan in input from user
	    	@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
	    	String inputLine = null;
	    	
	    	tts.say("Hello. Speech Mode Activated. Enter what you would like me to say.");
	    	System.out.println("Input speech: ");
	    	//Append each line to a vector, press enter (carriage return) once all lines entered
	    	while (!(inputLine = scanner.nextLine()).equals(""))
	    	{
	    		tts.say(inputLine);
	    	}
		}
	}
	
	@SuppressWarnings("unused")
	int Say(String text)
	{
		ALTextToSpeechProxy tts = new ALTextToSpeechProxy(NAOQI_IP, NAOQI_PORT);
		result = RESULT_FAILED;
		
		if (tts == null) System.out.println("Error: Talk Proxy not found");
		else
		{
	    	tts.say(text);
	    	result = RESULT_OK;
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	int StoreToMemory(String dataName, int data)
	{
		ALMemoryProxy memory = new ALMemoryProxy(NAOQI_IP, NAOQI_PORT);
		result = RESULT_FAILED;
		
		if (memory == null) System.out.println("Error: Memory Proxy not found");
		else
		{
			memory.insertData(dataName, data);
			Variant res = memory.getData("answer");
			System.out.format("The answer is %d\n", res.toInt());
			result = RESULT_OK;
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	int GetFromMemory(String dataName)
	{
		ALMemoryProxy memory = new ALMemoryProxy(NAOQI_IP, NAOQI_PORT);
		int data = -1;
		
		if (memory == null) System.out.println("Error: Memory Proxy not found");
		else
		{
			Variant res = memory.getData(dataName);
			data = res.toInt();
			System.out.format("The answer is %d\n", data);
			
		}
		
		return data;
	}
	
	@SuppressWarnings("unused")
	void TakePicture()
	{
		ALVideoDeviceProxy videoDevice = new ALVideoDeviceProxy(NAOQI_IP, NAOQI_PORT);
		
		if (videoDevice == null) System.out.println("Error: Video Proxy not found");
		else
		{
			BufferedImage img;
			videoDevice.subscribe("java", 1, 11, 250);
			Variant ret = videoDevice.getImageRemote("java");
			videoDevice.unsubscribe("java");

			// 	Video device documentation explain that image is element 6
			Variant imageV = ret.getElement(6);

			// 	display image from byte array
			byte[] binaryImage = imageV.toBinary();

			ShowImage myView = new ShowImage(binaryImage);
		}
	}
}
