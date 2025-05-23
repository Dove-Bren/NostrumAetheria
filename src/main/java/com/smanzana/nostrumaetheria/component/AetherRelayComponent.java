package com.smanzana.nostrumaetheria.component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public class AetherRelayComponent extends AetherHandlerComponent {
	
	public static interface AetherRelayListener extends IAetherComponentListener {
		
		public void onLinkChange();
		
	}

	private static final String NBT_LINK = "relay_links";
	
	protected final int range;
	protected Direction side;
	protected Level worldObj;
	protected BlockPos pos;
	
	private Set<BlockPos> links;
	
	// Transient list of relays for easy cleanup
	private List<AetherRelayComponent> linkCache;
	
	// transient list of the links we haven't fixed up yet
	private List<BlockPos> missingLinks;
	
	protected final AetherRelayListener listener;
	
	public AetherRelayComponent(AetherRelayListener listener, Direction side, int range) {
		super(null, null, listener, 0, 0);
		this.range = range;
		this.links = new HashSet<>();
		this.linkCache = new LinkedList<>();
		this.missingLinks = new LinkedList<>();
		this.listener = listener;
		
		
		setSide(side);
	}
	
	public AetherRelayComponent(AetherRelayListener listener, Direction side) {
		this(listener, side, 8);
	}
	
	public void setSide(Direction side) {
		// Configure connections to only allow the block we're actually attached to
		for (Direction s : Direction.values()) { // Note: leaves out null, which we use for relay connections
			this.enableSide(s, false, false);
		}
		this.enableSide(side.getOpposite(), true, false);
		this.side = side;
	}
	
	public void setPosition(Level world, BlockPos pos) {
		this.worldObj = world;
		this.pos = pos;
	}
	
	protected BlockPos getPos() {
		return pos;
	}
	
	protected Level getWorld() {
		return worldObj;
	}
	
	private static IAetherHandler getHandlerAt(Level world, BlockPos pos, Direction side) {
		return IAetherHandler.GetHandlerAt(world, pos, side);
	}
	
	protected void repairLinks() {
		if (!missingLinks.isEmpty()) {
			Iterator<BlockPos> it = missingLinks.iterator();
			while (it.hasNext()) {
				BlockPos pos = it.next();
				
				// Try to load it up
				if (!APIProxy.isBlockLoaded(worldObj, pos)) {
					continue;
				}
				
				IAetherHandler handler = getHandlerAt(worldObj, pos, null);
				if (handler != null && handler instanceof AetherRelayComponent) {
					this.linkCache.add((AetherRelayComponent) handler);
					this.addAetherConnection(handler, null);
				} else {
					this.links.remove(pos);
				}
				
				// Either way, the block was loaded. Either we re-added the link or removed it from mlink list.
				// It can come out of trhis 'uninitialized' missing link list
				it.remove();
			}
		}
	}
	
	/**
	 * The provided relay has indicated that it's being unloaded and will require linking again once the chunk is loaded again
	 * @param relay
	 */
	protected void addUnloadedLink(AetherRelayComponent relay) {
		if (linkCache.remove(relay)) {
			missingLinks.add(relay.pos);
			this.removeAetherConnection(relay, null);
		}
	}
	
	/**
	 * Prepare this relay for being unloaded.
	 * This notifies any links that the relay will be unavailable, but that it isn't being destroyed.
	 */
	public void unloadRelay() {
		for (AetherRelayComponent relay : linkCache) {
			relay.addUnloadedLink(this);
		}
		linkCache.clear();
		missingLinks.clear();
		missingLinks.addAll(links);
	}
	
	/**
	 * Add this relay to our linked relays.
	 * @param relay
	 */
	public void link(AetherRelayComponent relay) {
		repairLinks();
		if (links.add(relay.getPos().immutable())) {
			this.linkCache.add(relay);
			this.addAetherConnection(relay, null);
			
			// Add ourselves as link on their side, too
			relay.link(this);
			listener.onLinkChange();
		}
	}
	
	/**
	 * Permanently remove the relay from our list of linked relays.
	 * @param relay
	 */
	public void unlink(AetherRelayComponent relay) {
		if (links.remove(relay.getPos().immutable())) {
			linkCache.remove(relay);
			missingLinks.remove(relay.getPos());
			this.removeAetherConnection(relay, null);
			
			// Dissolve their link to us if not already done
			relay.unlink(this);
			listener.onLinkChange();
		}
	}
	
	public Collection<BlockPos> getLinkedPositions() {
		repairLinks();
		return links;
	}
	
	public boolean hasLinks() {
		return !links.isEmpty();
	}
	
	public void unlinkAll() {
		repairLinks();
		for (AetherRelayComponent relay : Lists.newArrayList(linkCache)) {
			// For any we were able to actually find and link to, let them know we're going away
			unlink(relay);
		}
		
		// Any links that aren't loaded yet in our missingLinks list will eventually notice we're gone when they're
		// back and loaded.
		
		links.clear();
		missingLinks.clear();
		linkCache.clear();
	}
	
	public void autoLink() {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		
		for (int i = -range; i <= range; i++) {
			int innerRadius = range - Math.abs(i);
			for (int j = -innerRadius; j <= innerRadius; j++) {
				int yRadius = innerRadius - Math.abs(j);
				for (int k = -yRadius; k <= yRadius; k++) {
					cursor.set(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					if (!APIProxy.isBlockLoaded(worldObj, pos)) {
						continue;
					}
					if (cursor.equals(pos)) {
						continue;
					}
					
					IAetherHandler handler = getHandlerAt(worldObj, cursor, null);
					if (handler != null && handler instanceof AetherRelayComponent) {
						this.link((AetherRelayComponent) handler);
					}
				}
			}
			
		}
	}
	
	/**
	 * Return the attached aether handler. That is, the aether handler corresponding to the block we're affixed to.
	 */
	protected @Nullable IAetherHandler getAttached() {
		BlockPos pos = this.pos.relative(side.getOpposite());
		return getHandlerAt(worldObj, pos, side);
	}
	
	/**
	 * Paired relay is checking whether we can accept aether.
	 * Check if our attached block can accept it.
	 * @param amount
	 * @return
	 */
	protected boolean canForward(int amount, Set<AetherRelayComponent> visitted) {
		if (visitted == null) {
			visitted = new HashSet<>(); // first-time entry, so just make it and recurse.
		} else {
			if (visitted.contains(this)) {
				return false;
			}
			
			IAetherHandler handler = getAttached();
			if (handler != null) {
				if (handler.canAdd(side, amount)) {
					visitted.add(this); // for good measure
					return true;
				}
			}
		}
		visitted.add(this);
		
		repairLinks();
		
		// Try linked relay
		for (AetherRelayComponent relay : linkCache) {
			if (relay.canForward(amount, visitted)) {
				return true;
			}
		}
			
		return false;
	}
	
	protected int forwardAether(int amount, Set<AetherRelayComponent> visitted) {
		if (visitted == null) {
			visitted = new HashSet<>(); // first-time entry, so just make it and recurse.
		} else {
			if (visitted.contains(this)) {
				return amount;
			}
			
			IAetherHandler handler = getAttached();
			if (handler != null) {
				amount = handler.addAether(side, amount);
			}
		}
		
		visitted.add(this);
		repairLinks();
		
		if (amount != 0) {
			for (AetherRelayComponent relay : linkCache) {
				amount = relay.forwardAether(amount, visitted);
				if (amount <= 0) {
					break;
				}
			}
		}
		
		return amount;
	}
	
	@Override
	public boolean canAdd(Direction side, int amount) {
		if (canAcceptOnSide(side)) {
			return canForward(amount, null);
		}
		
		return false;
	}
	
	@Override
	public int addAether(Direction side, int amount) {
		// We don't store aether and try to push it instead
		if (canAcceptOnSide(side)) {
			return forwardAether(amount, null);
		}
		
		return amount;
	}
	
	@Override
	protected List<AetherFlowConnection> getConnections() {
		repairLinks();
		return super.getConnections();
	}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		
		ListTag list = new ListTag();
		for (BlockPos link : links) {
			list.add(NbtUtils.writeBlockPos(link));
		}
		nbt.put(NBT_LINK, list);
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		
		links.clear();
		linkCache.clear();
		missingLinks.clear();
		ListTag list = nbt.getList(NBT_LINK, Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			BlockPos pos = NbtUtils.readBlockPos(list.getCompound(i));
			if (links.add(pos)) { // prevents dupes :)
				missingLinks.add(pos);
			}
		}
	}
}
