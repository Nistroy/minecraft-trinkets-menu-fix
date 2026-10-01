package io.github.nistroy.trinketsmenufix.test;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * L'écran accessoires d'Aether hérite d'{@link InventoryMenu} : le créer fait recréer par Trinkets les inventaires
 * de ses emplacements. Les tests créent un {@link InventoryMenu} de plus pour le joueur, comme Aether.
 */
public class InventoryMenuRebindGameTest implements FabricGameTest {
	@GameTest(template = EMPTY_STRUCTURE)
	public void playerMenuSlotStaysOnComponentInventory(GameTestHelper helper) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();

		new InventoryMenu(player.getInventory(), true, player);

		helper.assertTrue(backSlot(helper, player).container == backInventory(helper, player),
				"l'emplacement dos de l'inventaire du joueur doit viser l'inventaire Trinkets actuel");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void takingFromSlotDoesNotDuplicate(GameTestHelper helper) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();
		backInventory(helper, player).setItem(0, new ItemStack(Items.SADDLE));

		new InventoryMenu(player.getInventory(), true, player);
		ItemStack taken = backSlot(helper, player).remove(1);

		helper.assertTrue(taken.is(Items.SADDLE), "la selle doit sortir de l'emplacement, obtenu " + taken);
		helper.assertTrue(backInventory(helper, player).getItem(0).isEmpty(),
				"la selle retirée ne doit plus être dans l'emplacement (duplication)");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void puttingInSlotIsKept(GameTestHelper helper) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();

		new InventoryMenu(player.getInventory(), true, player);
		backSlot(helper, player).set(new ItemStack(Items.SADDLE));

		helper.assertTrue(backInventory(helper, player).getItem(0).is(Items.SADDLE),
				"la selle posée doit être dans l'emplacement sauvegardé (perte)");
		helper.succeed();
	}

	private static TrinketInventory backInventory(GameTestHelper helper, ServerPlayer player) {
		TrinketComponent component = TrinketsApi.getTrinketComponent(player).orElse(null);
		helper.assertTrue(component != null, "composant Trinkets absent");
		TrinketInventory inventory = component.getInventory().get("chest").get("back");
		helper.assertTrue(inventory != null && inventory.getContainerSize() == 1, "emplacement chest/back absent");
		return inventory;
	}

	private static Slot backSlot(GameTestHelper helper, ServerPlayer player) {
		for (Slot slot : player.inventoryMenu.slots) {
			if (slot.container instanceof TrinketInventory inventory
					&& inventory.getSlotType().getGroup().equals("chest")
					&& inventory.getSlotType().getName().equals("back")) {
				return slot;
			}
		}
		helper.fail("emplacement chest/back absent de l'inventaire du joueur");
		throw new IllegalStateException();
	}
}
