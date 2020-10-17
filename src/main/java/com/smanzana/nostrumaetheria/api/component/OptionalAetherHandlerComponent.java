package com.smanzana.nostrumaetheria.api.component;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class OptionalAetherHandlerComponent {

	private final @Nullable IAetherHandlerComponent component;
	
	public OptionalAetherHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		if (APIProxy.isEnabled()) {
			component = APIProxy.createHandlerComponent(listener, defaultAether, defaultMaxAether);
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
	
	public NBTBase toNBT() {
		if (component == null) {
			return new NBTTagString("ABSENT");
		} else {
			return component.writeToNBT(new NBTTagCompound());
		}
	}
	
	public void loadNBT(NBTBase nbt) {
		if (component == null) {
			if (!(nbt instanceof NBTTagString) || !((NBTTagString) nbt).getString().equals("ABSENT")) {
				System.out.println("Attempted to load an optional aether handler but currently disabled. Did the aether mod get removed??");
			}
		} else {
			if (nbt instanceof NBTTagCompound) {
				component.readFromNBT((NBTTagCompound) nbt);
			}
		}
	}
	
}
