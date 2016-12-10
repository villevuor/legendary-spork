package game
import processing.core._

class Obstacle(xCoordStart: Int, yCoordStart: Int) {
  var xCoord = xCoordStart
  var yCoord = yCoordStart
 
  def getPosition() = (this.xCoord, this.yCoord)
  
  def moveLeft() = this.xCoord -= 2
  
}
