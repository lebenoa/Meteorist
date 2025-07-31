package zgoly.meteorist.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.HungerManager;
import zgoly.meteorist.Meteorist;

enum Mode {
    HUNGER,
    SATURATION,
}

public class AutoFeed extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> feedCommand = sgGeneral.add(new StringSetting.Builder()
            .name("feed-command")
            .description("Command to refill hunger bar.")
            .defaultValue("/feed")
            .build()
    );

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("What threshold to check for")
            .defaultValue(Mode.HUNGER)
            .build()
    );

    private final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
            .name("level")
            .description("Hunger/Saturation level at which to send the command.")
            .defaultValue(12)
            .min(1)
            .sliderRange(1, 20)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Delay after sending a command in ticks (20 ticks = 1 sec).")
            .defaultValue(20)
            .min(1)
            .sliderRange(1, 40)
            .build()
    );

    private int timer;

    public AutoFeed() {
        super(Meteorist.CATEGORY, "auto-feed", "Writes command in chat when hunger/saturation level is low.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer >= delay.get()) {
            HungerManager hungerManager = mc.player.getHungerManager();
            Mode mode = this.mode.get();
            switch (mode) {
                case Mode.HUNGER:
                    if (hungerManager.getFoodLevel() < threshold.get()) {
                        ChatUtils.sendPlayerMsg(feedCommand.get());
                    }
                    break;
                case Mode.SATURATION:
                    if (hungerManager.getSaturationLevel() < threshold.get()) {
                        ChatUtils.sendPlayerMsg(feedCommand.get());
                    }
                    break;
                default:
                    ChatUtils.sendPlayerMsg(feedCommand.get());
                    break;
            }
            timer = 0;
        } else timer++;
    }
}