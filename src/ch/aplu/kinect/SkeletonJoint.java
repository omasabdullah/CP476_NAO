// SkeletonJointsjava 

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
* Enumeration of the 20 skeleton joints. The position corresponds to
* the joint index starting from 0. To retrieve the index, use 
* Enum.ordinal(), e.g. SkeletonJoint.HEAD.ordinal().
*/
public enum SkeletonJoint
{
  HIP_CENTER,
  SPINE,
  SHOULDER_CENTER,
  HEAD,
  SHOULDER_LEFT,
  ELBOW_LEFT,
  WRIST_LEFT,
  HAND_LEFT,
  SHOULDER_RIGHT,
  ELBOW_RIGHT,
  WRIST_RIGHT,
  HAND_RIGHT,
  HIP_LEFT,
  KNEE_LEFT,
  ANKLE_LEFT,
  FOOT_LEFT,
  HIP_RIGHT,
  KNEE_RIGHT,
  ANKLE_RIGHT,
  FOOT_RIGHT
}