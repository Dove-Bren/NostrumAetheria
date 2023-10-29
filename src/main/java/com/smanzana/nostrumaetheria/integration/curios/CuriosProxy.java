package com.smanzana.nostrumaetheria.integration.curios;

import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.integration.curios.items.AetheriaCurios;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.integration.curios.inventory.CurioInventoryWrapper;
import com.smanzana.nostrummagica.integration.curios.items.NostrumCurios;
import com.smanzana.nostrummagica.items.NostrumItemTags;
import com.smanzana.nostrummagica.items.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.items.SpellRune;
import com.smanzana.nostrummagica.research.NostrumResearch;
import com.smanzana.nostrummagica.research.NostrumResearch.NostrumResearchTab;
import com.smanzana.nostrummagica.research.NostrumResearch.Size;
import com.smanzana.nostrummagica.rituals.RitualRecipe;
import com.smanzana.nostrummagica.rituals.RitualRegistry;
import com.smanzana.nostrummagica.rituals.outcomes.OutcomeModifyCenterItemGeneric;
import com.smanzana.nostrummagica.rituals.outcomes.OutcomeSpawnItem;
import com.smanzana.nostrummagica.rituals.requirements.RRequirementResearch;
import com.smanzana.nostrummagica.spells.EMagicElement;
import com.smanzana.nostrummagica.spells.components.triggers.DamagedTrigger;
import com.smanzana.nostrummagica.spells.components.triggers.SelfTrigger;
import com.smanzana.nostrummagica.utils.Ingredients;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

public class CuriosProxy {

	@SubscribeEvent
	public void sendImc(InterModEnqueueEvent evt) {
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("ring").setSize(2));
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("body"));
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
		MinecraftForge.EVENT_BUS.register(this);
		registerCurioQuests();
		registerCurioRituals();
		registerCurioResearch();
		registerLore();
	}
	
	private void registerCurioQuests() {
		
	}
	
	private void registerCurioRituals() {
		RitualRecipe recipe;
		
		// Try to use silver, but use iron if no silver is in the modpack
		Ingredient silver = NostrumItemTags.Items.SilverIngot.getAllElements().isEmpty()
				? Ingredient.fromTag(Tags.Items.INGOTS_IRON)
				: Ingredient.fromTag(NostrumItemTags.Items.SilverIngot);
		
		recipe = RitualRecipe.createTier3("shield_ring_small",
				new ItemStack(AetheriaCurios.ringShieldSmall),
				EMagicElement.EARTH,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.fromItems(NostrumCurios.ringSilver),
				new Ingredient[] {Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall), Ingredients.MatchNBT(SpellRune.getRune(SelfTrigger.instance())), Ingredient.fromTag(NostrumItemTags.Items.CrystalMedium), Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall)},
				new RRequirementResearch("shield_rings"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.ringShieldSmall)));
		RitualRegistry.instance().addRitual(recipe);
		
		recipe = RitualRecipe.createTier3("shield_ring_large",
				new ItemStack(AetheriaCurios.ringShieldLarge),
				EMagicElement.EARTH,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.fromItems(AetheriaCurios.ringShieldSmall),
				new Ingredient[] {Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall), silver, Ingredient.fromTag(NostrumItemTags.Items.CrystalMedium), Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall)},
				new RRequirementResearch("shield_rings"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.ringShieldLarge)));
		RitualRegistry.instance().addRitual(recipe);
		
		recipe = RitualRecipe.createTier3("elude_cape_small",
				new ItemStack(AetheriaCurios.eludeCape),
				EMagicElement.WIND,
				new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT},
				Ingredient.fromTag(ItemTags.WOOL),
				new Ingredient[] {Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall), Ingredients.MatchNBT(SpellRune.getRune(DamagedTrigger.instance())), Ingredient.fromTag(NostrumItemTags.Items.CrystalMedium), Ingredient.fromTag(NostrumItemTags.Items.CrystalSmall)},
				new RRequirementResearch("elude_capes"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.eludeCape)));
		RitualRegistry.instance().addRitual(recipe);
		
		recipe = RitualRecipe.createTier3("aether_cloak",
				new ItemStack(AetheriaCurios.aetherCloak),
				EMagicElement.ICE,
				new ReagentType[] {ReagentType.MANDRAKE_ROOT, ReagentType.SPIDER_SILK, ReagentType.BLACK_PEARL, ReagentType.SKY_ASH},
				Ingredient.fromItems(AetheriaBlocks.smallBattery),
				new Ingredient[] {Ingredient.fromItems(AetheriaItems.aetherGem), Ingredient.fromItems(AetheriaCurios.eludeCape), Ingredient.fromTag(NostrumItemTags.Items.CrystalLarge), Ingredient.fromItems(AetheriaItems.aetherGem)},
				new RRequirementResearch("aether_cloaks"),
				new OutcomeSpawnItem(new ItemStack(AetheriaCurios.aetherCloak)));
		RitualRegistry.instance().addRitual(recipe);
		
		ItemStack casterCloak = new ItemStack(AetheriaCurios.aetherCloak);
		AetheriaCurios.aetherCloak.setAetherCaster(casterCloak, true);
		recipe = RitualRecipe.createTier3("aether_cloak_caster_upgrade",
				casterCloak,
				EMagicElement.FIRE,
				new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.CRYSTABLOOM},
				Ingredient.fromItems(AetheriaCurios.aetherCloak),
				new Ingredient[] {Ingredient.fromItems(AetheriaItems.passivePendant), Ingredient.fromTag(NostrumItemTags.Items.CrystalMedium), Ingredient.EMPTY, Ingredient.fromItems(AetheriaItems.passivePendant)},
				new RRequirementResearch("aether_cloaks"),
				new OutcomeModifyCenterItemGeneric((world, player, item, otherItems, centerPos, recipeIn) -> {
					if (!item.isEmpty() && item.getItem() instanceof AetherCloakItem) {
						((AetherCloakItem) item.getItem()).setAetherCaster(item, true);
					}
				}, Lists.newArrayList("Allows using aether from the cloak in place of reagents")));
		RitualRegistry.instance().addRitual(recipe);
	}
	
	private void registerCurioResearch() {
		NostrumResearch.startBuilding()
			.parent("rings")
			.hiddenParent("kani")
			.hiddenParent("aether_gem")
			.reference("ritual::shield_ring_small", "ritual.shield_ring_small.name")
			.reference("ritual::shield_ring_large", "ritual.shield_ring_large.name")
		.build("shield_rings", NostrumResearchTab.OUTFITTING, Size.NORMAL, -4, -1, true, new ItemStack(AetheriaCurios.ringShieldSmall));

		NostrumResearch.startBuilding()
			.parent("belts")
			.hiddenParent("shield_rings")
			.reference("ritual::elude_cape_small", "ritual.elude_cape_small.name")
		.build("elude_capes", NostrumResearchTab.OUTFITTING, Size.NORMAL, -6, 0, true, new ItemStack(AetheriaCurios.eludeCape));
		
		NostrumResearch.startBuilding()
			.parent("elude_capes")
			.reference("ritual::aether_cloak", "ritual.aether_cloak.name")
			.reference("ritual::aether_cloak_caster_upgrade", "ritual.aether_cloak_caster_upgrade.name")
		.build("aether_cloaks", NostrumResearchTab.OUTFITTING, Size.NORMAL, -6, 1, true, new ItemStack(AetheriaCurios.aetherCloak));
	}
	
	private void registerLore() {
		;
	}
	
	public void reinitResearch() {
		registerCurioResearch();
	}
	
	public IInventory getCurios(PlayerEntity player) {
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
