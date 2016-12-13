package game
import processing.core._

class Obstacle( private var xCoordStart: Int, private var yCoordStart: Int, val image: PImage, val whenHit: () => Unit ) {
  private var xCoord = xCoordStart
  private var yCoord = yCoordStart
 
  def getPosition() = (this.xCoord, this.yCoord)
  
  def moveLeft() = this.xCoord -= 5
  
}
