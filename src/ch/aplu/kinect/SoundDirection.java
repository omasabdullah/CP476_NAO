// SoundDirection.java

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
 * Packed information for sound position angle and confidence level.
 */
public class SoundDirection
{
  /** 
   * The horizontal angle (in degrees) to the sound source (zero in
   * perpendicular direction to the kinect device). Positive for a source to the right.
   */
  public int position;
  /**
   * The confidence level (in percents) delivered from the sound direction algorithm.  
   */
  public int confidence;
}
