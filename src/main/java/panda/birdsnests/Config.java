package panda.birdsnests;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class Config
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;
    public static final String CATEGORY_GENERAL = "general";
    public static ForgeConfigSpec.BooleanValue allowStacking;
    public static ForgeConfigSpec.BooleanValue allowDecayDrops;
    public static ForgeConfigSpec.IntValue nestRarity;
    public static ForgeConfigSpec.DoubleValue decayDropModifier;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> nestLootEntries;

    static
    {
        COMMON_BUILDER.comment("Bird Nest Settings").push(CATEGORY_GENERAL);

        nestRarity = COMMON_BUILDER
                .comment("[range: 0 ~ 1000, default: 40]")
                .defineInRange("NEST_DROP_RARITY", 40, 0, 1000);

        allowStacking = COMMON_BUILDER
                .comment("Allows to enable/disable nests stacking [default: false]")
                .define("ALLOW_STACKING", false);

        decayDropModifier = COMMON_BUILDER
                .comment("This makes nests more (or less) rare from decaying leaves. Leave at 1 for no change. [range: 0.0 ~ 1000.0, default: 1.25]")
                .defineInRange("NEST_DECAY_DROP_MULTIPLIER", 1.25, 0.0, 1000.0);

        allowDecayDrops = COMMON_BUILDER
                .comment("Allows to enable/disable nests dropping from decaying leaves [default: true]")
                .define("ALLOW_DECAY_DROPS", true);

        nestLootEntries = COMMON_BUILDER
                .comment("Loot entries for bird nests. Format: item_id, count, chance")
                .defineList("NEST_LOOT_ENTRIES",
                        Arrays.asList(
                                "minecraft:stick, 1-4, 1.0",
                                "minecraft:feather, 0-3, 1.0",
                                "minecraft:egg, 1, 0.33",
                                "minecraft:string, 1-2, 0.50",
                                "minecraft:bone, 1-2, 0.37",
                                "minecraft:flint, 1, 0.25",
                                "minecraft:wheat_seeds, 1-2, 0.125",
                                "minecraft:melon_seeds, 1, 0.04",
                                "minecraft:pumpkin_seeds, 1, 0.04",
                                "minecraft:beetroot_seeds, 1, 0.04",
                                "minecraft:diamond, 1, 0.04",
                                "minecraft:emerald, 1, 0.0333",
                                "minecraft:prismarine_shard, 1, 0.016667",
                                "minecraft:tropical_fish, 1, 0.05",
                                "minecraft:salmon, 1, 0.05",
                                "minecraft:pufferfish, 1, 0.05",
                                "minecraft:cod, 1, 0.05",
                                "minecraft:redstone, 1-2, 0.0625",
                                "minecraft:glowstone_dust, 1-2, 0.111",
                                "minecraft:blaze_powder, 1-2, 0.025",
                                "minecraft:gold_nugget, 1-4, 0.125",
                                "minecraft:iron_nugget, 1-3, 0.25",
                                "dangerzone:coarse_amethyst, 1, 0.0333"
                                ),
                        o -> o instanceof String);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent)
    {
        LOGGER.debug("Config Loaded Event ");
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) { LOGGER.debug("Config Re-Loaded Event "); }
}
