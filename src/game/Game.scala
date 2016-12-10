package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random

class Game {
  
  val windowHeight = 500
  val windowWidth = 700
  val taxiWidth = 92
  val taxiHeight = 58
  val blockSize = 45
  
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
    
    if ( this.taxiPosition < - this.taxiHeight / 2 || this.taxiPosition > this.windowHeight - this.taxiHeight / 2 ) {
      this.showStartScreen()
    }
  }
  
  def moveObstacles() = {
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
  }
  
  def createObstacles(frameCount: Int) = {
    if ( frameCount % 150 == 0 ) {
      val y = this.windowHeight * scala.util.Random.nextFloat
      this.obstacles += new Obstacle( this.windowWidth, y.toInt )
    }
  }
  
  def getHelpPage() = {
    "Welcome to space taxi!\n\n" + 
    "Wohoowohoo\n\n" + 
    "Some more instructions"
  }
  
  
  
}