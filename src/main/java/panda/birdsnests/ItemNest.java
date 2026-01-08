package panda.birdsnests;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemNest extends Item {

    public ItemNest() {
        super(new Item.Properties()
                .stacksTo(BirdsNests.nestStackSize)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!playerIn.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        worldIn.playSound(null, playerIn.position().x, playerIn.position().y, playerIn.position().z, SoundEvents.GRASS_BREAK, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            this.generateLoot(worldIn, playerIn);
        }

        return InteractionResultHolder.success(itemstack);
    }

    private void generateLoot(Level world, Player player) {
        ItemStack result = generateConfigLoot(player);

        if (!result.isEmpty()) {
            ItemEntity drop = new ItemEntity(world, player.getX(), player.getY() + 1.5D, player.getZ(), result);
            world.addFreshEntity(drop);
        }
    }

    private ItemStack generateConfigLoot(Player player) {
        RandomSource rand = player.level().random;

        class Entry {
            final String raw;
            final ResourceLocation id;
            final int min;
            final int max;
            final float weight;

            Entry(String raw, ResourceLocation id, int min, int max, float weight) {
                this.raw = raw;
                this.id = id;
                this.min = min;
                this.max = max;
                this.weight = weight;
            }
        }

        List<Entry> pool = new ArrayList<>();
        float totalWeight = 0f;

        // Build pool WITHOUT resolving items yet
        for (String raw : Config.nestLootEntries.get()) {
            try {
                if (raw == null || raw.isBlank()) continue;

                String[] parts = raw.split(",");
                if (parts.length < 3) continue;

                ResourceLocation id = new ResourceLocation(parts[0].trim());

                String countPart = parts[1].trim();
                int min, max;

                if (countPart.contains("-")) {
                    String[] r = countPart.split("-");
                    min = Integer.parseInt(r[0].trim());
                    max = Integer.parseInt(r[1].trim());
                } else {
                    min = max = Integer.parseInt(countPart);
                }

                float weight = Float.parseFloat(parts[2].trim());
                if (weight <= 0f) continue;

                pool.add(new Entry(raw, id, min, max, weight));
                totalWeight += weight;

            } catch (Exception ignored) {
            }
        }

        // Retry loop
        while (!pool.isEmpty()) {
            float roll = rand.nextFloat() * totalWeight;
            Entry chosen = null;

            for (Entry e : pool) {
                roll -= e.weight;
                if (roll <= 0f) {
                    chosen = e;
                    break;
                }
            }

            if (chosen == null) break;

            Item item = BuiltInRegistries.ITEM.get(chosen.id);

            // 🔑 Only log WHEN ACTUALLY CHOSEN
            if (item == Items.AIR) {
                System.out.println("[BirdsNests] Skipped missing loot entry: " + chosen.raw);

                totalWeight -= chosen.weight;
                pool.remove(chosen);
                continue; // reroll
            }

            int amount = chosen.min + rand.nextInt(chosen.max - chosen.min + 1);
            return new ItemStack(item, amount);
        }

        return ItemStack.EMPTY;
    }
}