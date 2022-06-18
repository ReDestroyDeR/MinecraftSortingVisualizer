package ru.red.sorting.items

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{CreativeModeTab, Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraft.world.{InteractionHand, InteractionResult, InteractionResultHolder}


object AreaSelectorActive extends Item(Item.Properties()
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  val minecraft: Minecraft = Minecraft.getInstance()

  {
    this.setRegistryName("area_selector_active")
  }

  private val positionSetMsg = """
    [
      {"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position ", "color": "yellow", "bold": false},
      {"text": "2 ", "color": "green"},
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
    val itemStack = ItemStack(AreaSelectorSelected, 1)
    itemStack.setTag(player.getUseItem.getTagElement("pos1"))
    val pos2Tag = CompoundTag()
    pos2Tag.putIntArray("pos2", Array[Int](pLocation.x.toInt, pLocation.y.toInt, pLocation.z.toInt))
    itemStack.setTag(pos2Tag)
    player.setItemInHand(player.getUsedItemHand, itemStack)
    InteractionResult.CONSUME
  }
}
