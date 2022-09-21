package ru.red.sorting.items

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.{Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import net.minecraft.world.{InteractionHand, InteractionResultHolder}
import ru.red.sorting.manager.{SortingManager, SortingPlain}

import scala.collection.mutable
import scala.util.Random

object BubbleSort extends Item(new Item.Properties()
  .rarity(Rarity.EPIC)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("bubble_sort")
  }

  private val tasks: mutable.Map[SortingPlain, Int] = new mutable.HashMap()

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

    val random = new Random()
    tasks.put(plain,0)
    InteractionResultHolder.success(itemStack)
  }

  override def inventoryTick(pStack: ItemStack,
                             pLevel: Level,
                             pEntity: Entity,
                             pSlotId: Int,
                             pIsSelected: Boolean): Unit = {
    var toDelete: List[SortingPlain] = List()
    tasks.foreach(task => {
      tasks.update(task._1, task._2 + 1);
      val tick = task._2;
      if (tick % 1 == 0) {
        val arr = task._1.getArray;
        val i = tick / arr.length / arr.length;
        val j = tick / arr.length % arr.length;
        if (i >= arr.length) {
          toDelete = toDelete.::(task._1)
          return
        }
        if (arr(j) < arr(i)) {
          val t = arr(i)
          arr(i) = arr(j)
          arr(j) = t
          task._1.sync(arr)
        }
      }
    })
    toDelete.foreach(key => tasks.remove(key))
  }
}
