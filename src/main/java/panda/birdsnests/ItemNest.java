package panda.birdsnests;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemNest extends Item
{

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

    private ItemStack generateConfigLoot(PlayerEntity player) {
        Random rand = player.world.rand;

        class Entry {
            Item item;
            int min;
            int max;
            float weight; // chance = weight

            Entry(Item i, int mn, int mx, float w) {
                item = i;
                min = mn;
                max = mx;
                weight = w;
            }
        }

        List<Entry> entries = new ArrayList<>();
        float totalWeight = 0f;

        for (String entry : Config.nestLootEntries.get()) {
            try {
                if (entry == null || entry.trim().isEmpty()) continue;

                String[] parts = entry.split(",");
                if (parts.length < 3) continue;

                ResourceLocation id = new ResourceLocation(parts[0].trim());
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item == null) continue;

                String countPart = parts[1].trim();
                int min, max;

                if (countPart.contains("-")) {
                    String[] r = countPart.split("-");
                    min = Integer.parseInt(r[0].trim());
                    max = Integer.parseInt(r[1].trim());
                } else {
                    min = max = Integer.parseInt(countPart.trim());
                }

                float weight = Float.parseFloat(parts[2].trim());
                if (weight <= 0f) continue;

                entries.add(new Entry(item, min, max, weight));
                totalWeight += weight;

            } catch (Exception e) {
                System.out.println("[BirdsNests] Bad loot entry: " + entry);
            }
        }

        // If no loot defined properly
        if (entries.isEmpty()) return ItemStack.EMPTY;

        // Weighted random roll using chance as weight
        float r = rand.nextFloat() * totalWeight;
        Entry chosen = null;

        for (Entry e : entries) {
            r -= e.weight;
            if (r <= 0f) {
                chosen = e;
                break;
            }
        }

        // Shouldn't happen, but safety
        if (chosen == null) return ItemStack.EMPTY;

        // Generate amount
        int amount = chosen.min + rand.nextInt(chosen.max - chosen.min + 1);
        return new ItemStack(chosen.item, amount);
    }

    private void generateLoot(World world, PlayerEntity player) {
        ItemStack result = generateConfigLoot(player);

        if (!result.isEmpty()) {
            ItemEntity drop = new ItemEntity(
                    world,
                    player.getPosX(),
                    player.getPosY() + 1.5D,
                    player.getPosZ(),
                    result
            );
            world.addEntity(drop);
        }
    }
    }