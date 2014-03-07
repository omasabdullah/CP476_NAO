// KinectEx1.java
// Numerical presentation of skeleton

package examples;

import ch.aplu.jaw.*;
import ch.aplu.util.*;
import ch.aplu.kinect.*;
import java.awt.*;

public class KinectEx1 implements KinectCloseListener
{
  private final String dllPath =  // Path of DLL
    Kinect.is64bit()? "KinectHandler64" : "KinectHandler";
  private final String title = "Kinect Video Frame";
  private final int ulx = 10; // Upper left x of window
  private final int uly = 20; // Upper left y of window
  private final int width = 640;  // Width of window in pixels
  private final int height = 480; // Height of window in pixels

  public KinectEx1()
  {
    // May be commented-out to send output to system console
    new Console(new Position(700, 10), 
      new Size(500, 500), new Font("Courier", Font.PLAIN, 12));

    Kinect kinect = new Kinect(dllPath, title, ulx, uly, width, height, 
      NativeHandler.WS_DEFAULT);
    kinect.addCloseListener(this);
    int nbFrame = 0;
    Point3D[] joints = new Point3D[20];
    for (int i = 0; i < 20; i++)
      joints[i] = new Point3D();
    
    while (true)
    {
      int skeletonId = kinect.getJoints(joints, 20);  // Blocks max 200 ms
      if (skeletonId > -1)  // Valid
      {
        nbFrame++;
        System.out.println("\nFrame #: " + nbFrame);
        System.out.println("Skeleton id: " + skeletonId);
        for (int i = 0; i < joints.length; i++)
        {  
          System.out.print("(" + joints[i].x + ", " + joints[i].y + ", " + joints[i].z + "), ");
          if (i % 4 == 0)
            System.out.println();
        }
        System.out.println();
      }
      else
        System.out.println("Invalid skeleton");
    }
  }

  public void notifyClose()
  {
    System.exit(0);
  }

  public static void main(String args[])
  {
    new KinectEx1();
  }
}
