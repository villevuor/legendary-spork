package game
import processing.core._


object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

class Window extends PApplet {
  private val game = new Game()
  
  override def settings () {
    size(700, 500)
  }
  
  override def setup() {
    background(0)
    smooth()
    text("Click to start", height/2, width/2)
  }

  override def draw() {
    // Draw is required
  }
  
  override def keyPressed() {
    key match {
      case '1' => game.startGame()  
      case '2' => game.showHelp()
      case ' ' => game.spacePressed()
      case _  => return
    }
  }
}
