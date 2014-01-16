package wlu.cp476;

import com.aldebaran.proxy.*;

public class NaoHandler
{
	static final int IDLE = 0;
	static final int WAIT_IMAGE = 1;
	static final int WAIT_DATABASE = 2;
	static final int ACTION_SPEAK = 3;
	static final int ACTION_FORWARD = 4;
	static final int ACTION_STOP = 5;
	static final int ACTION_TURN = 6;
	
	NaoHandler()
	{
		this.m_uiState = IDLE;
		this.m_bSimulationRunning = false;
	}
	
	private int m_uiState;
	private boolean m_bSimulationRunning;
	
	public int getStatee() {return m_uiState;}
	public boolean isSimulationStarted() {return m_bSimulationRunning;}
	
	public void stopSimulation()
	{
		//Cancel simulation
		m_bSimulationRunning = false;
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
				}break;
				case WAIT_DATABASE:
				{
				}break;
				case ACTION_SPEAK:
				{
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
