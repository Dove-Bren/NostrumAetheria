package com.smanzana.nostrumaetheria.client.render;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.tiles.WispBlockTileEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.SpellIcon;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.util.ColorUtil;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityWispBlockRenderer extends TileEntityRenderer<WispBlockTileEntity> {

	//private static final ResourceLocation MODEL_LOC = new ResourceLocation(NostrumMagica.MODID, "block/crystal.obj");
	public static final ResourceLocation BASE_TEX_LOC = new ResourceLocation(NostrumMagica.MODID, "textures/block/stone_generic1.png");
	public static final ResourceLocation PLATFORM_TEX_LOC = new ResourceLocation(NostrumMagica.MODID, "textures/block/ceramic_generic.png");
	public static final ResourceLocation GEM_TEX_LOC = new ResourceLocation(NostrumMagica.MODID, "textures/models/crystal_blank.png");
	
	//private IBakedModel model;
	
	public TileEntityWispBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	// 0,0 is bottom point
	protected void renderPlatform(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn) {
		final int combinedOverlayIn = OverlayTexture.NO_OVERLAY;
		final float red = 1f;
		final float green = 1f;
		final float blue = 1f;
		final float alpha = 1f;
		
		final Matrix4f transform = matrixStackIn.getLast().getMatrix();
		final Matrix3f normal = matrixStackIn.getLast().getNormal();
		
		// Top quad (two triangles)
		buffer.pos(transform, -.5f, 1, -.5f).color(red, green, blue, alpha).tex(0,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, 0.3272f, -.6682f).endVertex();
		buffer.pos(transform, -.5f, 1, .5f).color(red, green, blue, alpha).tex(0,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, 0.3272f, .6682f).endVertex();
		buffer.pos(transform, .5f, 1, .5f).color(red, green, blue, alpha).tex(1, 1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, 0.3272f, .6682f).endVertex();
		// -------------------
		buffer.pos(transform, .5f, 1, .5f).color(red, green, blue, alpha).tex(1, 1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, 0.3272f, .6682f).endVertex();
		buffer.pos(transform, .5f, 1, -.5f).color(red, green, blue, alpha).tex(1,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, 0.3272f, -.6682f).endVertex();
		buffer.pos(transform, -.5f, 1, -.5f).color(red, green, blue, alpha).tex(0,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, 0.3272f, -.6682f).endVertex();
		
		// Edges
		buffer.pos(transform, 0, 0, 0).color(red, green, blue, alpha).tex(.5f,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, 0, 1, 0).endVertex();
		buffer.pos(transform, -.5f, 1, -.5f).color(red, green, blue, alpha).tex(0,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, -0.3272f, -.6682f).endVertex();
		buffer.pos(transform, .5f, 1, -.5f).color(red, green, blue, alpha).tex(1,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, -0.3272f, -.6682f).endVertex(); // first
		
		buffer.pos(transform, 0, 0, 0).color(red, green, blue, alpha).tex(0, .5f).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, 0, 1, 0).endVertex();
		buffer.pos(transform, .5f, 1, -.5f).color(red, green, blue, alpha).tex(1,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, -0.3272f, -.6682f).endVertex();
		buffer.pos(transform, .5f, 1, .5f).color(red, green, blue, alpha).tex(1,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, -0.3272f, .6682f).endVertex(); // second
		
		buffer.pos(transform, 0, 0, 0).color(red, green, blue, alpha).tex(.5f,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, 0, 1, 0).endVertex();
		buffer.pos(transform, .5f, 1, .5f).color(red, green, blue, alpha).tex(1,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, .6682f, -0.3272f, .6682f).endVertex();
		buffer.pos(transform, -.5f, 1, .5f).color(red, green, blue, alpha).tex(0,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, -0.3272f, .6682f).endVertex(); // third
		
		buffer.pos(transform, 0, 0, 0).color(red, green, blue, alpha).tex(1,.5f).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, 0, 1, 0).endVertex();
		buffer.pos(transform, -.5f, 1, .5f).color(red, green, blue, alpha).tex(0,1).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, -0.3272f, .6682f).endVertex();
		buffer.pos(transform, -.5f, 1, -.5f).color(red, green, blue, alpha).tex(0,0).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(normal, -.6682f, -0.3272f, -.6682f).endVertex(); // fourth
	}
	
	// Origin at center of bottom
	protected void renderBase(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn) {
		matrixStackIn.push();
		matrixStackIn.translate(0, .1f, 0); // move so origin is 0, .1, 0
		matrixStackIn.scale(1f, .2f, 1f); // scale so range is -.5,5 for x and z but 0,.2 for y
		RenderFuncs.drawUnitCube(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
		matrixStackIn.pop();
		
//		// Top
//		buffer.pos(-.5, .2, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(-.5, .2, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, .2, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, .2, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		
//		// North
//		buffer.pos(.5, .2, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(1,.2).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, -.5).tex(0,.2).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, .2, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		
//		// East
//		buffer.pos(.5, .2, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(.8,1).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(.8,0).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, .2, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		
//		// South
//		buffer.pos(-.5, .2, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(0,.8).normal(-.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(1,.8).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, .2, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		
//		// West
//		buffer.pos(-.5, .2, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, -.5).tex(.2,0).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(.2,1).normal(-.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(-.5, .2, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		
//		// Bottom
//		buffer.pos(-.5, 0, -.5).tex(1,0).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(0,0).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(0,1).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(1,1).normal(-.5773f, -.5773f, .5773f).endVertex();
	}
	
	protected void renderGem(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, float red, float green, float blue, float alpha, boolean outline) {
		// if outline, using lines instead of quads
		
		matrixStackIn.push();
		matrixStackIn.translate(0, .5, 0); // move unit cube to be 0-1 in the y axis
		if (outline) {
			RenderFuncs.drawUnitCubeOutline(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
		} else {
			RenderFuncs.drawUnitCube(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
		}
		matrixStackIn.pop();
		
//		// Top
//		buffer.pos(-.5, 1, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 1, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, 1, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, 1, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		
//		if (outline) {
//			buffer.pos(-.5, 1, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//			tessellator.draw();
//			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
//		}
//		
//		// North
//		buffer.pos(.5, 1, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(1,1).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, -.5).tex(0,1).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 1, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		
//		if (outline) {
//			tessellator.draw();
//			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
//		}
//		
//		// East
//		buffer.pos(.5, 1, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(0,1).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(0,0).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, 1, -.5).tex(1,0).normal(.5773f, .5773f, -.5773f).endVertex();
//		
//		if (outline) {
//			tessellator.draw();
//			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
//		}
//		
//		// South
//		buffer.pos(-.5, 1, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(0,0).normal(-.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(1,0).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(.5, 1, .5).tex(1,1).normal(.5773f, .5773f, .5773f).endVertex();
//		
//		if (outline) {
//			tessellator.draw();
//			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
//		}
//		
//		// West
//		buffer.pos(-.5, 1, -.5).tex(0,0).normal(-.5773f, .5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, -.5).tex(1,0).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(1,1).normal(-.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(-.5, 1, .5).tex(0,1).normal(-.5773f, .5773f, .5773f).endVertex();
//		
//		if (outline) {
//			tessellator.draw();
//			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL);
//		}
//		
//		// Bottom
//		buffer.pos(-.5, 0, -.5).tex(1,0).normal(-.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, -.5).tex(0,0).normal(.5773f, -.5773f, -.5773f).endVertex();
//		buffer.pos(.5, 0, .5).tex(0,1).normal(.5773f, -.5773f, .5773f).endVertex();
//		buffer.pos(-.5, 0, .5).tex(1,1).normal(-.5773f, -.5773f, .5773f).endVertex();
//		
//		tessellator.draw();
	}
	
	protected void renderAetherDebug(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, WispBlockTileEntity te) {
//		final int aether = te.getHandler().getAether(null);
//		final int maxAether = te.getHandler().getMaxAether(null);
//		final String str = aether + " / " + maxAether;
//		final font fonter = Minecraft.getInstance().fontRendererObj;
//		
//		matrixStackIn.push();
//		
//		GlStateManager.rotatef(180, 1, 0, 0);
//		// Make billboard
//		GlStateManager.color4f(1f, 1f, 1f, 1f);
//		GlStateManager.disableBlend();
//		GlStateManager.disableLighting();
//		fonter.drawString(str, -(fonter.getStringWidth(str) / 2), 0, 0xFF000000);
//		matrixStackIn.pop();
	}
	
	@Override
	public void render(WispBlockTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		final float shortPeriod = 3f;
		final float longPeriod = 30f;
		// float progress = (te.ticks + partialTicks % period) / period;
		final double shortPeriodMS = shortPeriod * 1000;
		final double longPeriodMS = longPeriod * 1000;
		float progressShort = (float) ((System.currentTimeMillis() % shortPeriodMS) / shortPeriodMS);
		float progShortOffset = (float) Math.sin(progressShort * Math.PI * 2);
		float progressLong = (float) ((System.currentTimeMillis() % longPeriodMS) / longPeriodMS);
		//float progLongOffset = (float) Math.sin(progressLong * Math.PI * 2);
		
		// Figure out color and fetch scroll
		int baseColor = 0xFFFFFFFF;
		int spellIcon = -1;
		@Nonnull ItemStack scroll = te.getScroll();
		if (!scroll.isEmpty()) {
			Spell spell = SpellScroll.GetSpell(scroll);
			if (spell != null) {
				baseColor = spell.getPrimaryElement().getColor();
				spellIcon = spell.getIconIndex();
			}
			
		}
		ItemStack reagents = te.getReagent();
		
		matrixStackIn.push();
		matrixStackIn.translate(.5f, 0f, .5f);
		
		// Base
		{
			final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.WISPBLOCK_BASE);
			renderBase(matrixStackIn, buffer, combinedLightIn);
		}
		
		double platOffset = progShortOffset * .01;
		// Platform
		{
			final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.WISPBLOCK_PLATFORM);
			matrixStackIn.push();
			matrixStackIn.translate(0, .8 + platOffset, 0);
			matrixStackIn.scale(.5f, .5f, .5f);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * progressLong));
			renderPlatform(matrixStackIn, buffer, combinedLightIn);
			matrixStackIn.pop();
		}
		
		// Scroll
		if (!scroll.isEmpty()) {
			matrixStackIn.push();
			matrixStackIn.translate(0, 1.31 + platOffset, 0);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * progressLong));
			matrixStackIn.translate(0, 0, .1);
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90f));
			
			matrixStackIn.scale(.25f, .25f, .25f);
			//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			
			RenderFuncs.RenderWorldItem(scroll, matrixStackIn, bufferIn, combinedLightIn);
			
			matrixStackIn.pop();
		}
		
		// Reagent
		if (!reagents.isEmpty()) {
			matrixStackIn.push();
			matrixStackIn.translate(0, 1.31 + platOffset, 0);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * progressLong));
			matrixStackIn.translate(0, 0, -.15);
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90f));
			
			matrixStackIn.scale(.25f, .25f, .25f);
			
			RenderFuncs.RenderWorldItem(reagents, matrixStackIn, bufferIn, combinedLightIn);
			
			matrixStackIn.pop();
		}
		
		// Gem effect
		if (!scroll.isEmpty() || !reagents.isEmpty()) {
			
			// Draw spell icon
			if (!scroll.isEmpty()) {
				final Vector3d offset = Vector3d.copyCentered(te.getPos()).subtract(this.renderDispatcher.renderInfo.getProjectedView());
				final double x = offset.getX();
				final double z = offset.getZ();
				
				matrixStackIn.push();
				matrixStackIn.translate(0, 2 + platOffset, 0);
				matrixStackIn.scale(.5f, .5f, .5f);
				matrixStackIn.rotate(Vector3f.YN.rotationDegrees(90f + (float) (360.0 * (Math.atan2(z, x) / (2 * Math.PI)))));
				matrixStackIn.translate(.5, 0, 0);
				matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180f));
				// Make billboard
				SpellIcon.get(spellIcon).render(matrixStackIn, bufferIn, combinedLightIn, 1f, 1f, 1f, .4f, 1, 1);
				matrixStackIn.pop();
			}
			
			// Draw rotating 'gem' effect
			final int count = 16;
			for (int i = 0; i < count; i++) {
				final float voffset = !reagents.isEmpty() && !scroll.isEmpty() ? (float) (Math.sin((((float) i / ((float) count / 2f)) + progressShort) * Math.PI * 2) * .05) : 0f;
				//final float rotoffset = reagents != null && scroll != null ? (360f * progressLong) : 0f;
				final float rotoffset = 360f * progressLong;
				int color = baseColor;
				//double voffset = progShortOffset * .01;
				
				// draw white if aether levels are low.
				if ((float) i / (float) count
						>= (float) te.getHandler().getAether(null) / (float) te.getHandler().getMaxAether(null)) {
					color = 0xFFFFFFFF;
				}
				
				float[] colors = ColorUtil.ARGBToColor(color);
				
				matrixStackIn.push();
				matrixStackIn.translate(0, .8, 0);
				matrixStackIn.rotate(Vector3f.YN.rotationDegrees(rotoffset + (360f * ((float) i / (float) count))));
				matrixStackIn.translate(0, voffset, .3);
				matrixStackIn.scale(.05f, .05f, .05f);
				
				renderGem(matrixStackIn, bufferIn.getBuffer(AetheriaRenderTypes.WISPBLOCK_GEM), combinedLightIn, colors[0], colors[1], colors[2], .8f, false);
				renderGem(matrixStackIn, bufferIn.getBuffer(AetheriaRenderTypes.WISPBLOCK_GEM_OUTLINE), combinedLightIn, 0f, 0f, 0f, 1f, true);
				matrixStackIn.pop();
			}
		}
		
		matrixStackIn.push();
		matrixStackIn.translate(0, 2.5, 0);
		renderAetherDebug(matrixStackIn, bufferIn, combinedLightIn, te);
		matrixStackIn.pop();
		
		
		matrixStackIn.pop();
	}
	
}
