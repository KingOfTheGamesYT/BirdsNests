package panda.birdsnests;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class HarvestLeafEventHandler {

	@SubscribeEvent
	public void onDrops(BlockEvent.BreakEvent event) {
		Block theblock = event.getState().getBlock();
		Random random = new Random();
		BlockPos pos = event.getPos();

		double d0 = random.nextFloat() * 0.5D +0.25D;
		double d1 = random.nextFloat() * 0.5D +0.25D;
		double d2 = random.nextFloat() * 0.5D +0.25D;

		if (theblock.defaultBlockState().is(BlockTags.LEAVES))
		{
			if(random.nextInt(Config.nestRarity.get()) == 0){

				ItemStack stack = new ItemStack(RegistryHandler.BIRDSNEST.get(),1);
				ItemEntity entityitem = new ItemEntity((ServerLevel) event.getLevel(), pos.getX()+d0, pos.getY()+d1, pos.getZ()+d2, stack);
				event.getLevel().addFreshEntity(entityitem);
			}
		}
	}
}