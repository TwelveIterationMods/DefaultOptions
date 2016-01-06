package net.blay09.mods.defaultoptions.coremod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name(value = "Default Options")
@IFMLLoadingPlugin.MCVersion(value = "1.8.9")
@IFMLLoadingPlugin.TransformerExclusions(value = "net.blay09.mods.defaultoptions.coremod")
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class DefaultOptionsLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "net.blay09.mods.defaultoptions.coremod.DefaultOptionsClassTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
