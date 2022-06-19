package ru.red.sorting.manager

import org.apache.logging.log4j.{Level, LogManager}
import org.checkerframework.dataflow.qual.Pure

object SortingManager {
  private var sortingPlains: Array[SortingPlain] = Array.empty[SortingPlain]

  private val LOGGER = LogManager.getLogger

  def add(sortingPlain: SortingPlain): Int = {
    LOGGER.log(Level.INFO, s"ADDED SORTING PLAIN $sortingPlain")
    for (i <- sortingPlains.indices) {
      if (sortingPlains(i) == null) sortingPlains(i) = sortingPlain
      return i
    }
    sortingPlains = sortingPlains ++ Array(sortingPlain)
    sortingPlains.length - 1
  }

  def delete(index: Int): Unit = {
    LOGGER.log(Level.INFO, s"DELETED SORTING PLAIN $index")
    sortingPlains(index).destroy()
    sortingPlains(index) = null
  }

  @Pure def getById(index: Int): SortingPlain = {
    sortingPlains(index)
  }
}
