package game

import processing.core._
import scala.collection.mutable.Buffer

class Game {
  
  private var gameOn = false
  private var score = 0
  private var normalGravity = true
  private var obstacles = Buffer[Obstacle]()
  
  def startGame() = {   
    this.gameOn = true
    this.score = 0
    this.normalGravity = true
    this.obstacles = Buffer[Obstacle]()
  }
  
  def endGame() = this.gameOn = false
  
  def isOn() = this.gameOn
  
  def spacePressed() = this.normalGravity = !this.normalGravity
  
  def showHelp() = {
    println("help page requested")
  }
  
  def getScore() = this.score
  
  private def countScore() = score += 1
  
  private def moveDude() = {
    this.applyGravity()
    
    // if ( TORMAYS ) {
    this.endGame()
    // }
  }
  
  private def applyGravity() = {
    // Moves dude up or down
  }
  
  private def createObstacles() = {
    // Creates obstacles to right
    // this.obstacles += XXX
  }
  
  private def moveObstacles() = {
    // Moves obstacles from right to left
    for ( obstacle <- this.obstacles ) {
      obstacle.moveLeft()
    }
  }
  
  
  
  
}