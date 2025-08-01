package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.client.gui.container.AetherUnravelerGui;
import com.smanzana.nostrumaetheria.tiles.AetherUnravelerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.item.SpellRune;
import com.smanzana.nostrummagica.item.equipment.SpellScroll;
import com.smanzana.nostrummagica.item.equipment.SpellTome;
import com.smanzana.nostrummagica.loretag.ELoreCategory;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spell.RegisteredSpell;
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.spell.component.SpellEffectPart;
import com.smanzana.nostrummagica.spell.component.SpellShapePart;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class AetherUnravelerBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	public static void initDefaultRecipes() {
		APIProxy.addUnravelerRecipe(new ScrollUnravelerRecipe());
		APIProxy.addUnravelerRecipe(new TomeUnravelerRecipe());
	}
	
	public AetherUnravelerBlock() {
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
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		AetherUnravelerBlockEntity unraveler = (AetherUnravelerBlockEntity) worldIn.getBlockEntity(pos);
		NostrumMagica.Proxy.openContainer(player, AetherUnravelerGui.AetherUnravelerContainer.Make(unraveler));
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherUnravelerBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Unraveler);
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
		if (ent == null || !(ent instanceof AetherUnravelerBlockEntity))
			return;
		
		AetherUnravelerBlockEntity furnace = (AetherUnravelerBlockEntity) ent;
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
		return "aether_unraveler";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Unraveling";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.", "This allows it to take spell scrolls and return some of the runes that were used to craft it!");
	}

	@Override
	public ELoreCategory getCategory() {
		return ELoreCategory.BLOCK;
	}
	
	protected static class ScrollUnravelerRecipe implements IAetherUnravelerRecipe {

		private static final int AETHER_COST = 2000;
		private static final int DURATION = 20 * 100;
		
		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll;
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return AETHER_COST;
		}
		
		@Override
		public int getDuration(ItemStack stack) {
			return DURATION;
		}

		@Override
		public NonNullList<ItemStack> unravel(ItemStack stack) {
			NonNullList<ItemStack> ret = NonNullList.create();
			
			Spell spell = SpellScroll.GetSpell(stack);
			if (spell == null) {
				NostrumAetheria.logger.error("Tried to process spell scroll in unraveler but found no spell");
				return ret;
			}
			
			List<SpellShapePart> shapes = spell.getSpellShapeParts();
			List<SpellEffectPart> effects = spell.getSpellEffectParts();
			
			for (SpellShapePart shape : shapes) {
				ret.add(SpellRune.getRune(shape));
			}
			for (SpellEffectPart effect : effects) {
				ret.addAll(SpellRune.getRune(effect));
			}
			
			return ret;
		}
		
	}
	
	protected static class TomeUnravelerRecipe implements IAetherUnravelerRecipe {

		private static final int AETHER_COST = 5000;
		private static final int DURATION = 20 * 120;
		
		@Override
		public boolean matches(@Nonnull ItemStack stack) {
			return stack.getItem() instanceof SpellTome && !SpellTome.getSpellLibrary(stack).isEmpty();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return AETHER_COST;
		}
		
		@Override
		public int getDuration(ItemStack stack) {
			return DURATION;
		}

		@Override
		public NonNullList<ItemStack> unravel(ItemStack stack) {
			NonNullList<ItemStack> ret = NonNullList.create();
			ret.add(stack.copy());
			
			List<RegisteredSpell> spells = SpellTome.getSpellLibrary(stack);
			if (spells == null || spells.isEmpty()) {
				;
			} else {
				for (RegisteredSpell spell : spells) {
					ItemStack scroll = SpellScroll.create(spell);
					ret.add(scroll);
				}
			}
			
			// Clear tome
			SpellTome.clearSpells(ret.get(0));
			
			return ret;
		}
	}

}
