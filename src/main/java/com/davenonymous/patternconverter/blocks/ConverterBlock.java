package com.davenonymous.patternconverter.blocks;

import com.davenonymous.patternconverter.datacomponents.ConverterStyleDataComponent;
import com.davenonymous.patternconverter.mods.ConverterStyle;
import com.davenonymous.patternconverter.setup.ModBlocks;
import com.davenonymous.patternconverter.setup.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ConverterBlock extends Block implements EntityBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final EnumProperty<ConverterStyle> STYLE = EnumProperty.create("style", ConverterStyle.class);

	private final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 1, 1);

	public ConverterBlock(Properties properties) {
		super(properties
			.sound(SoundType.METAL)
			.strength(5.0f)
		);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(STYLE, ConverterStyle.INTEGRATED_DYNAMICS)
		);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		var forward = context.getHorizontalDirection().getOpposite();
		var player = context.getPlayer();
		if (player != null) {
			if (player.getXRot() > 65) {
				forward = Direction.UP;
			} else if (player.getXRot() < -65) {
				forward = Direction.DOWN;
			}
		}
		return this.defaultBlockState().setValue(FACING, forward);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(STYLE);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if(blockEntityType == ModBlocks.CONVERTER_ENTITY.get()) {
			return (level1, blockPos, blockState, blockEntity) -> {
				if(blockEntity instanceof ConverterBlockEntity converter) {
					converter.tick();
				}
			};
		}
		return null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(level.getBlockEntity(pos) instanceof ConverterBlockEntity converter) {
			player.openMenu(state.getMenuProvider(level, pos), pos);
		}

		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if(level.isClientSide()) {
			return;
		}

		if(!stack.has(ModDataComponents.CONVERTER_STYLE_COMPONENT.get())) {
			return;
		}

		ConverterStyle style = stack.get(ModDataComponents.CONVERTER_STYLE_COMPONENT.get()).style();
		state = state.setValue(STYLE, style);
		level.setBlockAndUpdate(pos, state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if(!(level.getBlockEntity(pos) instanceof ConverterBlockEntity converter)) {
			return super.getCloneItemStack(state, target, level, pos, player);
		}

		if(!state.hasProperty(STYLE)) {
			return super.getCloneItemStack(state, target, level, pos, player);
		}

		ConverterStyle style = state.getValue(STYLE);
		ItemStack clone = new ItemStack(this, 1);
		clone.set(ModDataComponents.CONVERTER_STYLE_COMPONENT.get(), new ConverterStyleDataComponent(style));
		return clone;
	}

	@Override
	protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider(
			(id, inventory, player) -> new ConverterContainer(id, pos, inventory, player),
			Component.translatable("container.patternconverter.converter")
		);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ConverterBlockEntity(blockPos, blockState);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return ModBlocks.CONVERTER_BLOCK_TYPE.get();
	}
}
