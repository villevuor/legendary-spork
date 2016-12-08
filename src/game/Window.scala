package game

import processing.core._
import processing.sound._
import scala.util.Random._
import scala.collection.mutable.Buffer

object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

class Window extends PApplet {
  
  private val game = new Game()
  
  private var gameStarted = false
  
  private var sound = new SoundFile(this, "game_music.mp3")
  
  var font: PFont = null
  
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }
  
  override def setup() = {
    smooth()
    frameRate(60)
    this.font = createFont("assets/MOZART_0.ttf", 32)
  }
  
  override def draw() = {
    background(0, 6, 23)
    drawBackground()
    
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
    
    textFont(this.font, 32);
    
    fill(245, 208, 0)
    
    textSize(80)
    text("LEGENDARY SPORK", 40, game.windowHeight - 100)
    
    textSize(50)
    
    // Cool blinking text
    if ( ( frameCount / 30 ) % 2 == 0 ) { 
      text("Press 1 to start", 40, game.windowHeight - 160)
    }
    
    // Display latest score
    if ( this.gameStarted ) {
      text("Your latest score: " + game.getScore(), 40, game.windowHeight - 40)
    }
  }
  
  def gameScreen() = {
    game.applyGravity()
    game.moveObstacles()
    
    this.drawDude()
    this.drawObstacles()
  }
  
  
  // Get dude position from game and draw it
  def drawDude() = {
    val coords = game.getDudePosition();
    fill(255)
    ellipse(20, coords, game.dudeSize, game.dudeSize);
  }
  
  // Loop through all the obstacles from Game class and draw them in right positions
  def drawObstacles() = {
    
  }
  
  // Background artefacts
  private var stars = Buffer[(Int, Int, Int)]() // (x, y, speed)
  
  // Draw background artefacts
  def drawBackground() = {
    
    val s = 4
    
    fill(255)
    
    for ( star <- stars ) {
      rect(star._1, star._2, s, s)
    }
    
    if ( frameCount % 20 == 0 ) {
      val y = game.windowHeight * scala.util.Random.nextFloat
      val speed = scala.util.Random.nextInt(2) + 2
      this.stars += ( ( game.windowWidth, y.toInt, speed ) )
    }
    
    println( stars.size )
    
    // Move all stars 1 pixel to left and remove those out of window
    // star => ( x, y, speed )
    this.stars = stars.map( star => ( star._1 - star._3, star._2, star._3 ) ).filter( _._1 > -s )
  }
}
