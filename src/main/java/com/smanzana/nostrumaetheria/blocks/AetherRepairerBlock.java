package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.client.gui.container.AetherRepairerGui;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;
import com.smanzana.nostrumaetheria.tiles.AetherRepairerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherRepairerBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	public static void initDefaultRecipes() {
		RepairerRecipeManager.instance().addRecipe(new ArmorRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new WeaponRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ToolRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ScrollRepairRecipe());
	}
	
	public AetherRepairerBlock() {
		super(Block.Properties.of(Material.STONE)
				.strength(3.0f, 10.0f)
				.sound(SoundType.STONE)
				);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ON);
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.getValue(ON);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		AetherRepairerBlockEntity repairer = (AetherRepairerBlockEntity) worldIn.getBlockEntity(pos);
		NostrumMagica.instance.proxy.openContainer(player, AetherRepairerGui.AetherRepairerContainer.Make(repairer));
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherRepairerBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Repairer);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(ON, false);
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeBlockEntity(pos);
		}
	}
	
	private void destroy(Level world, BlockPos pos, BlockState state) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof AetherRepairerBlockEntity))
			return;
		
		AetherRepairerBlockEntity furnace = (AetherRepairerBlockEntity) ent;
		for (int i = 0; i < furnace.getContainerSize(); i++) {
			if (furnace.getItem(i) != null) {
				ItemEntity item = new ItemEntity(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeItemNoUpdate(i));
				world.addFreshEntity(item);
			}
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.getValue(ON))
			return;
		
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 1.2D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.WITCH, d0, d1, d2, (rand.nextFloat() - .5) * .2, .75, (rand.nextFloat() - .5) * .2);
		
		if (rand.nextFloat() < .1f) {
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.25F, false);
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.LADDER_STEP, SoundSource.BLOCKS, 0.1F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_repairer";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Repairing";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.", "The amount of aether it takes to repair each point of damage depends on the material, type of tool, or form of equipment.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static @Nonnull ItemStack RepairTool(ItemStack tool, int amt) {
		tool.setDamageValue(Math.max(0, tool.getDamageValue() - amt));
		return tool;
	}
	
	protected static class ArmorRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ArmorItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
			ArmorItem armor = (ArmorItem) stack.getItem();
			switch (armor.getSlot()) {
			case FEET:
			case HEAD:
				base = 20f;
				break;
			case LEGS:
				base = 30f;
				break;
			case CHEST:
			case MAINHAND:
			case OFFHAND:
			default:
				base = 40f;
				break;
			}
			
			final ArmorMaterial material = armor.getMaterial();
			if (material == ArmorMaterials.LEATHER) {
				materialMod = .65f;
			} else if (material == ArmorMaterials.CHAIN || material == ArmorMaterials.IRON) {
				materialMod = 1f;
			} else if (material == ArmorMaterials.GOLD) {
				materialMod = .8f;
			} else if (material == ArmorMaterials.DIAMOND) {
				materialMod = 1.5f;
			} else {
				materialMod = 1.5f;
			}
				
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTags().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class WeaponRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SwordItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
			SwordItem sword = (SwordItem) stack.getItem();
			
			base = 25;
			
			Tier tier = sword.getTier();
			if (tier == Tiers.WOOD) {
				materialMod = .25f;
			} else if (tier == Tiers.STONE) {
				materialMod = .6f;
			} else if (tier == Tiers.IRON) {
				materialMod = 1f;
			} else if (tier == Tiers.GOLD) {
				materialMod = 1.4f;
			} else if (tier == Tiers.DIAMOND) {
				materialMod = 3f;
			} else {
				materialMod = 3f;
			}
			
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTags().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ToolRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof DiggerItem && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			float materialMod = 3f;
			final float enchantMod;
			
			DiggerItem tool = (DiggerItem) stack.getItem();
			base = 20f;
			
			Tier tier = tool.getTier();
			if (tier == Tiers.WOOD) {
				materialMod = .25f;
			} else if (tier == Tiers.STONE) {
				materialMod = .6f;
			} else if (tier == Tiers.IRON) {
				materialMod = 1f;
			} else if (tier == Tiers.GOLD) {
				materialMod = 1.4f;
			} else if (tier == Tiers.DIAMOND) {
				materialMod = 3f;
			} else {
				materialMod = 3f;
			}
			
			if (stack.isEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTags().size());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ScrollRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll && stack.isDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return 150;
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
}
