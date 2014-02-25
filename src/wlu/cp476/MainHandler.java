package wlu.cp476;

public class MainHandler
{
	public static void main(String[] args) throws InterruptedException
	{
		RobotHandler NaoH = new RobotHandler(RobotEnum.RobotType.TYPE_NAO);
		NaoH.overrideSpeech();
		//NaoH.DBConnect(2085);
		//NaoH.startSpeech();
	}
}


