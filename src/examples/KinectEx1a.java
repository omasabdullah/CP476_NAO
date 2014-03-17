// KinectEx1a.java
// Get data from right hand only, small video window

package examples;

import ch.aplu.jaw.*;
import ch.aplu.util.*;
import ch.aplu.kinect.*;
import java.awt.*;

public class KinectEx1a
{
  private String dllPath = 
    Kinect.is64bit()? "KinectHandler64" : "KinectHandler";
  private final int scaleFactor = 4;
  private final String title = "Kinect Video Frame";
  private final int ulx = 10;
  private final int uly = 10;
  private final int width = 640 / scaleFactor; 
  private final int height = 480 / scaleFactor;

  public KinectEx1a()
  {
    // May be commented-out to send output to system console
    new Console(new Position(200, 10), 
      new Size(500, 500), new Font("Courier", Font.PLAIN, 12));

    Kinect kinect = new Kinect(dllPath, title, ulx, uly, width, height, 
      NativeHandler.WS_BORDER | NativeHandler.WS_VISIBLE);
    kinect.setWindowScaleFactor(scaleFactor);
    Point3D[] joints = new Point3D[20];
    for (int i = 0; i < 20; i++)
      joints[i] = new Point3D();
    
    while (true)
    {
      int skeletonId = kinect.getJoints(joints, 20);  // Blocks max 200 ms
      if (skeletonId > -1)  // Valid
      {
        int handIndex = SkeletonJoint.HAND_RIGHT.ordinal();
        System.out.println("(" + joints[handIndex].x + ", " 
          + joints[handIndex].y + ", "  + joints[handIndex].z + ")");
      }
      else
        System.out.println("Invalid skeleton");
    }
  }

  public static void main(String args[])
  {
    new KinectEx1a();
  }
}
