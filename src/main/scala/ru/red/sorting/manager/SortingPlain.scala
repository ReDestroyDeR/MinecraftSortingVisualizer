package ru.red.sorting.manager

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager

class SortingPlain(private val pos1: Vec3, private val pos2: Vec3, private val level: Level) {

  private var array: Array[Column] = Array.empty[Column]
  private var border: (BlockPos, BlockPos) = (BlockPos.ZERO, BlockPos.ZERO)

  private val LOGGER = LogManager.getLogger
  private val minecraft = Minecraft.getInstance()

  private class Column(private var foundationPos: BlockPos, private val height: Int, private val axis: Axis) {
    var isAffected = false
    private val inactive = Blocks.WHITE_CONCRETE
    private val affected = Blocks.RED_CONCRETE
    private val air = Blocks.AIR

    def update(blockState: BlockState): Unit = {
      val fX = foundationPos.getX
      val fY = foundationPos.getY
      val fZ = foundationPos.getZ
      for (y <- fY until fY + height) {
        level.setBlock(new BlockPos(fX, y, fZ), blockState, 3)
      }
    }

    def render(): Unit = {
      update(inactive.defaultBlockState())
    }

    def destroy(): Unit = {
      update(air.defaultBlockState())
    }

    def move(delta: Int): Unit = {
      destroy()
      foundationPos = offsetByAxis(foundationPos, delta, axis)
      render()
    }
  }

  {
    val deltaX = Math.abs(pos1.x - pos2.x)
    val deltaZ = Math.abs(pos1.z - pos2.z)
    var axis = Axis.X
    if (deltaX >= deltaZ) {
      // oX Axis
      array = new Array[Column](deltaX.toInt)
      pos2.add(0.0d, 0.0d, deltaZ)
    } else {
      // oZ axis
      axis = Axis.Z
      array = new Array[Column](deltaZ.toInt)
      pos2.add(deltaX, 0.0d, 0.0d)
    }

    val floor = Math.min(pos1.y, pos2.y)
    border = (new BlockPos(pos1.x, floor, pos1.z), new BlockPos(pos2.x, floor, pos2.z))
    axis match {
      case Axis.X => if (pos1.x > pos2.x) {
        border = (new BlockPos(pos2), new BlockPos(pos1))
      }
      case Axis.Z => if (pos1.z > pos2.z) {
        border = (new BlockPos(pos2), new BlockPos(pos1))
      }
    }

    for (i <- array.indices) {
      array(i) = new Column(offsetByAxis(border._1, i, axis), i + 1, axis)
      array(i).render()
    }
    LOGGER.info(array.mkString("SORTING PLAIN Array(", ", ", ")"))
  }

  private def offsetByAxis(blockPos: BlockPos, delta: Int, axis: Axis): BlockPos = {
    blockPos.offset(if (axis == Axis.X) delta else 0, 0, if (axis == Axis.Z) delta else 0)
  }

  def destroy(): Unit = array.foreach(column => column.destroy())
}
