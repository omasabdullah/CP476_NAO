package wlu.cp476;

import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.aplu.jaw.*;
import ch.aplu.util.*;
import ch.aplu.kinect.*;
import ch.aplu.xboxcontroller.*;
import com.aldebaran.proxy.*;
import de.fruitfly.ovr.*;

public class RobotHandler
{
	public static void main(String[] args)
	{
		boolean debugMode = true;
		
		Scanner scan = new Scanner(System.in);
		String input = "";
				
		System.out.println("Debug mode?");
		System.out.println("1 - Yes");
		System.out.println("2 - No");
		
		input = scan.nextLine();
		if (input.equals("1"))
			debugMode = true;
		else
			debugMode = false;
		
		RobotHandler NaoH = new RobotHandler(debugMode, scan);
		NaoH.Start();
	}

	static final String NAO_IP = "169.254.172.97";
	static final int NAO_PORT = 9559;
	static final int RESULT_OK = 0;
	static final int RESULT_FAILED = 1;
	private final String dllPath = 
			Kinect.is64bit()? "KinectHandler64" : "KinectHandler";
	private final int scaleFactor = 1;
	private final String title = "Kinect Video Frame";
	private final int ulx = 10; // Upper left x of window
	private final int uly = 600; // Upper left y of window
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
	
	Kinect kinect;
	
	public class KinectDebuggingFrame
	{
		public KinectPanel kinectP = new KinectPanel();
		
		KinectDebuggingFrame()
	    {
	        SwingUtilities.invokeLater(new Runnable()
	        {
	            public void run()
	            {
	                createAndShowGUI();
	            }
	        });
	    }
	    
	    private void createAndShowGUI()
	    {
	        JFrame f = new JFrame("Swing Paint Demo");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.setSize(640,560);
	        f.add(kinectP);
	        f.pack();
	        f.setVisible(true);
	    }
	}
	
	class KinectPanel extends JPanel
	{
	    private int squareW = 10;
	    private int squareH = 10;
	    private int[][] bones = new int[20][2];
	    
	    public KinectPanel()
	    {
	        setBorder(BorderFactory.createLineBorder(Color.black));
	        
	        for (int i = 0; i < 20; i++)
		    {
		    	bones[i][0] = 25;
		    	bones[i][1] = 25;
		    }
	    }
	    
	    public void plotPoint(int x, int y, int joint)
	    {
	    	repaint(bones[joint][0], bones[joint][1], squareW, squareH);
	    	bones[joint][0] = x;
	    	bones[joint][1] = y;
	    }

	    public Dimension getPreferredSize()
	    {
	        return new Dimension(640,560);
	    }

	    public void paintComponent(Graphics g)
	    {
	        super.paintComponent(g);
	       
	        Graphics2D g2 = (Graphics2D) g;

	        // Draw Text
	        g.setColor(Color.RED);
	        
	        for (int i = 0; i < 20; i++)
	        {
	        	g.fillRect(bones[i][0],bones[i][1],squareW,squareH);
	        }
	        
	        // Head and Spine
	        g2.draw(new Line2D.Double(bones[1][0]+5, bones[1][1]+5, bones[2][0]+5, bones[2][1]+5));
	        g2.draw(new Line2D.Double(bones[3][0]+5, bones[3][1]+5, bones[2][0]+5, bones[2][1]+5));
	        g2.draw(new Line2D.Double(bones[1][0]+5, bones[1][1]+5, bones[0][0]+5, bones[0][1]+5));
	        
	        // Left Arm
	        g2.draw(new Line2D.Double(bones[2][0]+5, bones[2][1]+5, bones[4][0]+5, bones[4][1]+5));
	        g2.draw(new Line2D.Double(bones[4][0]+5, bones[4][1]+5, bones[5][0]+5, bones[5][1]+5));
	        g2.draw(new Line2D.Double(bones[5][0]+5, bones[5][1]+5, bones[6][0]+5, bones[6][1]+5));
	        g2.draw(new Line2D.Double(bones[6][0]+5, bones[6][1]+5, bones[7][0]+5, bones[7][1]+5));
	        
	        //Right Arm
	        g2.draw(new Line2D.Double(bones[2][0]+5, bones[2][1]+5, bones[8][0]+5, bones[8][1]+5));
	        g2.draw(new Line2D.Double(bones[8][0]+5, bones[8][1]+5, bones[9][0]+5, bones[9][1]+5));
	        g2.draw(new Line2D.Double(bones[9][0]+5, bones[9][1]+5, bones[10][0]+5, bones[10][1]+5));
	        g2.draw(new Line2D.Double(bones[10][0]+5, bones[10][1]+5, bones[11][0]+5, bones[11][1]+5));
	        
	        // Left Leg
	        g2.draw(new Line2D.Double(bones[0][0]+5, bones[0][1]+5, bones[12][0]+5, bones[12][1]+5));
	        g2.draw(new Line2D.Double(bones[12][0]+5, bones[12][1]+5, bones[13][0]+5, bones[13][1]+5));
	        g2.draw(new Line2D.Double(bones[13][0]+5, bones[13][1]+5, bones[14][0]+5, bones[14][1]+5));
	        g2.draw(new Line2D.Double(bones[14][0]+5, bones[14][1]+5, bones[15][0]+5, bones[15][1]+5));
	        
	        // Right Leg
        	g2.draw(new Line2D.Double(bones[0][0]+5, bones[0][1]+5, bones[16][0]+5, bones[16][1]+5));
	        g2.draw(new Line2D.Double(bones[16][0]+5, bones[16][1]+5, bones[17][0]+5, bones[17][1]+5));
	        g2.draw(new Line2D.Double(bones[17][0]+5, bones[17][1]+5, bones[18][0]+5, bones[18][1]+5));
	        g2.draw(new Line2D.Double(bones[18][0]+5, bones[18][1]+5, bones[19][0]+5, bones[19][1]+5));
	    }  
	}
	
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
	
