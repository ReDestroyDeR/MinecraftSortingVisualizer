package ru.red.sorting.items

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.{Item, ItemStack, Rarity}
import net.minecraft.world.level.Level
import net.minecraft.world.{InteractionHand, InteractionResultHolder}
import ru.red.sorting.manager.SortingManager

object AreaSelectorSelected extends Item(new Item.Properties()
  .rarity(Rarity.RARE)
  .stacksTo(1)
  .fireResistant()
) {

  {
    this.setRegistryName("area_selector_selected")
  }

  private val positionResetMsg =
    """
      [{"text": "[","bold": true},
      {"text": "Red's Sorting", "color": "red", "bold": false},
      {"text": "]", "bold": true},
      {"text": " Position reset!", "color": "red", "bold": false}
      ]
    """

  override def use(pLevel: Level,
                   pPlayer: Player,
                   pUsedHand: InteractionHand): InteractionResultHolder[ItemStack] = {
    val itemStack = pPlayer.getItemInHand(pUsedHand)
    if (pLevel.isClientSide || !pPlayer.isCrouching)
      return InteractionResultHolder.pass(itemStack)

    SortingManager.delete(itemStack.getTag.getInt("sortingPlainId"))
    pPlayer.setItemInHand(pPlayer.getUsedItemHand, new ItemStack(AreaSelector, 1))
    pPlayer.sendMessage(Component.Serializer.fromJson(positionResetMsg), pPlayer.getUUID)
    InteractionResultHolder.success(itemStack)
  }
}
