package matteroverdrive.core.block.state;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block.RotatableBlock.RotationType;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;

public class OverdriveBlockProperties extends BlockBehaviour.Properties {

  /**
   * Whether the block can be waterlogged or not.
   */
  private boolean canBeWaterlogged = false;


  /**
   * How the block can be rotated
   */
  @Nonnull
  private RotatableBlock.RotationType rotationType = RotationType.NONE;

  /**
   * Whether the machine can emit light or not
   */
  private boolean canBeLit = false;

  public OverdriveBlockProperties(Material material) {
    super(material, material.getColor());
  }
  
  public OverdriveBlockProperties(BlockBehaviour.Properties properties) {
	  super(properties.material, properties.material.getColor());
      destroyTime = properties.destroyTime;
      explosionResistance = properties.explosionResistance;
      hasCollision = properties.hasCollision;
      isRandomlyTicking = properties.isRandomlyTicking;
      lightEmission = properties.lightEmission;
      materialColor = properties.materialColor;
      soundType = properties.soundType;
      friction = properties.friction;
      speedFactor = properties.speedFactor;
      dynamicShape = properties.dynamicShape;
      canOcclude = properties.canOcclude;
      isAir = properties.isAir;
      requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
      offsetType = properties.offsetType;
  }
  
  public OverdriveBlockProperties setCanBeLit() {
	  canBeLit = true;
	  return this;
  }
  
  public OverdriveBlockProperties setRotationType(@Nonnull RotationType type) {
	  rotationType = type;
	  return this;
  }
  
  public OverdriveBlockProperties setCanBeWaterlogged() {
	  canBeWaterlogged = true;
	  return this;
  }

  public boolean canBeWaterlogged() {
    return canBeWaterlogged;
  }

  @Nonnull
  public RotatableBlock.RotationType getRotationType() {
    return rotationType;
  }

  public boolean canBeLit() {
    return canBeLit;
  }
  
  //I had to AT the variables regardless so copy-pasta seemed the best course of action
  public static OverdriveBlockProperties copy(@Nonnull OverdriveBlockProperties properties) {
	  
	  OverdriveBlockProperties newProperties = new OverdriveBlockProperties(properties.material);
	  newProperties.material = properties.material;
      newProperties.destroyTime = properties.destroyTime;
      newProperties.explosionResistance = properties.explosionResistance;
      newProperties.hasCollision = properties.hasCollision;
      newProperties.isRandomlyTicking = properties.isRandomlyTicking;
      newProperties.lightEmission = properties.lightEmission;
      newProperties.materialColor = properties.materialColor;
      newProperties.soundType = properties.soundType;
      newProperties.friction = properties.friction;
      newProperties.speedFactor = properties.speedFactor;
      newProperties.dynamicShape = properties.dynamicShape;
      newProperties.canOcclude = properties.canOcclude;
      newProperties.isAir = properties.isAir;
      newProperties.requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
      newProperties.offsetType = properties.offsetType;
      
      //Start of our stuff
      newProperties.canBeLit = properties.canBeLit;
      newProperties.rotationType = properties.rotationType;
      newProperties.canBeWaterlogged = properties.canBeWaterlogged;
      
      return newProperties;
  }
  
  public static OverdriveBlockProperties fromBlockProperties(BlockBehaviour.Properties properties) {
	  
	  OverdriveBlockProperties newProperties = new OverdriveBlockProperties(properties.material);
	  
	  newProperties.material = properties.material;
      newProperties.destroyTime = properties.destroyTime;
      newProperties.explosionResistance = properties.explosionResistance;
      newProperties.hasCollision = properties.hasCollision;
      newProperties.isRandomlyTicking = properties.isRandomlyTicking;
      newProperties.lightEmission = properties.lightEmission;
      newProperties.materialColor = properties.materialColor;
      newProperties.soundType = properties.soundType;
      newProperties.friction = properties.friction;
      newProperties.speedFactor = properties.speedFactor;
      newProperties.dynamicShape = properties.dynamicShape;
      newProperties.canOcclude = properties.canOcclude;
      newProperties.isAir = properties.isAir;
      newProperties.requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
      newProperties.offsetType = properties.offsetType;
      
      return newProperties;
  }

