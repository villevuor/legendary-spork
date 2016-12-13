package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random
import scala.math.max
import scala.concurrent.duration._
import scala.math._

// Game class takes care of creating game objects, calculating their positions
// and possible collisions between game elements. It also counts score and handles
// help page content.

class Game(val windowWidth: Int, val windowHeight: Int) {
  
  val taxiWidth = 92
  val taxiHeight = 58
  val obstacleWidth = 40
  val obstacleHeight = 45
  val taxiPositionX = 20
  
  private var gameOn = false
  private var gameOver = false
  private var canStartNewGameTime: Option[Deadline] = None 
  private var helpOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  private var taxiPositionY = 0 // upper left pixel of taxi
  private var currentLevel = 1
  private var startFrame = 0
  private var framesBeforeStart = 0
  private var specialMode = 0

  
  private var fxOn = true
  private var musicOn = true
  val sounds = Buffer[Sound]()
  
  // All the sounds are imported...
  val gameMusic = new Sound("assets/game_music.wav", false)
  val introMusic = new Sound("assets/intro_music.wav", false)
  val commandFx = new Sound("assets/sfx_button.wav", true)
  val startFx = new Sound("assets/sfx_poweron.wav", true)
  val loseFx = new Sound("assets/sfx_fall.wav", true)
  val modeFx = new Sound("assets/sfx_powerup.wav", true)
  val moneyFx = new Sound("assets/sfx_coin.wav", true)
  //  ...and saved into a buffer
  sounds += gameMusic
  sounds += introMusic
  sounds += commandFx
  sounds += startFx
  sounds += loseFx
  sounds += modeFx
  sounds += moneyFx
  
  // Toggles the music on and off
  def toggleMusic() = {
    if (musicOn) {
       sounds.filter(!_.fx).foreach( _.mute() ) // Mutes all the sounds that are categorized as music
       musicOn = false
    }
    else {
      sounds.filter(!_.fx).foreach( _.unMute() ) // Unmutes
      musicOn = true
    }
  }
  
  // Toggles the sound effects on and off
  def toggleFx() = {
    if (fxOn) {
      sounds.filter(_.fx).foreach( _.mute() ) // Mutes all the sounds that are categorized as effects
      fxOn = false
    }
    else {
      sounds.filter(_.fx).foreach( _.unMute() ) // Unmutes
      fxOn = true
    }
  }
  
  def isOn = this.gameOn
  def isOver = this.gameOver
  def isHelp = this.helpOn
  def isNormalGravity = this.normalGravity
  def isSpecialMode = this.specialMode > 0
  def canStartNewGame = ( this.canStartNewGameTime == None || this.canStartNewGameTime.get.timeLeft < 0.seconds )
  def getCurrentLevel() = this.currentLevel
  def getScore() = this.score
  def getObstacles() = this.obstacles
  def getTaxiPosition() = ( this.taxiPositionX, this.taxiPositionY )
  
  def startGame(frameCount: Int): Unit = {  
    this.currentLevel = 1
    this.gameOn = true
    this.gameOver = false
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.taxiPositionY = ( this.windowHeight / 2 ) - this.taxiHeight
    this.startFrame = frameCount
    this.currentLevel = 1
    this.specialMode = 0
    this.startFx.play()
  }
  
  def gameFrame(frameCount: Int) = frameCount - this.startFrame + 1
  
  // Ends help and current game
  def endGame(): Unit = {
    this.gameOn = false
    this.gameOver = true
    this.canStartNewGameTime = Some( 1.seconds.fromNow )
    this.specialMode = 0
    
    this.loseFx.play()
  }
  
  // Shows start screen
  def showStartScreen(): Unit = {
    this.gameOn = false
    this.gameOver = false
    this.helpOn = false
  }
  
  // Pressing space changes gravity
  def changeGravity(): Unit = this.normalGravity = !this.normalGravity
  
  // Show help page or start screen
  def toggleHelp(): Unit = {
    this.gameOn = false
    this.helpOn = !this.helpOn
  }
  
