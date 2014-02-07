package wlu.cp476;

import com.aldebaran.proxy.*;

import java.sql.*;
import java.util.Vector;

public class RobotHandler
{
	static final int IDLE 			= 0;
	static final int WAIT_IMAGE 	= 1;
	static final int WAIT_DATABASE 	= 2;
	static final int WAIT_SPEAKING 	= 3;
	static final int ACTION_SPEAK 	= 4;
	static final int ACTION_FORWARD = 5;
	static final int ACTION_STOP 	= 6;
	static final int ACTION_TURN 	= 7;
	
	private int m_uiState;
	private boolean m_bSimulationRunning;
	private boolean m_bIsSpeaking;
	private Vector<String[]> m_vSpeechArray = new Vector<String[]>();
	
	RobotHandler()
	{
		this.m_uiState = IDLE;
		this.m_bSimulationRunning = false;
		this.m_bIsSpeaking = false;
	}
	
	public int getState() {return m_uiState;}
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
	
	public void ForceStartSpeech() throws InterruptedException
	{
		System.out.println("FORCE START SPEECH");
		SpeechHandler testSpeech = new SpeechHandler(m_vSpeechArray);
		testSpeech.startSpeech();
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
				{
				}break;
				case WAIT_IMAGE:
				{
					//Image Recognition Here
					m_uiState++;
				}break;
				case WAIT_DATABASE:
				{
					//Cross reference image of faceplate with database
					
					m_uiState++;
				}break;
				case WAIT_SPEAKING:
				{
					//Start Speech Handler here
					if (m_bIsSpeaking)
						return;
					
					m_uiState = WAIT_IMAGE;
				}break;
				case ACTION_SPEAK:
				{
					m_bIsSpeaking = true;
				}break;
				case ACTION_FORWARD:
				{
				}break;
				case ACTION_STOP:
				{
				}break;
				case ACTION_TURN:
				{
				}break;
			}
		}
	}
}
