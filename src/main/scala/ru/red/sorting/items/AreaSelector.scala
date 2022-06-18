package ru.red.sorting
package items

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.{CompoundTag, ListTag, Tag}
import net.minecraft.network.chat.Component
import net.minecraft.world.{InteractionHand, InteractionResult, InteractionResultHolder}
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{CreativeModeTab, Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import ru.red.sorting.items.AreaSelectorActive

import scala.jdk.CollectionConverters.*

object AreaSelector extends Item(Item.Properties()
  .tab(CreativeModeTab.TAB_MISC)
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  val minecraft: Minecraft = Minecraft.getInstance()

  {
    this.setRegistryName("area_selector")
  }

  private val positionSetMsg = """
      [{"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position ", "color": "yellow", "bold": false},
      {"text": "1 ", "color": "green"},
      {"text": "- ", "color": "gray"},
      {"text": "%s", "color": "aqua"}
      ]
    """

  override def useOn(pContext: UseOnContext): InteractionResult = {
    if (pContext.getLevel.isClientSide)
      return InteractionResult.PASS

    val player = pContext.getPlayer
    val pLevel = player.getLevel
    val pLocation = pContext.getClickLocation
    player.sendMessage(
      Component.Serializer.fromJson(positionSetMsg.formatted(pLocation.toString)),
      player.getUUID
    )
    val itemStack = ItemStack(AreaSelectorActive, 1)
    val pos1Tag = CompoundTag()
    pos1Tag.putIntArray("pos1", Array[Int](pLocation.x.toInt, pLocation.y.toInt, pLocation.z.toInt))
    itemStack.setTag(pos1Tag)
    player.setItemInHand(player.getUsedItemHand, itemStack)
    InteractionResult.CONSUME
  }
}