  // Method for moving all the game elements (taxi + obstacles)
  def moveElements(): Unit = {
    // Move obstacles
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
    // Filter out obstacles that are not in screen anymore
    this.obstacles = this.obstacles.filter( _.getPosition()._1 + this.obstacleWidth >= 0 )
    
    // Move taxi
    var positionChange = 5
    if ( !this.normalGravity ) {
      positionChange *= -1
    }
    this.taxiPositionY += positionChange
    
    // Ends game if taxi is out of screen
    val taxiOutOfScreen = this.taxiPositionY < - this.taxiHeight / 2 || this.taxiPositionY > this.windowHeight - this.taxiHeight / 2
    if ( taxiOutOfScreen ) this.endGame()
   
    // Check if taxi hits obstacle
    this.obstacles.foreach( obstacle => {
      if ( this.taxiHitsObstacle( obstacle ) ) {
        if ( this.isSpecialMode ) {
          this.score += 1000
          this.moneyFx.play()
        } else {
            obstacle.whenHit()
        }
      }
    })
    // Filter out obstacles that hitted taxi
    this.obstacles = this.obstacles.filterNot( this.taxiHitsObstacle( _ ) )
    
    // Count score
    this.score += 1
    this.specialMode = max( this.specialMode - 1, 0 )
    
    if ( this.score % 500 == 0 ) {
      this.currentLevel += 1
    }
  }
  
  // Helper method for checking taxi hits obstacle
  private def taxiHitsObstacle(obstacle: Obstacle): Boolean = {
    val taxiPosition = this.getTaxiPosition()
    val ( x, y ) = obstacle.getPosition()
    
    val obstacleBottomLeft = ( x, y )
    val obstacleBottomRight = ( x + this.obstacleWidth, y )
    val obstacleTopLeft = ( x, y + this.obstacleHeight )
    val obstacleTopRight = ( x + this.obstacleWidth, y + this.obstacleHeight ) 
    
    // Obstacles are smaller than taxi so we don't have to think about the case "obstacle covers taxi"
    (
      this.pixelIsWithinRectangle( obstacleTopLeft, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      this.pixelIsWithinRectangle( obstacleTopRight, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      this.pixelIsWithinRectangle( obstacleBottomLeft, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      this.pixelIsWithinRectangle( obstacleBottomRight, taxiPosition, this.taxiWidth, this.taxiHeight )
    )
  }
  
  // General helper method for checking "is pixel within rectangle"
  private def pixelIsWithinRectangle(pixel: (Int, Int), rectanglePosition: (Int, Int), rectangleWidth: Int, rectangleHeight: Int): Boolean = (
    pixel._1 > rectanglePosition._1 &&
    pixel._1 < ( rectanglePosition._1 + rectangleWidth ) &&
    pixel._2 > rectanglePosition._2 &&
    pixel._2 < ( rectanglePosition._2 + rectangleHeight )
  )
  
  // Method that creates obstacles
  def createObstacles(frameCount: Int, orange: PImage, asteroid: PImage): Unit = {
    
    // Obstacles are created every 2 seconds and sometimes every 1 second
    
    //TÄÄ YLIMÄÄRÄNEN?? val create = ( frameCount % 120 == 0 || ( frameCount % 60 == 0 && Random.nextFloat < 0.3 ) )
    
    // Every 4 seconds the interval between objects is shortened by one second, caps at four objects per second.
    // In addition there's a 20% chance once a second to create an additional object. The chances of creating a random object
    // increase by 0.01 every three seconds, capping at one additional object per second. To sum it up, after three minutes of gameplay
    // there are 5 objects per second. You are doomed by then. Mwahahaha.
    val create = ( this.gameFrame(frameCount) % max(15,(60 - this.gameFrame(frameCount) / 240))  == 0 || ( this.gameFrame(frameCount) % 60 == 0 && Random.nextFloat < min(1,(0.4 + 0.01 * (this.gameFrame(frameCount) / 180) ) ) ) )
    
    if ( create ) {
      val y = ( this.windowHeight * Random.nextFloat ) - ( this.obstacleHeight / 2 )
      
      var image = asteroid
      var action = () => this.endGame()
      
      // Every 10th obstacle is orange (average)
      if ( Random.nextFloat < 0.1 ) {
        image = orange 
        action = () => {
          this.specialMode = 900 // 15 seconds of special mode
          this.modeFx.play()
        }
      }
      
      this.obstacles += new Obstacle( this.windowWidth, y.toInt, image, action )
    }
  }
  
  def getHelpPage(): String = {
    "\nWelcome to the legendary journey of the space bus!\n" +
    "How long can you travel without hitting the asteroids?\n" +
    "Oranges will send you to another dimension.\n\n" +
    "CONTROLS:\n\n" + 
    "SPACE: control the space bus\n" +
    "Q: quit to start screen\n" +
    "M: mute sound\n" +
    "F: mute FX (sound effects)\n\n" + 
    "Sound effects from:\nopengameart.org/content/512-sound-effects-8-bit-style"
  }
  
}