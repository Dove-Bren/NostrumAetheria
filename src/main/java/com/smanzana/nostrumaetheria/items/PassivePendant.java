package com.smanzana.nostrumaetheria.items;

import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.item.ISpellEquipment;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.spell.SpellCasting;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Advanced thano pendant. Stores and uses aether!
 * @author Skyler
 *
 */
public class PassivePendant extends AetherItem implements ILoreTagged, ISpellEquipment {

	private static final int AETHER_PER_CHARGE = 500;
	private static final int MAX_CHARGES = 3;
	
	public PassivePendant() {
		super(AetheriaItems.PropUnstackable()
				.durability(MAX_CHARGES));
	}
    
    @Override
	public String getLoreKey() {
		return "aetheria_passive_pendant";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aen Pendant";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aen Pendants store aether that's already been generated and channel it into reagent-free casts!");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aen Pendants store aether that's already been generated and channel it into reagent-free casts!", "The pendant itself seems to store about 1.5k aether, and uses 500 per cast.");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("item.info.aen.desc"));
		int charges = getWholeCharges(stack);
		tooltip.add(new TranslatableComponent("item.info.pendant.charges", charges).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	public int getWholeCharges(ItemStack stack) {
		int aether = getAether(stack);
		return aether / AETHER_PER_CHARGE;
	}
	
	public void spendCharge(ItemStack stack) {
		deductAether(stack, AETHER_PER_CHARGE);
	}
	
	@Override
	public int deductAether(ItemStack stack, int amount) {
		int ret = super.deductAether(stack, amount);
		
		return ret;
	}
	
	@Override
	public int addAether(ItemStack stack, int amount) {
		int ret = super.addAether(stack, amount);
		
		return ret;
	}
	
	@Override
	protected void onAetherChange(ItemStack stack, int currentAether) {
		super.onAetherChange(stack, currentAether);
		setDurability(stack);
	}
	
	@Override
	public void apply(LivingEntity caster, Spell spell, SpellCastSummary summary, ItemStack stack) {
		if (stack.isEmpty())
			return;
		
		if (summary.getReagentCost() <= 0 || SpellCasting.CalculateSpellReagentFree(spell, caster, summary)) {
			return;
		}
		
		int charges = getWholeCharges(stack);
		if (charges > 0) {
			if ((!(caster instanceof Player) || !((Player) caster).isCreative()) && !caster.level.isClientSide) {
				spendCharge(stack);
			}
			summary.addReagentCost(-1f);
		}
	}
	
	private void setDurability(ItemStack pendant) {
		int count = getWholeCharges(pendant);
		float max = MAX_CHARGES;
		pendant.setDamageValue((int) (max - count));
	}
	
	@Override
	public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
		super.onCraftedBy(stack, worldIn, playerIn);
		// Update durability to be correct as soon as it's created
		setDurability(stack);
	}
	
	@Override
	protected void onFirstTick(ItemStack stack, Level worldIn, Entity entityIn) {
		super.onFirstTick(stack, worldIn, entityIn);
		setDurability(stack);
	}

	@Override
	protected int getDefaultMaxAether(ItemStack stack) {
		return AETHER_PER_CHARGE * MAX_CHARGES;
	}

	@Override
	protected boolean shouldShowAether(ItemStack stack, Player playerIn, boolean advanced) {
		return false;
	}

	@Override
	protected boolean shouldAutoFill(ItemStack stack, Level worldIn, Entity entityIn) {
		return true;
	}

	@Override
	public boolean canBeDrawnFrom(ItemStack stack, Level worldIn, Entity entityIn) {
		return false;
	}
	
}
