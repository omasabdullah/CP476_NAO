package wlu.cp476;

import java.util.Scanner;

import com.aldebaran.proxy.*;

public class NaoFunctions
{	
	static final int RESULT_OK = 0;
	static final int RESULT_FAILED = 1;
	
	int result;
	
	NaoFunctions() {}

	// This is required so that we can use the 'Variant' object
	static
	{
		System.loadLibrary("jnaoqi");
	}
	
	int MoveTo(ALMotionProxy motion, float x, float y, float theta)
	{
		result = RESULT_FAILED;
		
		if (motion == null)	System.out.println("Error Motion Proxy not found");
		else
		{
			//MOVEMENT
			motion.wakeUp();
			motion.moveInit();
			motion.moveTo(x, y, theta);
			//motion.rest();
			result = RESULT_OK;
		}
		
		return result;
	}
	
	int HeadMovement(ALMotionProxy motion, float headYaw, float headPitch)
	{
		result = RESULT_FAILED;
		
		if (motion == null)	System.out.println("Error: Motion Proxy not found");
		else
		{
			Variant yawName  = new Variant(new String[] {"HeadYaw" });
			Variant yawAngle = new Variant(new float[] { headYaw });
			Variant yawTime  = new Variant(new float[] {	0.3f });
			
			Variant pitchName  = new Variant(new String[] {"HeadPitch" });
			Variant pitchAngle = new Variant(new float[] { -headPitch });
			Variant pitchTime  = new Variant(new float[] {	0.3f });
	
			motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {1.0f}));
			motion.setStiffnesses(new Variant(new String[] {"HeadPitch"}), new Variant(new float[] {1.0f}));
			
			motion.angleInterpolation(yawName, yawAngle, yawTime, true);
			motion.angleInterpolation(pitchName, pitchAngle, pitchTime, true);
			
			motion.setStiffnesses(new Variant(new String[] {"HeadYaw"}), new Variant(new float[] {0.0f}));
			motion.setStiffnesses(new Variant(new String[] {"HeadPitch"}), new Variant(new float[] {0.0f}));
			result = RESULT_OK;
		}
		
		return result;
	}
	
	int rightShoulderMovement(ALMotionProxy motion, float rightShoulderPitch, float rightShoulderRoll)
	{
		result = RESULT_FAILED;
		
		if (motion == null)	System.out.println("Error: Motion Proxy not found");
		else
		{
			Variant pitchName  = new Variant(new String[] {"RShoulderPitch" , "RShoulderRoll"});
			Variant pitchAngle = new Variant(new float[] { rightShoulderPitch, rightShoulderRoll });
			Variant pitchTime  = new Variant(new float[] {	1.0f, 1.0f });
	
			motion.setStiffnesses(new Variant(new String[] {"RShoulderPitch"}), new Variant(new float[] {1.0f}));
			motion.setStiffnesses(new Variant(new String[] {"RShoulderRoll"}), new Variant(new float[] {1.0f}));
			
			motion.angleInterpolation(pitchName, pitchAngle, pitchTime, true);
			
			motion.setStiffnesses(new Variant(new String[] {"RShoulderPitch"}), new Variant(new float[] {1.0f}));
			motion.setStiffnesses(new Variant(new String[] {"RShoulderRoll"}), new Variant(new float[] {1.0f}));
			result = RESULT_OK;
		}
		
		return result;
	}
	
	void ManualMovementOverride(ALMotionProxy motion, ALTextToSpeechProxy tts)
	{
		if (motion == null)	System.out.println("Error Motion Proxy not found");
		else
		{
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
	    	String inputLine = null;
	    	float 	x = 0.0f,
	    			y = 0.0f,
	    			z = 0.0f;
	    	
	    	if (tts != null)
	    		tts.say("Hello. Manual Movement Mode Activated. Enter coordinates");
	    	
	    	motion.wakeUp();
	    	
	    	//Append each line to a vector, press enter (carriage return) once all lines entered
	    	while (!(inputLine = scanner.nextLine()).equals(""))
	    	{
	    		// This command will 
	    		String[] inputParse = inputLine.split(" ");
	    		x = Float.parseFloat(inputParse[0]);
	    		y = Float.parseFloat(inputParse[1]);
	    		z = Float.parseFloat(inputParse[2]);
	    		
	    		//MOVEMENT
				motion.moveInit();
				motion.moveTo(x, y, z);
	    	}
	    	
	    	motion.rest();
		}
	}
	
	void ManualSpeechOverride(ALTextToSpeechProxy tts)
	{
		if (tts == null) System.out.println("Error: Talk Proxy not found");
		else
		{	
			// Scan in input from user
	    	@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
	    	String inputLine = null;
	    	
	    	tts.say("Hello");
	    	tts.setVolume(1.0f);
	    	System.out.println(tts.getVoice());
	    	System.out.println("Input speech: ");
	    	//Append each line to a vector, press enter (carriage return) once all lines entered
	    	while (!(inputLine = scanner.nextLine()).equals(""))
	    	{
	    		tts.say(inputLine);
	    	}
		}
	}
	
	int Say(ALTextToSpeechProxy tts, String text)
	{
		result = RESULT_FAILED;
		
		if (tts == null) System.out.println("Error: Speech Proxy not found");
		else
		{
	    	tts.say(text);
	    	result = RESULT_OK;
		}
		
		return result;
	}
	
	int StoreToMemory(ALMemoryProxy memory, String dataName, int data)
	{
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
	
	int GetFromMemory(ALMemoryProxy memory, String dataName)
	{
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
	
	byte[] TakePicture(ALVideoDeviceProxy videoDevice)
	{
		byte[] binaryImage = null;
		
		if (videoDevice == null) System.out.println("Error: Video Proxy not found");
		else
		{
			//BufferedImage img;
			videoDevice.subscribe("java", 2, 11, 250);
			Variant ret = videoDevice.getImageRemote("java");
			videoDevice.unsubscribe("java");

			// 	Video device documentation explain that image is element 6
			Variant imageV = ret.getElement(6);

			// 	display image from byte array
			binaryImage = imageV.toBinary();
		}
		
		return binaryImage;
	}
}
