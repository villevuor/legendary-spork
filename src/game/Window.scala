package game
import processing.core._

object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

class Window extends PApplet {
  private val game = new Game()
  private var gameStarted = false
  
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }
  
  override def setup() = {
    background(255)
    smooth()
  }

  override def draw() = {
    if ( game.isOn() ) {
      gameStarted = true
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
    if ( this.gameStarted ) {
      textSize(15); 
      text("Game over! Your score was " + game.getScore(), 200, 150);
    }
    textSize(30);
    text("Legendary Spork", 200, 200);
    textSize(15); 
    text("Press 1 to start", 200, 250);
  }
  
  def gameScreen() = {
    background(236, 240, 241);
    
    game.applyGravity()
    game.moveObstacles()
    
    this.drawDude()
    this.drawObstacles()
  }
  
  
  // Get dude position from game and draw it
  def drawDude() = {
    val coords = game.getDudePosition();
    fill(0)
    ellipse(20, coords, game.dudeSize, game.dudeSize);
  }
  
  // Loop through all the obstacles from Game class and draw them in right positions
  def drawObstacles() = {
    
  }
}
