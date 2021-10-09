package com.smanzana.nostrumaetheria.items;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.ISpellArmor;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Advanced thano pendant. Stores and uses aether!
 * @author Skyler
 *
 */
public class PassivePendant extends AetherItem implements ILoreTagged, ISpellArmor {

	public static final String ID = "pendant_passive";
	private static final int AETHER_PER_CHARGE = 500;
	private static final int MAX_CHARGES = 3;
	
	private static PassivePendant instance = null;
	public static PassivePendant instance() {
		if (instance == null)
			instance = new PassivePendant();
		
		return instance;
	}
	
	public PassivePendant() {
		super();
		this.setUnlocalizedName(ID);
		this.setRegistryName(ID);
		this.setMaxDamage(MAX_CHARGES);
		this.setMaxStackSize(1);
		this.setCreativeTab(APIProxy.creativeTab);
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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item.info.aen.desc", (Object[]) null));
		int charges = getWholeCharges(stack);
		tooltip.add(ChatFormatting.GREEN + I18n.format("item.info.pendant.charges", new Object[] {charges}));
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
	public void apply(EntityLivingBase caster, SpellCastSummary summary, ItemStack stack) {
		if (stack.isEmpty())
			return;
		
		if (summary.getReagentCost() <= 0) {
			return;
		}
		
		int charges = getWholeCharges(stack);
		if (charges > 0) {
			if ((!(caster instanceof EntityPlayer) || !((EntityPlayer) caster).isCreative()) && !caster.world.isRemote) {
				spendCharge(stack);
			}
			summary.addReagentCost(-1f);
		}
	}
	
	private void setDurability(ItemStack pendant) {
		int count = getWholeCharges(pendant);
		float max = MAX_CHARGES;
		pendant.setItemDamage((int) (max - count));
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		// Update durability to be correct as soon as it's created
		setDurability(stack);
	}
	
	@Override
	protected void onFirstTick(ItemStack stack, World worldIn, Entity entityIn) {
		super.onFirstTick(stack, worldIn, entityIn);
		setDurability(stack);
	}

	@Override
	protected int getDefaultMaxAether(ItemStack stack) {
		return AETHER_PER_CHARGE * MAX_CHARGES;
	}

	@Override
	protected boolean shouldShowAether(ItemStack stack, EntityPlayer playerIn, boolean advanced) {
		return false;
	}

	@Override
	protected boolean shouldAutoFill(ItemStack stack, World worldIn, Entity entityIn) {
		return true;
	}

	@Override
	public boolean canBeDrawnFrom(ItemStack stack, World worldIn, Entity entityIn) {
		return false;
	}
	
}
