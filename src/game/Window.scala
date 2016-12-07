package game
import processing.core._


object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

class Window extends PApplet {
  override def settings () {
    size(700, 500)
  }
  
  override def setup() {
    background(0)
    smooth()
    text("Click to start", height/2, width/2)
  }

  override def draw() {
    stroke(255)
    if (mousePressed) {
      line(mouseX,mouseY,pmouseX,pmouseY);
    }
  }
}
