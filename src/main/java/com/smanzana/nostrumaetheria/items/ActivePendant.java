package com.smanzana.nostrumaetheria.items;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.client.gui.container.ActivePendantGui;
import com.smanzana.nostrumaetheria.client.gui.container.ActivePendantGui.ActivePendantContainer;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.item.ISpellEquipment;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;
import com.smanzana.nostrummagica.util.Inventories;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Advanced thano pendant. Uses reagents, not xp. Slowly converts them.
 * @author Skyler
 *
 */
public class ActivePendant extends Item implements ILoreTagged, ISpellEquipment {

	private static final String NBT_PENDANT_REAGENTS = "reagent_stack";
	private static final String NBT_PENDANT_POINTS = "points"; // Reagents burnt, including fractional part for current. NOT charges.
	private static final String NBT_PENDANT_ID = "pid";
	private static final float REAGENTS_PER_CHARGE = 3f;
	private static final int MAX_CHARGES = 20;
	private static final float MAX_POINTS = MAX_CHARGES * REAGENTS_PER_CHARGE;
	private static final float REAGENT_PER_SECOND = 1f / 20f;
	
	public ActivePendant() {
		super(AetheriaItems.PropUnstackable()
				.maxDamage(MAX_CHARGES));
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("item.info.lyon.desc"));
		int charges = lyonGetWholeCharges(stack);
		tooltip.add(new TranslationTextComponent("item.info.pendant.charges", charges).mergeStyle(TextFormatting.GREEN));
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
		if (stack.isEmpty())
			return;
		
		if (points > (float) MAX_POINTS) {
			points = (float) MAX_POINTS;
		}
		
		CompoundNBT nbt = stack.getTag();
		if (nbt == null)
			nbt = new CompoundNBT();
		
		nbt.putFloat(NBT_PENDANT_POINTS, points);
		stack.setTag(nbt);
		
		setDurability(stack);
	}
	
	public static float lyonGetPoints(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasTag())
			return 0;
		
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		return nbt.getFloat(NBT_PENDANT_POINTS);
	}
	
	public static @Nonnull ItemStack lyonGetReagents(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasTag()) {
			return ItemStack.EMPTY;
		}
		
		return ItemStack.read(stack.getTag().getCompound(NBT_PENDANT_REAGENTS));
	}
	
	public static void lyonSetReagents(ItemStack stack, @Nonnull ItemStack reagent) {
		if (stack.isEmpty()) {
			return;
		}
		
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		if (reagent == null) {
			nbt.remove(NBT_PENDANT_REAGENTS);
		} else {
			nbt.put(NBT_PENDANT_REAGENTS, reagent.serializeNBT());
		}
		stack.setTag(nbt);
	}
	
	public static boolean lyonDecrementReagents(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		
		@Nonnull ItemStack reagent = lyonGetReagents(stack);
		if (reagent.isEmpty()) {
			return false;
		} else {
			reagent.shrink(1);
			if (reagent.isEmpty()) {
				reagent = ItemStack.EMPTY;
			}
			lyonSetReagents(stack, reagent);
			return true;
		}
	}
	
	public static UUID lyonGetID(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		
		UUID id;
		CompoundNBT nbt;
		if (!stack.hasTag()) {
			nbt = new CompoundNBT();
		} else {
			nbt = stack.getTag();
		}
		
		if (!nbt.contains(NBT_PENDANT_ID)) {
			id = UUID.randomUUID();
			
			nbt.putString(NBT_PENDANT_ID, id.toString());
			stack.setTag(nbt);
		} else {
			id = UUID.fromString(nbt.getString(NBT_PENDANT_ID));
		}
		return id;
	}

	@Override
	public void apply(LivingEntity caster, SpellCastSummary summary, ItemStack stack) {
		if (stack.isEmpty())
			return;
		
		if (summary.getReagentCost() <= 0) {
			return;
		}
		
		int charges = lyonGetWholeCharges(stack);
		if (charges > 0) {
			if ((!(caster instanceof PlayerEntity) || !((PlayerEntity) caster).isCreative()) && !caster.world.isRemote) {
				lyonSpendCharge(stack);
			}
			summary.addReagentCost(-1f);
		}
	}
	
	private static void setDurability(ItemStack pendant) {
		int count = lyonGetWholeCharges(pendant);
		float max = MAX_CHARGES;
		pendant.setDamage((int) (max - count));
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		// Update durability to be correct as soon as it's created
		setDurability(stack);
		
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.hasTag()) {
			// First time ticking!
			lyonGetID(stack); // generates it if it's missing
			setDurability(stack);
		}
		
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote && entityIn.ticksExisted % (int) ((float) 20 / REAGENT_PER_SECOND) == 0) {
			if (entityIn instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entityIn;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		int pos = Inventories.getPlayerHandSlotIndex(playerIn.inventory, Hand.MAIN_HAND);
		ItemStack inHand = playerIn.getHeldItemMainhand();
		if (inHand.isEmpty()) {
			inHand = playerIn.getHeldItemOffhand();
			pos = Inventories.getPlayerHandSlotIndex(playerIn.inventory, Hand.OFF_HAND);
		}
		NostrumMagica.instance.proxy.openContainer(playerIn, ActivePendantGui.ActivePendantContainer.Make(pos));
		
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}
	
}
