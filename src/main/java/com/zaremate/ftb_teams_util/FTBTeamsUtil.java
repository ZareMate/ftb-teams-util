package com.zaremate.ftb_teams_util;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(FTBTeamsUtil.MOD_ID)
public final class FTBTeamsUtil {
    public static final String MOD_ID = "ftb_teams_util";
    public static final String PERMISSION_ROOT = "ftbteamsutil.command";
    public static final String PERMISSION_TEAM = "ftbteamsutil.team";
    public static final String PERMISSION_ADM_MSG = "ftbteamsutil.adm_msg";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FTBTeamsUtil() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ftbteams-util")
            .requires(FTBTeamsUtil::rootAccess)
            .then(FTBTeamsUtilCommands.teamCommand())
            .then(FTBTeamsUtilCommands.admMessageCommand()));
    }

    private static boolean rootAccess(CommandSourceStack source) {
        return source.hasPermission(3)
            || LuckPermsPermissions.hasPermission(source, PERMISSION_ROOT)
            || LuckPermsPermissions.hasPermission(source, PERMISSION_TEAM)
            || LuckPermsPermissions.hasPermission(source, PERMISSION_ADM_MSG);
    }
}
