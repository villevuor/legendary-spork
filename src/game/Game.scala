package game

import processing.core._
import scala.collection.mutable.Buffer

class Game {
  
  val windowHeight = 500
  val windowWidth = 700
  val dudeSize = 30
  val blockSize = 20
  
  private var gameOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  private var dudePosition = 0
  
  // "get methods" for vars
  def isOn() = this.gameOn
  def getScore() = this.score
  def getDudePosition() = this.dudePosition
  
  def startGame() = {   
    this.gameOn = true
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
    this.dudePosition = ( this.windowHeight / 2 ) - ( this.dudeSize / 2 )
  }
  
  def endGame() = this.gameOn = false
  
  def spacePressed() = this.normalGravity = !this.normalGravity
  
  def showHelp() = {
    println("help page requested")
  }
  
  def applyGravity() = {
    var positionChange = 0
    
    if ( this.normalGravity ) {
      positionChange = 1
    } else {
      positionChange = -1
    }
    
    this.dudePosition += positionChange
    
    this.score += 1
    
    if ( this.dudePosition < 0 || this.dudePosition > this.windowHeight ) {
      this.endGame()
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
  
  
  
  
}