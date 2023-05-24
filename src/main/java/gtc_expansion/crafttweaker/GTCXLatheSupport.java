package gtc_expansion.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import gtc_expansion.recipes.GTCXRecipeLists;
import gtc_expansion.tile.GTCXTileLathe;
import gtclassic.api.crafttweaker.GTCraftTweakerActions;
import ic2.api.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Locale;

@ZenClass("mods.gtclassic.Lathe")
@ZenRegister
public class GTCXLatheSupport {
    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input1, @Optional(valueLong = 400L)int totalEu){
        GTCraftTweakerActions.apply(new LatheRecipeAction(GTCraftTweakerActions.of(input1), totalEu, CraftTweakerMC.getItemStack(output)));
    }

    private static final class LatheRecipeAction implements IAction {

        private final IRecipeInput input1;
        private final int totalEu;
        private final ItemStack output;

        LatheRecipeAction(IRecipeInput input1, int totalEu, ItemStack output) {
            this.input1 = input1;
            this.totalEu = totalEu;
            this.output = output;
        }

        @Override
        public void apply() {
            if (totalEu <= 0) {
                CraftTweakerAPI.logError(CraftTweakerAPI.getScriptFileAndLine() + " > "
                        + "Eu amount must be greater then 0!!");
                return;
            }
            GTCXTileLathe.addRecipe(input1, totalEu, output, output.getTranslationKey() + "_ct");
        }

        @Override
        public String describe() {
            return String.format(Locale.ENGLISH, "Add Recipe[%s, %s -> %s] to %s", input1, totalEu, output, GTCXRecipeLists.LATHE_RECIPE_LIST);
        }
    }
}
