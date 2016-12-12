package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random
import scala.math.max
import scala.concurrent.duration._

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
  private var specialMode = 0
  
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
  
  def startGame(): Unit = {  
    this.gameOn = true
    this.gameOver = false
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.taxiPositionY = ( this.windowHeight / 2 ) - this.taxiHeight
    this.currentLevel = 1
    this.specialMode = 0
  }
  
  // Ends help and current game
  def endGame(): Unit = {
    this.gameOn = false
    this.gameOver = true
    this.canStartNewGameTime = Some( 1.seconds.fromNow )
    this.specialMode = 0
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
    var positionChange = 3
    if ( !this.normalGravity ) {
      positionChange *= -1
    }
    this.taxiPositionY += positionChange
    
    // End game if taxi is out of screen
    val taxiOutOfScreen = this.taxiPositionY < - this.taxiHeight / 2 || this.taxiPositionY > this.windowHeight - this.taxiHeight / 2
    if ( taxiOutOfScreen ) this.endGame()
   
    // Check if taxi hits obstacle
    this.obstacles.foreach( obstacle => {
      if ( this.taxiHitsObstacle( obstacle ) ) {
        if ( this.isSpecialMode ) {
          this.score += 1000
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
  
  // Helper method fot checking taxi hits obstacle
  private def taxiHitsObstacle(obstacle: Obstacle): Boolean = {
    val taxiPosition = this.getTaxiPosition()
    val ( x, y ) = obstacle.getPosition()
    
    val obstacleBottomLeft = ( x, y )
    val obstacleBottomRight = ( x + this.obstacleWidth, y )
    val obstacleTopLeft = ( x, y + this.obstacleHeight )
    val obstacleTopRight = ( x + this.obstacleWidth, y + this.obstacleHeight ) 
    
    // Obstacles are smaller than taxi so we don't have to think case "obstacle covers taxi"
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
    
    // Obstacles are created every 2 seconds, but sometimes every 1 second
    val create = ( frameCount % 120 == 0 || ( frameCount % 60 == 0 && Random.nextFloat < 0.3 ) )
    
    if ( create ) {
      val y = ( this.windowHeight * Random.nextFloat ) - ( this.obstacleHeight / 2 )
      
      var image = asteroid
      var action = () => this.endGame()
      
      // Every 10th obstacle is orange (average)
      if ( Random.nextFloat < 0.1 ) {
        image = orange 
        action = () => this.specialMode = 900 // 15 seconds of specialmode
      }
      
      this.obstacles += new Obstacle( this.windowWidth, y.toInt, image, action )
    }
  }
  
  def getHelpPage(): String = {
    "Welcome to space taxi!\n\n" + 
    "Wohoowohoo\n\n" + 
    "Some more instructions"
  }
  
}