package com.example.easyentityride;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget particleField;
    private Text feedbackText = Text.empty();

    private static final Text TITLE_TEXT = Text.literal("EasyEntityRide Config");
    private static final Text HELP_TEXT = Text
            .literal("Enter the particle name (e.g. flame, heart) to change the selection effect.");
    private static final Text LABEL_TEXT = Text.literal("Particle Name");

    public ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Label
        // createDrawableChild is not needed for just text, we draw in render()

        // Text Field
        this.particleField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 20, 200, 20,
                Text.literal("Particle Name"));
        this.particleField.setMaxLength(64);
        this.particleField.setText(ConfigHandler.getParticleName());
        this.particleField.setChangedListener(this::onTextChanged);
        this.addDrawableChild(this.particleField);

        // Save Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            saveAndClose();
        })
                .dimensions(centerX - 105, centerY + 20, 100, 20)
                .build());

        // Cancel Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            this.client.setScreen(this.parent);
        })
                .dimensions(centerX + 5, centerY + 20, 100, 20)
                .build());

        // Initialize feedback
        validate(this.particleField.getText());
    }

    private void onTextChanged(String text) {
        validate(text);
    }

    private void validate(String text) {
        boolean valid = ConfigHandler.isValidParticle(text);
        if (valid) {
            this.particleField.setEditableColor(0xFFFFFF); // White
            this.feedbackText = Text.literal("Valid Particle").formatted(Formatting.GREEN);
        } else {
            this.particleField.setEditableColor(0xFF0000); // Red
            this.feedbackText = Text.literal("Invalid Particle (Will use default)").formatted(Formatting.RED);
        }
    }

    private void saveAndClose() {
        ConfigHandler.setParticleName(this.particleField.getText());
        ConfigHandler.save();
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        // Draw Title
        DrawableHelper.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20,
                0xFFFFFF);

        // Draw Help Text
        DrawableHelper.drawCenteredTextWithShadow(matrices, this.textRenderer,
                HELP_TEXT,
                this.width / 2, 40, 0xAAAAAA);

        // Draw Label
        DrawableHelper.drawTextWithShadow(matrices, this.textRenderer, LABEL_TEXT, this.width / 2 - 100,
                this.height / 2 - 35, 0xA0A0A0);

        // Draw Feedback
        DrawableHelper.drawCenteredTextWithShadow(matrices, this.textRenderer, this.feedbackText, this.width / 2,
                this.height / 2 + 50,
                0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
