package com.smanzana.nostrumaetheria.client.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity.EffectSpark;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class TileEntityAetherInfuserRenderer extends TileEntityRenderer<AetherInfuserTileEntity> {

	public static final float ORB_RADIUS = 2f;
	
	//private static final ModelResourceLocation ORB_MODEL_LOC = new ModelResourceLocation(new ResourceLocation(NostrumMagica.MODID, "effects/orb_pure"), "normal");
	//private static IBakedModel MODEL_ORB;
	public static final ResourceLocation SPARK_TEX_LOC = new ResourceLocation(NostrumMagica.MODID, "textures/effects/glow_orb.png");
	
	private List<EffectSpark> sparks;
	
	public TileEntityAetherInfuserRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		sparks = new ArrayList<>();
	}
	
	private void renderOrb(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, float opacity, boolean outside) {
		
		final float mult = 2 * ORB_RADIUS * (outside ? 1 : -1);
		
		// outside
		matrixStackIn.push();
		matrixStackIn.translate(0, ORB_RADIUS - .5f, 0);
		matrixStackIn.scale(mult, mult, mult);
		
		{
			final float red = .2f;
			final float green = .73f;
			final float blue = .53f;
			final float alpha = opacity;
			
			//GlStateManager.alphaFunc(516, 0);
			final int rows = 10;
			final int cols = 10;
			final float radius = 1;
			
			RenderFuncs.drawOrb(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha, rows, cols, radius, radius, radius);
//			for (int i = 1; i <= rows; i++) {
//				final double yRad0 = Math.PI * (-0.5f + (float) (i - 1) / (float) rows);
//				final double y0 = Math.sin(yRad0);
//				final double yR0 = Math.cos(yRad0);
//				final double yRad1 = Math.PI * (-0.5f + (float) (i) / (float) rows);
//				final double y1 = Math.sin(yRad1);
//				final double yR1 = Math.cos(yRad1);
//
//				buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
//				for (int j = 0; j <= cols; j++) {
//					final double xRad = Math.PI * 2 * (double) ((float) (j-1) / (float) cols);
//					final double x = Math.cos(xRad);
//					final double z = Math.sin(xRad);
//					
//					buffer.pos(radius * x * yR0, radius * y0, radius * z * yR0).tex(0, 0).color(red, green, blue, alpha)
//						.normal((float) (x * yR0), (float) (y0), (float) (z * yR0)).endVertex();
//					buffer.pos(radius * x * yR1, radius * y1, radius * z * yR1).tex(1, 1).color(red, green, blue, alpha)
//						.normal((float) (x * yR1), (float) (y1), (float) (z * yR1)).endVertex();
//				}
//				tessellator.draw();
//				
//			}
		}

		matrixStackIn.pop();
	}
	
	private void renderSpark(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, ActiveRenderInfo renderInfo, long ticks, float partialTicks, EffectSpark spark) {
		
		// Translation
		final float pitch = spark.getPitch(ticks, partialTicks);
		final float yaw = spark.getYaw(ticks, partialTicks);
		final double pitchRad = 2 * Math.PI * pitch;
		final double yawRad = 2 * Math.PI * yaw;
		final float offsetX = (float) (ORB_RADIUS * Math.sin(pitchRad) * Math.cos(yawRad));
		final float offsetZ = (float) (ORB_RADIUS * Math.sin(pitchRad) * Math.sin(yawRad));
		
		// Y offset so that lowest is at -.5, not -radius
		final float offsetY = (float) (ORB_RADIUS * -Math.cos(pitchRad)) + ORB_RADIUS -.5f;
		
		// Rotation
		;
		
		// Size
		final float scale = .2f * spark.yawStart;
		final float radius = scale;
		final float smallRadius = scale / 2f;
		
		// Color
		final float brightness = spark.getBrightness(ticks, partialTicks);
		final float red = .24f;
		final float green = .8f;
		final float blue = .93f;
		final float alphaInner = .01f + .2f * brightness;
		final float alphaOuter = .01f + .2f * brightness;
		
		matrixStackIn.push();
		matrixStackIn.translate(offsetX, offsetY, offsetZ);
		RenderFuncs.renderSpaceQuadFacingCamera(matrixStackIn, buffer, renderInfo, radius, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alphaOuter);
		RenderFuncs.renderSpaceQuadFacingCamera(matrixStackIn, buffer, renderInfo, smallRadius, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alphaInner);
		matrixStackIn.pop();
	}
	
	@Override
	public void render(AetherInfuserTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		final float ORB_PERIOD = 200f;
		
		final int ticks = te.getEffectTicks();
		final float allTicks = ticks + partialTicks;
		final float t = (allTicks % ORB_PERIOD) / ORB_PERIOD;
		
		te.getSparks(sparks);
		
		// Calculate opacity for orb. Probably should add glow.
		// 0f to .4f
		final float maxOrbOpacity = .15f;
		final float orbOpacity = maxOrbOpacity * (.75f + .25f * (float)Math.sin(t * 2 * Math.PI)) * te.getChargePerc();
		
		matrixStackIn.push();
		matrixStackIn.translate(.5f, 1f, .5f);
		
		final IVertexBuilder orbBuffer = bufferIn.getBuffer(AetheriaRenderTypes.INFUSER_ORB);
		//renderOrb(matrixStackIn, orbBuffer, combinedLightIn, orbOpacity, false);
		renderOrb(matrixStackIn, orbBuffer, combinedLightIn,orbOpacity, true);
		
		final IVertexBuilder sparkBuffer = bufferIn.getBuffer(AetheriaRenderTypes.INFUSER_SPARK);
		
		//GlStateManager.alphaFunc(516, 0);
//		GlStateManager.disableLighting();
		for (EffectSpark spark : sparks) {
			renderSpark(matrixStackIn, sparkBuffer, combinedLightIn, this.renderDispatcher.renderInfo, ticks, partialTicks, spark);
		}
		
		matrixStackIn.pop();
	}
}