	Scanner myScanner;
	ALMotionProxy naoMotion;
	ALTextToSpeechProxy naoSpeech;
	ALMemoryProxy naoMemory;
	ALVideoDeviceProxy naoVideo;
	
	// References to functions. Maybe move these to RobotHandler
	NaoFunctions myNao = new NaoFunctions();
	
	RobotHandler(boolean debugMode, Scanner scan)
	{
		Initialize(debugMode, scan);
	}
	
	static {System.loadLibrary("jnaoqi");}
	
	private void Initialize(boolean debug, Scanner scan)
	{
		m_uiSpeechStep = 0;
		m_uiState = StateCode.IDLE;
		myScanner = scan;
		
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
		
    	String inputLine = "";
    	
    	while (!inputLine.equals("7"))
    	{
    		inputLine = "";
    		System.out.println("Please select what you would like to do below:");
    		printBreak();
    		System.out.println("1. Play Game");
    		System.out.println("2. Credits");
    		System.out.println("3. Initialize Oculus");
    		System.out.println("4. Override Speech");
    		System.out.println("5. Initialize Video");
    		System.out.println("6. Initialize Kinect");
    		System.out.println("7. Quit");
    		System.out.println();
    		inputLine = myScanner.nextLine();
    		
    		switch (inputLine)
    		{
	    		case "1":	startGameMaze(myScanner);	break;
	    		case "2":	printCredits();		break;
	    		case "3":	initializeOculus();	break;
	    		case "4":	overrideSpeech();	break;
	    		case "5":	initializeVideo();	break;
	    		case "6":	initializeKinect();	break;
	    		case "7":						break;
	    		default: System.out.println("Unknown command");
	    		break;
    		}
    	}
    	
    	System.out.println("Goodbye!");
    	
    	myScanner.close();
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
			myNao.HeadMovement(naoMotion, or.getYaw(), or.getPitch());
			
			try{Thread.sleep(50);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		or.destroy();
	}
	private void initializeKinect()
	{
		kinect = new Kinect(dllPath, title, ulx, uly, width, height, 
			      NativeHandler.WS_BORDER | NativeHandler.WS_VISIBLE);
		KinectDebuggingFrame kinectFrame = new KinectDebuggingFrame();
		new Console(new Position(700, 10), 
			      new Size(700, 500), new Font("Courier", Font.PLAIN, 12));
		
		kinect.setWindowScaleFactor(scaleFactor);
		Point3D[] joints = new Point3D[20];
		for (int i = 0; i < 20; i++)
			joints[i] = new Point3D();
		
		int count = 0;
		int timer = 0;

		while (count < 5)
		{
			int skeletonId = kinect.getJoints(joints, 20);
			if (skeletonId > -1)
			{
				int rightShoulderIndex = SkeletonJoint.SHOULDER_RIGHT.ordinal();
				int rightElbowIndex = SkeletonJoint.ELBOW_RIGHT.ordinal();
				
				for (int i = 0; i < 20; i++)
				{
					kinectFrame.kinectP.plotPoint(joints[i].x, joints[i].y, i);
				}
				
				float rightShoulderRoll = getAngle(joints[rightShoulderIndex].x, joints[rightShoulderIndex].y,
						joints[rightElbowIndex].x, joints[rightElbowIndex].y);
						
				float rollAngle = (float) (rightShoulderRoll*Math.PI/180);
				
				if (timer%200 == 0)
				{
					count++;
					//myNao.rightShoulderMovement(naoMotion, 1.8f, -rollAngle);
					
					//myNao.Say(naoSpeech, "" + (int)rightShoulderRoll);
					
					System.out.print("Setting Angle to: " + (float)rightShoulderRoll + " (" + rollAngle + " radians)");
	    
					System.out.print(" Right Shoulder: (" + joints[rightShoulderIndex].x + ", " 
							+ joints[rightShoulderIndex].y + ", "  + joints[rightShoulderIndex].z + ")");

					System.out.println(" Right Elbow: (" + joints[rightElbowIndex].x + ", " 
							+ joints[rightElbowIndex].y + ", "  + joints[rightElbowIndex].z + ")");
				}
				
				timer++;
			}
			else
				System.out.println("Invalid skeleton");
		}
	}
	public float getAngle(int x1, int y1, int x2, int y2)
	{
	    float angle = (float) Math.toDegrees(Math.atan2(Math.abs(x1 - x2),Math.abs(y1 - y2)));
	    
	    if (angle < 0)
	    	angle += 360;
	    
	    return angle;
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
