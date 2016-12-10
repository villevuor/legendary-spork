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
  val taxiPositionX = 30
  
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
    
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
    
    var positionChange = 3

    
    if ( !this.normalGravity ) {
      positionChange *= -1
    }
    
    this.taxiPositionY += positionChange
    
    this.score += 1
    
    val taxiOutOfScreen = this.taxiPositionY < - this.taxiHeight / 2 || this.taxiPositionY > this.windowHeight - this.taxiHeight / 2
    
    if ( taxiOutOfScreen ) this.showStartScreen()
   
    var topLeft = (this.taxiPositionX, this.taxiPositionY)
    var topRight = (this.taxiPositionX + this.taxiWidth, this.taxiPositionY)
    var botLeft = (this.taxiPositionX, this.taxiPositionY + this.taxiHeight)
    var botRight = (this.taxiPositionX + this.taxiWidth, this.taxiPositionY + this.taxiHeight)
        
    for (obstacle <- obstacles) {
      var coords = (obstacle.xCoord, obstacle.yCoord)
      if ( isPixelWithinRectangle(topLeft, coords, this.taxiWidth, this.taxiHeight) || 
           isPixelWithinRectangle(topRight, coords, this.taxiWidth, this.taxiHeight) ||
           isPixelWithinRectangle(botLeft, coords, this.taxiWidth, this.taxiHeight) ||
           isPixelWithinRectangle(botRight, coords, this.taxiWidth, this.taxiHeight ) ) {
        obstacle.whenHit()
      }
    }
    
    
  }
  
  def isPixelWithinRectangle(pixelToCheck: (Int, Int), rectanglePosition: (Int, Int), width: Int, height: Int) = (
    rectanglePosition._1 > pixelToCheck._1 &&
    rectanglePosition._1 < ( pixelToCheck._1 + height ) &&
    rectanglePosition._2 > pixelToCheck._2 &&
    rectanglePosition._2 < ( pixelToCheck._2 + width )
  )
  
  def createObstacles(frameCount: Int, orange: PImage, asteroid: PImage) = {
    if ( frameCount % 150 == 0 ) {
      val y = this.windowHeight * scala.util.Random.nextFloat
      
      var image = asteroid
      var action = () => this.showStartScreen()
      
      if ( scala.util.Random.nextFloat < 0.1 ) {
        image = orange 
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