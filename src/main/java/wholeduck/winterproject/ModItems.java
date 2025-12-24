package wholeduck.winterproject;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {
    public static void InitializeModItems() {
        WinterProject.LOGGER.info("Initializing ModItems!");

        // Register the item group.
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, modItemgroupId("winter-project"), CUSTOM_ITEM_GROUP);
        // Register item to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(modItemgroupId("winter-project")).register(itemGroup -> {
            itemGroup.accept(ModItems.DEBUGSTICK);
        });

    }

    //region Register items
    public static final Item DEBUGSTICK = registerItem(modItemId("debugstick"), Item::new);
    //endregion

    //region Item Groups
    public static final CreativeModeTab CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.DEBUGSTICK))
            .title(Component.translatable("itemGroup.WinterProject"))
            .build();
    //endregion

    //region ResourceKey methods
    private static ResourceKey<Item> modItemId(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(WinterProject.MOD_ID, name));
    }

    private static ResourceKey<CreativeModeTab> modItemgroupId(final String name) {
        return ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(WinterProject.MOD_ID, name));
    }
    //endregion

    //<editor-fold desc="Register methods (Mojang)">
    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(final Block block) {
        return p -> new BlockItem(block, p.useItemDescriptionPrefix());
    }

    private static ResourceKey<Item> vanillaItemId(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.withDefaultNamespace(name));
    }

    private static ResourceKey<Item> blockIdToItemId(final ResourceKey<Block> blockName) {
        return ResourceKey.create(Registries.ITEM, blockName.identifier());
    }

    private static Item registerSpawnEgg(final EntityType<?> type) {
        return registerItem(
                ResourceKey.create(Registries.ITEM, EntityType.getKey(type).withSuffix("_spawn_egg")), SpawnEggItem::new, new Item.Properties().spawnEgg(type)
        );
    }

    private static Item registerBlock(final Block block) {
        return registerBlock(block, BlockItem::new);
    }

    private static Item registerBlock(final Block block, final Item.Properties properties) {
        return registerBlock(block, BlockItem::new, properties);
    }

    private static Item registerBlock(final Block block, final UnaryOperator<Item.Properties> propertiesFunction) {
        return registerBlock(block, (BiFunction<Block, Item.Properties, Item>)((b, p) -> new BlockItem(b, (Item.Properties)propertiesFunction.apply(p))));
    }

    private static Item registerBlock(final Block block, final Block... alternatives) {
        Item item = registerBlock(block);

        for (Block alternative : alternatives) {
            Item.BY_BLOCK.put(alternative, item);
        }

        return item;
    }

    private static Item registerBlock(final Block block, final BiFunction<Block, Item.Properties, Item> itemFactory) {
        return registerBlock(block, itemFactory, new Item.Properties());
    }

    private static Item registerBlock(final Block block, final BiFunction<Block, Item.Properties, Item> itemFactory, final Item.Properties properties) {
        return registerItem(blockIdToItemId(block.builtInRegistryHolder().key()), p -> (Item)itemFactory.apply(block, p), properties.useBlockDescriptionPrefix());
    }

    private static Item registerItem(final String name, final Function<Item.Properties, Item> itemFactory) {
        return registerItem(vanillaItemId(name), itemFactory, new Item.Properties());
    }

    private static Item registerItem(final String name, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        return registerItem(vanillaItemId(name), itemFactory, properties);
    }

    private static Item registerItem(final String name, final Item.Properties properties) {
        return registerItem(vanillaItemId(name), Item::new, properties);
    }

    private static Item registerItem(final String name) {
        return registerItem(vanillaItemId(name), Item::new, new Item.Properties());
    }

    private static Item registerItem(final ResourceKey<Item> key, final Function<Item.Properties, Item> itemFactory) {
        return registerItem(key, itemFactory, new Item.Properties());
    }

    private static Item registerItem(final ResourceKey<Item> key, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = (Item)itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
    //</editor-fold>
}
