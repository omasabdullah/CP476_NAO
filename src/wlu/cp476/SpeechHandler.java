package wlu.cp476;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.aldebaran.proxy.ALTextToSpeechProxy;

public class SpeechHandler
{
	// The array should be structured in following way
	// [roomid, speech_id, delay, text, gestures]
	private Vector<String[]> speechArray = new Vector<String[]>();
	private int m_uiSpeechStep;
	Timer m_Timer = new Timer();
	int interval;
	boolean m_bIsSpeaking;
	
	//Nao Variables
	private static String NAOQI_IP = "nao.local";
	private static int NAOQI_PORT = 9559;
	
	SpeechHandler(Vector<String[]> receivedInput)
	{
		this.speechArray = receivedInput;
		m_uiSpeechStep = 0;
		m_bIsSpeaking = false;
	}
	
	// Call after loading string[] vector to initiate speech
	public void startSpeech()
	{
		m_bIsSpeaking = true;

		while (m_uiSpeechStep <= speechArray.size() && !m_bIsSpeaking)
		{
			ExecuteSpeech();
		}
	}
	
	private void ExecuteSpeech()
	{
		String sayText = speechArray.get(m_uiSpeechStep)[3];
		interval = Integer.parseInt(speechArray.get(m_uiSpeechStep)[2]);
		
		ALTextToSpeechProxy tts = new ALTextToSpeechProxy(NAOQI_IP, NAOQI_PORT);
		tts.say(sayText);

		m_Timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				System.out.println(setInterval());
			}
		}, 0, 1000);
	}
	
	// Counter timer
	private int setInterval()
	{
		if (interval <= 1)
		{
			m_uiSpeechStep++;
			m_bIsSpeaking = false;
			
			m_Timer.cancel();
		}
		return --interval;
	}
	
	// Manual override to skip speech
	public void stopSpeech()
	{
		m_uiSpeechStep = speechArray.size();
	}
	
	// Check for completion of Speech Array
	public boolean isCompletedSpeech() {return (m_uiSpeechStep == speechArray.size()+1);}
}
