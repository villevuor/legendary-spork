package game
import processing.core._

class Obstacle( private var xCoordStart: Int, private var yCoordStart: Int, val image: PImage, val whenHit: () => Unit ) {
  var xCoord = xCoordStart
  var yCoord = yCoordStart
 
  def getPosition() = (this.xCoord, this.yCoord)
  
  def moveLeft() = this.xCoord -= 2
  
}
