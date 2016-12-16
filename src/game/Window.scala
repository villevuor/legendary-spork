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
  
  private var font: PFont = null
  private var bus: PImage = null
  private var rocket: PImage = null
  private var orange: PImage = null
  private var bonusBlock: PImage = null
  
  // Set window size (method of processing library)
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }

  // Set basic settings, load fonts and images (method of processing library)
  override def setup() = {
    smooth()
    frameRate(60)
    surface.setTitle("")
    
    this.font = createFont("assets/MOZART_0.ttf", 32)
    
    this.bus = loadImage("assets/bus.png")
    this.rocket = loadImage("assets/rocket.png") 
    this.orange = loadImage("assets/orange.png")
    this.bonusBlock = loadImage("assets/bonuspoints.png")
  }
  
  // Called once per frame (method of processing library)
  override def draw() = {
    this.drawBackground()
    
    if ( this.game.isHelp ) {
      this.helpScreen()
    } else if ( this.game.isOn ) {
      this.gameScreen()
    } else if ( this.game.isOver ) {
      this.gameOverScreen()
    } else {
      this.startScreen()
    }
  }
  
  // Handle every key press event
  override def keyPressed() = {
    key match {
      case ' ' => { // space
        if ( this.game.isOn ) {
          this.game.changeGravity()
        } else if ( this.game.canStartNewGame ) { 
          this.game.startGame(frameCount)
        }
      }
      case 'r' => {
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
      case _  => {} // do nothing on other keys
    }
  }
  
  // Displays start screen
  private def startScreen() = {    
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
  
  // Show help screen
  private def helpScreen() = {
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
  
  // Show game over screen
  private def gameOverScreen() = {    
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
      text("Press R to return to the start screen", half, 335)
      text("Press H for help", half, 360)
    }
  }
  
  // Shows actual game. Called by gameScreen.
  private def gameScreen() = {
    game.introMusic.stop()
    game.gameMusic.loop()
    
    this.game.loop( frameCount, this.orange, this.rocket )
    
    this.drawBus()
    this.drawObstacles()
    this.drawScore()
  }
  
  // Get bus position from game and draw it. Called by gameScreen.
  private def drawBus() = {
    val ( busX, busY ) = game.getBusPosition()
    image( this.bus, busX, busY )
  }
  
  // Display player's current score. Called by gameScreen.
  private def drawScore() = {
    textAlign(1)
    textSize(40)
    
    if ( this.game.isSpecialMode ) {
      fill(0)
    } else {
      fill(245, 208, 0)
    }
    
    // f"${X}%08d" adds front zeros, looks better
    text( f"${ this.game.getScore() }%08d", this.game.windowWidth - 135, 35 )
  }
  
  // Loop through all the obstacles from Game class and draw them in right positions. Called by gameScreen. 
  private def drawObstacles() = {
    for ( obstacle <- this.game.getObstacles() ) {
      val ( x, y ) = obstacle.getPosition()
      val img = if ( this.game.isSpecialMode ) bonusBlock else obstacle.image
      image( img, x, y )
    }
  }
  
  // Background artefacts ("stars"). These are created in Window since they have
  // nothing to do with game elements – they are made just for cooler appearance.
 
  private var stars = Buffer[(Int, Int, Int)]() // (x, y, speed)

  // Draw background stars
  private def drawBackground() = {
    
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
