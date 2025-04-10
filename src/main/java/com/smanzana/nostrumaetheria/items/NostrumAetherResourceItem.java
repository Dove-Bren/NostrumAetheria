package com.smanzana.nostrumaetheria.items;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.capability.AetherBurnableWrapper;
import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Misc. resource items for aether-related progression
 * @author Skyler
 *
 */
public class NostrumAetherResourceItem extends Item implements ILoreTagged, ICapabilityProvider {

	private final int burnTicks;
	private final int aetherYield;
	
	public NostrumAetherResourceItem(int burnTicks, int aetherYield, Item.Properties builder) {
		super(builder);
		this.burnTicks = burnTicks;
		this.aetherYield = aetherYield;
	}
	
    @Override
	public String getLoreKey() {
		return "nostrum_aether_resource";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Flowers";
	}
	
	@Override
	public Lore getBasicLore() {
		return new Lore().add("Flowers of mandrake and ginseng that can't be used as reagents... and yet, you can tell there's something magical about them.");
				
	}
	
	@Override
	public Lore getDeepLore() {
		return new Lore().add("Flowers of mandrake and ginseng with high levels of aether.", "These flowers cannot be used as reagents by themselves but produce more aether than regular reagents when burned.");
	}
	
	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_ITEMS;
	}
	
	private LazyOptional<IAetherBurnable> BurnableOptional = LazyOptional.of(() -> new AetherBurnableWrapper(getBurnTicks(), getAetherYield()));
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == IAetherBurnable.CAPABILITY) {
			return BurnableOptional.cast();
		}
		
		return LazyOptional.empty();
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return this::getCapability;
	}
	
	public int getBurnTicks() {
		return this.burnTicks;
	}

	public float getAetherYield() {
		return this.aetherYield;
	}
}
