package ru.red.sorting.items

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{Item, ItemStack, Rarity}
import net.minecraft.world.phys.Vec3
import ru.red.sorting.manager.{SortingManager, SortingPlain}


object AreaSelectorActive extends PositionSelector(new Item.Properties()
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("area_selector_active")
  }

  private val positionSetMsg =
    """
      [{"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position set! ", "color": "green", "bold": false},
      {"text": "Pos1: %s; Pos2: %s ", "color": "green"}
      ]
    """

  override def useOn(pContext: UseOnContext): InteractionResult =
    super.useOn(pContext, 2, AreaSelectorSelected) match {
      case some: Some[ItemStack] =>
        val player = pContext.getPlayer
        val itemStack = some.get
        val tag = itemStack.getTag
        val pos1 = tag.getIntArray("pos1")
        val pos2 = tag.getIntArray("pos2")
        player.sendMessage(
          Component.Serializer.fromJson(
            positionSetMsg
              formatted(
              s"${pos1.mkString("Array(", ", ", ")")}",
              s"${pos1.mkString("Array(", ", ", ")")}")
          ),
          player.getUUID)

        val pos1d = pos1.map(x => x.toDouble)
        val pos2d = pos2.map(x => x.toDouble)

        val sortingPlain = new SortingPlain(
          new Vec3(pos1d(0), pos1d(1), pos1d(2)),
          new Vec3(pos2d(0), pos2d(1), pos2d(2)),
          pContext.getLevel
        )
        val (plain, plainId) = SortingManager.add(sortingPlain)
        tag.putInt("sortingPlainId", plainId)

        val shuffle = new ItemStack(Shuffle, 1)
        val bubbleSort = new ItemStack(BubbleSort, 1)
        val mergeSort = new ItemStack(MergeSort, 1)
        shuffle.setTag(tag)
        bubbleSort.setTag(tag)
        mergeSort.setTag(tag)
        player.addItem(shuffle)
        player.addItem(bubbleSort)
        player.addItem(mergeSort)
        InteractionResult.SUCCESS
      case _ => InteractionResult.PASS
    }
}
