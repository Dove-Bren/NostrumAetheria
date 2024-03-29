package com.smanzana.nostrumaetheria.items;

import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Basic hand-held battery
 * @author Skyler
 *
 */
public class AetherGem extends AetherItem implements ILoreTagged {

	private static final int MAX_AETHER = 1000;
	
	public AetherGem() {
		super(AetheriaItems.PropUnstackable()
				.maxDamage(MAX_AETHER));
	}
    
    @Override
	public String getLoreKey() {
		return "aetheria_aether_gem";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Gem";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether Gems are special gems that can store aether!");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether Gems are special gems that can store aether!", "Created from a kani gem, each aether gem seems to store at most 1000 aether.", "Items that take aether will automatically draw from any available aether gems in your inventory.");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("item.info.aether_gem.desc"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	@Override
	protected void onAetherChange(ItemStack stack, int currentAether) {
		super.onAetherChange(stack, currentAether);
		this.setDurability(stack);
	}
	
	private void setDurability(ItemStack pendant) {
		int aether = getAether(pendant);
		pendant.setDamage(MAX_AETHER - aether);
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		// Update durability to be correct as soon as it's created
		setDurability(stack);
	}
	
	@Override
	protected void onFirstTick(ItemStack stack, World worldIn, Entity entityIn) {
		super.onFirstTick(stack, worldIn, entityIn);
		this.setDurability(stack);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	protected int getDefaultMaxAether(ItemStack stack) {
		return MAX_AETHER;
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
	public boolean canBeDrawnFrom(ItemStack stack, World worldIn, Entity entityIn) {
		return true;
	}
	
}
