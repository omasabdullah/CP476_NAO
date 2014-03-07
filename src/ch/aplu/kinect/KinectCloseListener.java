// KinectCloseListener.java

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
 * Interface with the declaration of a callback method called when the close button
 * of the native video window is clicked.
 */
public interface KinectCloseListener
{
  /**
  * Called when the title bar's close button of the native window is hit.
  */
  void notifyClose();
}
