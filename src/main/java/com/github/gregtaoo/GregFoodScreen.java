package com.github.gregtaoo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class GregFoodScreen {
    public static class UpdateScreen extends Screen {
        private MultilineText lines;
        public UpdateScreen(Text title) {
            super(title);
            this.lines = MultilineText.EMPTY;
        }
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.lines.drawCenterWithShadow(matrices,this.width/2,40,12,65535);
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
            drawTextWithShadow(matrices,this.textRenderer,new TranslatableText("screen.update.tip"),160,160,65280);
            drawCenteredText(matrices,this.textRenderer, Text.of("Copyright GregFood; Mod version:"+ gregfood.verNum+"; Last version:"+gregfood.newVerNum),this.width/2,this.height-40,16777215);
            super.render(matrices, mouseX, mouseY, delta);
        }
        protected void init() {
            super.init();
            TranslatableText text = new TranslatableText("menu.update.false");
            if(gregfood.hasNewVer){ text = new TranslatableText("menu.update.true");}
            this.addDrawableChild(new ButtonWidget(20,150,140,20, text, (buttonWidget) -> {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+"https://www.mcbbs.net/thread-1120123-1-1.html");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            this.addDrawableChild(new ButtonWidget(20,50,100,20, new TranslatableText("screen.advice"), (buttonWidget) -> {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+"https://www.wjx.cn/vm/OdYrJvJ.aspx");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            this.addDrawableChild(new ButtonWidget(20,70,100,20, new TranslatableText("screen.email"), (buttonWidget) -> {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+"mailto:gregtaoo@outlook.com");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            this.lines = MultilineText.create(this.textRenderer,new TranslatableText("screen.update.info"),this.width - 20);
            this.addDrawableChild(new ButtonWidget(20, this.height - 27, 150, 20, new TranslatableText("screen.announce"), (button) -> {
                if(this.client!=null)
                    this.client.openScreen(new AnnounceScreen(new TranslatableText("screen.announce")));
            }));
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, ScreenTexts.DONE, (button) -> {
                if(this.client!=null)
                  this.client.openScreen(new TitleScreen());
            }));
        }
    }
    public static class AnnounceScreen extends Screen {
        private MultilineText lines;
        public AnnounceScreen(Text title) {
            super(title);
            this.lines = MultilineText.EMPTY;
        }
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.lines.drawCenterWithShadow(matrices,this.width/2,40,12,65535);
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
            super.render(matrices, mouseX, mouseY, delta);
        }
        protected void init() {
            super.init();
            this.lines = MultilineText.create(this.textRenderer, Text.of(gregfood.Announcement), this.width - 20);
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, new TranslatableText("screen.update"), (button) -> {
                if(this.client!=null)
                    this.client.openScreen(new UpdateScreen(new TranslatableText("screen.update")));
            }));
        }

    }
    public static class ForceUpdateScreen extends Screen {
        private MultilineText lines;
        public ForceUpdateScreen(Text title) {
            super(title);
            this.lines = MultilineText.EMPTY;
        }
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.lines.drawCenterWithShadow(matrices,this.width/2,40,12,65535);
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
            super.render(matrices, mouseX, mouseY, delta);
        }
        protected void init() {
            super.init();
            this.lines = MultilineText.create(this.textRenderer, Text.of(gregfood.forceUpdateAnn), this.width - 20);
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, new TranslatableText("screen.force.button"), (button) -> {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+"https://www.mcbbs.net/thread-1120123-1-1.html");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(this.client!=null)
                    this.client.scheduleStop();
            }));
            this.addDrawableChild(new ButtonWidget(5, this.height - 27, 75, 15, new TranslatableText("screen.force.cancel"), (button) -> {
                if(this.client!=null)
                    this.client.openScreen(new TitleScreen());
            }));
        }

    }
}

