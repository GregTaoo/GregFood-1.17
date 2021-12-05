package com.github.gregtaoo.mixin;

import com.github.gregtaoo.GregFoodScreen;
import com.github.gregtaoo.gregfood;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info){
        if(gregfood.forceUpdate && this.client != null){
            this.client.openScreen(new GregFoodScreen.ForceUpdateScreen(new TranslatableText("screen.force")));
        }
        TranslatableText text = new TranslatableText("screen.update");
        if(gregfood.hasNewVer){
            text = new TranslatableText("menu.update.true");
        }
        this.addButton(new ButtonWidget(20,85,140,20, text, (buttonWidget) -> {
            if(this.client!=null)
              this.client.openScreen(new GregFoodScreen.AnnounceScreen(new TranslatableText("screen.announce")));
        }));
    }
}