package ru.red.sorting.items

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.{Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import net.minecraft.world.{InteractionHand, InteractionResultHolder}
import ru.red.sorting.manager.{SortingManager, SortingPlain}

import scala.collection.mutable
import scala.util.Random

object Shuffle extends Item(new Item.Properties()
  .rarity(Rarity.EPIC)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("shuffle")
  }

  private val tasks: mutable.Map[SortingPlain, (Int, Array[Int])] = new mutable.HashMap()

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
    val plan = random.shuffle(plain.getArray.indices.toList).toArray
    tasks.put(plain, (0, plan))
    InteractionResultHolder.success(itemStack)
  }

  override def inventoryTick(pStack: ItemStack,
                             pLevel: Level,
                             pEntity: Entity,
                             pSlotId: Int,
                             pIsSelected: Boolean): Unit = {
    var toDelete: List[SortingPlain] = List()
    val random = new Random()
    tasks.foreach(task => {
      tasks.update(task._1, (task._2._1 + 1, task._2._2))
      if (task._2._1 % 5 == 0) {
        val i = task._2._1 / 5
        val arr = task._1.getArray
        if (i >= arr.length) {
          toDelete = toDelete.::(task._1)
          return
        }
        arr(i) = task._2._2(i) + 1
        task._1.sync(arr)
      }
    })
    toDelete.foreach(key => tasks.remove(key))
  }
}
