package matteroverdrive.core.command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.References;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.generator.AbstractMatterValueGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandGenerateMatterValues {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(References.ID).requires(source -> source.hasPermission(2))
				.then(Commands.literal("genmatterfile").executes(source -> generateMatterFile(source.getSource(), 300))
						.then(Commands.argument("loops", IntegerArgumentType.integer(1))
								.executes(source -> generateMatterFile(source.getSource(), IntegerArgumentType.getInteger(source, "loops"))))));

	}

	private static int generateMatterFile(CommandSourceStack source, int loops) {

		source.sendSuccess(Component.translatable("command.matteroverdrive.startmattercalc"), true);
		RecipeManager manager = source.getRecipeManager();
		HashMap<Item, Double> generatedValues = new HashMap<>();

		List<AbstractMatterValueGenerator> generators = MatterRegister.INSTANCE.getConsumers();
		for (int i = 0; i < loops; i++) {
			for (AbstractMatterValueGenerator generator : generators) {
				generator.run(generatedValues, manager, i);
				generator.applyGeneratorCorrections(generatedValues, i);
			}
		}

		// now we sort them alphabetically
		List<Pair<String, Double>> sorted = new ArrayList<>();
		List<String> names = new ArrayList<>();
		generatedValues.keySet().forEach(item -> {
			names.add(ForgeRegistries.ITEMS.getKey(item).toString());
		});
		Collections.sort(names);
		names.forEach(string -> {
			String[] split = string.split(":");
			sorted.add(Pair.of(string, generatedValues
					.get(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(split[0], split[1])).get().value())));
		});

		JsonObject json = new JsonObject();

		sorted.forEach(entry -> {
			if (entry.getSecond() > 0) {
				json.addProperty(entry.getFirst(), entry.getSecond());
			}
		});

		Path path = Paths.get("Matter Overdrive/generated.json");
		try {
			String s = GSON.toJson(json);

			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}

			BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

			try {
				bufferedwriter.write(s);
			} catch (Throwable throwable1) {
				if (bufferedwriter != null) {
					try {
						bufferedwriter.close();
					} catch (Throwable throwable) {
						throwable1.addSuppressed(throwable);
					}
				}

				throw throwable1;
			}

			if (bufferedwriter != null) {
				bufferedwriter.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		source.sendSuccess(Component.translatable("command.matteroverdrive.endmattercalc"), true);
		return 1;
	}

}
