package com.smanzana.nostrumaetheria.research;

import com.smanzana.nostrumaetheria.api.lib.AetheriaResearches;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.entity.WispEntity;
import com.smanzana.nostrummagica.item.NostrumItems;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.progression.research.NostrumResearch;
import com.smanzana.nostrummagica.progression.research.NostrumResearch.Size;
import com.smanzana.nostrummagica.progression.research.NostrumResearchTab;
import com.smanzana.nostrummagica.progression.research.NostrumResearches;

import net.minecraft.world.item.ItemStack;

public class AetheriaResearchesImpl {

	public static NostrumResearch Active_Pendant;
	public static NostrumResearch Aether_Bath;
	public static NostrumResearch Aether_Battery;
	public static NostrumResearch Aether_Battery_Adv;
	public static NostrumResearch Aether_Boiler;
	public static NostrumResearch Aether_Carts;
	public static NostrumResearch Aether_Charger;
	public static NostrumResearch Aether_Furnace;
	public static NostrumResearch Aether_Furnace_Adv;
	public static NostrumResearch Aether_Gem;
	public static NostrumResearch Aether_Infusers;
	public static NostrumResearch Aether_Relay;
	public static NostrumResearch Aether_Relay_Enhanced;
	public static NostrumResearch Aether_Repairer;
	public static NostrumResearch Aether_Sight_Item;
	public static NostrumResearch Aether_Unraveler;
	public static NostrumResearch Passive_Pendant;
	public static NostrumResearch WispBlock;
	
	public static void init() {
		Active_Pendant = NostrumResearch.startBuilding()
			.hiddenParent(NostrumResearches.ID_Rituals)
			.hiddenParent(NostrumResearches.ID_Thano_Pendant)
			.lore(NostrumItems.thanoPendant)
			.reference(AetheriaItems.activePendant)
			.reference("ritual::active_pendant", "ritual.active_pendant.name")
		.build(AetheriaResearches.ID_Active_Pendant, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 0, -1, true, new ItemStack(AetheriaItems.activePendant));
	
		Passive_Pendant = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Active_Pendant)
			.hiddenParent(AetheriaResearches.ID_Aether_Bath)
			.hiddenParent(NostrumResearches.ID_Kani)
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.lore(AetheriaItems.mandrakeFlower)
			.reference(AetheriaItems.passivePendant)
			.reference("ritual::passive_pendant", "ritual.passive_pendant.name")
		.build(AetheriaResearches.ID_Passive_Pendant, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, -1, true, new ItemStack(AetheriaItems.passivePendant));
		
		Aether_Furnace = NostrumResearch.startBuilding()
			.hiddenParent(AetheriaResearches.ID_Active_Pendant)
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.reference("ritual::aether_furnace_small", "ritual.aether_furnace_small.name")
		.build(AetheriaResearches.ID_Aether_Furnace, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.GIANT, 0, 0, true, new ItemStack(AetheriaBlocks.smallFurnace));
		
