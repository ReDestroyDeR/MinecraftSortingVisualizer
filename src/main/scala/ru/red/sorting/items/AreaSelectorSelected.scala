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


object AreaSelectorSelected extends Item(Item.Properties()
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  val minecraft: Minecraft = Minecraft.getInstance()

  private val positionSetMsg = """
      [{"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position set! ", "color": "green", "bold": false},
      {"text": "%s; %s ", "color": "green"}
      ]
    """

  {
    this.setRegistryName("area_selector_selected")
  }


}
