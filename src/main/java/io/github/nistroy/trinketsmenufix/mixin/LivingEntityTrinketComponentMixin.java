package io.github.nistroy.trinketsmenufix.mixin;

import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code update()} remplace les inventaires des emplacements. Trinkets l'appelle à la création de tout
 * {@code InventoryMenu} du joueur, sous-classes comprises (écran accessoires d'Aether), mais ne reconstruit que
 * l'écran en création : l'inventaire du joueur garde les anciens, ce qui duplique au retrait et perd à la pose.
 */
@Mixin(value = LivingEntityTrinketComponent.class, remap = false)
abstract class LivingEntityTrinketComponentMixin {
	@Inject(method = "update", at = @At("RETURN"))
	private void trinketsmenufix$rebindInventoryMenu(CallbackInfo ci) {
		// inventoryMenu est null pendant la construction du joueur : son écran se construit alors sur les nouveaux.
		if (((LivingEntityTrinketComponent) (Object) this).entity instanceof Player player
				&& player.inventoryMenu != null) {
			// false : reconstruit les emplacements sans rappeler update().
			((TrinketPlayerScreenHandler) player.inventoryMenu).trinkets$updateTrinketSlots(false);
		}
	}
}
