package panda.birdsnests;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(BirdsNests.MODID)
public class BirdsNests {

	public static final String MODID = "birdsnests";
	public static boolean allowStacking = false;
	public static int nestStackSize = 64;

	public BirdsNests()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("birdsnests-common.toml"));
		setSettings();
		RegistryHandler.init(modEventBus);
		modEventBus.addListener(this::addCreative);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(new HarvestLeafEventHandler());
		MinecraftForge.EVENT_BUS.register(new DecayLeafEventHandler());
	}

	private void setup(final FMLCommonSetupEvent event)
	{}

	private static void setSettings()
	{
		BirdsNests.allowStacking = Config.allowStacking.get();

		if(BirdsNests.allowStacking == false)
		{
			BirdsNests.nestStackSize = 1;
		}
		else
		{
			BirdsNests.nestStackSize = 64;
		}
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			event.accept(RegistryHandler.BIRDSNEST);

		}
	}
}