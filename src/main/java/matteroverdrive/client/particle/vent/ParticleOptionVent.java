package matteroverdrive.client.particle.vent;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import matteroverdrive.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionVent extends ParticleType<ParticleOptionVent> implements ParticleOptions {

	public float scale;
	public float alpha;

	public static final Codec<ParticleOptionVent> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> {
			return instance0.scale;
		}), Codec.FLOAT.fieldOf("alpha").forGetter(instance0 -> {
			return instance0.alpha;
		})).apply(instance, (scale, alpha) -> new ParticleOptionVent().setScale(scale).setAlpha(alpha));
	});

	public static final ParticleOptions.Deserializer<ParticleOptionVent> DESERIALIZER = new ParticleOptions.Deserializer<ParticleOptionVent>() {

		@Override
		public ParticleOptionVent fromCommand(ParticleType<ParticleOptionVent> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			float scale = reader.readFloat();
			reader.expect(' ');
			float alpha = reader.readFloat();
			return new ParticleOptionVent().setScale(scale).setAlpha(alpha);
		}

		@Override
		public ParticleOptionVent fromNetwork(ParticleType<ParticleOptionVent> type, FriendlyByteBuf buffer) {
			return new ParticleOptionVent().setScale(buffer.readFloat()).setAlpha(buffer.readFloat());
		}
	};

	public ParticleOptionVent() {
		super(false, DESERIALIZER);
	}

	public ParticleOptionVent setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public ParticleOptionVent setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}

	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.PARTICLE_VENT.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeFloat(scale);
		buffer.writeFloat(alpha);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", scale: " + scale + ", alpha: " + alpha;
	}

	@Override
	public Codec<ParticleOptionVent> codec() {
		return CODEC;
	}

}
