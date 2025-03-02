package com.finallion.graveyard.blocks;


import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.init.TGSounds;
import com.google.common.base.Predicates;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class AltarBlock extends Block {
    public static final BooleanProperty BLOODY = BooleanProperty.of("bloody");
    private static BlockPattern COMPLETED_ALTAR;


    public AltarBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BLOODY, false)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BLOODY);
    }


    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (state.get(BLOODY)) {
            if (random.nextInt(10) == 0) {
                world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, TGSounds.ALTAR_AMBIENT, SoundCategory.BLOCKS, 0.05F, random.nextFloat() * 0.4F + 0.8F, true);
            }
        }

    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_ALTAR == null) {
            COMPLETED_ALTAR = BlockPatternBuilder.start().aisle("???x???", "???????", "???????", "???????", "???????", "???????", "???????", "a??b??c")
                    .where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
                    .where('a', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF).or(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF))))
                    .where('b', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.MIDDLE_BONE_STAFF)))
                    .where('c', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF).or(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF))))
                    .where('x', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.ALTAR)))
                    .build();
        }

        return COMPLETED_ALTAR;
    }

}
