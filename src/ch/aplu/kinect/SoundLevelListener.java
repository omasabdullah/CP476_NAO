// SoundLevelListener.java

/*
This software is part of the KinectJLib package.
It is Open Source Free Software, so you may
- run the code for any purpose
- study how the code works and adapt it to your needs
- integrate all or parts of the code in your own programs
- redistribute copies of the code
- improve the code and release your improvements to the public
However the use of the code is entirely your responsibility.

Author: Aegidius Pluess, www.aplu.ch
 */
package ch.aplu.kinect;

/**
 * Interface with the declaration of a callback method for the standard sound input device.
 */
public interface SoundLevelListener extends java.util.EventListener
{
  /**
   * Called when the a new sound sample becomes available and reports the
   * level (amplitude) of the maximum value in this sample. The kinect microphone
   * must be your default sound source and the volume has to set by
   * the system control panel.
   * @param level the current sound level
   * @return timeout (in ms) to wait until next sample is detected 
   */
   public int soundLevel(int level);
}
