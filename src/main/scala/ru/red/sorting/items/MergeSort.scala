package ru.red.sorting.items

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.{Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import net.minecraft.world.{InteractionHand, InteractionResultHolder}
import ru.red.sorting.manager.{SortingManager, SortingPlain}

import scala.collection.mutable
import scala.language.postfixOps
import scala.util.Random

object MergeSort extends Item(new Item.Properties()
  .rarity(Rarity.EPIC)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("merge_sort")
  }

  private val tasks: mutable.Map[SortingPlain, (Int, Array[Array[Int]])] = new mutable.HashMap()

  override def use(pLevel: Level,
                   pPlayer: Player,
                   pUsedHand: InteractionHand): InteractionResultHolder[ItemStack] = {
    val itemStack = pPlayer.getItemInHand(pUsedHand)
    val plain = SortingManager.getById(
      itemStack.getTag
        .getInt("sortingPlainId")
    ) match {
      case p: Some[SortingPlain] => p.get
      case None => return InteractionResultHolder.fail(ItemStack.EMPTY)
    }

    tasks.put(plain,(0, new Array[Array[Int]](plain.getArray.length)))
    InteractionResultHolder.success(itemStack)
  }

  override def inventoryTick(pStack: ItemStack,
                             pLevel: Level,
                             pEntity: Entity,
                             pSlotId: Int,
                             pIsSelected: Boolean): Unit = {
    var toDelete: List[SortingPlain] = List()
    tasks.foreach(task => {
      val tick = task._2._1;
      val memory = task._2._2;
      var arr = task._1.getArray;
      val occupied = memory.count(e => e != null);
      if (occupied == 0) {
        memory(0) = arr.take(arr.length / 2)
        memory(1) = arr.takeRight((arr.length / 2) + (if (arr.length % 2 == 0) 0 else 1))
        return
      }
      tasks.update(task._1, (tick + 1, memory));
      if (occupied != arr.length) {
        implicit val ordArray: Ordering[Array[Int]] = (x: Array[Int], y: Array[Int]) => {
          if (x == null & y == null) 0 else
            if (y == null) 1 else
              if (x == null) -1 else x.lengthCompare(y.length)
        }
        val hi: Array[Int] = memory.max
        val split = hi.splitAt(hi.length / 2)
        memory(memory.indexOf(hi)) = split._1
        memory(occupied) = split._2
      } else {
        implicit val ordArray: Ordering[Array[Int]] = (x: Array[Int], y: Array[Int]) => {
          if (x == null & y == null) 0 else
            if (y == null) -1 else
              if (x == null) 1 else x.lengthCompare(y.length)
        }
        val lo1 = memory.min
        val lo1Index = memory.indexOf(lo1)
        memory(lo1Index) = null
        val lo2 = memory.min
        val lo2Index = memory.indexOf(lo2)
        memory(lo2Index) = null

        def merge(a1: Array[Int], a2: Array[Int]): Array[Int] = {
          val merged = new Array[Int](a1.length + a2.length)
          var j = 0;
          for (i <- 0 until  a1.length + a2.length) {
            if (i < a1.length && a1(i) < a2(j)) {
              merged(i) = a1(i)
            } else if (i < a1.length) {
              merged(i) = a2(j)
              merged(i + 1) = a1(i)
              j += 1
            } else {
              merged(i) = a2(i % a2.length)
            }
          }
          merged
        }

        val merged = merge(lo1, lo2)
        val foldStart: ((Array[Int], Array[Int]) => Array[Int]) => Array[Int] = memory.foldLeft[Array[Int]](new Array[Int](arr.length))
        var i = 0;
        arr = foldStart{(init: Array[Int], b: Array[Int]) =>
          if (b == null) init else {
            b.copyToArray(init, i)
            i += b.length
            init
          }
        }
        memory(lo1Index) = merged
        merged.copyToArray(arr, i)
        task._1.sync(arr)
      }
    })
    toDelete.foreach(key => tasks.remove(key))
  }
}
