package tfar.bossmobs;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class ModItems {

    public static final SpawnEggItem BOSS_BLAZE_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.BOSS_BLAZE, 0xffffff,0x000000,new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final SpawnEggItem BOSS_ENDERMAN_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.BOSS_ENDERMAN, 0xffffff,0x000000,new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final SpawnEggItem BOSS_PILLAGER_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.BOSS_PILLAGER, 0xffffff,0x000000,new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final SpawnEggItem BOSS_SHEEP_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.BOSS_SHEEP, 0xffffff,0x000000,new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final SpawnEggItem BOSS_WITCH_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.BOSS_WITCH, 0xffffff,0x000000,new Item.Properties().tab(CreativeModeTab.TAB_MISC));


    public static void register() {
        Registry.register(Registry.ITEM,new ResourceLocation(BossMobs.MODID,"boss_blaze_spawn_egg"),BOSS_BLAZE_SPAWN_EGG);
        Registry.register(Registry.ITEM,new ResourceLocation(BossMobs.MODID,"boss_enderman_spawn_egg"),BOSS_ENDERMAN_SPAWN_EGG);
        Registry.register(Registry.ITEM,new ResourceLocation(BossMobs.MODID,"boss_pillager_spawn_egg"),BOSS_PILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM,new ResourceLocation(BossMobs.MODID,"boss_sheep_spawn_egg"),BOSS_SHEEP_SPAWN_EGG);
        Registry.register(Registry.ITEM,new ResourceLocation(BossMobs.MODID,"boss_witch_spawn_egg"),BOSS_WITCH_SPAWN_EGG);
    }
}
