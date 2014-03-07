// Kinect.java

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

import ch.aplu.jaw.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

/**
 * Java class wrapper for the Microsoft Kinect C++ Library using the 
 * Java API Wrapper (JAW) framework.
 */
public class Kinect
{
  // ---------------- Internal class ClosePoller ---------------
  private class ClosePoller extends Thread
  {
    public void run()
    {
      boolean isRunning = true;
      while (isRunning)
      {
        delay(100);
        if (closeListener != null && nh != null && nh.invoke(2) == 1)
        {
          closeListener.notifyClose();
          isRunning = false;
        }
      }
    }

  }
  // ---------------- End of internal class --------------------
  //
  //

  private final int windowWidth = 640;
  private final int windowHeight = 480;
  private final int nbJoints = 20;
  private NativeHandler nh;
  private int[] values = new int[3 * nbJoints + 1];
  private KinectCloseListener closeListener = null;
  private SoundLevelDetector soundDetector = null;
  private boolean isSoundDirectionRunning = false;
  private boolean isSoundDirectionDone = false;
  // Native variables
  private int width;
  private int height;
  private String snapShotFilename;
  private int[] ary = new int[windowWidth * windowHeight];
  private int cameraElevationAngle;
  private String windowTitle;
  private int metricsIndex;
  private int windowScaleFactor;

  // End of native variables
  /**
   * Same as Kinect(dllPath, title, ulx, uly, width, height, windowStyle, 
   " audioBufferSize) with audioBufferSize = 0 (sound level detection disabled).
   */
  public Kinect(String dllPath, String title, int ulx, int uly,
    int width, int height, long windowStyle)
  {
    this(dllPath, title, ulx, uly, width, height, windowStyle, 0);
  }

  /**
   * Creates an abstraction of Microsoft's Kinect device using the
   * Java Native Interface (JNI) and the Java API Wrapper (JAW). A native window
   * may be shown that contains a camera image in real time using DirectX enhancement.
   * @param dllPath the path to the native library DLL. Provide either the full 
   * path (Drive:/Directory/Filename) or the DLL name without extension. 
   * In the latter case, the DLL is searched in the Windows system path.
   * @param title the title displayed in the native video window
   * @param ulx the upper left x of the native video window (in screen pixels)
   * @param uly the upper left y of the native video window (in screen pixels)
   * @param width the width of the native video window
   * @param height the height of the native video window
   * @param windowStyle the Window style (see the fields in ch.aplu.jaw.NativeHandler).
   * The native video window may be hidden
   * @param audioBufferSize the size in bytes used for sound level detection.
   * The following audio format is used: 22050 Hz sampling rate, 8 bit, mono, signed, little endian.
   * For audioBufferSize < 0, sound leve detection is disabled.
   * @see ch.aplu.jaw.NativeHandler
   */
  public Kinect(String dllPath, String title, int ulx, int uly,
    int width, int height, long windowStyle, int audioBufferSize)
  {
    //   System.out.println("Current JVM: " + System.getProperty("sun.arch.data.model") + " bit");
    this.width = width;
    this.height = height;
    if (audioBufferSize > 0)
      soundDetector = new SoundLevelDetector(audioBufferSize);
    nh = new NativeHandler(
      dllPath,
      this,
      title,
      ulx, uly,
      width, height,
      windowStyle);
    nh.createBuf(values);  // JNIBuffer with default bufSize
  }

  /**
   * Registers a SoundLevelListener for sound level detection.
   * @param listener the SoundLevelListener to register.
   */
  public void addSoundLevelListener(SoundLevelListener listener)
  {
    if (soundDetector != null)
      soundDetector.addMicrophoneListener(listener);
  }

  /** 
   * Returns the maximum level of the last captured sound sample.
   * @return the level of the sound sample (0..100) 
   */
  public int getSoundLevel()
  {
    if (soundDetector == null)
      return -1;
    return soundDetector.getLevel();
  }

