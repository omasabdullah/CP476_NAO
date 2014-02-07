package wlu.cp476;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class SpeechHandler
{
	// The array should be structured in following way
	// [roomid, speech_id, delay, text, gestures]
	private Vector<String[]> speechArray = new Vector<String[]>();
	private int m_uiSpeechStep;
	
	//Nao Variables
	private static String NAOQI_IP = "nao.local";
	private static int NAOQI_PORT = 9559;
	NaoFunctions myNaoFunctions = new NaoFunctions(NAOQI_IP, NAOQI_PORT);
	
	SpeechHandler(Vector<String[]> receivedInput)
	{
		this.speechArray = receivedInput;
		m_uiSpeechStep = 0;
	}
	
	// Call after loading string[] vector to initiate speech
	public void startSpeech() throws InterruptedException
	{
		while (m_uiSpeechStep < speechArray.size())
		{
			int delay = Integer.parseInt(speechArray.get(m_uiSpeechStep)[2]);
			System.out.println("Current step is: " + m_uiSpeechStep);
			//myNaoFunctions.Say(speechArray.get(m_uiSpeechStep)[3]);
			System.out.println(speechArray.get(m_uiSpeechStep)[3]);
			System.out.println("Setting delay to: " + delay + " seconds");
			System.out.println();
			TimeUnit.MILLISECONDS.sleep(delay);
			m_uiSpeechStep++;
		}
		
		System.out.println("Speech Complete");
	}
	
	// Manual override to skip speech
	public void stopSpeech() {m_uiSpeechStep = speechArray.size();}
	
	// Check for completion of Speech Array
	public boolean isCompletedSpeech() {return (m_uiSpeechStep == speechArray.size());}
}
