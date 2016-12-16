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
  
  val busWidth = 92
  val busHeight = 58
  val obstacleWidth = 40
  val obstacleHeight = 45
  val busPositionX = 20
  
  private var gameOn = false
  private var gameOver = false
  private var canStartNewGameTime: Option[Deadline] = None 
  private var helpOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  private var busPositionY = 0 // upper left pixel of bus
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
    if ( musicOn ) {
       sounds.filter( !_.fx ).foreach( _.mute() ) // Mutes all the sounds that are categorized as music
       musicOn = false
    } else {
      sounds.filter( !_.fx ).foreach( _.unMute() ) // Unmutes
      musicOn = true
    }
  }
  
  // Toggles the sound effects on and off
  def toggleFx() = {
    if ( fxOn ) {
      sounds.filter( _.fx ).foreach( _.mute() ) // Mutes all the sounds that are categorized as effects
      fxOn = false
    } else {
      sounds.filter( _.fx ).foreach( _.unMute() ) // Unmutes
      fxOn = true
    }
  }
  
  // "Get methods" for private vars
  def isOn = this.gameOn
  def isOver = this.gameOver
  def isHelp = this.helpOn
  def isNormalGravity = this.normalGravity
  def isSpecialMode = this.specialMode > 0
  def canStartNewGame = ( this.canStartNewGameTime == None || this.canStartNewGameTime.get.timeLeft < 0.seconds )
  def getScore() = this.score
  def getObstacles() = this.obstacles
  def getBusPosition() = ( this.busPositionX, this.busPositionY )
  
  // Resets all the game variables
  def startGame(frameCount: Int): Unit = {  
    this.gameOn = true
    this.gameOver = false
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.busPositionY = ( this.windowHeight / 2 ) - this.busHeight
    this.startFrame = frameCount
    this.specialMode = 0
    
    this.startFx.play()
  }
  
  // Calculates the frame number from beginning of single game
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
  
  // Method for general "game loop". When game is on, this is called once
  // on every frame by Window class.
  def loop(frameCount: Int, orange: PImage, rocket: PImage): Unit = {

    // Images (orange + rocket) has to be forwarded to createObstacles
    this.createObstacles(frameCount, orange, rocket)
    
    // Move obstacles
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
    // Filter out obstacles that are not in screen anymore
    this.obstacles = this.obstacles.filter( _.getPosition()._1 + this.obstacleWidth >= 0 )
    
    // Move bus
    var positionChange = 5
    if ( !this.normalGravity ) {
      positionChange *= -1
    }
    this.busPositionY += positionChange
    
    // Ends game if bus is out of screen (there is little margin that allows going out of screen)
    val busOutOfScreen = this.busPositionY < - this.busHeight / 2 || this.busPositionY > this.windowHeight - this.busHeight / 2
    if ( busOutOfScreen ) this.endGame()
   
    // Check if bus hits obstacle
    this.obstacles.foreach( obstacle => {
      if ( this.busHitsObstacle( obstacle ) ) {
        if ( this.isSpecialMode ) {
          this.score += 1000
          this.moneyFx.play()
        } else {
            obstacle.whenHit()
        }
      }
    })
    // Filter out obstacles that hitted bus
    this.obstacles = this.obstacles.filterNot( this.busHitsObstacle( _ ) )
    
    // Count score
    this.score += 1
    
    // If special mode, remove one frame from it's length
    if ( this.specialMode > 0 ) this.specialMode -= 1
  }
  
  // Method that creates obstacles. Is called once in every loop.
  private def createObstacles(frameCount: Int, orange: PImage, rocket: PImage): Unit = {
    
    // Obstacles are not created if special mode is ending soon
    // Otherwise player would crash with rocket just after it was bonus block
    if ( specialMode == 0 || specialMode >= 120 ) {
      
      // Every 4 seconds the interval between objects is shortened by one second,
      // caps at four objects per second. In addition there's a 20% chance once
      // a second to create an additional object. The chances of creating a random
      // object increase by 0.01 every three seconds, capping at one additional
      // object per second. To sum it up, after three minutes of gameplay there
      // are 5 objects per second. You are doomed by then. Mwahahaha.
      val shouldCreateObstacle = (
        this.gameFrame(frameCount) % max( 15, ( 60 - this.gameFrame(frameCount) / 240 ) ) == 0 ||
        (
          this.gameFrame(frameCount) % 60 == 0 &&
          Random.nextFloat < min( 1, ( 0.4 + 0.01 * ( this.gameFrame(frameCount) / 180 ) ) )
        )
      )
      
      if ( shouldCreateObstacle ) {
        val y = ( this.windowHeight * Random.nextFloat ) - ( this.obstacleHeight / 2 )
        
        var image = rocket
        var action = () => this.endGame()
        
        // Every 20th obstacle is orange (average)
        if ( Random.nextFloat < 0.05 ) {
          image = orange 
          action = () => {
            this.specialMode = 600 // 10 seconds of special mode
            this.modeFx.play()
          }
        }
        
        this.obstacles += new Obstacle( this.windowWidth, y.toInt, image, action )
      }
    }
  }
  
  // Helper method for checking if bus hits obstacle
  private def busHitsObstacle(obstacle: Obstacle): Boolean = {
    val busPosition = this.getBusPosition()
    val ( x, y ) = obstacle.getPosition()
    
    val obstacleBottomLeft = ( x, y )
    val obstacleBottomRight = ( x + this.obstacleWidth, y )
    val obstacleTopLeft = ( x, y + this.obstacleHeight )
    val obstacleTopRight = ( x + this.obstacleWidth, y + this.obstacleHeight ) 
    
    // Obstacles are smaller than bus so we don't have to think about the case "obstacle covers bus"
    (
      this.pixelIsWithinRectangle( obstacleTopLeft, busPosition, this.busWidth, this.busHeight ) || 
      this.pixelIsWithinRectangle( obstacleTopRight, busPosition, this.busWidth, this.busHeight ) || 
      this.pixelIsWithinRectangle( obstacleBottomLeft, busPosition, this.busWidth, this.busHeight ) || 
      this.pixelIsWithinRectangle( obstacleBottomRight, busPosition, this.busWidth, this.busHeight )
    )
  }
  
  // General helper method for checking "is pixel within rectangle"
  private def pixelIsWithinRectangle(pixel: (Int, Int), rectanglePosition: (Int, Int), rectangleWidth: Int, rectangleHeight: Int): Boolean = (
    pixel._1 > rectanglePosition._1 &&
    pixel._1 < ( rectanglePosition._1 + rectangleWidth ) &&
    pixel._2 > rectanglePosition._2 &&
    pixel._2 < ( rectanglePosition._2 + rectangleHeight )
  )
  
}