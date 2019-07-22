package net.kyrptonaught.cmdkeybind.config;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.kyrptonaught.cmdkeybind.ConfigOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyBindEntry extends TooltipListEntry<Pair<String, Integer>> {
    private String keyValue;
    private int keyType;
    private Boolean isListening = false;
    private ButtonWidget buttonWidget, resetButton;
    private Consumer<Pair<String, Integer>> saveConsumer;
    private Supplier<Pair<String, Integer>> defaultValue;
    private List<Element> widgets;

    public KeyBindEntry(String fieldName, String KeyValue, Consumer<Pair<String, Integer>> saveConsumer) {
        super(fieldName, null, false);
        this.defaultValue = null;
        this.keyValue = KeyValue;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, getCleanName(keyValue), widget -> {
            if (isListening)
                widget.setMessage(getCleanName(keyValue));
            else widget.setMessage(getCleanName(">" + keyValue + "<"));
            isListening = !isListening;
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate("text.cloth-config.reset_value")) + 6, 20, I18n.translate("text.cloth-config.reset_value"), widget -> {
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }

    public String getCleanName(String str) {
        return str.replace("key.", "");
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (isListening) {
            keyValue = ConfigOptions.MacroKeyBind.getName(1, int_1);
            keyType = 1;
            updateBtnTxt();
            return true;
        } else return super.mouseClicked(double_1, double_2, int_1);
    }

    private void updateBtnTxt() {
        buttonWidget.setMessage(getCleanName(keyValue));
        changeFocus(false);
        getScreen().setEdited(true, isRequiresRestart());
        isListening = false;
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        keyValue = ConfigOptions.MacroKeyBind.getName(0, int_1);
        keyType = 0;
        updateBtnTxt();
        return true;
    }

    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        Window window = MinecraftClient.getInstance().window;
        //this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get().booleanValue() != bool.get();
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(getFieldName())), y + 5, 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), x, y + 5, 16777215);
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        }
        resetButton.render(mouseX, mouseY, delta);
        buttonWidget.render(mouseX, mouseY, delta);
    }

    @Override
    public Pair<String, Integer> getValue() {
        return Pair.of(keyValue,keyType);
    }

    @Override
    public Optional<Pair<String, Integer>> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }
}
