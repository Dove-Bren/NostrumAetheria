package com.smanzana.nostrumaetheria.client.gui.container;

import com.smanzana.nostrummagica.util.ContainerUtil.IAutoContainerInventory;

// Have to do this, as defaults don't come through from Nostrum. Presumably because they are obfuscated.
public interface IAutoContainerInventoryWrapper extends IAutoContainerInventory {

	default int getCount() { return this.getFieldCount(); }
	
	default int get(int index) { return this.getField(index); }
	
	default void set(int index, int val) { this.setField(index, val); }
	
}
