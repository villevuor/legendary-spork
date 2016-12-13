package game

import javax.sound.sampled._
import java.io.File

class Sound(fileName: String, val fx: Boolean) {
  
  private var muted = false
  
  // Fetches the sound file and opens it for use as a clip
  val file: File = new File(fileName)
  val sound: AudioInputStream = AudioSystem.getAudioInputStream(file)
  val clip: Clip = AudioSystem.getClip()
  clip.open(sound)
  
  // Mutes the sound
  def mute() = {
    this.muted = true
    clip.stop()
  }
  
  // Unmutes the sound
  def unMute() = {
    this.muted = false
  }

  // Plays the sound from the start if it is not muted
  def play() = {
    if (!muted) {
      clip.setFramePosition(0)
      clip.start()
    }
  }
  
  // Loops the sound if it is not muted
  def loop() = {
    if (!muted) {
    clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
  }
  
  // Stops the sound and rewinds it
  def stop() = {
    clip.stop()
    clip.setFramePosition(0)
  }
}