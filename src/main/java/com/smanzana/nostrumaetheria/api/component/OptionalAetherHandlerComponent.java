package com.smanzana.nostrumaetheria.api.component;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class OptionalAetherHandlerComponent {
	
	public static interface AetherHandlerFactory {
		
		public IAetherHandlerComponent make();
	}

	private final @Nullable IAetherHandlerComponent component;
	
	public OptionalAetherHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		this((ResourceKey<Level>) null, null, listener, defaultAether, defaultMaxAether);
	}
	
	public OptionalAetherHandlerComponent(@Nullable Level world, @Nullable BlockPos pos, IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		this(() -> APIProxy.createHandlerComponent(world == null ? null : world.dimension(), pos, listener, defaultAether, defaultMaxAether));
	}
	
	public OptionalAetherHandlerComponent(@Nullable ResourceKey<Level> dimension, @Nullable BlockPos pos, IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		this(() -> APIProxy.createHandlerComponent(dimension, pos, listener, defaultAether, defaultMaxAether));
	}
	
	public OptionalAetherHandlerComponent(AetherHandlerFactory supplier) {
		if (APIProxy.isEnabled()) {
			component = supplier.make();
		} else {
			component = null;
		}
	}
	
	public OptionalAetherHandlerComponent(IAetherHandlerComponent presetComponent) {
		this.component = presetComponent;
	}
	
	public void setAutoFill(boolean autoFill) {
		if (component != null) {
			component.setAutoFill(autoFill);
		}
	}
	
	public void setAutoFill(boolean autoFill, int maxPerTick) {
		if (component != null) {
			component.setAutoFill(autoFill, maxPerTick);
		}
	}
	
	public void configureInOut(boolean inputAllowed, boolean outputAllowed) {
		if (component != null) {
			component.configureInOut(inputAllowed, outputAllowed);
		}
	}
	
	public void tick() {
		if (component != null) {
			component.tick();
		}
	}
	
	public boolean isPresent() {
		return component != null;
	}
	
	/**
	 * Fetch a raw ref to the handler.
	 * Useful if you need to do very specific things with the handler. If all you need to do is withdraw
	 * aether, consider using the other methods.
	 * @return
	 */
	public @Nullable IAetherHandlerComponent getHandlerIfPresent() {
		return component;
	}
	
	/**
	 * Try to withdraw the provided amount of aether from the handler.
	 * Returns true if aetheria is not enabled, or the provided amount of aether was able to be withdrawn.
	 * @param amount
	 * @return
	 */
	public boolean checkAndWithdraw(int amount) {
		if (component == null) {
			return true;
		}
		
		if (component.getAether(null) >= amount) {
			component.drawAether(null, amount);
			return true;
		}
		
		return false;
	}
	
	public boolean check(int amount) {
		if (component == null) {
			return true;
		}
		
		return (component.getAether(null) >= amount);
	}
	
	public Tag toNBT() {
		if (component == null) {
			return StringTag.valueOf("ABSENT");
		} else {
			return component.writeToNBT(new CompoundTag());
		}
	}
	
	public void loadNBT(Tag nbt) {
		if (component == null) {
			if (!(nbt instanceof StringTag) || !((StringTag) nbt).getAsString().equals("ABSENT")) {
				System.out.println("Attempted to load an optional aether handler but currently disabled. Did the aether mod get removed??");
			}
		} else {
			if (nbt instanceof CompoundTag) {
				component.readFromNBT((CompoundTag) nbt);
			}
		}
	}
	
}
