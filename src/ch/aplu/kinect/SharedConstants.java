// SharedConstants.java

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

/* History:
 * V1.00 - Dec 2011: - First public release
 * V1.01 - Jan 2012: - Distribution revisted
 * V2.00 - Apr 2012: - Ported to Kinect SDK-v1.0
 * V2.01 - Apr 2012: - Skeletal joints now with z coordinates (distance from camera)
                     - Modified joints are of type Point3D now (no backward compatibility)
 * V2.02 - Apr 2012: - KinectGig updated
 * V2.03 - Apr 2012: - Event model implemented (SkeletonListener)
 * V2.04 - Apr 2012: - Problems with events when using Webstart, so removed event model
 * V2.05 - Apr 2012: - Added Kinect.setWindowScaleFactor()
 * V2.06 - Apr 2012: - Added sound direction finder
 */

package ch.aplu.kinect;  

interface SharedConstants
{
  String ABOUT =
    "2003-2012 Aegidius Pluess\n" +
    "OpenSource Free Software\n" +
    "http://www.aplu.ch\n" +
    "All rights reserved";
  String VERSION = "2.06 - May 2012";
}
