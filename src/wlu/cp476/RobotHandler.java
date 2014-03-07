package wlu.cp476;

import java.sql.*;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import ch.aplu.xboxcontroller.*;
import com.aldebaran.proxy.*;

public class RobotHandler
{	
	static final String NAO_IP = "169.254.172.97";
	static final int NAO_PORT = 9559;
	// Robot Types
	public enum SimulationType
	{
		TYPE_NONE,
		TYPE_GAME_MAZE
	}
	
	// State Codes
	public enum StateCode
	{
		// Default OpCodes
		IDLE,
		
		// Wait Opcodes
		WAIT_IMAGE,
		WAIT_DATABASE,
		WAIT_SPEAKING,
		WAIT_GESTURE,
		
		// Action OpCodes
		ACTION_SPEAK,
		ACTION_FORWARD,
		ACTION_STOP,
		ACTION_TURN,
		ACTION_GESTURE,
	}
	
	/*
	 * Default Variables
	 */
	
	// Current State (See StateCode Enumeration)
	private StateCode m_uiState;
	// Initialize Simulation boolean
	private boolean m_bSimulationRunning;
	// Simulation Type to run
	private SimulationType m_uiSimulationType;
	
	/*
	 * Speech Handling
	 */
	
	// Array of strings to be recited: [roomid, speech_id, delay, text, gestures]
	private Vector<String[]> m_vSpeechArray = new Vector<String[]>();
	// Boolean to check if Robot is in Speech
	private boolean m_bIsSpeaking;
	// Speech step when speaking dialogue
	private int m_uiSpeechStep;
	
	
	/*
	 * DEFAULT ROBOT FUNCTIONS
	 */
	
	ALMotionProxy naoMotion;
	ALTextToSpeechProxy naoSpeech;
	ALMemoryProxy naoMemory;
	ALVideoDeviceProxy naoVideo;
	
	
	NaoFunctions myNao = new NaoFunctions();
	
	RobotHandler()
	{
		Initialize();
	}
	
	static
	{
		System.loadLibrary("jnaoqi");
	}
	
	public void Initialize()
	{
		m_uiSpeechStep = 0;
		m_uiState = StateCode.IDLE;
		m_uiSimulationType = SimulationType.TYPE_NONE;
		m_bSimulationRunning = false;
		m_bIsSpeaking = false;
		
		naoMotion = new ALMotionProxy(NAO_IP, NAO_PORT);
		naoSpeech = new ALTextToSpeechProxy(NAO_IP, NAO_PORT);
		naoMemory = new ALMemoryProxy(NAO_IP, NAO_PORT);
		naoVideo = new ALVideoDeviceProxy(NAO_IP, NAO_PORT);

	}
	public StateCode getState() {return m_uiState;}
	public boolean isSimulationStarted() {return m_bSimulationRunning;}
	public void stopSimulation()
	{
		//Cancel simulation
		m_bSimulationRunning = false;
	}
	public void DBConnect(int RoomNumber)
	{
		String url = "jdbc:mysql://hopper.wlu.ca:3306/";
        String dbName = "naodb";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "nao";
        String password = "nao";
        
        try
        {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url+dbName,userName,password);
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM  waypoints WHERE room_id = " + RoomNumber);

            while (res.next())
            {
            	int RoomID = res.getInt("room_id");
            	int Speech_ID = res.getInt("speech_id");
            	int Delay = res.getInt("delay");
            	String Text = res.getString("text");
            	
            	String[] newEntry = new String[4];
            	newEntry[0] = Integer.toString(RoomID);
            	newEntry[1] = Integer.toString(Speech_ID);
            	newEntry[2] = Integer.toString(Delay);
            	newEntry[3] = Text;
            	
            	System.out.println("RoomID: " + RoomID);
            	System.out.println("Speech_ID: " + Speech_ID);
            	System.out.println("Delay: " + Delay);
            	System.out.println("Text: " + Text);
            	m_vSpeechArray.add(newEntry);
            	
            	System.out.println("Successfully fetched " + Integer.toString(m_vSpeechArray.size()) + " entries to waypoint");
            	System.out.println();
            }
            
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
    	}
	}
	public void startSimulation()
	{
		//Initialize simulation
		m_bSimulationRunning = true;
		
		//Begin the simulation
		while (m_bSimulationRunning)
		{
			switch (m_uiState)
			{
				case IDLE:
					break;
				case WAIT_IMAGE:
					//Image Recognition Here
					break;
				case WAIT_DATABASE:
					//Cross reference image of faceplate with database
					break;
				case WAIT_SPEAKING:
					if (m_bIsSpeaking)
						return;
					break;
				case ACTION_SPEAK:
					m_bIsSpeaking = true;
					break;
				case ACTION_FORWARD:
					break;
				case ACTION_STOP:
					break;
				case ACTION_TURN:
					break;
				default:
					break;
			}
		}
	}

	
	/*
	 * SPEECH ROBOT FUNCTIONS
	 */
	
	//Call after loading string[] vector to initiate speech
	public void startSpeech() throws InterruptedException
	{
		while (m_uiSpeechStep < m_vSpeechArray.size())
		{
			int delay = Integer.parseInt(m_vSpeechArray.get(m_uiSpeechStep)[2]);
			System.out.println("Current step is: " + m_uiSpeechStep);
			//myNaoFunctions.Say(speechArray.get(m_uiSpeechStep)[3]);
			System.out.println(m_vSpeechArray.get(m_uiSpeechStep)[3]);
			System.out.println("Setting delay to: " + delay + " seconds");
			System.out.println();
			TimeUnit.MILLISECONDS.sleep(delay);
			m_uiSpeechStep++;
		}
		
		System.out.println("Speech Complete");
		m_uiSpeechStep = 0;
	}
	// Manual override to control speech
	public void overrideSpeech() {myNao.ManualSpeechOverride(naoSpeech);}
	// Manual override to skip speech
	public void stopSpeech() {m_uiSpeechStep = m_vSpeechArray.size();}
	// Check for completion of Speech Array
	public boolean isCompletedSpeech() {return (m_uiSpeechStep == m_vSpeechArray.size());}
	
	/*
	 * MOVEMENT ROBOT FUNCTIONS
	 */
	
	public void overrideMovement() {myNao.ManualMovementOverride(naoMotion, naoSpeech);}
	
	
}
