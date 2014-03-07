package wlu.cp476;

public class RobotEnum
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
}