		Aether_Furnace_Adv = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Furnace)
			.hiddenParent(AetheriaResearches.ID_Aether_Boiler)
			.reference("ritual::aether_furnace_medium", "ritual.aether_furnace_medium.name")
			.reference("ritual::aether_furnace_large", "ritual.aether_furnace_large.name")
		.build(AetheriaResearches.ID_Aether_Furnace_Adv, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -1, 1, true, new ItemStack(AetheriaBlocks.largeFurnace));
		
		Aether_Bath = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Furnace)
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.reference("ritual::aether_bath", "ritual.aether_bath.name")
		.build(AetheriaResearches.ID_Aether_Bath, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 2, 1, true, new ItemStack(AetheriaBlocks.bath));
		
		Aether_Charger = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Bath)
			.parent(AetheriaResearches.ID_Aether_Furnace)
			.hiddenParent(AetheriaResearches.ID_Aether_Gem)
			.reference("ritual::aether_charger", "ritual.aether_charger.name")
		.build(AetheriaResearches.ID_Aether_Charger, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 1, true, new ItemStack(AetheriaBlocks.charger));
		
		Aether_Repairer = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Charger)
			.hiddenParent(NostrumResearches.ID_Vani)
			.reference("ritual::aether_repairer", "ritual.aether_repairer.name")
		.build(AetheriaResearches.ID_Aether_Repairer, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 2, true, new ItemStack(AetheriaBlocks.repairer));
		
		Aether_Unraveler = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Repairer)
			.reference("ritual::aether_unraveler", "ritual.aether_unraveler.name")
		.build(AetheriaResearches.ID_Aether_Unraveler, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 3, true, new ItemStack(AetheriaBlocks.unraveler));
		
		Aether_Boiler = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Furnace)
			.reference("ritual::aether_boiler", "ritual.aether_boiler.name")
		.build(AetheriaResearches.ID_Aether_Boiler, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -2, 1, true, new ItemStack(AetheriaBlocks.boiler));
		
		Aether_Battery = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Bath)
			.reference("ritual::aether_battery_small", "ritual.aether_battery_small.name")
			.reference("ritual::aether_battery_medium", "ritual.aether_battery_medium.name")
		.build(AetheriaResearches.ID_Aether_Battery, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.LARGE, 2, 2, true, new ItemStack(AetheriaBlocks.smallBattery));
		
		Aether_Battery_Adv = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Battery)
			.hiddenParent(NostrumResearches.ID_Kani)
			.reference("ritual::aether_battery_large", "ritual.aether_battery_large.name")
			.reference("ritual::aether_battery_giant", "ritual.aether_battery_giant.name")
		.build(AetheriaResearches.ID_Aether_Battery_Adv, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 4, 2, true, new ItemStack(AetheriaBlocks.giantBattery));
		
		Aether_Gem = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Battery)
			.hiddenParent(NostrumResearches.ID_Vani)
			.reference("ritual::aether_gem", "ritual.aether_gem.name")
		.build(AetheriaResearches.ID_Aether_Gem, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 3, 1, true, new ItemStack(AetheriaItems.aetherGem));
		
		Aether_Relay = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Battery)
			.hiddenParent(NostrumResearches.ID_Geogems)
			.hiddenParent(NostrumResearches.ID_Kani)
			.reference("ritual::aether_relay", "ritual.aether_relay.name")
		.build(AetheriaResearches.ID_Aether_Relay, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.LARGE, 2, 3, true, new ItemStack(AetheriaBlocks.relay));
		
		Aether_Relay_Enhanced = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Relay)
			.reference("ritual::enhanced_aether_relay", "ritual.enhanced_aether_relay.name")
			.reference("ritual::enhanced_aether_relay_direct", "ritual.enhanced_aether_relay_direct.name")
		.build(AetheriaResearches.ID_Aether_Relay_Enhanced, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 2, 4, true, new ItemStack(AetheriaBlocks.enhancedRelay));
		
		// TODO if 'pipes' get added for short-range aether transport, consider making that a requirement for pumps & rails and stuff
		// For example, pipes -> pumps -> rails with pumps as a separate thing?
		Aether_Carts = NostrumResearch.startBuilding()
			.parent(AetheriaResearches.ID_Aether_Battery)
			.parent(AetheriaResearches.ID_Aether_Relay)
			.reference("ritual::aether_battery_cart", "ritual.aether_battery_cart.name")
			.reference("ritual::aether_pump", "ritual.aether_pump.name")
		.build(AetheriaResearches.ID_Aether_Carts, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.GIANT, 3, 3, true, new ItemStack(AetheriaItems.aetherBatteryMinecart));
		
		WispBlock = NostrumResearch.startBuilding()
			.hiddenParent(NostrumResearches.ID_Kani)
			.hiddenParent(AetheriaResearches.ID_Aether_Battery)
			.lore(WispEntity.LoreKey)
			.reference("ritual::wisp_crystal", "ritual.wisp_crystal.name")
		.build(AetheriaResearches.ID_WispBlock, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -3, 2, true, new ItemStack(AetheriaBlocks.wispBlock));
		
		Aether_Infusers = NostrumResearch.startBuilding()
			.hiddenParent(AetheriaResearches.ID_Aether_Battery)
			.parent(AetheriaResearches.ID_Aether_Charger)
			.reference("ritual::construct_aether_infuser", "ritual.construct_aether_infuser.name")
			.reference("ritual::make_lens_holder", "ritual.make_lens_holder.name")
			.reference("ritual::make_lens_spread", "ritual.make_lens_spread.name")
			.reference("ritual::make_lens_charge", "ritual.make_lens_wide_charge.name")
			.reference("ritual::make_lens_grow", "ritual.make_lens_grow.name")
			.reference("ritual::make_lens_heal", "ritual.make_lens_heal.name")
			.reference("ritual::make_lens_bore", "ritual.make_lens_bore.name")
			.reference("ritual::make_lens_elevator", "ritual.make_lens_elevator.name")
			.reference("ritual::make_lens_swiftness", "ritual.make_lens_swiftness.name")
		.build(AetheriaResearches.ID_Aether_Infusers, (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 0, 2, true, new ItemStack(AetheriaBlocks.infuser));
		
		Aether_Sight_Item = NostrumResearch.startBuilding()
			.hiddenParent(AetheriaResearches.ID_Aether_Relay)
			.reference("ritual::aether_sight_item", "ritual.aether_sight_item.name")
		.build(AetheriaResearches.ID_Aether_Sight_Item, (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 0, 0, true, new ItemStack(AetheriaItems.aetherSightTool));
	}
	
}
