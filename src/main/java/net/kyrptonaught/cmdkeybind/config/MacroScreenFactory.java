package net.kyrptonaught.cmdkeybind.config;

import net.kyrptonaught.cmdkeybind.CmdKeybindMod;
import net.kyrptonaught.cmdkeybind.MacroTypes.BaseMacro;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigScreen;
import net.kyrptonaught.kyrptconfig.config.screen.ConfigSection;
import net.kyrptonaught.kyrptconfig.config.screen.items.*;
import net.kyrptonaught.kyrptconfig.config.screen.items.number.IntegerItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MacroScreenFactory {

    public static Screen buildScreen(Screen screen) {
        ConfigOptions options = CmdKeybindMod.getConfig();

        ConfigScreen configScreen = new ConfigScreen(screen, Text.translatable("key.cmdkeybind.config.title"));
        configScreen.setSavingEvent(() -> {
            CmdKeybindMod.config.save();
            CmdKeybindMod.buildMacros();
        });
        ConfigSection mainSection = new ConfigSection(configScreen, Text.translatable("key.cmdkeybind.config.category.main"));
        mainSection.addConfigItem(new BooleanItem(Text.translatable("key.cmdkeybind.config.enabled"), options.enabled, true).setSaveConsumer(val -> options.enabled = val));
        mainSection.addConfigItem(new KeybindItem(Text.translatable("key.cmdkeybind.config.openmacrokeybind"), options.openMacroScreenKeybind.rawKey, "key.keyboard.unknown").setSaveConsumer(val -> options.openMacroScreenKeybind.setRaw(val)));
        for (int i = 0; i < options.macros.size(); i++)
            mainSection.addConfigItem(buildNewMacro(mainSection, i));

        mainSection.addConfigItem(new ButtonItem(Text.translatable("key.cmdkeybind.config.add")).setClickEvent(() -> {
            CmdKeybindMod.addEmptyMacro();
            mainSection.insertConfigItem(buildNewMacro(mainSection, options.macros.size() - 1), mainSection.configs.size() - 1);
        }));
        return configScreen;
    }

    private static SubItem<?> buildNewMacro(ConfigSection configSection, int macroNum) {
        ConfigOptions.ConfigMacro macro = CmdKeybindMod.getConfig().macros.get(macroNum);
        SubItem<?> macroSub = (SubItem<?>) new SubItem<>(Text.literal(macro.command)).setToolTip(Text.literal(macro.keyName));
        macroSub.addConfigItem(new TextItem(Text.translatable("key.cmdkeybind.config.macro.command"), macro.command, "/").setMaxLength(1024).setSaveConsumer(cmd -> macro.command = cmd));
        macroSub.addConfigItem(new KeybindItem(Text.translatable("key.cmdkeybind.config.macro.key"), macro.keyName, InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_0).getTranslationKey()).setSaveConsumer(key -> macro.keyName = key));
        macroSub.addConfigItem(new KeybindItem(Text.translatable("key.cmdkeybind.config.macro.keymod"), macro.keyModName, InputUtil.UNKNOWN_KEY.getTranslationKey()).setSaveConsumer(key -> macro.keyModName = key));
        macroSub.addConfigItem(new EnumItem<>(Text.translatable("key.cmdkeybind.config.macrotype"), BaseMacro.MacroType.values(), macro.macroType, BaseMacro.MacroType.SingleUse).setSaveConsumer(val -> macro.macroType = val));
        macroSub.addConfigItem(new IntegerItem(Text.translatable("key.cmdkeybind.config.delay"), macro.delay, 0).setSaveConsumer(val -> macro.delay = val));
        macroSub.addConfigItem(new ButtonItem(Text.translatable("key.cmdkeybind.config.remove")).setClickEvent(() -> {
            CmdKeybindMod.getConfig().macros.remove(macro);
            configSection.configs.remove(macroSub);
        }));
        return macroSub;
    }
}
