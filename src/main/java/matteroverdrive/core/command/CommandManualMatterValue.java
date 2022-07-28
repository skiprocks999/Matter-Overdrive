package matteroverdrive.core.command;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import matteroverdrive.References;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandManualMatterValue {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

		dispatcher.register(Commands.literal(References.ID).requires(source -> source.hasPermission(2)).then(Commands
				.literal("addmattervalue").requires(source -> source.hasPermission(2))
				.then(Commands.argument("value", DoubleArgumentType.doubleArg(0)).executes(
						source -> addManualValue(source.getSource(), DoubleArgumentType.getDouble(source, "value"))))));

	}

	private static int addManualValue(CommandSourceStack source, double value) {

		ServerPlayer player = null;

		try {
			player = source.getPlayerOrException();
			ItemStack target = player.getItemInHand(InteractionHand.MAIN_HAND);

			if (target.isEmpty()) {
				source.sendFailure(Component.translatable("command.matteroverdrive.mainhandempty"));
				return 0;
			}

			Path path = Paths.get("Matter Overdrive/manual.json");

			JsonObject obj = new JsonObject();

			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}

			if (Files.exists(path)) {
				obj = (JsonObject) GsonHelper.fromJson(GSON, Files.newBufferedReader(path), JsonElement.class);
			}

			String key = ForgeRegistries.ITEMS.getKey(target.getItem()).toString();

			if (obj.has(key)) {
				obj.remove(key);
			}

			obj.addProperty(key, value);

			Map<String, Double> temp = new HashMap<>();
			List<String> names = new ArrayList<>();

			obj.entrySet().forEach(element -> {
				names.add(element.getKey());
				temp.put(element.getKey(), element.getValue().getAsDouble());
			});

			Collections.sort(names);

			JsonObject ordered = new JsonObject();

			names.forEach(string -> {
				ordered.addProperty(string, temp.get(string));
			});

			String s = GSON.toJson(ordered);

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

			source.sendSuccess(Component.translatable("command.matteroverdrive.assignedvalue", value, key), true);

		} catch (Exception e) {
			source.sendFailure(Component.translatable("command.matteroverdrive.manualfailed"));
		}

		return 0;
	}

}
