package com.zaremate.ftb_teams_util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public final class LuckPermsPermissions {
    private LuckPermsPermissions() {}
    public static boolean hasPermission(CommandSourceStack source, String permission) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return true;
        try {
            LuckPerms lp = LuckPermsProvider.get();
            return lp.getPlayerAdapter(ServerPlayer.class).getPermissionData(player).checkPermission(permission).asBoolean();
        } catch (IllegalStateException e) {
            FTBTeamsUtil.LOGGER.warn("LuckPerms unavailable; denying '{}' for {}", permission, player.getGameProfile().getName());
            return false;
        }
    }
}
