package wlu.cp476;

import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.aplu.jaw.NativeHandler;
import ch.aplu.kinect.Kinect;
import ch.aplu.xboxcontroller.*;
import com.aldebaran.proxy.*;
import de.fruitfly.ovr.*;

public class RobotHandler
{
	public static void main(String[] args) throws InterruptedException
	{
		RobotHandler NaoH = new RobotHandler(true);
		NaoH.Start();
	}

	static final String NAO_IP = "169.254.172.97";
	static final int NAO_PORT = 9559;
	static final int RESULT_OK = 0;
	static final int RESULT_FAILED = 1;
	private final String dllPath = 
			Kinect.is64bit()? "KinectHandler64" : "KinectHandler";
	private final String title = "Kinect Video Frame";
	private final int ulx = 10; // Upper left x of window
	private final int uly = 20; // Upper left y of window
	private final int width = 640;  // Width of window in pixels
	private final int height = 480; // Height of window in pixels
	
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
	private StateCode m_uiState;
	
	// Oculus Rift Frame
	OculusRift or = new OculusRift();
	JFrame videoFrame = new JFrame();
	JPanel videoLeftPanel = new JPanel();
	JPanel videoRightPanel = new JPanel();
	JLabel videoLabel = new JLabel();
	
	Kinect kinect = new Kinect(dllPath, title, ulx, uly, width, height, 
		      NativeHandler.WS_BORDER | NativeHandler.WS_VISIBLE);
	
	
	
	/*
	 * Speech Handling
	 */
	
	// Array of strings to be recited: [roomid, speech_id, delay, text, gestures]
	private Vector<String[]> m_vSpeechArray = new Vector<String[]>();
	// Speech step when speaking dialogue
	private int m_uiSpeechStep;
	
	/*
	 * DEFAULT ROBOT MODULES
	 */
	
	ALMotionProxy naoMotion;
	ALTextToSpeechProxy naoSpeech;
	ALMemoryProxy naoMemory;
	ALVideoDeviceProxy naoVideo;
	
	// References to functions. Maybe move these to RobotHandler
	NaoFunctions myNao = new NaoFunctions();
	
	RobotHandler(boolean debugMode)
	{
		Initialize(debugMode);
	}
	
	static {System.loadLibrary("jnaoqi");}
	
	private void Initialize(boolean debug)
	{
		m_uiSpeechStep = 0;
		m_uiState = StateCode.IDLE;
		
		if (debug)
			return;
		
		naoMotion = new ALMotionProxy(NAO_IP, NAO_PORT);
		naoSpeech = new ALTextToSpeechProxy(NAO_IP, NAO_PORT);
		naoMemory = new ALMemoryProxy(NAO_IP, NAO_PORT);
		naoVideo = new ALVideoDeviceProxy(NAO_IP, NAO_PORT);
		
		if (naoMotion == null)	System.out.println("Motion Proxy not found");
		if (naoSpeech == null)	System.out.println("Speech Proxy not found");
		if (naoMemory == null)	System.out.println("Memory Proxy not found");
		if (naoVideo == null)	System.out.println("Video Proxy not found");
	}
	public void Start()
	{
		System.out.println("Welcome to NAO's Simulation!");
		
		Scanner scanner = new Scanner(System.in);
    	String inputLine = "";
    	
    	while (!inputLine.equals("6"))
    	{
    		inputLine = "";
    		System.out.println("Please select what you would like to do below:");
    		printBreak();
    		System.out.println("1. Play Game");
    		System.out.println("2. Credits");
    		System.out.println("3. OculusVision");
    		System.out.println("4. Override Speech");
    		System.out.println("5. Initialize Video");
    		System.out.println("6. Quit");
    		System.out.println();
    		inputLine = scanner.nextLine();
    		
    		switch (inputLine)
    		{
	    		case "1":	startGameMaze(scanner);	break;
	    		case "2":	printCredits();		break;
	    		case "3":	initializeOculus();	break;
	    		case "4":	overrideSpeech();	break;
	    		case "5":	initializeVideo();	break;
	    		case "6":						break;
	    		default: System.out.println("Unknown command");
	    		break;
    		}
    	}
    	
    	scanner.close();
	}
	
	// Getters
	public StateCode getState() {return m_uiState;}

