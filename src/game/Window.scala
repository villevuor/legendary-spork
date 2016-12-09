package game

import processing.core._
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
  
  var font: PFont = null
  var taxi: PImage = null
  
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }
  
  override def setup() = {
    smooth()
    frameRate(60)
    frame.setTitle("")
    this.font = createFont("assets/MOZART_0.ttf", 32)
    this.taxi = loadImage("assets/taxi.png")
  }
  
  override def draw() = {
    background(0, 6, 23)
    
    if ( game.isHelp ) {
      helpScreen()
    } else {
      drawBackground()
      
      if ( game.isOn ) {
        gameStarted = true
        gameScreen()
      } else {
        initScreen()
      }
    }
  }
  
  override def keyPressed() = {
    key match {
      case ' ' => if ( game.isOn ) game.spacePressed() else game.startGame()
      case 'q' => game.showStartScreen() // ends game or hides help page
      case 'h' => game.toggleHelp()
      case _  => {}
    }
  }
  
  def initScreen() = {
    
    textFont(this.font, 32);
    
    fill(245, 208, 0)
    
    textSize(80)
    text("LEGENDARY SPORK", 40, game.windowHeight - 120)
    
    textSize(40)
    
    // Cool blinking text
    if ( ( frameCount / 30 ) % 2 == 0 ) { 
      text("Press SPACE to start", 40, game.windowHeight - 180)
    }
    
    // Display latest score
    if ( this.gameStarted ) {
      text("Latest score: " + game.getScore(), this.game.windowWidth - 300, 35 )
    }
    
    textSize(30)
    text("Need help? Just press H", 40, game.windowHeight - 80)
  }
  
  def helpScreen() = {
    textSize(40)
    text("INSTRUCTIONS", 40, 40)
    textSize(30)
    text( this.game.getHelpPage(), 40, 70)
  }
  
  def gameScreen() = {
    game.applyGravity()
    game.moveObstacles()
    
    this.drawTaxi()
    this.drawObstacles()
    this.drawScore()
  }
  
  
  // Get taxi position from game and draw it
  def drawTaxi() = {
    val coords = game.getTaxiPosition();
    
    val taxi = this.taxi
    
    if ( this.game.isNormalGravity ) {
      image( taxi, 30, coords )
    } else {
      this.flipImage( taxi, 30, coords )
    } 
  }
  
  def flipImage( img: PImage, x: Int, y: Int ) = {
    pushMatrix()
    scale( 1, -1 )
    image( img, x, - y - img.height ) 
    popMatrix()
  }
  
  def drawScore() = {
    textSize(40)
    fill(245, 208, 0)
    // f"${X}%07d" adds front zeros
    text( f"${ this.game.getScore() }%07d", this.game.windowWidth - 120, 35 )
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
    
    // Move all stars 1 pixel to left and remove those out of window
    // star => ( x, y, speed )
    this.stars = stars.map( star => ( star._1 - star._3, star._2, star._3 ) ).filter( _._1 > -s )
  }
}
