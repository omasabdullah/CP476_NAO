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
	private int m_uiTimer;
	
	Timer m_Timer = new Timer();
	
	
	
	//Nao Variables
	private static String NAOQI_IP = "nao.local";
	private static int NAOQI_PORT = 9559;
	
	SpeechHandler(Vector<String[]> receivedInput)
	{
		this.speechArray = receivedInput;
		m_uiSpeechStep = 0;
		m_uiTimer = 0;
	}
	
	private void scheduleTimer(int seconds)
	{
		long delay = seconds * 1000;
		m_Timer.schedule(new TimerTask() {
			public void run()
			{
			}
		}, delay);
	}
	
	public void startSpeech()
	{
		while (m_uiSpeechStep != speechArray.size())
		{
			
		}
	}
	
	public void say(String sayText)
	{
		ALTextToSpeechProxy tts = new ALTextToSpeechProxy(NAOQI_IP, NAOQI_PORT);
		tts.say(sayText);
	}
	
	public void stopSpeech()
	{
		m_uiSpeechStep = speechArray.size();
	}	
}
