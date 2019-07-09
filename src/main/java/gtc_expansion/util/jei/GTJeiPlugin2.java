package gtc_expansion.util.jei;

import gtclassic.util.jei.GTJeiPlugin;
import ic2.jeiIntigration.SubModul;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import javax.annotation.Nonnull;

@JEIPlugin
public class GTJeiPlugin2 implements IModPlugin {
    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime arg0) {
        // empty method for construction
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if (SubModul.load) {
            for (GTJeiRegistry2 entry : GTJeiRegistry2.values()) {
                GTJeiPlugin.wrapperUtil(registry, entry.getRecipeList(), entry.getCatalyst(), entry.getGuiClass(), entry.getClickX(), entry.getClickY(), entry.getSizeX(), entry.getSizeY());
            }
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        for (GTJeiRegistry2 entry : GTJeiRegistry2.values()) {
            GTJeiPlugin.categoryUtil(registry, entry.getRecipeList(), entry.getCatalyst());
        }

    }
}
