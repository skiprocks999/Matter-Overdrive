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

	public double xCenter;
	public double yCenter;
	public double zCenter;

	public float gravity;

	public int age;

	public static final Codec<ParticleOptionReplicator> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(Codec.DOUBLE.fieldOf("xCenter").forGetter(instance0 -> {
			return instance0.xCenter;
		}), Codec.DOUBLE.fieldOf("yCenter").forGetter(instance0 -> {
			return instance0.yCenter;
		}), Codec.DOUBLE.fieldOf("zCenter").forGetter(instance0 -> {
			return instance0.zCenter;
		}), Codec.FLOAT.fieldOf("gravity").forGetter(instance0 -> {
			return instance0.gravity;
		}), Codec.INT.fieldOf("age").forGetter(instance0 -> {
			return instance0.age;
		})).apply(instance, (x, y, z, gravity, age) -> new ParticleOptionReplicator().setCenter(x, y, z)
				.setGravity(gravity).setAge(age));
	});

	public static final ParticleOptions.Deserializer<ParticleOptionReplicator> DESERIALIZER = new ParticleOptions.Deserializer<ParticleOptionReplicator>() {

		@Override
		public ParticleOptionReplicator fromCommand(ParticleType<ParticleOptionReplicator> type, StringReader reader)
				throws CommandSyntaxException {
			ParticleOptionReplicator replicator = new ParticleOptionReplicator();
			reader.expect(' ');
			double centerX = reader.readDouble();
			reader.expect(' ');
			double centerY = reader.readDouble();
			reader.expect(' ');
			double centerZ = reader.readDouble();
			reader.expect(' ');
			float gravity = reader.readFloat();
			reader.expect(' ');
			int age = reader.readInt();
			return replicator.setCenter(centerX, centerY, centerZ).setGravity(gravity).setAge(age);
		}

		@Override
		public ParticleOptionReplicator fromNetwork(ParticleType<ParticleOptionReplicator> type,
				FriendlyByteBuf buffer) {
			return new ParticleOptionReplicator()
					.setCenter(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
					.setGravity(buffer.readFloat()).setAge(buffer.readInt());
		}
	};

	public ParticleOptionReplicator() {
		super(false, DESERIALIZER);
	}

	public ParticleOptionReplicator setCenter(double x, double y, double z) {
		xCenter = x;
		yCenter = y;
		zCenter = z;
		return this;
	}

	public ParticleOptionReplicator setGravity(float gravity) {
		this.gravity = gravity;
		return this;
	}

	public ParticleOptionReplicator setAge(int age) {
		this.age = age;
		return this;
	}

	@Override
	public ParticleType<ParticleOptionReplicator> getType() {
		return DeferredRegisters.PARTICLE_REPLICATOR.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeDouble(xCenter);
		buffer.writeDouble(yCenter);
		buffer.writeDouble(zCenter);
		buffer.writeFloat(gravity);
		buffer.writeInt(age);
	}

	@Override
	public String writeToString() {
		return getType().getRegistryName().toString() + ", xCenter: " + xCenter + ", yCenter: " + yCenter
				+ ", zCenter: " + zCenter + ", gravity: " + gravity + ", age: " + age;
	}

	@Override
	public Codec<ParticleOptionReplicator> codec() {
		return CODEC;
	}

}
