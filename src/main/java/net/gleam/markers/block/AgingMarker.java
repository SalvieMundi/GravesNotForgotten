package net.gleam.markers.block;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import net.gleam.markers.Markers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface AgingMarker extends Ageable<AgingMarker.BlockAge> {
	
	Supplier<ImmutableBiMap<Object, Object>> BLOCK_AGE_INCREASES = Suppliers.memoize(() -> {
        return ImmutableBiMap.builder().put(Markers.MARKER, Markers.MARKER_OLD).put(Markers.MARKER_OLD, Markers.MARKER_WEATHERED).put(Markers.MARKER_WEATHERED, Markers.MARKER_FORGOTTEN).build();
    });
	
    Supplier<BiMap<MarkerBase, MarkerBase>> BLOCK_AGE_DECREASES = Suppliers.memoize(() -> {
        return ((BiMap)BLOCK_AGE_INCREASES.get()).inverse();
    });

    static Optional<Block> getDecreasedOxidationBlock(Block block) {
        return Optional.ofNullable((Block)((BiMap)BLOCK_AGE_DECREASES.get()).get(block));
    }

    static Block getUnaffectedOxidationBlock(Block block) {
        Block block2 = block;

        for(Block block3 = (Block)((BiMap)BLOCK_AGE_DECREASES.get()).get(block); block3 != null; block3 = (Block)((BiMap)BLOCK_AGE_DECREASES.get()).get(block3)) {
            block2 = block3;
        }

        return block2;
    }

    static Optional<BlockState> getDecreasedOxidationState(BlockState state) {
        return getDecreasedOxidationBlock(state.getBlock()).map((block) -> {
            return block.getStateWithProperties(state);
        });
    }

    static Optional<Block> getIncreasedOxidationBlock(Block block) {
        return Optional.ofNullable((Block)((BiMap)BLOCK_AGE_INCREASES.get()).get(block));
    }

    static BlockState getUnaffectedOxidationState(BlockState state) {
        return getUnaffectedOxidationBlock(state.getBlock()).getStateWithProperties(state);
    }

    /*default Optional<BlockEntity> getDegradationResultEntity(BlockEntity blockEntity) {
        
        return getIncreasedOxidationBlock(blockEntity.getWorld().getBlockState(blockEntity.getPos())).map((block) -> {
            return block;
        });
    }*/
    
    default Optional<BlockState> getDegradationResultState(BlockState state) {
        return getIncreasedOxidationBlock(state.getBlock()).map((block) -> {
            return block.getStateWithProperties(state);
        });
    }

    default float getDegradationChanceMultiplier() {
        return this.getDegradationLevel() == AgingMarker.BlockAge.FRESH ? 0.75F : 1.0F;
    }

    public static enum BlockAge {
        FRESH,
        OLD,
        WEATHERED,
        FORGOTTEN;

        private BlockAge() {
        }
    }
}