  /**
   * Returns the new 20 skeleton points and the skeleton identifier 
   * as soon as they are available.
   * Blocks until persons are tracked and skeleton joints are valid or timeout is reached.
   * @param joints the 20 joint coordinates passed back 
   * @param timeout the maximum time to wait for a valid skeleton (in units of 10 ms),
   * <= 0 to wait indefinitely.
   * @return the skeleton identifier (two persons may be tracked) or -1,
   * if the timeout is reached
   */
  public int getJoints(Point3D[] joints, int timeout)
  {
    if (timeout < 0)
      timeout = 0;
    if (timeout == 0)  // Wait for ever
    {
      while (nh.countBuf() < values.length)
        delay(10);
    }
    else
    {
      int nb = 0;
      while (nh.countBuf() < values.length && nb < timeout)
      {
        delay(10);
        nb++;
      }
      if (nb == timeout)
        return -1;
    }
    nh.readBuf(values.length);
    int skeletonId = values[0];
    for (int i = 0; i < nbJoints; i++)
    {
      joints[i].x = values[3 * i + 1];
      joints[i].y = values[3 * i + 2];
      joints[i].z = values[3 * i + 3];
    }
    return skeletonId;
  }

  /**
   * Enables/disables notification of clicking the title bar's close button.
   * @param enable if true, the notification is enabled, otherwise the
   * application terminates when the close button is hit.
   */
  public void enableClose(boolean enable)
  {
    nh.invoke(enable ? 1 : 0);
  }

  /**
   * Registers a callback that is called when the close button of the native
   * window is hit.
   * @param listener the listener that gets the close notification
   */
  public void addCloseListener(KinectCloseListener listener)
  {
    closeListener = listener;
    new ClosePoller().start();
  }

  /**
   * Delays current thread execution for given amount of time.
   * @param timeout the time to sleep (in ms) 
   */
  public static void delay(int timeout)
  {
    try
    {
      Thread.sleep(timeout);
    }
    catch (InterruptedException ex)
    {
    }
  }

