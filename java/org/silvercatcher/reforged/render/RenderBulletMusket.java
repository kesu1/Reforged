package org.silvercatcher.reforged.render;

import org.silvercatcher.reforged.ReforgedReferences.Textures;
import org.silvercatcher.reforged.models.ModelBulletMusket;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBulletMusket extends ReforgedRender {
	
	public RenderBulletMusket(RenderManager renderManager) {
		super(renderManager, new ModelBulletMusket(), 0);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return Textures.BULLET_MUSKET;
	}
}