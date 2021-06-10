package com.smanzana.nostrumaetheria.items;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.gui.container.ActivePendantGui.ActivePendantContainer;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.ISpellArmor;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Advanced thano pendant. Uses reagents, not xp. Slowly converts them.
 * @author Skyler
 *
 */
public class ActivePendant extends Item implements ILoreTagged, ISpellArmor {

	public static final String ID = "pendant_active";
	private static final String NBT_PENDANT_REAGENTS = "reagent_stack";
	private static final String NBT_PENDANT_POINTS = "points"; // Reagents burnt, including fractional part for current. NOT charges.
	private static final String NBT_PENDANT_ID = "pid";
	private static final float REAGENTS_PER_CHARGE = 3f;
	private static final int MAX_CHARGES = 20;
	private static final float MAX_POINTS = MAX_CHARGES * REAGENTS_PER_CHARGE;
	private static final float REAGENT_PER_SECOND = 1f / 20f;
	
	private static ActivePendant instance = null;
	public static ActivePendant instance() {
		if (instance == null)
			instance = new ActivePendant();
		
		return instance;
	}
	
	public ActivePendant() {
		super();
		this.setUnlocalizedName(ID);
		this.setMaxDamage(MAX_CHARGES);
		this.setMaxStackSize(1);
		this.setCreativeTab(APIProxy.creativeTab);
	}
    
    @Override
	public String getLoreKey() {
		return "aetheria_active_pendant";
	}

	@Override
	public String getLoreDisplayName() {
		return "Lyon Pendant";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("Lyon Pendants store up magical energy like Thano Pendants do, but use raw energy from reagents instead of experience.");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("Lyon Pendants store up magical energy like Thano Pendants do, but use raw energy from reagents instead of experience.", "The breakdown process is very slow. Despite that, these pendants can store up to 20 charges!");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (stack == null)
			return;
		
		tooltip.add(I18n.format("item.info.lyon.desc", (Object[]) null));
		int charges = lyonGetWholeCharges(stack);
		tooltip.add(ChatFormatting.GREEN + I18n.format("item.info.pendant.charges", new Object[] {charges}));
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	public static int lyonGetWholeCharges(ItemStack stack) {
		float points = lyonGetPoints(stack);
		return (int) (points / REAGENTS_PER_CHARGE);
	}
	
	public static void lyonSpendCharge(ItemStack stack) {
		float points = lyonGetPoints(stack);
		if (points >= REAGENTS_PER_CHARGE) {
			points -= REAGENTS_PER_CHARGE;
			lyonSetPoints(stack, points);
		}
	}
	
//	/**
//	 * Returns leftover charge
//	 * @param stack
//	 * @param charge
//	 * @return
//	 */
//	public static float lyonAddCharge(ItemStack stack, float charge) {
//		if (stack == null)
//			return charge;
//		
//		float inPendant = lyonGetCharges(stack);
//		float space = MAX_CHARGES - inPendant;
//		if (space >= charge) {
//			inPendant += charge;
//			charge = 0;
//		} else {
//			inPendant = (float) MAX_CHARGES;
//			charge -= space;
//		}
//		
//		lyonSetCharges(stack, inPendant);
//		
//		return charge;
//	}
	
	private static void lyonSetPoints(ItemStack stack, float points) {
		if (stack == null)
			return;
		
		if (points > (float) MAX_POINTS) {
			points = (float) MAX_POINTS;
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null)
			nbt = new NBTTagCompound();
		
		nbt.setFloat(NBT_PENDANT_POINTS, points);
		stack.setTagCompound(nbt);
		
		setDurability(stack);
	}
	
	public static float lyonGetPoints(ItemStack stack) {
		if (stack == null || !stack.hasTagCompound())
			return 0;
		
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		return nbt.getFloat(NBT_PENDANT_POINTS);
	}
	
	public static @Nullable ItemStack lyonGetReagents(ItemStack stack) {
		if (stack == null || !stack.hasTagCompound()) {
			return null;
		}
		
		return ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(NBT_PENDANT_REAGENTS));
	}
	
	public static void lyonSetReagents(ItemStack stack, @Nullable ItemStack reagent) {
		if (stack == null) {
			return;
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		if (reagent == null) {
			nbt.removeTag(NBT_PENDANT_REAGENTS);
		} else {
			nbt.setTag(NBT_PENDANT_REAGENTS, reagent.serializeNBT());
		}
		stack.setTagCompound(nbt);
	}
	
	public static boolean lyonDecrementReagents(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		
		@Nullable ItemStack reagent = lyonGetReagents(stack);
		if (reagent == null) {
			return false;
		} else {
			reagent.stackSize--;
			if (reagent.stackSize <= 0) {
				reagent = null;
			}
			lyonSetReagents(stack, reagent);
			return true;
		}
	}
	
	public static UUID lyonGetID(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		
		UUID id;
		NBTTagCompound nbt;
		if (!stack.hasTagCompound()) {
			nbt = new NBTTagCompound();
		} else {
			nbt = stack.getTagCompound();
		}
		
		if (!nbt.hasKey(NBT_PENDANT_ID)) {
			id = UUID.randomUUID();
			
			nbt.setString(NBT_PENDANT_ID, id.toString());
			stack.setTagCompound(nbt);
		} else {
			id = UUID.fromString(nbt.getString(NBT_PENDANT_ID));
		}
		return id;
	}

	@Override
	public void apply(EntityLivingBase caster, SpellCastSummary summary, ItemStack stack) {
		if (stack == null)
			return;
		
		if (summary.getReagentCost() <= 0) {
			return;
		}
		
		int charges = lyonGetWholeCharges(stack);
		if (charges > 0) {
			if ((!(caster instanceof EntityPlayer) || !((EntityPlayer) caster).isCreative()) && !caster.worldObj.isRemote) {
				lyonSpendCharge(stack);
			}
			summary.addReagentCost(-1f);
		}
	}
	
	private static void setDurability(ItemStack pendant) {
		int count = lyonGetWholeCharges(pendant);
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
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.hasTagCompound()) {
			// First time ticking!
			lyonGetID(stack); // generates it if it's missing
			setDurability(stack);
		}
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote && entityIn.ticksExisted % (int) ((float) 20 / REAGENT_PER_SECOND) == 0) {
			if (entityIn instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entityIn;
				if (player.openContainer != null && player.openContainer instanceof ActivePendantContainer) {
					ActivePendantContainer gui = (ActivePendantContainer) player.openContainer;
					if (Objects.equals(lyonGetID(gui.getPendant()), lyonGetID(stack))) {
						return; // open in a container!
					}
				}
			}
			
			
			// Every second, update progress
			float current = lyonGetPoints(stack);
			if (current - (int) current > 0.001) {
				// Reagent's still being consumed.
				; // Pass
			} else if (!lyonDecrementReagents(stack)) {
				// No reagents to burn.
				return;
			} else {
				// Just consumed a reagent stack.
				; //Fall through
			}
			
			// Here means we're good to keep adding
			int oldInt = (int) current;
			current += 1;
			if ((int) current > oldInt) {
				// Just passed threshold. Make sure to leave no fractional part.
				current = (int) current;
			}
			lyonSetPoints(stack, current);
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.activePendantID, worldIn,
				(int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
	
}
