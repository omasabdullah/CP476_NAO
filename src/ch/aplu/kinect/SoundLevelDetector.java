// SoundLevelDetector.java

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

import ch.aplu.util.*;
import javax.sound.sampled.*;
import java.io.*;
import javax.swing.JOptionPane;

// Not public
class SoundLevelDetector implements SoundSampleListener
{
  private AudioFormat audioFormat =
    new AudioFormat(22050.0F, 8, 1, true, false);
  private SoundRecorder recorder;
  private ByteArrayOutputStream data = new ByteArrayOutputStream();
  private int level;
  private SoundLevelListener microphoneListener;
  private int timeout;
  private LoResAlarmTimer timer;

  protected SoundLevelDetector(int bufSize)
  {
    recorder = new SoundRecorder(bufSize, audioFormat);
    recorder.addSoundSampleListener(this);
    startCapture();
  }

  protected void startCapture()
  {
    try
    {
      recorder.capture(data);
    }
    catch (LineUnavailableException ex)
    {
      fail("Sound card not available");
    }
  }

  protected void stopCapture()
  {
    recorder.stopCapture();
  }

  protected void addMicrophoneListener(SoundLevelListener listener)
  {
    microphoneListener = listener;
  }

  public void sampleReceived(int count)
  {
    byte[] samples = data.toByteArray();
    setSampleLevel(samples);
    data.reset();
    if (microphoneListener != null)
    {
      if (timer == null || !timer.isRunning())
      {
        timeout = microphoneListener.soundLevel(level);
        if (timeout > 0)
          timer = new LoResAlarmTimer(1000 * timeout, true);
        else
          timer = null;
      }
    }
  }

  private void setSampleLevel(byte[] samples)
  {
    byte max = 0;
    for (int i = 0; i < samples.length; i++)
    {
      if (samples[i] > max)
        max = samples[i];
    }
    level = (int)(100 * max / 127.0);
  }

  private void delay(int timeout)
  {
    try
    {
      Thread.sleep(timeout);
    }
    catch (InterruptedException ex)
    {
    }
  }

  protected int getLevel()
  {
    return level;
  }

  protected static void fail(String message)
  {
    JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
    System.exit(0);
  }
}
