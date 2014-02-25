package wlu.cp476;

import java.sql.*;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class RobotHandler
{	
	/*
	 * Default Variables
	 */
	
	// Robot Type (See RobotType Enumeration)
	private RobotEnum.RobotType m_uiRobotType;
	// Current State (See StateCode Enumeration)
	private RobotEnum.StateCode m_uiState;
	// Initialize Simulation boolean
	private boolean m_bSimulationRunning;
	
	/*
	 * Speech Handling
	 */
	
	// Array of strings to be recited: [roomid, speech_id, delay, text, gestures]
	private Vector<String[]> m_vSpeechArray = new Vector<String[]>();
	// Boolean to check if Robot is in Speech
	private boolean m_bIsSpeaking;
	// Speech step when speaking dialogue
	private int m_uiSpeechStep;
	
	NaoFunctions myNao = new NaoFunctions("169.254.172.97", 9559);
	
	/*
	 * DEFAULT ROBOT FUNCTIONS
	 */
	RobotHandler(RobotEnum.RobotType type)
	{
		this.m_uiRobotType = type;
		this.m_uiState = RobotEnum.StateCode.IDLE;
		this.m_bSimulationRunning = false;
		this.m_bIsSpeaking = false;
		
		Initialize();
	}
	
	public void Initialize()
	{
		m_uiSpeechStep = 0;
		
		switch (m_uiRobotType)
		{
			case TYPE_NAO:
				System.out.println("Nao RobotHandler Initialized");
				break;
			case TYPE_SCOOTER:
				System.out.println("Scooter RobotHandler Initialized");
				break;
		}
	}
	public RobotEnum.StateCode getState() {return m_uiState;}
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
        String userName = "abdu0250";
        String password = "pre7awac";
        
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
	public void overrideSpeech() {myNao.OverrideSpeech();}
	// Manual override to skip speech
	public void stopSpeech() {m_uiSpeechStep = m_vSpeechArray.size();}
	// Check for completion of Speech Array
	public boolean isCompletedSpeech() {return (m_uiSpeechStep == m_vSpeechArray.size());}
	
	/*
	 * MOVEMENT ROBOT FUNCTIONS
	 */
	
	
}
