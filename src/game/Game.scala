package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random

class Game {
  
  val windowHeight = 500
  val windowWidth = 700
  val taxiWidth = 92
  val taxiHeight = 58
  val obstacleSize = 20
  
  private var gameOn = false
  private var helpOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()  
  private var taxiPosition = 0 // upper left pixel of taxi

  
  // "get methods" for vars
  def isOn = this.gameOn
  def isHelp = this.helpOn
  def isNormalGravity = this.normalGravity
  def getScore() = this.score
  def getObstacles() = this.obstacles
  def getTaxiPosition() = this.taxiPosition
  
  def startGame() = {  
    this.gameOn = true
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle](new Obstacle(500,300))
    this.taxiPosition = ( this.windowHeight / 2 ) - this.taxiHeight
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
    
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
    
    var positionChange = 3

    
    if ( !this.normalGravity ) {
      positionChange *= -1
    }
    
    this.taxiPosition += positionChange
    
    this.score += 1
    
    val taxiOutOfScreen = this.taxiPosition < - this.taxiHeight / 2 || this.taxiPosition > this.windowHeight - this.taxiHeight / 2
    
    var collision = false
    var topLeft = (20, this.taxiPosition)
    var topRight = (20 + this.taxiWidth, this.taxiPosition)
    var botLeft = (20, this.taxiPosition + this.taxiHeight)
    var botRight = (20 + this.taxiWidth, this.taxiPosition + this.taxiHeight)
      
        
    for (obstacle <- obstacles) {
      var coords = (obstacle.xCoord, obstacle.yCoord)
      if ( isPixelWithinRectangle(topLeft, coords, this.taxiWidth, this.taxiHeight) || 
           isPixelWithinRectangle(topRight, coords, this.taxiWidth, this.taxiHeight) ||
           isPixelWithinRectangle(botLeft, coords, this.taxiWidth, this.taxiHeight) ||
           isPixelWithinRectangle(botRight, coords, this.taxiWidth, this.taxiHeight ) ) {
        collision = true
      }
    }
    
    if ( taxiOutOfScreen || collision ) this.showStartScreen()
  }
  
  def isPixelWithinRectangle(pixelToCheck: (Int, Int), rectanglePosition: (Int, Int), width: Int, height: Int): Boolean = {
      if (rectanglePosition._1 > pixelToCheck._1 &&
          rectanglePosition._1 < (pixelToCheck._1 + height) &&
          rectanglePosition._2 > pixelToCheck._2 &&
          rectanglePosition._2 < (pixelToCheck._2 + width)) { 
        true } else { false
     }
  }
  
  def getHelpPage() = {
    "Welcome to space taxi!\n\n" + 
    "Wohoowohoo\n\n" + 
    "Some more instructions"
  }
  
  
  
}