  /**
   * Returns a snap shot image of the current video image for further treatment
   * by the Java code.
   * @return a buffered image of the current video image
   */
  public BufferedImage getImage()
  {
    nh.invoke(4);
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < 480; y++)
      for (int x = 0; x < 640; x++)
        image.setRGB(x, y, ary[y * 640 + x]);
    return image;
  }

  /**
   * Performs a native snap shot of the current image and copies it in the given
   * file in BMP format by native code.
   * @param filename the snap shot file
   */
  public void doBMPSnapShot(String filename)
  {
    snapShotFilename = new String(filename);
    nh.invoke(3);
  }

  private boolean doSnapShot(String filename, String type)
  {
    try
    {
      ImageIO.write(getImage(), type, new File(filename));
    }
    catch (IOException ex)
    {
      return false;
    }
    return true;
  }

  /**
   * Performs snap shot of the current image and copies it in the given
   * file in PNG format by Java code.
   * @param filename the snap shot file
   */
  public boolean doPNGSnapShot(String filename)
  {
    return doSnapShot(filename, "png");
  }

  /**
   * Performs snap shot of the current image and copies it in the given
   * file in GIF format by Java code.
   * @param filename the snap shot file
   */
  public boolean doGIFSnapShot(String filename)
  {
    return doSnapShot(filename, "gif");
  }

  /**
   * Performs snap shot of the current image and copies it in the given
   * file in JPG format by Java code.
   * @param filename the snap shot file
   */
  public boolean doJPGSnapShot(String filename)
  {
    return doSnapShot(filename, "jpg");
  }

  /**
   * You should tilt the Kinect sensor as few times as possible, 
   * to minimize wear on the camera and to minimize tilting time. 
   * The camera motor is not designed for constant or repetitive movement, 
   * and attempts to use it that way may cause degradation of motor function. 
   * This method limits the rate at which applications can tilt the sensor,
   * in order to protect the Kinect hardware. 
   * Calls to setCameraElevationAngle are limited to one per second 
   * and a maximum of 15 calls in any 20-second period. 
   * If the application exceeds these limits, the method imposes a short 
   * lockout period during which any further calls will return an error code.
   *
   * @param angle to elevation angle in degrees. Zero indicates that the sensor
   * array should point exactly horizontally. Positive values indicate that 
   * the sensor array should point above the horizon, and negative values 
   * indicate that the sensor array should point below the horizon. 
   * The values are limited to the interval between getCameraMinimumElevationAngle()
   * and getCameraMaximumElevationAngle().
   * @return true if successfull, false if device is not operational
   * or  the application tried to move the camera more then 15 times per 20 
   * secondes or the angle is outside min-max interval
   */
  public boolean setCameraElevationAngle(int angle)
  {
    cameraElevationAngle = angle;
    return (nh.invoke(5) == 0);
  }

  /**
   * Returns the current camera elevation angle.
   * @return the elevation angle in degrees.
   */
  public int getCameraElevationAngle()
  {
    return nh.invoke(6);
  }

  /**
   * Returns the minimum camera elevation angle.
   * @return the minimum elevation angle in degrees
   */
  public int getCameraMinimumElevationAngle()
  {
    return nh.invoke(7);
  }

  /**
   * Returns the maximum camera elevation angle.
   * @return the maximum elevation angle in degrees
   */
  public int getCameraMaximumElevationAngle()
  {
    return nh.invoke(8);
  }

  /**
   * Returns true, if the initialization of the Kinect device was successful after
   * creating the Kinect class instance.
   * @return true, if the Kinect device is ready for use; otherwise false
   */
  public boolean isInitialized()
  {
    return (nh.invoke(9) == 1);
  }

  /**
   * Returns true, if the Windows operating system is 64 bit.
   * @return ture, if running on a 64-bit Windows machine; otherwise false
   */
  public static boolean is64bit()
  {
    return System.getProperty("sun.arch.data.model").equals("64");
  }

  /**
   * Sets the visibility of the native video window.
   * @param visible if true, the window is showed; otherwise it is hidden
   +
   */
  public void setVisible(boolean visible)
  {
    if (visible)
      nh.showWindow();
    else
      nh.hideWindow();
  }

  /**
   * Returns the NativeHandler instance reference used for communication with the
   * native dll.
   * @return reference to the NativeHandler
   */
  public NativeHandler getNativeHandler()
  {
    return nh;
  }

  /**
   * Returns the name of the loaded native library.
   * @return the name of the Windows DLL
   */
  public String getLoadedDllName()
  {
    int n = nh.invoke(10);
    if (n == 32)
      return "KinectHandler";
    if (n == 64)
      return "KinectHandler64";
    return "none";

  }

  /** 
   * Sets the title of the native window.
   * @param text the new text in the window title bar
   */
  public void setTitle(String text)
  {
    windowTitle = new String(text);
    nh.invoke(11);
  }

  /**
   * Returns the system metrics of the native windowing system.
   * @param metricsIndex (search Internet 'msdn GetSystemMetrics'
   * @return the value provided by the native API call GetSystemMetrics()
   */
  public int getSystemMetrics(int metricsIndex)
  {
    this.metricsIndex = metricsIndex;
    return nh.invoke(12);
  }

  /**
   * Returns current version information.
   * @return a string with the current version of the JKinectLib package
   */
  public static String getVersion()
  {
    return SharedConstants.VERSION;
  }

  /**
   * Sets a new window scale factor. This factor is used as multiplier to
   * the joints x- and y-coordinates. Default factor 1 will deliver x in the range 0..windowWidth,
   * y in the range 0..windowHeight.
   * @param windowScaleFactor the new scale factor applied to x- and y-coordinates
   */
  public void setWindowScaleFactor(int windowScaleFactor)
  {
    this.windowScaleFactor = windowScaleFactor;
    nh.invoke(13);
  }

  /**
   * Enables/disable the native sound direction finder. When disabled, sound
   * resources are released and the direction finder can't be enabled again until
   * restarting the program.
   * @param enabled if true, enables the native sound direction scanner; if false,
   * releases all sound resources
   */
  public void enableSoundDirection(boolean enabled)
  {
    if (enabled)
    {
      if (!isSoundDirectionRunning && !isSoundDirectionDone)
      {
        isSoundDirectionRunning = true;
        nh.startThread();
        while (!isSoundDirectionMachineReady())  // Wait until audio is ready
          delay(50);
      }
    }
    else
    {
      if (isSoundDirectionRunning && !isSoundDirectionDone)
      {
        isSoundDirectionRunning = false;
        isSoundDirectionDone = true;
        nh.invoke(101);
      }
    }
  }

  private boolean isSoundDirectionMachineReady()
  {
    return (nh.invoke(100) == 1);
  }

  /**
   * Asks the native sound detection finder to deliver the last
   * detected sound direction and its confidence level.
   * Polling periods shorter than 50 ms may block the application.
   * @return the latest SoundDirection
   */
  public SoundDirection getSoundDirection()
  {
    int posdirValue = nh.invoke(102);
    SoundDirection ad = new SoundDirection();
    byte b = (byte)(posdirValue & 0x000000FF);  // Sign extension
    ad.position = b;
    posdirValue = posdirValue & 0x0000FF00;
    ad.confidence = (byte)(posdirValue >> 8);
    return ad;
  }

}
