package com.pizzarush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PizzaRushMod implements ModInitializer {
	public static final String MOD_ID = "pizzarush";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item PIZZA_SLICE = registerItem("pizza_slice",
			new Item(new Item.Properties().food(new FoodProperties.Builder()
					.nutrition(5)
					.saturationMod(0.5F)
					.fast()
					.build())));

	public static final Item DELIVERY_PIZZA = registerItem("delivery_pizza",
			new Item(new Item.Properties().food(new FoodProperties.Builder()
					.nutrition(10)
					.saturationMod(0.8F)
					.effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0), 0.35F)
					.build())));

	public static final Block PIZZA_OVEN = registerBlock("pizza_oven",
			new Block(BlockBehaviour.Properties.copy(Blocks.BRICKS).strength(3.5F)));

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
			entries.accept(PIZZA_SLICE);
			entries.accept(DELIVERY_PIZZA);
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.accept(PIZZA_OVEN);
		});

		LOGGER.info("Pizza Rush is ready for orders.");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	private static Item registerItem(String path, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, id(path), item);
	}

	private static Block registerBlock(String path, Block block) {
		Block registered = Registry.register(BuiltInRegistries.BLOCK, id(path), block);
		registerItem(path, new BlockItem(registered, new Item.Properties()));
		return registered;
	}
}
