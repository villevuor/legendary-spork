package game

import processing.core._
import scala.util.Random
import scala.collection.mutable.Buffer

// This actually starts program and draws window

object Window {
  def main(args: Array[String]) {
    PApplet.main(Array[String]("game.Window"))
  }
}

// Class Window draws game graphics and forwards key press events to Game class.
// It also fetches game element positions from Game class.

class Window extends PApplet {
  
  private val windowHeight = 500
  private val windowWidth = 700
  
  private val game = new Game(windowWidth, windowHeight)
  
  var font: PFont = null
  var taxi: PImage = null
  var asteroid: PImage = null
  var orange: PImage = null
  var bonus: PImage = null
  
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }
  
  override def setup() = {
    smooth()
    frameRate(60)
    surface.setTitle("")
    this.font = createFont("assets/MOZART_0.ttf", 32)
    this.taxi = loadImage("assets/bus.png")
    this.asteroid = loadImage("assets/asteroid.png") 
    this.orange = loadImage("assets/orange.png")
    this.bonus = loadImage("assets/bonuspoints.png")
  }
  
  override def draw() = {
    this.drawBackground()
    
    if ( this.game.isHelp ) {
      this.helpScreen()
    } else if ( this.game.isOn ) {
      this.gameScreen()
    } else if ( this.game.isOver ) {
      this.gameOverScreen()
    } else {
      this.initScreen()
    }
  }
  
  override def keyPressed() = {
    key match {
      case ' ' => {
        if ( this.game.isOn ) {
          this.game.changeGravity()
        } else if ( this.game.canStartNewGame ) { 
          this.game.startGame()
        }
      }
      case 'q' => {
        this.game.showStartScreen() // ends game or hides help page
        game.commandFx.play()
      }
      case 'h' => {
        this.game.toggleHelp()
        game.commandFx.play()
      }
      case 'm' => {
        game.toggleMusic()
        game.commandFx.play()
      }
      case 'f' => {
        game.toggleFx()
        game.commandFx.play()
      }
      case _  => {}
    }
  }
  
  def initScreen() = {    
    game.introMusic.loop()
    
    textFont(this.font, 32)
    
    fill(245, 208, 0)
    textAlign(1) // left
    
    textSize(80)
    text("LEGENDARY SPACE BUS", 40, this.windowHeight - 120)
    
    textSize(40)
    
    // Cool blinking text
    if ( ( frameCount / 30 ) % 2 == 0 ) { 
      text("Press SPACE to start", 40, this.windowHeight - 180)
    }
    
    textSize(30)
    text("Need help? Just press H", 40, this.windowHeight - 80)
  }
  
  def helpScreen() = {
    
    game.gameMusic.stop()
    game.introMusic.loop()
    
    textAlign(1) // left
    fill(245, 208, 0)
    textSize(40)
    text("INSTRUCTIONS", 40, 40)
    fill(255)
    textSize(30)
    text( this.game.getHelpPage(), 40, 70)
  }
  
  def gameOverScreen() = {    
    val half = this.windowWidth / 2
    
    game.gameMusic.stop()
    game.introMusic.loop()
    
    fill(245, 208, 0)
    textAlign(3) // center
    
    textSize(80)
    text("GAME OVER", half, 200)
    textSize(40)
    text("You got " + this.game.getScore() + " points!", half, 250)
    
    if ( this.game.canStartNewGame ) {
      fill(255)
      textSize(25)
      text("Press SPACE to start a new game", half, 310)
      text("Press Q to show the start screen", half, 335)
      text("Press H for help", half, 360)
    }
  }
  
  def gameScreen() = {
    game.introMusic.stop()
    game.gameMusic.loop()
    
    this.game.createObstacles( frameCount, this.orange, this.asteroid )
    this.game.moveElements()
    
    this.drawTaxi()
    this.drawObstacles()
    this.drawScore()
  }
  
  // Get taxi position from game and draw it
  def drawTaxi() = {
    val ( taxiX, taxiY ) = game.getTaxiPosition()
    image( this.taxi, taxiX, taxiY )
  }
  
  def drawScore() = {
    textSize(40)
    
    if ( this.game.isSpecialMode ) {
      fill(0)
    } else {
      fill(245, 208, 0)
    }
    
    // f"${X}%07d" adds front zeros
    text( f"${ this.game.getScore() }%07d", this.game.windowWidth - 120, 35 )
  }
  
  // Loop through all the obstacles from Game class and draw them in right positions
  def drawObstacles() = {
    for (obstacle <- this.game.getObstacles) {
      val (x, y) = obstacle.getPosition()
      val img = if ( this.game.isSpecialMode ) bonus else obstacle.image
      image( img, x, y )
    }
  }
  
  // Background artefacts ("stars"). These are created in Window since they have
  // nothing to do with game elements – they are made just for cooler appearance.
 
  private var stars = Buffer[(Int, Int, Int)]() // (x, y, speed)

  // Draw background stars
  def drawBackground() = {
    
    if ( this.game.isSpecialMode ) {
      background(255, 203, 253)
    } else {
      background(0, 6, 23)
    }

    if ( !this.game.isHelp ) {
      val size = 4
      
      noStroke()
      
      if ( this.game.isSpecialMode ) {
        fill(119, 9, 115)
      } else {
        fill(255)
      }
      
      for ( star <- stars ) {
        rect(star._1, star._2, size, size)
      }
      
      if ( frameCount % 20 == 0 ) {
        val y = game.windowHeight * Random.nextFloat
        val speed = Random.nextInt(2) + 2
        this.stars += ( ( game.windowWidth, y.toInt, speed ) )
      }
      
      // Move all stars 1 pixel to left and remove those out of window
      // star => ( x, y, speed )
      this.stars = this.stars.map(star => ( star._1 - star._3, star._2, star._3 ) )
      this.stars = this.stars.filter( _._1 > -size )
    }
  }
}
