package gtc_expansion.tile.multi;

import gtc_expansion.GTBlocks2;
import gtc_expansion.GTGuiMachine2;
import gtc_expansion.GTMod2;
import gtc_expansion.container.GTContainerImplosionCompressor;
import gtclassic.GTBlocks;
import gtclassic.tile.multi.GTTileMultiBaseMachine;
import gtclassic.util.int3;
import gtclassic.util.recipe.GTRecipeMultiInputList;
import ic2.api.classic.item.IMachineUpgradeItem;
import ic2.core.RotationList;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.inventory.filters.ArrayFilter;
import ic2.core.inventory.filters.BasicItemFilter;
import ic2.core.inventory.filters.CommonFilters;
import ic2.core.inventory.filters.IFilter;
import ic2.core.inventory.filters.MachineFilter;
import ic2.core.inventory.management.AccessRule;
import ic2.core.inventory.management.InventoryHandler;
import ic2.core.inventory.management.SlotType;
import ic2.core.platform.registry.Ic2Items;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GTTileMultiImplosionCompressor extends GTTileMultiBaseMachine {
    protected static final int[] slotInputs = { 0, 1 };
    protected static final int[] slotOutputs = { 2, 3 };
    protected static final int slotFuel = 4;
    public IFilter filter = new MachineFilter(this);
    public static final IBlockState casingStandardState = GTBlocks2.casingStandard.getDefaultState();
    public static final IBlockState casingReinforcedState = GTBlocks.casingReinforced.getDefaultState();
    public static final IBlockState airState = Blocks.AIR.getDefaultState();
    public static final GTRecipeMultiInputList RECIPE_LIST = new GTRecipeMultiInputList("gt.implosioncompressor");
    public static final ResourceLocation GUI_LOCATION = new ResourceLocation(GTMod2.MODID, "textures/gui/implosioncompressor.png");
    private static final int defaultEu = 32;

    public GTTileMultiImplosionCompressor() {
        super(5, 2, defaultEu, 32);
    }

    @Override
    protected void addSlots(InventoryHandler handler) {
        handler.registerDefaultSideAccess(AccessRule.Both, RotationList.ALL);
        handler.registerDefaultSlotAccess(AccessRule.Both, slotFuel);
        handler.registerDefaultSlotAccess(AccessRule.Import, slotInputs);
        handler.registerDefaultSlotAccess(AccessRule.Export, slotOutputs);
        handler.registerDefaultSlotsForSide(RotationList.UP, slotInputs);
        handler.registerDefaultSlotsForSide(RotationList.HORIZONTAL, slotInputs);
        handler.registerDefaultSlotsForSide(RotationList.UP.invert(), slotOutputs);
        handler.registerInputFilter(new ArrayFilter(CommonFilters.DischargeEU, new BasicItemFilter(Items.REDSTONE), new BasicItemFilter(Ic2Items.suBattery)), slotFuel);
        handler.registerInputFilter(filter, slotInputs);
        handler.registerOutputFilter(CommonFilters.NotDischargeEU, slotFuel);
        handler.registerSlotType(SlotType.Fuel, slotFuel);
        handler.registerSlotType(SlotType.Input, slotInputs);
        handler.registerSlotType(SlotType.Output, slotOutputs);
    }

    @Override
    public int[] getInputSlots() {
        return slotInputs;
    }

    @Override
    public IFilter[] getInputFilters(int[] ints) {
        return new IFilter[]{filter};
    }

    @Override
    public boolean isRecipeSlot(int slot) {
        for (int i : this.getInputSlots()) {
            if (slot <= i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getOutputSlots() {
        return slotOutputs;
    }

    @Override
    public GTRecipeMultiInputList getRecipeList() {
        return RECIPE_LIST;
    }

    @Override
    public Class<? extends GuiScreen> getGuiClass(EntityPlayer player) {
        return GTGuiMachine2.GTImplosionCompressorGui.class;
    }

    public ResourceLocation getGuiTexture() {
        return GUI_LOCATION;
    }

    @Override
    public Set<IMachineUpgradeItem.UpgradeType> getSupportedTypes() {
        return new LinkedHashSet<>(Arrays.asList(IMachineUpgradeItem.UpgradeType.values()));
    }

    @Override
    public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
        return new GTContainerImplosionCompressor(entityPlayer.inventory, this);
    }

    @Override
    public Map<BlockPos, IBlockState> provideStructure() {
        Map<BlockPos, IBlockState> states = super.provideStructure();
        int3 dir = new int3(getPos(), getFacing());
        //Top Layer
        states.put(dir.down(1).asBlockPos(), casingReinforcedState);
        states.put(dir.back(1).asBlockPos(), casingReinforcedState);
        states.put(dir.left(1).asBlockPos(), casingStandardState);
        states.put(dir.forward(1).asBlockPos(), casingReinforcedState);
        states.put(dir.forward(1).asBlockPos(), casingStandardState);
        states.put(dir.right(1).asBlockPos(), casingReinforcedState);
        states.put(dir.right(1).asBlockPos(), casingStandardState);
        states.put(dir.back(1).asBlockPos(), casingReinforcedState);
        states.put(dir.back(1).asBlockPos(), casingStandardState);
        //Middle Layer
        states.put(dir.down(1).asBlockPos(), casingReinforcedState);
        int i;
        for (i = 0; i < 2; i++){
            states.put(dir.forward(1).asBlockPos(), casingReinforcedState);
        }
        for (i = 0; i < 2; i++){
            states.put(dir.left(1).asBlockPos(), casingReinforcedState);
        }
        for (i = 0; i < 2; i++){
            states.put(dir.back(1).asBlockPos(), casingReinforcedState);
        }
        states.put(dir.right(1).asBlockPos(), casingReinforcedState);
        states.put(dir.forward(1).asBlockPos(), airState);
        //Bottom Layer
        states.put(dir.down(1).asBlockPos(), casingReinforcedState);
        states.put(dir.back(1).asBlockPos(), casingReinforcedState);
        states.put(dir.left(1).asBlockPos(), casingStandardState);
        states.put(dir.forward(1).asBlockPos(), casingReinforcedState);
        states.put(dir.forward(1).asBlockPos(), casingStandardState);
        states.put(dir.right(1).asBlockPos(), casingReinforcedState);
        states.put(dir.right(1).asBlockPos(), casingStandardState);
        states.put(dir.back(1).asBlockPos(), casingReinforcedState);
        states.put(dir.back(1).asBlockPos(), casingStandardState);
        return states;
    }

    @Override
    public boolean checkStructure() {
        if (!world.isAreaLoaded(pos, 3)) {
            return false;
        }
        int3 dir = new int3(getPos(), getFacing());
        //Top Layer
        if (!isReinforcedCasing(dir.down(1))){
            return false;
        }
        if (!(isReinforcedCasing(dir.back(1)))) {
            return false;
        }
        if (!(isStandardCasing(dir.left(1)))) {
            return false;
        }
        if (!isReinforcedCasing(dir.forward(1))) {
            return false;
        }
        if (!(isStandardCasing(dir.forward(1)))) {
            return false;
        }
        if (!(isReinforcedCasing(dir.right(1)))) {
            return false;
        }
        if (!isStandardCasing(dir.right(1))){
            return false;
        }
        if (!(isReinforcedCasing(dir.back(1)))) {
            return false;
        }
        if (!(isStandardCasing(dir.back(1)))) {
            return false;
        }
        //Middle Layer
        if (!isReinforcedCasing(dir.down(1))) {
            return false;
        }
        int i;
        for (i = 0; i < 2; i++){
            if (isReinforcedCasing(dir.forward(1))) {
                return false;
            }
        }
        for (i = 0; i < 2; i++){
            if (!isReinforcedCasing(dir.left(1))) {
                return false;
            }
        }
        for (i = 0; i < 2; i++){
            if (!(isReinforcedCasing(dir.back(1)))) {
                return false;
            }
        }
        if (!(isReinforcedCasing(dir.right(1)))) {
            return false;
        }
        if (world.getBlockState(dir.forward(1).asBlockPos()) != airState) {
            return false;
        }
        //Bottom Layer
        if (!isReinforcedCasing(dir.down(1))){
            return false;
        }
        if (!(isReinforcedCasing(dir.back(1)))) {
            return false;
        }
        if (!(isStandardCasing(dir.left(1)))) {
            return false;
        }
        if (!isReinforcedCasing(dir.forward(1))) {// left
            return false;
        }
        if (!(isStandardCasing(dir.forward(1)))) {
            return false;
        }
        if (!(isReinforcedCasing(dir.right(1)))) {
            return false;
        }
        if (!isStandardCasing(dir.right(1))){
            return false;
        }
        if (!(isReinforcedCasing(dir.back(1)))) {
            return false;
        }
        if (!(isStandardCasing(dir.back(1)))) {
            return false;
        }
        return true;
    }

    public boolean isStandardCasing(int3 pos) {
        return world.getBlockState(pos.asBlockPos()) == casingStandardState;
    }

    public boolean isReinforcedCasing(int3 pos) {
        return world.getBlockState(pos.asBlockPos()) == casingReinforcedState;
    }
}
