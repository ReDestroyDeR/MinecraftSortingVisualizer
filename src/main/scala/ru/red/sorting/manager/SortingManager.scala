package ru.red.sorting.manager

import org.apache.logging.log4j.{Level, LogManager}
import org.checkerframework.dataflow.qual.Pure

import scala.None

object SortingManager {
  private var sortingPlains: Array[SortingPlain] = Array.empty[SortingPlain]

  private val LOGGER = LogManager.getLogger

  def add(sortingPlain: SortingPlain): (SortingPlain, Int) = {
    LOGGER.log(Level.INFO, s"ADDED SORTING PLAIN $sortingPlain")
    for (i <- sortingPlains.indices) {
      if (sortingPlains(i) == null) sortingPlains(i) = sortingPlain
      return (sortingPlain, i)
    }
    sortingPlains = sortingPlains ++ Array(sortingPlain)
    (sortingPlain, sortingPlains.length - 1)
  }

  def delete(index: Int): Unit = {
    LOGGER.log(Level.INFO, s"DELETED SORTING PLAIN $index")
    sortingPlains(index).destroy()
    sortingPlains(index) = null
  }

  @Pure def getById(index: Int): Option[SortingPlain] = {
    try {
      val r = sortingPlains(index)
      if (r == null) return None
      Some(r)
    } catch {
      case e: ArrayIndexOutOfBoundsException => None
    }
  }
}
