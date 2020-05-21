package gtc_expansion.tile.hatch;

import gtc_expansion.GTCXBlocks;
import gtc_expansion.container.GTCXContainerItemFluidHatch;
import gtc_expansion.util.IGTCasingBackgroundBlock;
import gtclassic.api.helpers.GTHelperFluid;
import gtclassic.api.helpers.GTUtility;
import gtclassic.api.interfaces.IGTDebuggableTile;
import gtclassic.api.interfaces.IGTItemContainerTile;
import ic2.api.classic.network.adv.NetworkField;
import ic2.core.RotationList;
import ic2.core.block.base.tile.TileEntityMachine;
import ic2.core.fluid.IC2Tank;
import ic2.core.inventory.base.IHasGui;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.inventory.gui.GuiComponentContainer;
import ic2.core.inventory.management.AccessRule;
import ic2.core.inventory.management.InventoryHandler;
import ic2.core.inventory.management.SlotType;
import ic2.core.item.misc.ItemDisplayIcon;
import ic2.core.util.obj.IClickable;
import ic2.core.util.obj.ITankListener;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GTCXTileItemFluidHatches extends TileEntityMachine implements ITankListener, ITickable, IClickable, IGTItemContainerTile, IHasGui, IGTCasingBackgroundBlock, IGTDebuggableTile {
    boolean input;
    @NetworkField(index = 3)
    protected IC2Tank tank;
    private static final int slotInput = 0;
    private static final int slotOutput = 1;
    private static final int slotDisplay = 2;
    public static final String NBT_TANK = "tank";
    @NetworkField(
            index = 4
    )
    public int casing = 0;
    private int prevCasing = 0;

    @NetworkField(
            index = 5
    )
    public int config = 0;
    private int prevConfig = 0;
    public GTCXTileItemFluidHatches(boolean input) {
        super(3);
        this.input = input;
        this.tank = new IC2Tank(32000);
        this.tank.addListener(this);
        this.addGuiFields(NBT_TANK);
        this.addNetworkFields("casing", "config");
    }

    @Override
    protected void addSlots(InventoryHandler handler) {
        handler.registerDefaultSideAccess(AccessRule.Both, RotationList.ALL);
        handler.registerDefaultSlotAccess(AccessRule.Import, slotInput);
        handler.registerDefaultSlotAccess(AccessRule.Export, slotOutput);
        handler.registerDefaultSlotsForSide(RotationList.ALL, slotInput);
        handler.registerDefaultSlotsForSide(RotationList.ALL, slotOutput);
        handler.registerSlotType(SlotType.Input, slotInput);
        handler.registerSlotType(SlotType.Output, slotOutput);
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("casing") || field.equals("config")) {
            this.prevCasing = this.casing;
            this.prevConfig = this.config;
            this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
        }
    }

    @Override
    public boolean canSetFacing(EntityPlayer player, EnumFacing facing) {
        return this.getFacing() != facing;
    }

    @Override
    public void onTankChanged(IFluidTank iFluidTank) {
        this.getNetwork().updateTileGuiField(this, NBT_TANK);
        this.inventory.set(slotDisplay, ItemDisplayIcon.createWithFluidStack(this.tank.getFluid()));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.tank)
                : super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.tank.readFromNBT(nbt.getCompoundTag(NBT_TANK));
        casing = nbt.getInteger("casing");
        config = nbt.getInteger("config");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.tank.writeToNBT(this.getTag(nbt, NBT_TANK));
        nbt.setInteger("casing", casing);
        nbt.setInteger("config", config);
        return nbt;
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> list = new ArrayList<>();
        list.addAll(getInventoryDrops());
        return list;
    }

    @Override
    public List<ItemStack> getInventoryDrops() {
        List<ItemStack> list = new ArrayList<>();
        list.add(this.getStackInSlot(slotInput));
        list.add(this.getStackInSlot(slotOutput));
        return list;
    }

    @Override
    public boolean canRemoveBlock(EntityPlayer player) {
        return true;
    }

    int tickSkipper = 0;

    @Override
    public void update() {
        if (tickSkipper <= 0){
            if (input) {
                GTUtility.importFromSideIntoMachine(this, this.getFacing());
                importFluidFromMachineToSide(this, tank, this.getFacing(), 1000);
            } else {
                GTUtility.exportFromMachineToSide(this, this.getFacing(), slotOutput);
                GTUtility.exportFluidFromMachineToSide(this, tank, this.getFacing(), 1000);
            }
            GTHelperFluid.doFluidContainerThings(this, this.tank, slotInput, slotOutput);
            if (tickSkipper < 0){
                tickSkipper = 0;
            }
        } else {
            tickSkipper--;
        }

    }

    public void skip5Ticks(){
        tickSkipper = 5;
    }

    /**
     * Export a FluidStack from a TileEntityMachine tank to another tile.
     *
     * @param machine - The TileEntityMachine which has the tank, provides World and
     *                BlockPos data.
     * @param tank    - the IC2Tank to try to import to.
     * @param side    - the EnumFacing to try to export fluids out of.
     * @param amount  - the amount of fluid to transfer
     */
    public static void importFluidFromMachineToSide(TileEntityMachine machine, IC2Tank tank, EnumFacing side,
                                                    int amount) {
        BlockPos importPos = machine.getPos().offset(side);
        if (!machine.getWorld().isBlockLoaded(importPos)) {
            return;
        }
        IFluidHandler fluidTile = FluidUtil.getFluidHandler(machine.getWorld(), importPos, side.getOpposite());
        boolean canImport = (tank.getFluidAmount() == 0 || tank.getFluid() != null) && fluidTile != null;
        if (canImport) {
            FluidUtil.tryFluidTransfer(tank, fluidTile, amount, true);
        }
    }

    @Override
    public boolean hasRightClick() {
        return true;
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumHand hand, EnumFacing enumFacing, Side side) {
        return input ? GTHelperFluid.doClickableFluidContainerEmptyThings(player, hand, world, pos, this.tank) : GTHelperFluid.doClickableFluidContainerFillThings(player, hand, world, pos, this.tank);
    }

    @Override
    public boolean hasLeftClick() {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer entityPlayer, Side side) {

    }

    @Override
    public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
        return new GTCXContainerItemFluidHatch(entityPlayer.inventory, this);
    }

    @Override
    public Class<? extends GuiScreen> getGuiClass(EntityPlayer entityPlayer) {
        return GuiComponentContainer.class;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return !this.isInvalid();
    }

    @Override
    public boolean hasGui(EntityPlayer entityPlayer) {
        return true;
    }

    public IC2Tank getTank() {
        return tank;
    }

    public ItemStack getOutput(){
        return this.getStackInSlot(slotOutput);
    }

    public ItemStack getInput(){
        return this.getStackInSlot(slotInput);
    }

    @Override
    public void getData(Map<String, Boolean> map) {
        map.put("Casing: " + fromCasing(casing).getLocalizedName(), true);
        map.put("Config: "+ config, true);
    }

    public Block fromCasing(int casing){
        switch (casing){
            case 1: return GTCXBlocks.casingStandard;
            case 2: return GTCXBlocks.casingReinforced;
            case 3: return GTCXBlocks.casingAdvanced;
            default: return Blocks.AIR;
        }
    }

    @Override
    public int getCasing(){
        return casing;
    }

    @Override
    public void setCasing(){
        int standard = 0;
        int reinforced = 0;
        int advanced = 0;
        for (EnumFacing facing : EnumFacing.VALUES){
            BlockPos offset = this.getPos().offset(facing);
            if (world.getBlockState(offset).getBlock() == GTCXBlocks.casingStandard){
                standard++;
            } else if (world.getBlockState(offset).getBlock() == GTCXBlocks.casingReinforced){
                reinforced++;
            } else if (world.getBlockState(offset).getBlock() == GTCXBlocks.casingAdvanced){
                advanced++;
            }
        }
        if (standard == 0 && reinforced == 0 && advanced == 0){
            casing = 0;
        }
        else if (standard > 3){
            casing = 1;
        }
        else if (reinforced > 3){
            casing = 2;
        }
        else if (advanced > 3){
            casing = 3;
        }
        else if (standard == 3 && reinforced == 3){
            casing = world.rand.nextInt(1) + 1;
        }
        else if (standard == 3 && advanced == 3){
            casing = world.rand.nextInt(1) == 0 ? 1 : 3;
        }
        else if (reinforced == 3 && advanced == 3){
            casing = world.rand.nextInt(1) + 2;
        }
        else if ((standard == 2 && reinforced == 2 && advanced == 2) || (standard == 1 && reinforced == 1 && advanced == 1)){
            casing = world.rand.nextInt(2) + 1;
        }
        else if (standard == 3){
            casing = 1;
        }
        else if (reinforced == 3){
            casing = 2;
        }
        else if (advanced == 3){
            casing = 3;
        }
        else if ((standard + reinforced == 4) || (standard + reinforced == 2)){
            casing = world.rand.nextInt(1) + 1;
        }
        else if ((standard + advanced == 4) || (standard + advanced == 2)){
            casing = world.rand.nextInt(1) == 0 ? 1 : 3;
        }
        else if ((reinforced + advanced == 4) || (reinforced + advanced == 2)){
            casing = world.rand.nextInt(1) + 2;
        }
        else if (standard == 2){
            casing = 1;
        }
        else if (reinforced == 2){
            casing = 2;
        }
        else if (advanced == 2){
            casing = 3;
        }
        else if (standard == 1){
            casing = 1;
        }
        else if (reinforced == 1){
            casing = 2;
        }
        else if (advanced == 1){
            casing = 3;
        }
        if (casing != this.prevCasing) {
            this.getNetwork().updateTileEntityField(this, "casing");
        }

        this.prevCasing = casing;
    }

    @Override
    public int getConfig(){
        return config;
    }

    @Override
    public void setConfig(){
        if (config != this.prevConfig) {
            this.getNetwork().updateTileEntityField(this, "config");
        }

        this.prevConfig = config;
    }

    @Override
    public EnumFacing getFacing(){
        return super.getFacing();
    }

    @Override
    public boolean getActive(){
        return super.getActive();
    }

    public static class GTCXTileInputHatch extends GTCXTileItemFluidHatches{

        public GTCXTileInputHatch() {
            super(true);
        }
    }

    public static class GTCXTileOutputHatch extends GTCXTileItemFluidHatches{

        public GTCXTileOutputHatch() {
            super(false);
        }
    }

    public static class GTCXTileFusionMaterialInjector extends GTCXTileItemFluidHatches{

        public GTCXTileFusionMaterialInjector() {
            super(true);
        }

        @Override
        public boolean canSetFacing(EntityPlayer player, EnumFacing facing) {
            return this.getFacing() != facing && facing.getAxis().isHorizontal();
        }
    }

    public static class GTCXTileFusionMaterialExtractor extends GTCXTileItemFluidHatches{

        public GTCXTileFusionMaterialExtractor() {
            super(false);
        }

        @Override
        public boolean canSetFacing(EntityPlayer player, EnumFacing facing) {
            return this.getFacing() != facing && facing.getAxis().isHorizontal();
        }
    }
}
