package com.smanzana.nostrumaetheria.integration.curios;

import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.integration.curios.items.AetheriaCurios;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.crafting.NostrumTags;
import com.smanzana.nostrummagica.integration.curios.inventory.CurioInventoryWrapper;
import com.smanzana.nostrummagica.integration.curios.items.NostrumCurios;
import com.smanzana.nostrummagica.item.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.item.SpellRune;
import com.smanzana.nostrummagica.progression.requirement.ResearchRequirement;
import com.smanzana.nostrummagica.progression.research.NostrumResearch;
import com.smanzana.nostrummagica.progression.research.NostrumResearch.NostrumResearchTab;
import com.smanzana.nostrummagica.progression.research.NostrumResearch.Size;
import com.smanzana.nostrummagica.ritual.RitualRecipe;
import com.smanzana.nostrummagica.ritual.RitualRegistry;
import com.smanzana.nostrummagica.ritual.outcome.OutcomeModifyCenterItemGeneric;
import com.smanzana.nostrummagica.ritual.outcome.OutcomeSpawnItem;
import com.smanzana.nostrummagica.spell.EMagicElement;
import com.smanzana.nostrummagica.spell.component.shapes.NostrumSpellShapes;
import com.smanzana.nostrummagica.util.Ingredients;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class CuriosProxy {

	@SubscribeEvent
	public void sendImc(InterModEnqueueEvent evt) {
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
	}
	
	private boolean enabled;
	
	public CuriosProxy() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}
	
	public void enable() {
		this.enabled = true;
	}

	public void preInit() {
		// TODO Auto-generated method stub
		
	}
	
	public void init() {
		MinecraftForge.EVENT_BUS.addListener(this::registerCurioRituals);
		registerCurioQuests();
//		registerCurioRituals();
		registerCurioResearch();
		registerLore();
	}
	
	private void registerCurioQuests() {
		
	}
	
	// @SubscribeEvent registered manually to avoid bus collision
	public void registerCurioRituals(RitualRegistry.RitualRegisterEvent event) {
		RitualRegistry registry = event.registry;
		RitualRecipe recipe;
		
		// Try to use silver, but use iron if no silver is in the modpack
		Ingredient silver = //NostrumTags.Items.SilverIngot.getValues().isEmpty() ?
				Ingredient.of(Tags.Items.INGOTS_IRON)
				//: Ingredient.of(NostrumTags.Items.SilverIngot)
				;
		
		recipe = RitualRecipe.createTier3("shield_ring_small",
				new ItemStack(AetheriaCurios.ringShieldSmall),
				EMagicElement.EARTH,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.of(NostrumCurios.ringSilver),
				new Ingredient[] {Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredients.MatchNBT(SpellRune.getRune(NostrumSpellShapes.Self)), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumTags.Items.CrystalSmall)},
				new ResearchRequirement("shield_rings"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.ringShieldSmall)));
		registry.register(recipe);
		
		recipe = RitualRecipe.createTier3("shield_ring_large",
				new ItemStack(AetheriaCurios.ringShieldLarge),
				EMagicElement.EARTH,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.of(AetheriaCurios.ringShieldSmall),
				new Ingredient[] {Ingredient.of(NostrumTags.Items.CrystalSmall), silver, Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumTags.Items.CrystalSmall)},
				new ResearchRequirement("shield_rings"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.ringShieldLarge)));
		registry.register(recipe);
		
		recipe = RitualRecipe.createTier3("elude_cape_small",
				new ItemStack(AetheriaCurios.eludeCape),
				EMagicElement.WIND,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.of(ItemTags.WOOL),
				new Ingredient[] {Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredients.MatchNBT(SpellRune.getRune(NostrumSpellShapes.OnDamage)), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumTags.Items.CrystalSmall)},
				new ResearchRequirement("elude_capes"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.eludeCape)));
		registry.register(recipe);
		
		recipe = RitualRecipe.createTier3("aether_cloak",
				new ItemStack(AetheriaCurios.aetherCloak),
				EMagicElement.ICE,
				new ReagentType[] {ReagentType.MANDRAKE_ROOT, ReagentType.SPIDER_SILK, ReagentType.BLACK_PEARL, ReagentType.SKY_ASH},
				Ingredient.of(AetheriaBlocks.smallBattery),
				new Ingredient[] {Ingredient.of(AetheriaItems.aetherGem), Ingredient.of(AetheriaCurios.eludeCape), Ingredient.of(NostrumTags.Items.CrystalLarge), Ingredient.of(AetheriaItems.aetherGem)},
				new ResearchRequirement("aether_cloaks"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.aetherCloak)));
		registry.register(recipe);
		
		ItemStack casterCloak = new ItemStack(AetheriaCurios.aetherCloak);
		AetheriaCurios.aetherCloak.setAetherCaster(casterCloak, true);
		recipe = RitualRecipe.createTier3("aether_cloak_caster_upgrade",
				casterCloak,
				EMagicElement.FIRE,
				new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.CRYSTABLOOM},
				Ingredient.of(AetheriaCurios.aetherCloak),
				new Ingredient[] {Ingredient.of(AetheriaItems.passivePendant), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.EMPTY, Ingredient.of(AetheriaItems.passivePendant)},
				new ResearchRequirement("aether_cloaks"),
				new OutcomeModifyCenterItemGeneric((world, player, item, otherItems, centerPos, recipeIn) -> {
					if (!item.isEmpty() && item.getItem() instanceof AetherCloakItem) {
						((AetherCloakItem) item.getItem()).setAetherCaster(item, true);
					}
				}, Lists.newArrayList("Allows using aether from the cloak in place of reagents")));
		registry.register(recipe);
		
		recipe = RitualRecipe.createTier3("aether_sight_pendant",
				new ItemStack(AetheriaCurios.sightPendant),
				null,
				new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.CRYSTABLOOM},
				Ingredient.of(AetheriaItems.aetherSightTool),
				new Ingredient[] {Ingredient.of(Tags.Items.INGOTS_GOLD)},
				new ResearchRequirement("aether_sight_pendant"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.sightPendant)));
		registry.register(recipe);
	}
	
	private void registerCurioResearch() {
		NostrumResearch.startBuilding()
			.hiddenParent("rings")
			.hiddenParent("kani")
			.hiddenParent("aether_gem")
			.reference("ritual::shield_ring_small", "ritual.shield_ring_small.name")
			.reference("ritual::shield_ring_large", "ritual.shield_ring_large.name")
		.build("shield_rings", (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 1, 0, true, new ItemStack(AetheriaCurios.ringShieldSmall));

		NostrumResearch.startBuilding()
			.hiddenParent("belts")
			.hiddenParent("shield_rings")
			.reference("ritual::elude_cape_small", "ritual.elude_cape_small.name")
		.build("elude_capes", (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 2, 0, true, new ItemStack(AetheriaCurios.eludeCape));
		
		NostrumResearch.startBuilding()
			.parent("elude_capes")
			.reference("ritual::aether_cloak", "ritual.aether_cloak.name")
			.reference("ritual::aether_cloak_caster_upgrade", "ritual.aether_cloak_caster_upgrade.name")
		.build("aether_cloaks", (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 2, 1, true, new ItemStack(AetheriaCurios.aetherCloak));
		
		NostrumResearch.startBuilding()
			.parent("aether_sight_item")
			.reference("ritual::aether_sight_pendant", "ritual.aether_sight_pendant.name")
		.build("aether_sight_pendant", (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 0, 1, true, new ItemStack(AetheriaCurios.sightPendant));
	}
	
	private void registerLore() {
		;
	}
	
	public void reinitResearch() {
		registerCurioResearch();
	}
	
	public Container getCurios(Player player) {
		if (!enabled) {
			return null;
		}
		
		return CurioInventoryWrapper.getCuriosInventory(player);
	}
	
	public void forEachCurio(LivingEntity entity, Predicate<ItemStack> action) {
		if (!enabled) {
			return;
		}
		
		CurioInventoryWrapper.forEach(entity, action);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
}
