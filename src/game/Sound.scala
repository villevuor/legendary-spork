package game

import javax.sound.sampled._
import java.io.File

class Sound(fileName: String) {
  
  val file: File = new File(fileName)
  
  val sound: AudioInputStream = AudioSystem.getAudioInputStream(file)
  
  val clip: Clip = AudioSystem.getClip()
  
  clip.open(sound)
  
  def play() = {
    clip.setFramePosition(0)
    clip.start()
  }
  
  def loop() = {
    clip.loop(Clip.LOOP_CONTINUOUSLY)
  }
  
  def stop() = {
    clip.stop()
    clip.setFramePosition(0)
  }
}