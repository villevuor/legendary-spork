package game

import javax.sound.sampled._
import java.io.File

class Sound(fileName: String, val fx: Boolean) {
  
  private var muted = false
  
  val file: File = new File(fileName)
  
  val sound: AudioInputStream = AudioSystem.getAudioInputStream(file)
  
  val clip: Clip = AudioSystem.getClip()
  
  clip.open(sound)
  
  def mute() = {
    this.muted = true
    clip.stop()
  }
  
  def unMute() = {
    this.muted = false
  }

  def play() = {
    if (!muted) {
      clip.setFramePosition(0)
      clip.start()
    }
  }
  
  def lose() = {
    if(!muted) {
      clip.start()
    }
  }
  
  def rewind() = clip.setFramePosition(0)
  
  def loop() = {
    if (!muted) {
    clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
  }
  
  def stop() = {
    clip.stop()
    clip.setFramePosition(0)
  }
}