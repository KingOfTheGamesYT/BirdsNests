package panda.birdsnests.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import panda.birdsnests.Config;
import panda.birdsnests.RegistryHandler;

import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeavesDecayMixin
{
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
    {
        // PERSISTENT = player placed, skip those
        if (state.get(LeavesBlock.PERSISTENT)) return;
        // DISTANCE 7 = fully disconnected from log, about to decay
        if (state.get(LeavesBlock.DISTANCE) < 7) return;

        if (!Config.allowDecayDrops.get()) return;

        if (random.nextInt((int)(Config.nestRarity.get() * Config.decayDropModifier.get())) == 0){
            double d0 = random.nextFloat() * 0.5D + 0.25D;
            double d1 = random.nextFloat() * 0.5D + 0.25D;
            double d2 = random.nextFloat() * 0.5D + 0.25D;

            ItemStack stack = new ItemStack(RegistryHandler.BIRDSNEST, 1);
            ItemEntity entityItem = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            world.addEntity(entityItem);
        }
    }
}