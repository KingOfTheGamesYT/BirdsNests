package panda.birdsnests;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;

public class ItemNest extends Item
{
	private static final ResourceLocation LOOT_TABLE = new ResourceLocation("birdsnests:nest_loot");


	public ItemNest(String name)
	{
		super(new Item.Properties()
				.maxStackSize(BirdsNests.nestStackSize)
				.group(ItemGroup.MISC)
		);
		this.setRegistryName(new ResourceLocation(BirdsNests.MODID, name));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if(!playerIn.abilities.isCreativeMode) {
			itemstack.shrink(1);
		}
		worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote)
		{
			this.generateLoot(worldIn,playerIn);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}





	private void generateLoot(World world,PlayerEntity player)
	{
		if (LOOT_TABLE == LootTables.EMPTY)
		{
		}
		else
		{
			BlockPos playerPos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());

			LootTable loottable = ServerLifecycleHooks.getCurrentServer().getLootTableManager().getLootTableFromLocation(LOOT_TABLE);
			LootContext lootcontext = (new LootContext.Builder(player.getServer().func_241755_D_())).withParameter(LootParameters.THIS_ENTITY, player).withParameter(LootParameters.ORIGIN, player.getPositionVec()).withRandom(player.getRNG()).withLuck(player.getLuck()).build(LootParameterSets.GIFT);


			List<ItemStack> itemstacklist = loottable.generate(lootcontext);

			for (ItemStack itemstack : itemstacklist)
			{
				ItemEntity entityitem = new ItemEntity(world, player.getPosX(), player.getPosY() + 1.5D, player.getPosZ(), itemstack);
				world.addEntity(entityitem);
			}
		}
	}

	//LootContext lootcontext = (new LootContext.Builder(player.getServerWorld())).withParameter(LootParameters.THIS_ENTITY, player).withParameter(LootParameters.ORIGIN, player.getPositionVec()).withRandom(player.getRNG()).withLuck(player.getLuck()).build(LootParameterSets.ADVANCEMENT);

}