package matteroverdrive.core.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import matteroverdrive.core.tile.utils.MenuProviderFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public abstract class GenericTile<T extends MachineTile<T>> extends MachineTile<T> implements Nameable, IUpgradableTile {

  private MenuProviderFactory menuFactory = ((provider, blockPos, access, playerInventory, menuId) ->
          new BasicAddonContainer(provider, new TileEntityLocatorInstance(blockPos), access, playerInventory, menuId));

  public GenericTile(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
    super(base, blockEntityType, pos, state);
  }

  @Override
  public InteractionResult onActivated(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Direction facing, double hitX, double hitY, double hitZ) {
    InteractionResult result = super.onActivated(player, hand, facing, hitX, hitY, hitZ);
    if (result == InteractionResult.PASS) {
      this.openGui(player);
      return InteractionResult.SUCCESS;
    }
    return result;
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, @NotNull Inventory playerInventory, @NotNull Player player) {
    if (this.level != null) {
      return this.menuFactory.build(this, this.worldPosition, ContainerLevelAccess.create(this.level, this.worldPosition), playerInventory, menuId);
    }
    return null;
  }

  public void setMenuFactory(MenuProviderFactory menuFactory) {
    this.menuFactory = menuFactory;
  }



}
