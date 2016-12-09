package game

import processing.core._
import scala.collection.mutable.Buffer

class Game {
  
  val windowHeight = 500
  val windowWidth = 700
  val dudeSize = 30
  val blockSize = 20
  
  private var gameOn = false
  private var helpOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  private var taxiPosition = 0
  
  // "get methods" for vars
  def isOn = this.gameOn
  def isHelp = this.helpOn
  def isNormalGravity = this.normalGravity
  def getScore() = this.score
  def getTaxiPosition() = this.taxiPosition
  
  def startGame() = {  
    this.gameOn = true
    this.helpOn = false
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.taxiPosition = ( this.windowHeight / 2 ) - ( this.dudeSize / 2 )
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
  
  def applyGravity() = {
    var positionChange = 0
    
    if ( this.normalGravity ) {
      positionChange = 2
    } else {
      positionChange = -2
    }
    
    this.taxiPosition += positionChange
    
    this.score += 1
    
    if ( this.taxiPosition < 0 || this.taxiPosition > this.windowHeight ) {
      this.showStartScreen()
    }
  }
  
  def createObstacles() = {
    // Creates obstacles to right
    // this.obstacles += XXX
  }
  
  def moveObstacles() = {
    // Moves obstacles from right to left
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
  }
  
  def getHelpPage() = {
    "Welcome to space taxi!\n\n" + 
    "Wohoowohoo\n\n" + 
    "Some more instructions"
  }
  
  
  
}