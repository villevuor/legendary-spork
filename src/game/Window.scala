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
  
  private var fxOn = true
  private var musicOn = true
  val sounds = Buffer[Sound]()
  val gameMusic = new Sound("assets/game_music.wav", false)
  val introMusic = new Sound("assets/intro_music.wav", false)
  sounds += gameMusic
  sounds += introMusic
  
  def toggleMusic() = {
    if (musicOn) {
       sounds.filter(!_.fx).foreach( _.mute() ) 
       musicOn = false
    }
    else {
      sounds.filter(!_.fx).foreach( _.unMute() )
      musicOn = true
    }
  }
  
  def toggleFx() = {
    if (fxOn) {
      sounds.filter(_.fx).foreach( _.mute() )
      fxOn = false
    }
    else {
      sounds.filter(_.fx).foreach( _.unMute() )
      fxOn = true
    }
  }
  
  override def settings () = {
    size(game.windowWidth, game.windowHeight)
  }
  
  override def setup() = {
    smooth()
    frameRate(60)
    surface.setTitle("")
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
      case 'm' => this.toggleMusic()
      case 'f' => this.toggleFx()
      case _  => {}
    }
  }
  
  def initScreen() = {
    
    this.gameMusic.stop()
    this.introMusic.loop()
    
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
    this.introMusic.stop()
    this.gameMusic.loop()
    
    game.moveElements()
    
    this.drawTaxi()
    this.drawObstacles()
    this.drawScore()
  }
  
  // Get taxi position from game and draw it
  def drawTaxi() = image( this.taxi, 30, game.getTaxiPosition() ) 
  
  def drawScore() = {
    textSize(40)
    fill(245, 208, 0)
    // f"${X}%07d" adds front zeros
    text( f"${ this.game.getScore() }%07d", this.game.windowWidth - 120, 35 )
  }
  
  // Loop through all the obstacles from Game class and draw them in right positions
  def drawObstacles() = {
    fill(0)
    for (obstacle <- game.getObstacles) {
      val coords = obstacle.getPosition()
      rect(coords._1, coords._2, 50, 50)
    }
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
