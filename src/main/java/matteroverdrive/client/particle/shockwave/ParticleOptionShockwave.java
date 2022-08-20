package matteroverdrive.client.particle.shockwave;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import matteroverdrive.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleOptionShockwave extends ParticleType<ParticleOptionShockwave> implements ParticleOptions {

	public float maxScale;
	public int r;
	public int g;
	public int b;
	public int a;

	public static final Codec<ParticleOptionShockwave> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(Codec.FLOAT.fieldOf("maxscale").forGetter(instance0 -> {
			return instance0.maxScale;
		}), Codec.INT.fieldOf("r").forGetter(instance0 -> {
			return instance0.r;
		}), Codec.INT.fieldOf("g").forGetter(instance0 -> {
			return instance0.g;
		}), Codec.INT.fieldOf("b").forGetter(instance0 -> {
			return instance0.b;
		}), Codec.INT.fieldOf("a").forGetter(instance0 -> {
			return instance0.a;
		})).apply(instance,
				(maxScale, r, g, b, a) -> new ParticleOptionShockwave().setMaxScale(maxScale).setColor(r, g, b, a));
	});

	public static final ParticleOptions.Deserializer<ParticleOptionShockwave> DESERIALIZER = new ParticleOptions.Deserializer<ParticleOptionShockwave>() {

		@Override
		public ParticleOptionShockwave fromCommand(ParticleType<ParticleOptionShockwave> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			float maxScale = reader.readFloat();
			reader.expect(' ');
			int r = reader.readInt();
			reader.expect(' ');
			int g = reader.readInt();
			reader.expect(' ');
			int b = reader.readInt();
			reader.expect(' ');
			int a = reader.readInt();
			return new ParticleOptionShockwave().setMaxScale(maxScale).setColor(r, g, b, a);
		}

		@Override
		public ParticleOptionShockwave fromNetwork(ParticleType<ParticleOptionShockwave> type, FriendlyByteBuf buffer) {
			return new ParticleOptionShockwave().setMaxScale(buffer.readFloat()).setColor(buffer.readInt(),
					buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
	};

	public ParticleOptionShockwave() {
		super(false, DESERIALIZER);
	}

	public ParticleOptionShockwave setMaxScale(float scale) {
		this.maxScale = scale;
		return this;
	}

	public ParticleOptionShockwave setColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.PARTICLE_SHOCKWAVE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeFloat(maxScale);
		buffer.writeInt(r);
		buffer.writeInt(g);
		buffer.writeInt(b);
		buffer.writeInt(a);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString() + ", maxScale: " + maxScale + ", r: " + r
				+ ", g: " + g + ", b: " + b + ", a: " + a;
	}

	@Override
	public Codec<ParticleOptionShockwave> codec() {
		return CODEC;
	}

}
