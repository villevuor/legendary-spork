package game

class Obstacle(xCoordStart: Int, yCoordStart: Int) {
  private var xCoord = xCoordStart
  private var yCoord = yCoordStart
 
  def getPosition() = (this.xCoord, this.yCoord)
  
  def moveLeft() = this.xCoord -= 1
}