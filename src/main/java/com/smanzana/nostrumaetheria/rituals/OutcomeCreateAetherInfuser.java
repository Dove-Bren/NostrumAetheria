package com.smanzana.nostrumaetheria.rituals;

import java.util.List;

import com.smanzana.nostrumaetheria.blocks.AetherInfuser;
import com.smanzana.nostrummagica.block.AltarBlock;
import com.smanzana.nostrummagica.block.CandleBlock;
import com.smanzana.nostrummagica.block.ChalkBlock;
import com.smanzana.nostrummagica.ritual.IRitualLayout;
import com.smanzana.nostrummagica.ritual.RitualRecipe;
import com.smanzana.nostrummagica.ritual.outcome.IRitualOutcome;
import com.smanzana.nostrummagica.tile.AltarTileEntity;
import com.smanzana.nostrummagica.util.TextUtils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class OutcomeCreateAetherInfuser implements IRitualOutcome {

	public OutcomeCreateAetherInfuser() {
	}
	
	@Override
	public void perform(World world, PlayerEntity player, BlockPos center, IRitualLayout layout, RitualRecipe recipe) {
		if (!world.isRemote) {
			AetherInfuser.CreateAetherInfuser(world, center);
			// clear altar on server
			TileEntity te = world.getTileEntity(center.add(0, 0, 0));
			if (te == null || !(te instanceof AltarTileEntity))
				return;
			((AltarTileEntity) te).setItem(ItemStack.EMPTY);
			
			// Break all altars, chalk, candles
			int radius = 4;
			for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++) {
				BlockPos pos = center.add(i, 0, j);
				BlockState state = world.getBlockState(pos);
				if (state != null &&
						(state.getBlock() instanceof CandleBlock || state.getBlock() instanceof AltarBlock || state.getBlock() instanceof ChalkBlock)) {
					world.destroyBlock(pos, true);
				}
			}
		}
	}

	@Override
	public List<ITextComponent> getDescription() {
		return TextUtils.GetTranslatedList("ritual.outcome.create_infuser.desc");
	}

	@Override
	public String getName() {
		return "create_aether_infuser";
	}

}