  public static class Defaults {

    public static void init() {}

    // No Rotation

    /**
     * Default Machine (No States)
     */
    public static final OverdriveBlockProperties defaultMachine(Material material) {
    	return new OverdriveBlockProperties(material);
    }
    public static final OverdriveBlockProperties defaultMachine(BlockBehaviour.Properties properties) {
    	return new OverdriveBlockProperties(properties);
    }

    /**
     * Default Machine /w LIT state
     */
    public static final OverdriveBlockProperties litMachine(Material material) {
    	return new OverdriveBlockProperties(material).setCanBeLit();
    }
    public static final OverdriveBlockProperties litMachine(BlockBehaviour.Properties properties) {
    	return new OverdriveBlockProperties(properties).setCanBeLit();
    }
            

    /**
     * Default Machine /w Waterloggable state
     */
    public static final OverdriveBlockProperties waterloggableMachine(Material material) {
    	return new OverdriveBlockProperties(material).setCanBeWaterlogged();
    }
    public static final OverdriveBlockProperties waterloggableMachine(BlockBehaviour.Properties properties) {
    	return new OverdriveBlockProperties(properties).setCanBeWaterlogged();
    }     
    
    /**
     * Default Machine /w Waterloggable state
     */
    public static final OverdriveBlockProperties waterloggableLit(Material material) {
    	return new OverdriveBlockProperties(material).setCanBeWaterlogged().setCanBeLit();
    }
    public static final OverdriveBlockProperties waterloggableLit(BlockBehaviour.Properties properties) {
    	return new OverdriveBlockProperties(properties).setCanBeWaterlogged().setCanBeLit();
    }     

    // With Rotations;

    /**
     * Defaults with Four-Way & Six-Way Rotations
     */
    public static final OverdriveBlockProperties defaultFourWay(Material material) {
    	return OverdriveBlockProperties.copy(defaultMachine(material)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties defaultSixWay(Material material) { 
    	return OverdriveBlockProperties.copy(defaultMachine(material)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties litFourWay(Material material) { 
    	return OverdriveBlockProperties.copy(litMachine(material)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties litSixWay(Material material) { 
    	return OverdriveBlockProperties.copy(litMachine(material)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties waterloggableFourway(Material material) { 
    	return OverdriveBlockProperties.copy(waterloggableMachine(material)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties waterloggableSixway(Material material) { 
    	return OverdriveBlockProperties.copy(waterloggableMachine(material)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties waterloggableLitFourway(Material material) { 
    	return OverdriveBlockProperties.copy(waterloggableLit(material)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties waterloggableLitSixway(Material material) { 
    	return OverdriveBlockProperties.copy(waterloggableLit(material)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    
    public static final OverdriveBlockProperties defaultFourWay(BlockBehaviour.Properties properties) {
    	return OverdriveBlockProperties.copy(defaultMachine(properties)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties defaultSixWay(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(defaultMachine(properties)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties litFourWay(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(litMachine(properties)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties litSixWay(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(litMachine(properties)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties waterloggableFourway(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(waterloggableMachine(properties)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties waterloggableSixway(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(waterloggableMachine(properties)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
    public static final OverdriveBlockProperties waterloggableLitFourway(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(waterloggableLit(properties)).setRotationType(RotatableBlock.RotationType.FOUR_WAY);
    }
    public static final OverdriveBlockProperties waterloggableLitSixway(BlockBehaviour.Properties properties) { 
    	return OverdriveBlockProperties.copy(waterloggableLit(properties)).setRotationType(RotatableBlock.RotationType.SIX_WAY);
    }
  }
}
