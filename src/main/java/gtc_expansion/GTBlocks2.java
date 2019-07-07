package gtc_expansion;

import gtc_expansion.block.GTBlockCasing2;
import gtc_expansion.block.GTBlockTile;
import gtc_expansion.tile.GTTileAlloySmelter;
import gtc_expansion.tile.GTTileElectrolyzer;
import gtc_expansion.tile.multi.GTTileMultiImplosionCompressor;
import gtc_expansion.tile.multi.GTTileMultiIndustrialGrinder;
import gtc_expansion.tile.multi.GTTileMultiVacuumFreezer;
import gtclassic.color.GTColorBlockInterface;
import gtclassic.color.GTColorItemBlock;
import gtclassic.itemblock.GTItemBlockInterface;
import gtclassic.itemblock.GTItemBlockRare;
import ic2.core.IC2;
import ic2.core.item.block.ItemBlockRare;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GTBlocks2 {
    static final List<Block> toRegister = new ArrayList();
    public static GTBlockTile electrolyzer = registerBlock(new GTBlockTile("industrialelectrolyzer", 1));
    public static GTBlockTile alloySmelter = registerBlock(new GTBlockTile("alloysmelter"));
    public static GTBlockTile industrialGrinder = registerBlock(new GTBlockTile("industrialgrinder"));
    public static GTBlockTile implosionCompressor = registerBlock(new GTBlockTile("implosioncompressor"));
    public static GTBlockTile vacuumFreezer = registerBlock(new GTBlockTile("vacuumfreezer"));

    public static GTBlockCasing2 casingStandard = registerBlock(new GTBlockCasing2("standard", 2));
    public static GTBlockCasing2 casingAdvanced = registerBlock(new GTBlockCasing2("advanced", 1));
    protected static final String[] textureTileBasic = new String[]{"industrialelectrolyzer", "alloysmelter", "industrialgrinder", "vacuumfreezer", "implosioncompressor"};
    public static void registerBlocks() {
        Iterator var0 = toRegister.iterator();
        Block block;
        while(var0.hasNext()) {
            block = (Block)var0.next();
            createBlock(block);
        }

    }

    static <T extends Block> T registerBlock(T block) {
        toRegister.add(block);
        return block;
    }

    public static void createBlock(Block block) {
        IC2.getInstance().createBlock(block, getItemBlock(block));
    }

    static Class<? extends ItemBlockRare> getItemBlock(Block block) {
        if (block instanceof GTItemBlockInterface) {
            return ((GTItemBlockInterface)block).getCustomItemBlock();
        } else {
            return block instanceof GTColorBlockInterface ? GTColorItemBlock.class : GTItemBlockRare.class;
        }
    }

    public static void registerTiles() {
        registerUtil(GTTileElectrolyzer.class, "IndustrialElectrolyzer");
        registerUtil(GTTileAlloySmelter.class, "AlloySmelter");
        registerUtil(GTTileMultiIndustrialGrinder.class, "IndustrialGrinder");
        registerUtil(GTTileMultiImplosionCompressor.class, "ImplosionCompressor");
        registerUtil(GTTileMultiVacuumFreezer.class, "VacuumFreezer");
    }

    public static void registerUtil(Class tile, String name) {
        GameRegistry.registerTileEntity(tile, new ResourceLocation("gtclassic", "tileEntity" + name));
    }
}
