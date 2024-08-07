package com.smanzana.nostrumaetheria.integration.curios.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent.Phase;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.capabilities.INostrumMagic;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.integration.curios.items.INostrumCurio;
import com.smanzana.nostrummagica.item.ISpellEquipment;
import com.smanzana.nostrummagica.item.armor.ICapeProvider;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;
import com.smanzana.nostrummagica.util.ColorUtil;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public class AetherCloakItem extends AetherItem implements INostrumCurio, ILoreTagged, ISpellEquipment, ICapeProvider {
	
	public static interface UpgradeToggleFunc {
		public boolean isSet(@Nonnull ItemStack stack);
		public void toggle(@Nonnull ItemStack stack);
	}
	
	public static interface UpgradeColorFunc {
		public boolean isSet(@Nonnull ItemStack stack, DyeColor color);
		public void set(@Nonnull ItemStack stack, DyeColor color);
	}
	
	public static enum ToggleUpgrades {
		SHOW_WINGS(new UpgradeToggleFunc() {
			@Override
			public boolean isSet(ItemStack stack) {
				return AetheriaCurios.aetherCloak.getDisplayWings(stack);
			}
			@Override
			public void toggle(ItemStack stack) {
				AetheriaCurios.aetherCloak.setDisplayWings(stack, true);
			}
		}),
		TRIMMED(new UpgradeToggleFunc() {
			@Override
			public boolean isSet(ItemStack stack) {
				return AetheriaCurios.aetherCloak.getDisplayTrimmed(stack);
			}
			@Override
			public void toggle(ItemStack stack) {
				AetheriaCurios.aetherCloak.setDisplayTrimmed(stack, true);
			}
		}),
		SHOW_RUNES(new UpgradeToggleFunc() {
			@Override
			public boolean isSet(ItemStack stack) {
				return AetheriaCurios.aetherCloak.getDisplayRunes(stack);
			}
			@Override
			public void toggle(ItemStack stack) {
				AetheriaCurios.aetherCloak.setDisplayRunes(stack, true);
			}
		});
		
		private final UpgradeToggleFunc func;
		
		private ToggleUpgrades(UpgradeToggleFunc func) {
			this.func = func;
		}
		
		public UpgradeToggleFunc getFunc() {
			return func;
		}
	}
	
	public static enum ColorUpgrades {
		COLOR_RUNES(new UpgradeColorFunc() {
			@Override
			public boolean isSet(ItemStack stack, DyeColor color) {
				return AetheriaCurios.aetherCloak.getRuneColor(stack) == color;
			}
			@Override
			public void set(ItemStack stack, DyeColor color) {
				AetheriaCurios.aetherCloak.setRuneColor(stack, color);
			}
		}),
		COLOR_INSIDE(new UpgradeColorFunc() {
			@Override
			public boolean isSet(ItemStack stack, DyeColor color) {
				return AetheriaCurios.aetherCloak.getInsideColor(stack) == color;
			}
			@Override
			public void set(ItemStack stack, DyeColor color) {
				AetheriaCurios.aetherCloak.setInsideColor(stack, color);
			}
		}),
		COLOR_OUTSIDE(new UpgradeColorFunc() {
			@Override
			public boolean isSet(ItemStack stack, DyeColor color) {
				return AetheriaCurios.aetherCloak.getOutsideColor(stack) == color;
			}
			@Override
			public void set(ItemStack stack, DyeColor color) {
				AetheriaCurios.aetherCloak.setOutsideColor(stack, color);
			}
		});
		
		private final UpgradeColorFunc func;
		
		private ColorUpgrades(UpgradeColorFunc func) {
			this.func = func;
		}
		
		public UpgradeColorFunc getFunc() {
			return func;
		}
	}

	public static final String ID = "aether_cloak";
	private static final String NBT_AETHER_PROGRESS = "aether_progress";
	private static final String NBT_AETHER_SPENDER = "aether_spender";
	private static final String NBT_DISPLAY_WINGS = "display_wings";
	private static final String NBT_DISPLAY_TRIMMED = "display_trimmed";
	private static final String NBT_DISPLAY_COLOR_OUTSIDE = "color_outside";
	private static final String NBT_DISPLAY_COLOR_INSIDE = "color_inside";
	private static final String NBT_DISPLAY_RUNES = "display_runes";
	private static final String NBT_DISPLAY_COLOR_RUNES = "color_runes";
	
	private static final UUID MANA_MOD_ID = UUID.fromString("31125EE0-CF1F-493D-9AAD-CBC923841E26");
	
	private static final int MAX_AETHER_MIN = 5000;
	private static final int MAX_AETHER_MAX = 100000;
	
	public static final DyeColor COLOR_DEFAULT_OUTSIDE = DyeColor.BLUE;
	public static final DyeColor COLOR_DEFAULT_INSIDE = DyeColor.GRAY;
	public static final DyeColor COLOR_DEFAULT_RUNES = DyeColor.WHITE;

	public AetherCloakItem() {
		super(AetheriaCurios.PropCurio());
		
		MinecraftForge.EVENT_BUS.addListener(AetherCloakItem::onAetherDraw);
	}
	
//    @Override
//	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
//		if (this.isInCreativeTab(tab)) {
//			//super.getSubItems(itemIn, tab, subItems);
//			
//			ItemStack basic = new ItemStack(instance());
//			subItems.add(basic);
//			
//			ItemStack full = new ItemStack(instance());
//			
//			IAetherHandlerComponent comp = this.getAetherHandler(full);
//			comp.setMaxAether(MAX_AETHER_MAX);
//			comp.setAether(MAX_AETHER_MAX);
//			AetherItem.SaveItem(full);
//			
//	//		tag.putInt(NBT_AETHER_MAX, MAX_AETHER_MAX);
//	//		tag.putInt(NBT_AETHER_STORED, MAX_AETHER_MAX);
//			
//			subItems.add(full);
//			
//			ItemStack complete = new ItemStack(instance());
//			comp = this.getAetherHandler(complete);
//			comp.setMaxAether(MAX_AETHER_MAX);
//			comp.setAether(MAX_AETHER_MAX);
//			AetherItem.SaveItem(complete);
//			setRuneColor(complete, DyeColor.RED);
//			setOutsideColor(complete, DyeColor.BLACK);
//			setInsideColor(complete, DyeColor.PINK);
//			setDisplayWings(complete, true);
//			setDisplayRunes(complete, true);
//			setDisplayTrimmed(complete, true);
//			setAetherCaster(complete, true);
//			subItems.add(complete);
//		}
//	}
	
	@Override
	public String getLoreKey() {
		return "nostrum_aether_cloak";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Cloak";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		// Handled by NostrumCurio class
//		if (I18n.contains("item.aether_cloak.desc")) {
//			// Format with placeholders for blue and red formatting
//			String translation = I18n.format("item.aether_cloak.desc", TextFormatting.GRAY, TextFormatting.BLUE, TextFormatting.DARK_RED);
//			if (translation.trim().isEmpty())
//				return;
//			String lines[] = translation.split("\\|");
//			for (String line : lines) {
//				tooltip.add(line);
//			}
//		}
		
		final boolean displayRunes = getDisplayRunes(stack);
		final boolean displayWings = getDisplayWings(stack);
		final boolean displayTrimmed = getDisplayTrimmed(stack);
		DyeColor colorRunes = getRuneColor(stack);
		DyeColor colorOutside = getOutsideColor(stack);
		DyeColor colorInside = getInsideColor(stack);
		final boolean castUpgrade = isAetherCaster(stack);

		if (castUpgrade) {
			tooltip.add(new TranslationTextComponent("item.aether_cloak.spender").mergeStyle(TextFormatting.DARK_GREEN));
		}
		if (displayTrimmed) {
			tooltip.add(new TranslationTextComponent("item.aether_cloak.trimmed").mergeStyle(TextFormatting.DARK_GRAY));
		}
		if (displayRunes) {
			tooltip.add(new TranslationTextComponent("item.aether_cloak.runed").mergeStyle(TextFormatting.DARK_GRAY));
		}
		if (displayWings) {
			tooltip.add(new TranslationTextComponent("item.aether_cloak.wings").mergeStyle(TextFormatting.DARK_GRAY));
		}
		if (colorOutside != COLOR_DEFAULT_OUTSIDE) {
			String name = I18n.format(colorOutside.getTranslationKey());
			name = name.toLowerCase();
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			tooltip.add(new TranslationTextComponent("item.aether_cloak.color.outside", name).mergeStyle(TextFormatting.DARK_BLUE));
		}
		if (colorInside != COLOR_DEFAULT_INSIDE) {
			String name = I18n.format(colorInside.getTranslationKey());
			name = name.toLowerCase();
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			tooltip.add(new TranslationTextComponent("item.aether_cloak.color.inside", name).mergeStyle(TextFormatting.DARK_BLUE));
		}
		if (colorRunes != COLOR_DEFAULT_RUNES) {
			String name = I18n.format(colorRunes.getTranslationKey());
			name = name.toLowerCase();
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			tooltip.add(new TranslationTextComponent("item.aether_cloak.color.runes", name).mergeStyle(TextFormatting.DARK_BLUE));
		}
	}
	
	public static float GetAetherProgress(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			return 0;
		}
		
		return nbt.getFloat(NBT_AETHER_PROGRESS);
	}
	
	protected static void SetAetherProgress(ItemStack stack, float progress) {
		if (stack.isEmpty()) {
			return;
		}
		
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putFloat(NBT_AETHER_PROGRESS, progress);
		stack.setTag(nbt);
	}
	
	@Override
	public void onEquipped(ItemStack itemstack, LivingEntity player) {
		INostrumMagic attr = NostrumMagica.getMagicWrapper(player);
		if (attr == null) {
			return;
		}
		
		attr.addManaRegenModifier(MANA_MOD_ID, 0.25f);
	}
	
	/**
	 * This method is called when the bauble is unequipped by a player
	 */
	@Override
	public void onUnequipped(ItemStack itemstack, LivingEntity player) {	
		INostrumMagic attr = NostrumMagica.getMagicWrapper(player);
		if (attr == null) {
			return;
		}
		
		attr.removeManaRegenModifier(MANA_MOD_ID);
	}

	/**
	 * can this bauble be placed in a bauble slot
	 */
	@Override
	public boolean canEquip(ItemStack itemstack, LivingEntity player) {
		if (player.world.isRemote && player != NostrumMagica.instance.proxy.getPlayer()) {
			return true;
		}
		
		INostrumMagic attr = NostrumMagica.getMagicWrapper(player);
		return attr != null && attr.isUnlocked();
	}

	@Override
	public void apply(LivingEntity caster, SpellCastSummary summary, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		
		if (!(stack.getItem() instanceof AetherCloakItem)) {
			return;
		}
		
		summary.addEfficiency(.25f);
		
		if (isAetherCaster(stack) && summary.getReagentCost() > 0f) {
			// Attempt to spend aether to cover reagent cost
			// 100 aether for full cast
			final int cost = (int) Math.ceil(100 * summary.getReagentCost());
			if (this.getAether(stack) >= cost) {
				if ((!(caster instanceof PlayerEntity) || !((PlayerEntity) caster).isCreative()) && !caster.world.isRemote) {
					int taken = this.deductAether(stack, cost);
					if (taken > 0) {
						growFromAether(stack, -taken);
					}
				}
				summary.addReagentCost(-summary.getReagentCost());
			}
		}
	}
	
	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		;
	}
	
	public static void onAetherDraw(LivingAetherDrawEvent event) {
		// Aether cloak contributes aether after inventory items if it has any
		if (event.phase == Phase.BEFORE_LATE) {
			if (event.getAmtRemaining() > 0 && event.getEntity() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				
				IInventory inv = NostrumMagica.instance.curios.getCurios(player);
				if (inv != null) {
					for (int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);
						if (stack.isEmpty() || !(stack.getItem() instanceof AetherCloakItem))
							continue;
						
						AetherCloakItem cloak = (AetherCloakItem) stack.getItem();
						CompoundNBT nbt = stack.getTag();
						if (nbt != null) {
							final int taken = cloak.deductAether(stack, event.getAmtRemaining());
							
							event.contributeAmt(taken);
							
							if (taken > 0) {
								cloak.growFromAether(stack, -taken);
							}
							
							if (event.isFinished()) {
								break; // no more needed
							}
						}
					}
				}
			}
		}
	}
	
	protected void growFromAether(ItemStack stack, int diff) {
		// Award more capacity any time aether is drawn from the cape
		if (diff < 0) {
			// for every 100 aether, award 5?
			float adv = (float) -diff / 20f + GetAetherProgress(stack);
			final int whole = (int) adv;
			adv -= whole;
			
			IAetherHandlerComponent comp = this.getAetherHandler(stack);
			int currentMax = Math.min(comp.getMaxAether(null) + whole, MAX_AETHER_MAX);
			CompoundNBT nbt = stack.getTag();
			if (nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.putFloat(NBT_AETHER_PROGRESS, adv);
			stack.setTag(nbt);
			comp.setMaxAether(currentMax);
			AetherItem.SaveItem(stack);
		}
	}
	
	public DyeColor getRuneColor(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_COLOR_RUNES)) {
			return COLOR_DEFAULT_RUNES;
		}
		
		int colorIdx = nbt.getInt(NBT_DISPLAY_COLOR_RUNES);
		return DyeColor.values()[colorIdx % DyeColor.values().length];
	}
	
	public void setRuneColor(ItemStack stack, DyeColor color) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putInt(NBT_DISPLAY_COLOR_RUNES, color.ordinal());
		stack.setTag(nbt);
	}
	
	public DyeColor getOutsideColor(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_COLOR_OUTSIDE)) {
			return COLOR_DEFAULT_OUTSIDE;
		}
		
		int colorIdx = nbt.getInt(NBT_DISPLAY_COLOR_OUTSIDE);
		return DyeColor.values()[colorIdx % DyeColor.values().length];
	}
	
	public void setOutsideColor(ItemStack stack, DyeColor color) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putInt(NBT_DISPLAY_COLOR_OUTSIDE, color.ordinal());
		stack.setTag(nbt);
	}

	public DyeColor getInsideColor(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_COLOR_INSIDE)) {
			return COLOR_DEFAULT_INSIDE;
		}
		
		int colorIdx = nbt.getInt(NBT_DISPLAY_COLOR_INSIDE);
		return DyeColor.values()[colorIdx % DyeColor.values().length];
	}
	
	public void setInsideColor(ItemStack stack, DyeColor color) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putInt(NBT_DISPLAY_COLOR_INSIDE, color.ordinal());
		stack.setTag(nbt);
	}
	
	public boolean getDisplayWings(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_WINGS)) {
			return false;
		}
		return nbt.getBoolean(NBT_DISPLAY_WINGS);
	}
	
	public void setDisplayWings(ItemStack stack, boolean display) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putBoolean(NBT_DISPLAY_WINGS, display);
		stack.setTag(nbt);
	}
	
	public boolean getDisplayTrimmed(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_TRIMMED)) {
			return false;
		}
		return nbt.getBoolean(NBT_DISPLAY_TRIMMED);
	}
	
	public void setDisplayTrimmed(ItemStack stack, boolean trimmed) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putBoolean(NBT_DISPLAY_TRIMMED, trimmed);
		stack.setTag(nbt);
	}
	
	public boolean getDisplayRunes(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_DISPLAY_RUNES)) {
			return false;
		}
		return nbt.getBoolean(NBT_DISPLAY_RUNES);
	}
	
	public void setDisplayRunes(ItemStack stack, boolean display) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putBoolean(NBT_DISPLAY_RUNES, display);
		stack.setTag(nbt);
	}
	
	public boolean isAetherCaster(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null || !nbt.contains(NBT_AETHER_SPENDER)) {
			return false;
		}
		return nbt.getBoolean(NBT_AETHER_SPENDER);
	}
	
	public void setAetherCaster(ItemStack stack, boolean caster) {
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		
		nbt.putBoolean(NBT_AETHER_SPENDER, caster);
		stack.setTag(nbt);
	}

	@Override
	protected int getDefaultMaxAether(ItemStack stack) {
		return MAX_AETHER_MIN;
	}

	@Override
	protected boolean shouldShowAether(ItemStack stack, PlayerEntity playerIn, boolean advanced) {
		return true;
	}

	@Override
	protected boolean shouldAutoFill(ItemStack stack, World worldIn, Entity entityIn) {
		return false;
	}

	@Override
	public boolean canBeDrawnFrom(@Nullable ItemStack stack, @Nullable World worldIn, Entity entityIn) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldRenderCape(LivingEntity entity, ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof AetherCloakItem;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation[] getCapeModels(LivingEntity entity, ItemStack stack) {
		final boolean trimmed = getDisplayTrimmed(stack);
		final boolean runes = getDisplayRunes(stack);
		if (trimmed) {
			if (runes) {
				return AetherCloakModels.CapeModelsTrimmedDecor;
			} else {
				return AetherCloakModels.CapeModelsTrimmed;
			}
		} else {
			if (runes) {
				return AetherCloakModels.CapeModelsFullDecor;
			} else {
				return AetherCloakModels.CapeModelsFull;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public RenderType[] getCapeRenderTypes(LivingEntity entity, ItemStack stack) {
		final boolean trimmed = getDisplayTrimmed(stack);
		final boolean runes = getDisplayRunes(stack);
		if (trimmed) {
			if (runes) {
				return AetherCloakModels.CapeRenderTypesTrimmedDecor;
			} else {
				return AetherCloakModels.CapeRenderTypesTrimmed;
			}
		} else {
			if (runes) {
				return AetherCloakModels.CapeRenderTypesFullDecor;
			} else {
				return AetherCloakModels.CapeRenderTypesFull;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void preRender(Entity entity, int model, ItemStack stack, MatrixStack matrixStackIn, float headYaw, float partialTicks) {
		if (model == 2) {
			// Decor needs to be scaled just a litle to not z fight
			matrixStackIn.scale(1.001f, 1.001f, 1.001f);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldPreventOtherRenders(LivingEntity entity, ItemStack stack) {
		return !getDisplayWings(stack);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColor(LivingEntity entity, ItemStack stack, int model) {
		if (model == 0) {
			return ColorUtil.dyeToARGB(getInsideColor(stack));
		} else if (model == 1) {
			return ColorUtil.dyeToARGB(getOutsideColor(stack));
		} else {
			final int glowPeriod = 20 * 8;
			final float brightnessMod = (float) Math.sin(((float) entity.ticksExisted % (float) glowPeriod) / (float) glowPeriod
					* Math.PI * 2) // -1 to 1
					* .25f // -.25 to .25
					+ .5f; // .25 to .75
			
			float[] colors = SheepEntity.getDyeRgb(getRuneColor(stack));
			return ColorUtil.colorToARGB(colors[0] * brightnessMod,
					colors[1] * brightnessMod,
					colors[2] * brightnessMod);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getEquippedAttributeModifiers(ItemStack stack) {
		return null;
	}

	@Override
	public boolean hasRender(ItemStack stack, LivingEntity living) {
		return false;
	}

	@Override
	public void doRender(ItemStack stack, MatrixStack matrixStackIn, int index, IRenderTypeBuffer bufferIn, int packedLightIn,
			LivingEntity player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class AetherCloakModels {
		private static final ResourceLocation CapeModelTrimmedOutside = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_trimmed_outside");
		private static final ResourceLocation CapeModelTrimmedInside = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_trimmed_inside");
		private static final ResourceLocation CapeModelTrimmedDecor = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_trimmed_decor");
		private static final ResourceLocation CapeModelFullOutside = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_medium_outside");
		private static final ResourceLocation CapeModelFullInside = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_medium_inside");
		private static final ResourceLocation CapeModelFullDecor = new ResourceLocation(NostrumAetheria.MODID, "entity/cloak_medium_decor");
		private static final RenderType CapeRenderTypeCutout = RenderType.getCutoutMipped(); // block texture atlas and vertex format
		private static final RenderType CapeRenderTypeTranslucent = RenderType.getTranslucent(); // block texture atlas and vertex format
		
		private static final ResourceLocation[] CapeModelsTrimmed = new ResourceLocation[] {
			CapeModelTrimmedInside,
			CapeModelTrimmedOutside,
		};
		
		private static final RenderType[] CapeRenderTypesTrimmed = new RenderType[] {
			CapeRenderTypeCutout,
			CapeRenderTypeCutout,
		};

		private static final ResourceLocation[] CapeModelsTrimmedDecor = new ResourceLocation[] {
			CapeModelTrimmedInside,
			CapeModelTrimmedOutside,
			CapeModelTrimmedDecor,
		};
		
		private static final RenderType[] CapeRenderTypesTrimmedDecor = new RenderType[] {
			CapeRenderTypeCutout,
			CapeRenderTypeCutout,
			CapeRenderTypeTranslucent,
		};
		
		private static final ResourceLocation[] CapeModelsFull = new ResourceLocation[] {
			CapeModelFullInside,
			CapeModelFullOutside,
		};
		
		private static final RenderType[] CapeRenderTypesFull = new RenderType[] {
			CapeRenderTypeCutout,
			CapeRenderTypeCutout,
		};
		
		private static final ResourceLocation[] CapeModelsFullDecor = new ResourceLocation[] {
			CapeModelFullInside,
			CapeModelFullOutside,
			CapeModelFullDecor,
		};
		
		private static final RenderType[] CapeRenderTypesFullDecor = new RenderType[] {
			CapeRenderTypeCutout,
			CapeRenderTypeCutout,
			CapeRenderTypeTranslucent,
		};
		
		public static final ResourceLocation[] AllCapeModels = new ResourceLocation[] {
				CapeModelTrimmedOutside,
				CapeModelTrimmedInside,
				CapeModelTrimmedDecor,
				CapeModelFullOutside,
				CapeModelFullInside,
				CapeModelFullDecor,
		};
	}

}
