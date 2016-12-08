package game
import processing.core._

//LOL
object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

class Window extends PApplet {
  private val game = new Game()
  
  override def settings () = {
    size(700, 500)
  }
  
  override def setup() = {
    background(255)
    smooth()
  }

  override def draw() = {
    if ( game.isOn() ) { 
      gameScreen()
    } else { 
      initScreen()
    }
  }
  
  override def keyPressed() = {
    key match {
      case '1' => game.startGame()
      case '2' => game.showHelp()
      case 'q' => game.endGame()
      case ' ' => game.spacePressed()
      case _  => {}
    }
  }
  
  def initScreen() = {
    background(236, 240, 241);
    fill(52, 73, 94);
    textSize(70);
    text("Legendary Spork", 200, 200);
    textSize(15); 
    text("Click to start", 200, 250);
  }
  
  def gameScreen() = {
    background(236, 240, 241);
    this.drawDude()
    this.drawObstacles()
  }
  
  def drawDude() = {
    fill(0)
    ellipse(50, 50, 30, 30);
    // Get dude position from game and draw it
  }
  
  def drawObstacles() = {
    // Loop through all the obstacles from Game class and draw them in right positions
  }
}
