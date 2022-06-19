package ru.red.sorting.items

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{CreativeModeTab, Item, ItemStack, Rarity}

object AreaSelector extends PositionSelector(new Item.Properties()
  .tab(CreativeModeTab.TAB_MISC)
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("area_selector")
  }

  override def useOn(pContext: UseOnContext): InteractionResult =
    super.useOn(pContext, 1, AreaSelectorActive) match {
      case some: Some[ItemStack] => InteractionResult.SUCCESS
      case _ => InteractionResult.PASS
    }
}
