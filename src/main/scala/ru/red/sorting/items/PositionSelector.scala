package ru.red.sorting.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{Item, ItemStack}
import net.minecraft.world.phys.Vec3

abstract class PositionSelector(private val itemProperties: Item.Properties)
  extends Item(itemProperties) {

  private val positionSetMsg = (posN: Int, pLocation: Vec3) =>
    s"""
    [
      {"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position ", "color": "yellow", "bold": false},
      {"text": "$posN ", "color": "green"},
      {"text": "- ", "color": "gray"},
      {"text": "${pLocation.x.toInt}, ${pLocation.y.toInt}, ${pLocation.z.toInt}", "color": "aqua"}
    ]
    """

  def useOn(pContext: UseOnContext, posN: Int, next: Item): Option[ItemStack] = {
    if (pContext.getLevel.isClientSide)
      return None

    val player = pContext.getPlayer
    val pLevel = player.getLevel
    val pLocation = pContext.getClickLocation
    player.sendMessage(
      Component.Serializer.fromJson(positionSetMsg.apply(posN, pLocation)),
      player.getUUID
    )
    val itemStack = new ItemStack(next, 1)
    val tag = player.getUseItem.getOrCreateTag()
    itemStack.setTag(tag)
    tag.putIntArray(s"pos$posN", Array[Int](pLocation.x.toInt, pLocation.y.toInt, pLocation.z.toInt))
    player.setItemInHand(player.getUsedItemHand, itemStack)
    Some(itemStack)
  }
}
