// Point3D.java
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
 * Class representing a three-dimensional vector with integer coordinates (x, y, z).
*/
public class Point3D
{
  /**
   * The x coordinate.
   */
  public int x;

  /**
   * The y coordinate.
   */
  public int y;

  /**
   * The z coordinate.
   */
  public int z;
  
  /**
   * Creates an instance with given coordinates.
   * @param x the x coordinate
   * @param y the y coordnate
   * @param z the z coordinate
   */
  public Point3D(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  /**
   * Creates an instance with coordinates (0, 0, 0).
   */
  public Point3D()
  {
    x = 0;
    y = 0;
    z = 0;
  }
  
  /**
   * Returns a copy of this point with same coordinates.
   * @return a clone of this point
   */
  public Point3D clone()
  {
    return new Point3D(x, y, z);
  }
  
  /**
   * Checks whether the coordinates of the given Point3D
   * are equal to the coordinates of the current instance
   * (overrides Object.equals()).
   * @param obj the object whose coordinates are checked
   * @return true if the given object is of class Point3D and  
   * has the same (x, y, z) as the current point; otherwise false
   */
  public boolean equals(Object obj)
  {
    if (obj.getClass() != Point3D.class)
      return false;
    Point3D point = (Point3D)obj;
    return x == point.x && y == point.y && z == point.z;
  }
  
   /**
   * Returns a string that represents this Point3D.
   * @return a string in the format (x, y, z)
   */
  public String toString()
  {
    return "(" + x + ", " + y + "," + z + ")";
  }
}
