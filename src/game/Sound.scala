package game

import javax.sound.sampled._
import java.io.File

class Sound(fileName: String) {
  
  var file: File = new File(fileName)
  
  var sound: AudioInputStream = AudioSystem.getAudioInputStream(file)
  
  var clip: Clip = AudioSystem.getClip()
  
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
  }
}