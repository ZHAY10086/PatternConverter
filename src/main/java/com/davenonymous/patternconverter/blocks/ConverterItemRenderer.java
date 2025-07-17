package com.davenonymous.patternconverter.blocks;

import com.davenonymous.patternconverter.setup.ModBlocks;
import com.davenonymous.patternconverter.setup.ModDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ConverterItemRenderer  extends BlockEntityWithoutLevelRenderer {
	public ConverterItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		ModelManager modelManager = Minecraft.getInstance().getModelManager();
		BlockState converterState = ModBlocks.CONVERTER_BLOCK.get().defaultBlockState();

		if(stack.has(ModDataComponents.CONVERTER_STYLE_COMPONENT)) {
			var style = stack.get(ModDataComponents.CONVERTER_STYLE_COMPONENT).style();
			converterState = converterState.setValue(ConverterBlock.STYLE, style);
		}

		BakedModel converterModel = modelManager.getBlockModelShaper().getBlockModel(converterState);
		poseStack.pushPose();

		// Center the model before applying item transforms
		poseStack.translate(0.5, 0.5, 0.5);
		if(converterModel.getTransforms().hasTransform(displayContext)) {
			ItemTransform transform = converterModel.getTransforms().getTransform(displayContext);
			if(displayContext.firstPerson() || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
				ItemTransform corrected = new ItemTransform(
					transform.rotation,
					transform.translation,
					transform.scale,
					transform.rightRotation
				);
				corrected.rotation.add(0, 90, 0);
				if(displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
					corrected.rotation.add(0, 270, 0);
				}
				if(displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
					corrected.rotation.add(0, 90, 0);
				}
				corrected.apply(false, poseStack);
			} else {
				transform.apply(false, poseStack);
			}
		}
		poseStack.translate(-0.5, -0.5, -0.5);

		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
			poseStack.last(), buffer.getBuffer(RenderType.solid()), converterState, converterModel, 1.0f, 1.0f, 1.0f, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid()
		);

		poseStack.popPose();
	}
}
