package matteroverdrive.client.particle.replicator;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import matteroverdrive.DeferredRegisters;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class ParticleOptionReplicator extends ParticleType<ParticleOptionReplicator> implements ParticleOptions {

	public float gravity;
	public float scale;
	public int age;

	public static final Codec<ParticleOptionReplicator> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(Codec.FLOAT.fieldOf("gravity").forGetter(instance0 -> {
			return instance0.gravity;
		}),Codec.FLOAT.fieldOf("scale").forGetter(instance0 -> {
			return instance0.scale;
		}), Codec.INT.fieldOf("age").forGetter(instance0 -> {
			return instance0.age;
		})).apply(instance, (gravity, scale, age) -> new ParticleOptionReplicator()
				.setGravity(gravity).setScale(scale).setAge(age));
	});

	public static final ParticleOptions.Deserializer<ParticleOptionReplicator> DESERIALIZER = new ParticleOptions.Deserializer<ParticleOptionReplicator>() {

		@Override
		public ParticleOptionReplicator fromCommand(ParticleType<ParticleOptionReplicator> type, StringReader reader)
				throws CommandSyntaxException {
			ParticleOptionReplicator replicator = new ParticleOptionReplicator();
			reader.expect(' ');
			float gravity = reader.readFloat();
			reader.expect(' ');
			float scale = reader.readFloat();
			reader.expect(' ');
			int age = reader.readInt();
			return replicator.setGravity(gravity).setScale(scale).setAge(age);
		}

		@Override
		public ParticleOptionReplicator fromNetwork(ParticleType<ParticleOptionReplicator> type,
				FriendlyByteBuf buffer) {
			return new ParticleOptionReplicator()
					.setGravity(buffer.readFloat()).setScale(buffer.readFloat()).setAge(buffer.readInt());
		}
	};

	public ParticleOptionReplicator() {
		super(false, DESERIALIZER);
	}

	public ParticleOptionReplicator setGravity(float gravity) {
		this.gravity = gravity;
		return this;
	}

	public ParticleOptionReplicator setAge(int age) {
		this.age = age;
		return this;
	}
	
	public ParticleOptionReplicator setScale(float scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public ParticleType<ParticleOptionReplicator> getType() {
		return DeferredRegisters.PARTICLE_REPLICATOR.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeFloat(gravity);
		buffer.writeFloat(scale);
		buffer.writeInt(age);
	}

	@Override
	public String writeToString() {
		return getType().getRegistryName().toString() + ", gravity: " + gravity + ", scale: " + scale + ", age: " + age;
	}

	@Override
	public Codec<ParticleOptionReplicator> codec() {
		return CODEC;
	}

}
