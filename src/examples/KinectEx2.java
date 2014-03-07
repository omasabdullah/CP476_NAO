// KinectEx2.java
// Draw right hand position into GPanel

package examples;

import ch.aplu.jaw.*;
import ch.aplu.util.*;
import ch.aplu.kinect.*;
import java.awt.*;

public class KinectEx2 implements KinectCloseListener
{
  private final String dllPath =
    Kinect.is64bit() ? "KinectHandler64" : "KinectHandler";
  private final String title = "Kinect Video Frame";
  private final int ulx = 10; 
  private final int uly = 10;
  private final int width = 640;
  private final int height = 480; 

  public KinectEx2()
  {
    GPanel p = new GPanel(0, 640, 480, 0);
    p.windowPosition(650, 10);
    p.color(Color.red);

    Kinect kinect = new Kinect(dllPath, title, ulx, uly, width, height, 
      NativeHandler.WS_DEFAULT);
    kinect.addCloseListener(this);
    
    Point3D[] joints = new Point3D[20];
    for (int i = 0; i < 20; i++)
      joints[i] = new Point3D();

    while (true)
    {
      int skeletonId = kinect.getJoints(joints, 20);  // Blocks max 200 ms
      if (skeletonId > -1)  // Valid
      {
        int rightHandIndex = SkeletonJoint.HAND_RIGHT.ordinal();
        int leftHandIndex = SkeletonJoint.HAND_LEFT.ordinal();
        if (joints[leftHandIndex].y < 100)
          p.clear();
        else
        {
          int x = joints[rightHandIndex].x;
          int y = joints[rightHandIndex].y;
          int z = joints[rightHandIndex].z;
          p.title("Right hand at: (" + x + "," + y + "," + z + ")");
          p.pos(x, y);
          int red = -255 * z / 1000 + 510;
          int green = -100 * z / 3000 + 100;
          int blue = -50 * z / 3000 + 50;
          red = Math.min(Math.max(red, 0), 255);
          green = Math.min(Math.max(green, 0), 255);
          blue = Math.min(Math.max(blue, 0), 255);
          p.color(new Color(red, green, blue));
          p.fillCircle(10);
        }
      }
      else
        p.title("Invalid skeleton");
    }
  }

  public void notifyClose()
  {
    System.exit(0);
  }

  public static void main(String args[])
  {
    new KinectEx2();
  }

}