	// Initializers
	private void initializeVideo()
	{
		videoFrame.getContentPane().add(videoLeftPanel,BorderLayout.EAST);
		videoFrame.getContentPane().add(videoRightPanel,BorderLayout.WEST);
		videoFrame.setSize(640, 480);
		videoFrame.setVisible(true);
		
		videoLeftPanel.add(videoLabel);
		videoRightPanel.add(videoLabel);
		
		while (true)
		{
			updateImage();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void updateImage()
	{
		BufferedImage img;
		byte[] buff = myNao.TakePicture(naoVideo);
		int[] intArray;
		intArray = new int[640*480];
		for(int i = 0; i < 640*480; i++)
		{
			intArray[i] = ((255 & 0xFF) << 24) | //alpha
				((buff[i*3+0] & 0xFF) << 16) | //red
				((buff[i*3+1] & 0xFF) << 8)  | //green
				((buff[i*3+2] & 0xFF) << 0); //blue
		}

		img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, 640, 480, intArray, 0, 640);
		ImageIcon icon = new ImageIcon(img);
		videoLabel.setIcon(icon);
		videoLeftPanel.revalidate();
		videoLeftPanel.repaint();
		videoRightPanel.revalidate();
		videoRightPanel.repaint();
	}
	private void initializeOculus()
	{
		or.init();
		HMDInfo hmdInfo = or.getHMDInfo();
		System.out.println(hmdInfo);
		
		while (or.isInitialized())
		{
			or.poll();
			
			System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());
			myNao.HeadOrientation(naoMotion, or.getYaw(), or.getPitch());
			
			try{Thread.sleep(50);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		or.destroy();
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
	
	private void startGameMaze(Scanner scanner)
	{
		String inputLine = "";
    	
		System.out.println("Guide NAO through the maze! The controls are as follows:");
		System.out.println("==============================================");
		System.out.println("F - FORWARD");
		System.out.println("R - TURN RIGHT");
		System.out.println("L - TURN LEFT");
		System.out.println("D - DONE");

		while (!inputLine.equals("D"))
    	{
			inputLine = scanner.nextLine();
			
    		switch (inputLine)
    		{
				case "F":
					if (myNao.MoveTo(naoMotion, 0.3f, 0.0f, 0.0f) != RESULT_OK)
						System.out.println("ERROR");
					break;
				case "R":
					if (myNao.MoveTo(naoMotion, 0.0f, 0.0f, (float) -Math.PI/2) != RESULT_OK)
						System.out.println("ERROR");
					break;
				case "L":
					if (myNao.MoveTo(naoMotion, 0.0f, 0.0f, (float) Math.PI/2) != RESULT_OK)
						System.out.println("ERROR");
					break;
				case "D":
					System.out.println("Game Over!");
					printBreak();
					break;
				default: System.out.println("Unknown Command");
					break;
    		}
    	}
	}
	
	// Printing
	void printCredits()
	{
		printBreak();
		System.out.println("The following people have worked on this project:");
		System.out.println("Omas");
		System.out.println("Ryan");
		System.out.println("Sean");
		System.out.println("Peter");
		System.out.println("Landon");
		System.out.println("Shane");
		System.out.println("Ryan");
		printBreak();
	}
	void printBreak()
	{
		System.out.println("==============================================");
	}
	
	/*
	 * SPEECH ROBOT FUNCTIONS
	 */
	
	public void startSpeech()
	{
		while (m_uiSpeechStep < m_vSpeechArray.size())
		{
			int delay = Integer.parseInt(m_vSpeechArray.get(m_uiSpeechStep)[2]);
			System.out.println("Current step is: " + m_uiSpeechStep);
			//myNaoFunctions.Say(speechArray.get(m_uiSpeechStep)[3]);
			System.out.println(m_vSpeechArray.get(m_uiSpeechStep)[3]);
			System.out.println("Setting delay to: " + delay + " seconds");
			System.out.println();
			try {Thread.sleep(delay);}
			catch (InterruptedException e) {e.printStackTrace();}
			m_uiSpeechStep++;
		}
		
		System.out.println("Speech Complete");
		m_uiSpeechStep = 0;
	}
	public void overrideSpeech() {myNao.ManualSpeechOverride(naoSpeech);}
	public void stopSpeech() {m_uiSpeechStep = m_vSpeechArray.size();}
	public boolean isCompletedSpeech() {return (m_uiSpeechStep == m_vSpeechArray.size());}
	
	/*
	 * MOVEMENT ROBOT FUNCTIONS
	 */
	
	public void overrideMovement() {myNao.ManualMovementOverride(naoMotion, naoSpeech);}
}
