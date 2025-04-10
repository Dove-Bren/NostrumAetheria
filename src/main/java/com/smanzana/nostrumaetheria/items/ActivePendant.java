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
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.spell.SpellCasting;
import com.smanzana.nostrummagica.spelltome.SpellCastSummary;
import com.smanzana.nostrummagica.util.Inventories;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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
				.durability(MAX_CHARGES));
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
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("item.info.lyon.desc"));
		int charges = lyonGetWholeCharges(stack);
		tooltip.add(new TranslatableComponent("item.info.pendant.charges", charges).withStyle(ChatFormatting.GREEN));
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
		
		CompoundTag nbt = stack.getTag();
		if (nbt == null)
			nbt = new CompoundTag();
		
		nbt.putFloat(NBT_PENDANT_POINTS, points);
		stack.setTag(nbt);
		
		setDurability(stack);
	}
	
	public static float lyonGetPoints(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasTag())
			return 0;
		
		CompoundTag nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundTag();
		}
		return nbt.getFloat(NBT_PENDANT_POINTS);
	}
	
	public static @Nonnull ItemStack lyonGetReagents(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasTag()) {
			return ItemStack.EMPTY;
		}
		
		return ItemStack.of(stack.getTag().getCompound(NBT_PENDANT_REAGENTS));
	}
	
	public static void lyonSetReagents(ItemStack stack, @Nonnull ItemStack reagent) {
		if (stack.isEmpty()) {
			return;
		}
		
		CompoundTag nbt = stack.getTag();
		if (nbt == null) {
			nbt = new CompoundTag();
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
		CompoundTag nbt;
		if (!stack.hasTag()) {
			nbt = new CompoundTag();
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
	public void apply(LivingEntity caster, Spell spell, SpellCastSummary summary, ItemStack stack) {
		if (stack.isEmpty())
			return;
		
		if (summary.getReagentCost() <= 0 || SpellCasting.CalculateSpellReagentFree(spell, caster, summary)) {
			return;
		}
		
		int charges = lyonGetWholeCharges(stack);
		if (charges > 0) {
			if ((!(caster instanceof Player) || !((Player) caster).isCreative()) && !caster.level.isClientSide) {
				lyonSpendCharge(stack);
			}
			summary.addReagentCost(-1f);
		}
	}
	
	private static void setDurability(ItemStack pendant) {
		int count = lyonGetWholeCharges(pendant);
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
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.hasTag()) {
			// First time ticking!
			lyonGetID(stack); // generates it if it's missing
			setDurability(stack);
		}
		
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isClientSide && entityIn.tickCount % (int) ((float) 20 / REAGENT_PER_SECOND) == 0) {
			if (entityIn instanceof Player) {
				Player player = (Player) entityIn;
				if (player.containerMenu != null && player.containerMenu instanceof ActivePendantContainer) {
					ActivePendantContainer gui = (ActivePendantContainer) player.containerMenu;
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
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
		int pos = Inventories.getPlayerHandSlotIndex(playerIn.getInventory(), InteractionHand.MAIN_HAND);
		ItemStack inHand = playerIn.getMainHandItem();
		if (inHand.isEmpty()) {
			inHand = playerIn.getOffhandItem();
			pos = Inventories.getPlayerHandSlotIndex(playerIn.getInventory(), InteractionHand.OFF_HAND);
		}
		NostrumMagica.instance.proxy.openContainer(playerIn, ActivePendantGui.ActivePendantContainer.Make(pos));
		
		return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
	}
	
}
