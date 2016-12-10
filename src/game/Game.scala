package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random

class Game {
  
  val windowHeight = 500
  val windowWidth = 700
  val taxiWidth = 92
  val taxiHeight = 58
  val obstacleWidth = 40
  val obstacleHeight = 45
  val taxiPositionX = 20
  
  private var gameOn = false
  private var helpOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  private var taxiPositionY = 0 // upper left pixel of taxi

  
  // "get methods" for vars
  def isOn = this.gameOn
  def isHelp = this.helpOn
  def isNormalGravity = this.normalGravity
  def getScore() = this.score
  def getObstacles() = this.obstacles
  def getTaxiPosition() = (this.taxiPositionX, this.taxiPositionY)
  
  def startGame() = {  
    this.gameOn = true
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.taxiPositionY = ( this.windowHeight / 2 ) - this.taxiHeight
  }
  
  // Ends help and current game
  def showStartScreen() = {
    this.gameOn = false
    this.helpOn = false
  }
  
  def spacePressed() = this.normalGravity = !this.normalGravity
  
  // Show help page or start screen
  def toggleHelp() = {
    this.gameOn = false
    this.helpOn = !this.helpOn
  }
  
  def moveElements() = {
    
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
    if ( taxiOutOfScreen ) this.showStartScreen()
   
    // Check if taxi hits obstacle
    for (obstacle <- obstacles) {
      if ( taxiHitsObstacle( obstacle ) ) {
        obstacle.whenHit()
      }
    }
    // Filter out obstacles that hitted taxi
    this.obstacles = this.obstacles.filterNot( taxiHitsObstacle( _ ) )
    
    // Count score
    this.score += 1
  }
  
  def taxiHitsObstacle(obstacle: Obstacle) = {
    val taxiPosition = this.getTaxiPosition()
    val ( x, y ) = obstacle.getPosition()
    
    val obstacleBottomLeft = ( x, y )
    val obstacleBottomRight = ( x + this.obstacleWidth, y )
    val obstacleTopLeft = ( x, y + this.obstacleHeight )
    val obstacleTopRight = ( x + this.obstacleWidth, y + this.obstacleHeight ) 
    
    // Obstacles are smaller than taxi so we don't have to think case "obstacle covers taxi"
    (
      pixelIsWithinRectangle( obstacleTopLeft, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      pixelIsWithinRectangle( obstacleTopRight, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      pixelIsWithinRectangle( obstacleBottomLeft, taxiPosition, this.taxiWidth, this.taxiHeight ) || 
      pixelIsWithinRectangle( obstacleBottomRight, taxiPosition, this.taxiWidth, this.taxiHeight )
    )
  }
  
  def pixelIsWithinRectangle(pixel: (Int, Int), rectanglePosition: (Int, Int), width: Int, height: Int) = (
    pixel._1 > rectanglePosition._1 &&
    pixel._1 < ( rectanglePosition._1 + width ) &&
    pixel._2 > rectanglePosition._2 &&
    pixel._2 < ( rectanglePosition._2 + height )
  )
  
  def createObstacles(frameCount: Int, orange: PImage, asteroid: PImage) = {
    if ( frameCount % 150 == 0 ) {
      val y = this.windowHeight * scala.util.Random.nextFloat
      
      var image = asteroid
      var action = () => this.showStartScreen()
      
      if ( scala.util.Random.nextFloat < 0.1 ) {
        image = orange 
        action = () => this.score += 10000
        // action = () => this.changeMode() or something like that
      }
      
      this.obstacles += new Obstacle( this.windowWidth, y.toInt, image, action )
    }
  }
  
  def getHelpPage() = {
    "Welcome to space taxi!\n\n" + 
    "Wohoowohoo\n\n" + 
    "Some more instructions"
  }
  
  
  
}