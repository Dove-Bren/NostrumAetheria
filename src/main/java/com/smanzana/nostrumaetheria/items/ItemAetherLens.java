package com.smanzana.nostrumaetheria.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.IAetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.api.item.IAetherInfuserLens;
import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.capabilities.INostrumMagic;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.crafting.NostrumTags;
import com.smanzana.nostrummagica.effect.NostrumPotions;
import com.smanzana.nostrummagica.entity.IMagicEntity;
import com.smanzana.nostrummagica.item.armor.ElementalArmor;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.util.Entities;
import com.smanzana.nostrummagica.util.Inventories;
import com.smanzana.petcommand.api.entity.ITameableEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemAetherLens extends Item implements ILoreTagged, IAetherInfuserLens {

	private static final String ID_PREFIX = "lens_";
	public static final String ID_SPREAD = ID_PREFIX + "spread";
	public static final String ID_CHARGE = ID_PREFIX + "wide_charge";
	public static final String ID_GROW = ID_PREFIX + "grow";
	public static final String ID_SWIFTNESS = ID_PREFIX + "swiftness";
	public static final String ID_ELEVATOR = ID_PREFIX + "elevator";
	public static final String ID_HEAL = ID_PREFIX + "heal";
	public static final String ID_BORE = ID_PREFIX + "bore";
	public static final String ID_BORE_REVERSED = ID_PREFIX + "bore_reversed";
	public static final String ID_MANA_REGEN = ID_PREFIX + "mana_regen";
	public static final String ID_NO_SPAWN = ID_PREFIX + "no_spawn";
	
	public static enum LensType {
		SPREAD(ID_SPREAD, true, 1, 0, () -> Ingredient.fromTag(Tags.Items.GEMS_DIAMOND)),
		CHARGE(ID_CHARGE, true, 1, 0, () -> Ingredient.fromTag(NostrumTags.Items.CrystalSmall)),
		GROW(ID_GROW, false, 20, 10, () -> Ingredient.fromItems(Items.BONE_BLOCK)),
		SWIFTNESS(ID_SWIFTNESS, false, 1, 0, () -> new NostrumPotions.PotionIngredient(Potions.SWIFTNESS)), // aether taken per entity
		ELEVATOR(ID_ELEVATOR, false, 1, 0, () -> Ingredient.fromItems(Blocks.DISPENSER)), // aether taken per entity
		HEAL(ID_HEAL, false, 5, 0, () -> new NostrumPotions.PotionIngredient(Potions.HEALING)), // aether taken per entity
		BORE(ID_BORE, false, 20, 50, () -> Ingredient.fromItems(Items.DIAMOND_PICKAXE)),
		BORE_REVERSED(ID_BORE_REVERSED, false, 20, 50, () -> Ingredient.EMPTY),
		MANA_REGEN(ID_MANA_REGEN, false, 20, 0, () -> new NostrumPotions.PotionIngredient(NostrumPotions.MANAREGEN.getType())),
		NO_SPAWN(ID_NO_SPAWN, true, 1, 0, () -> Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD)),
		;
		
		private final String unlocName; // unlocalized name fragment
		private final boolean isMaster; // whether this lense requires an original aether infuser
		private final int aetherPerTick; // Aether we require to work each work tick
		private final int interval;
		private final Supplier<Ingredient> ingredientSupplier;
		private Ingredient ingredientCache = null;
		
		private LensType(String unlocName, boolean isMaster, int tickInterval, int aetherPerTick, Supplier<Ingredient> ingredientSupplier) {
			this.unlocName = unlocName;
			this.isMaster = isMaster;
			this.aetherPerTick = aetherPerTick;
			this.interval = tickInterval;
			this.ingredientSupplier = ingredientSupplier;
		}
		
		public String getUnlocSuffix() {
			return unlocName;
		}
		
		public boolean isMasterOnly() {
			return isMaster;
		}
		
		public int getTickInterval() {
			return this.interval;
		}
		
		public int getAetherPerTick() {
			return this.aetherPerTick;
		}
		
		private Ingredient getIngredientInternal() {
			if (this.ingredientCache == null) {
				this.ingredientCache = this.ingredientSupplier.get();
			}
			return this.ingredientCache;
		}
		
		public Ingredient getIngredient() {
			return this.getIngredientInternal();
		}
	}
	
	public static Item GetLens(LensType type) {
		Item item = null;
		switch (type) {
		case SPREAD:
			item = AetheriaItems.spreadAetherLens;
			break;
		case CHARGE:
			item = AetheriaItems.chargeAetherLens;
			break;
		case GROW:
			item = AetheriaItems.growAetherLens;
			break;
		case SWIFTNESS:
			item = AetheriaItems.swiftnessAetherLens;
			break;
		case ELEVATOR:
			item = AetheriaItems.elevatorAetherLens;
			break;
		case HEAL:
			item = AetheriaItems.healAetherLens;
			break;
		case BORE:
			item = AetheriaItems.boreAetherLens;
			break;
		case BORE_REVERSED:
			item = AetheriaItems.reversedBoreAetherLens;
			break;
		case MANA_REGEN:
			item = AetheriaItems.manaRegenAetherLens;
			break;
		case NO_SPAWN:
			item = AetheriaItems.noSpawnAetherLens;
			break;
		}
		
		return item;
	}
	
	private final LensType type;
	
	public ItemAetherLens(LensType type, Item.Properties builder) {
		super(builder);
		this.type = type;
	}
	
