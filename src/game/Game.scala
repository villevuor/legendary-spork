package game

import processing.core._
import scala.collection.mutable.Buffer
import scala.util.Random

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
  private var obstaclePosition = 700
  
  // "get methods" for vars
  def isOn() = this.gameOn
  def getScore() = this.score
  def getDudePosition() = this.dudePosition
  def getObstacles() = this.obstacles
  def getObstaclePosition = this.obstaclePosition
  
  def startGame() = {   
    this.gameOn = true
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle](new Obstacle(500,300))
    this.dudePosition = ( this.windowHeight / 2 ) - ( this.dudeSize / 2 )
  }
  
  def endGame() = this.gameOn = false
  
  def spacePressed() = this.normalGravity = !this.normalGravity
  
  def showHelp() = {
    println("help page requested")
  }
  
  def moveElements() = {
    
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
    
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
  
  def moveObstacles() = {
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
  }
  
  
  
  
}