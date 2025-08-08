package panda.birdsnests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

public class ItemNest extends Item
{
	private static final ResourceLocation LOOT_TABLE = new ResourceLocation("birdsnests:nest_loot");

	public ItemNest()
	{
		super(new Item.Properties()
				.stacksTo(BirdsNests.nestStackSize)
				//.group(ItemGroup.MISC)
		);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
	{
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		if(!playerIn.getAbilities().instabuild) {
			itemstack.shrink(1);
		}
		worldIn.playSound(null, playerIn.position().x, playerIn.position().y, playerIn.position().z, SoundEvents.GRASS_BREAK, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isClientSide)
		{
			this.generateLoot(worldIn,playerIn);
		}

		return InteractionResultHolder.success(itemstack);
	}

	private void generateLoot(Level world, Player player) {
		if (LOOT_TABLE.equals(BuiltInLootTables.EMPTY)) {
			return;
		}

		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		ServerLevel serverLevel = server.getLevel(Level.OVERWORLD);
		LootTable lootTable = server.getLootData().getLootTable(LOOT_TABLE);

		LootParams.Builder lootParamsBuilder = new LootParams.Builder(serverLevel)
				.withParameter(LootContextParams.THIS_ENTITY, player)
				.withParameter(LootContextParams.ORIGIN, player.position())
				.withLuck(player.getLuck());

		List<ItemStack> itemStackList = lootTable.getRandomItems(lootParamsBuilder.create(LootContextParamSets.GIFT));

		for (ItemStack itemStack : itemStackList) {
			ItemEntity itemEntity = new ItemEntity(world, player.position().x, player.position().y + 1.5D, player.position().z, itemStack);
			world.addFreshEntity(itemEntity);
		}
	}
		}