//	public static final ItemStack Create(LensType type, int count) {
//		return new ItemStack(instance(), count, MetaFromType(type));
//	}
	
	@Override
	public String getLoreKey() {
		return "nostrum_aether_lenses";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Lenses";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether lenses are used in conjunction with the Aether Infuser multiblock to provide various effects at the cost of aether!");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether lenses are placed on altars 'affected' by Aether Infuses. Each lens does something different, and some lenses can only placed on the main altar of an infuser.", "All lenses can be placed directly in the altar above an Aether Infuser.", "Most lenses on altars that are near Aether Infuses that have a Spread lense on their main altar will also work!");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		if (I18n.hasKey("item." + type.getUnlocSuffix() + ".desc")) {
			// Format with placeholders for blue and red formatting
			String translation = I18n.format("item." + type.getUnlocSuffix() + ".desc", TextFormatting.GRAY, TextFormatting.BLUE, TextFormatting.DARK_RED);
			if (translation.trim().isEmpty())
				return;
			String lines[] = translation.split("\\|");
			for (String line : lines) {
				tooltip.add(new StringTextComponent(line));
			}
		}
		
		if (type.isMasterOnly()) {
			tooltip.add(new TranslationTextComponent("item.lens.master").mergeStyle(TextFormatting.DARK_RED));
		}
	}
	
	@Override
	public boolean canAcceptAetherInfuse(ItemStack stack, BlockPos pos, IAetherInfuserTileEntity source, int maxAether) {
		// Masteronly ones are handled directly in aether infuser in non-dynamic manner
		return !type.isMasterOnly()
				&& source.getInfuserWorld().getGameTime() % type.getTickInterval() == 0
				&& maxAether > 0
				&& maxAether >= type.aetherPerTick;
	}

	@Override
	public int acceptAetherInfuse(ItemStack stack, BlockPos pos, IAetherInfuserTileEntity source, int maxAether) {
		final ServerWorld world = (ServerWorld) source.getInfuserWorld();
		int cost = 0;
		
		switch (type) {
		case BORE:
			cost = doBore(world, pos, maxAether);
			break;
		case BORE_REVERSED:
			cost = doBoreReversed(world, pos, maxAether);
			break;
		case ELEVATOR:
			cost = doElevator(world, pos, maxAether);
			break;
		case GROW:
			cost = doGrow(world, pos, maxAether);
			break;
		case HEAL:
			cost = doHeal(world, pos, maxAether);
			break;
		case SWIFTNESS:
			cost = doSwiftness(world, pos, maxAether);
			break;
		case MANA_REGEN:
			cost = doManaRegen(world, pos, maxAether);
			break;
		// Master lenses
		case SPREAD:
		case CHARGE:
		case NO_SPAWN:
			cost = 0;
			break;
		}
		
		return maxAether - cost;
	}
	
	protected int doGrow(ServerWorld world, BlockPos center, int maxAether) {
		@Nullable BlockPos growPos = ElementalArmor.DoEarthGrow(world, center); 
		if (growPos != null) {
			AetherInfuserTileEntity.DoChargeEffect(world,
					new Vector3d(center.getX() + .5, center.getY() + 1, center.getZ() + .5),
					new Vector3d(growPos.getX() + .5, growPos.getY() + .5, growPos.getZ() + .5),
					1, 0x6622FF44);
			
			return LensType.GROW.getAetherPerTick();
		}
		return 0;
	}
	
	protected int doSwiftness(ServerWorld world, BlockPos center, int maxAether) {
		final double MAX_DIST_SQ = 900;
		int cost = 0;
		
		List<LivingEntity> entities = Entities.GetEntities(world, (e) -> {
			return e.isAlive()
					&& e.getDistanceSq(center.getX() + .5, center.getY() + .5, center.getZ() + .5) < MAX_DIST_SQ
					&& (e.getActivePotionEffect(Effects.SPEED) == null
						|| e.getActivePotionEffect(Effects.SPEED).getDuration() < 20);
		});
		
		for (LivingEntity ent : entities) {
			ent.addPotionEffect(new EffectInstance(Effects.SPEED, 40, 0, false, false));
			cost += 2;
			AetherInfuserTileEntity.DoChargeEffect(ent, 1, 0xFF77AA22);
		}
		return cost;
	}
	
	protected int doElevator(ServerWorld world, BlockPos center, int maxAether) {
		final double HORZ_DIST_RADIUS = 2.5;
		final double MAX_HEIGHT = 30;
		int cost = 0;
		for (LivingEntity ent : Entities.GetEntities(world, (e) -> {
			return e.isAlive()
					&& e.getPosY() >= center.getY() && e.getPosY() < center.getY() + MAX_HEIGHT
					&& Math.abs(e.getPosX() - (center.getX() + .5)) < HORZ_DIST_RADIUS
					&& Math.abs(e.getPosZ() - (center.getZ() + .5)) < HORZ_DIST_RADIUS;
		})) {
			if (!ent.isSneaking() && !ent.isOnGround()) {
				cost += 1;
				Vector3d motion = ent.getMotion();
				ent.setMotion(motion.x, Math.min(.3, motion.y + .1), motion.z);
				ent.velocityChanged = true;
				AetherInfuserTileEntity.DoChargeEffect(ent, 1, 0xFF77AA22);
			}
			ent.fallDistance = 0;
		}
		return cost;
	}
	
	protected int doHeal(ServerWorld world, BlockPos center, int maxAether) {
		final double MAX_DIST_SQ = 900;
		int cost = 0;
		for (LivingEntity ent : Entities.GetEntities(world, (e) -> {
			return e.isAlive()
					&& (e instanceof PlayerEntity 
							|| (e instanceof ITameableEntity && ((ITameableEntity) e).getOwner() != null)
							|| (e instanceof TameableEntity) && ((TameableEntity) e).getOwnerId() != null)
					&& e.getDistanceSq(center.getX() + .5, center.getY() + .5, center.getZ() + .5) < MAX_DIST_SQ
					&& e.getHealth() < e.getMaxHealth();
		})) {
			ent.heal(.25f);
			cost += 2;
			AetherInfuserTileEntity.DoChargeEffect(ent, 1, 0xFFFF5555);
		}
		return cost;
	}
	
	protected @Nonnull List<ItemStack> pushToNearbyInventories(@Nonnull List<ItemStack> items, ServerWorld world, BlockPos center) {
		if (items.isEmpty()) {
			return items;
		}
		
		// We'll look in the 4 neighbors for inventory handlers and try to empty our list
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			final BlockPos pos = center.offset(dir);
			final @Nullable TileEntity te = world.getTileEntity(pos);
			
			if (te != null) {
				LazyOptional<IItemHandler> cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
				if (cap.isPresent()) {
					List<ItemStack> toInsert = items;
					items = new ArrayList<>(toInsert.size());
					
					for (ItemStack insert : toInsert) {
						ItemStack leftover = Inventories.addItem(cap.orElse(null), insert);
						if (leftover != null && !leftover.isEmpty()) {
							items.add(leftover);
						}
					}
				} else if (te instanceof IInventory) {
					List<ItemStack> toInsert = items;
					items = new ArrayList<>(toInsert.size());
					
					for (ItemStack insert : toInsert) {
						ItemStack leftover = Inventories.addItem((IInventory) te, insert);
						if (leftover != null && !leftover.isEmpty()) {
							items.add(leftover);
						}
					}
				}
			}
			
			if (items.isEmpty()) {
				break;
			}
		}
		
		return items;
	}
	
	protected boolean doBoreInternal(ServerWorld world, BlockPos center, int maxAether, boolean down) {
		BlockPos.Mutable cursor = new BlockPos.Mutable().setPos(
				down ? center.down().down() : center.up().up().up()
				);
		while (cursor.getY() >= 0 && cursor.getY() < world.getHeight()) {
			if (world.isAirBlock(cursor)) {
				cursor.move(down ? Direction.DOWN : Direction.UP);
				continue;
			}
			
			final BlockState blockstate = world.getBlockState(cursor);
			if (blockstate.getBlockHardness(world, cursor) < 0 || blockstate.getMaterial().isLiquid()) {
				cursor.move(down ? Direction.DOWN : Direction.UP);
				continue;
			}
			
			break; // breakable!
		}
		
		if (cursor.getY() < 0 || cursor.getY() >= world.getHeight()) {
			return false; // nothing to do
		}
		
		List<ItemStack> drops = new ArrayList<>();
		for (int x = -2; x <= 2; x++)
		for (int z = -2; z <= 2; z++) {
			final BlockPos pos = cursor.toImmutable().add(x, 0, z);
			final BlockState state = world.getBlockState(pos);
			drops.addAll(Block.getDrops(state, world, pos, null));
			world.destroyBlock(pos, false);
			AetherInfuserTileEntity.DoChargeEffect(world, pos, 1, 0xFF664400);
		}
		
		// Try to push drops into nearby inventories first
		drops = pushToNearbyInventories(drops, world, center);
		
		// Then drop any that remain
		for (ItemStack stack : drops) {
			// put drops right above bore altar
			world.addEntity(new ItemEntity(world, center.getX() + .5, center.getY() + 1.2, center.getZ() + .5, stack));
		}
		
		return true;
	}
	
	protected int doBore(ServerWorld world, BlockPos center, int maxAether) {
		return doBoreInternal(world, center, maxAether, true) ? LensType.BORE.getAetherPerTick() : 0;
	}
	
	protected int doBoreReversed(ServerWorld world, BlockPos center, int maxAether) {
		return doBoreInternal(world, center, maxAether, false) ? LensType.BORE_REVERSED.getAetherPerTick() : 0;
	}
	
	protected int doManaRegen(ServerWorld world, BlockPos center, int maxAether) {
		final double MAX_DIST_SQ = 900;
		final int MANA_PER_AETHER = 10;
		int cost = 0;
		for (LivingEntity ent : Entities.GetEntities(world, (e) -> {
			return e.isAlive()
					&& (e instanceof PlayerEntity || (e instanceof IMagicEntity))
					&& e.getDistanceSq(center.getX() + .5, center.getY() + .5, center.getZ() + .5) < MAX_DIST_SQ;
		})) {
			if (ent instanceof PlayerEntity) {
				INostrumMagic attr = NostrumMagica.getMagicWrapper(ent);
				if (attr.getMana() < attr.getMaxMana()) {
					cost++;
					attr.addMana(MANA_PER_AETHER);
					AetherInfuserTileEntity.DoChargeEffect(ent, 1, 0xFFBB6DFF);
				}
			} else /* if (ent instanceof IMagicEntity) */ {
				IMagicEntity ment = (IMagicEntity) ent;
				if (ment.getMana() < ment.getMaxMana()) {
					cost++;
					ment.addMana(MANA_PER_AETHER);
					AetherInfuserTileEntity.DoChargeEffect(ent, 1, 0xFFBB6DFF);
				}
			}
			
		}
		return cost;
	}

}
