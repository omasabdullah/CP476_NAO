package wlu.cp476;

public class MainHandler
{
	public static void main(String[] args) throws InterruptedException
	{
		RobotHandler NaoH = new RobotHandler();
		NaoH.DBConnect(2085);
		NaoH.ForceStartSpeech();
	}
}