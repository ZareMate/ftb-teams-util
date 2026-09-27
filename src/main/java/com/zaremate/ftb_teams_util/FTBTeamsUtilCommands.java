package com.zaremate.ftb_teams_util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import dev.ftb.mods.ftbteams.api.TeamRank;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import java.util.*;

public final class FTBTeamsUtilCommands {
    private FTBTeamsUtilCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> teamCommand() {
        return Commands.literal("team")
            .requires(s -> s.hasPermission(3) || LuckPermsPermissions.hasPermission(s, FTBTeamsUtil.PERMISSION_TEAM))
            .then(Commands.argument("identifier", StringArgumentType.word())
                .suggests(identifierSuggestions())
                .executes(c -> showTeam(c.getSource(), StringArgumentType.getString(c, "identifier"))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> admMessageCommand() {
        return Commands.literal("adm-msg")
            .requires(s -> s.hasPermission(3) || LuckPermsPermissions.hasPermission(s, FTBTeamsUtil.PERMISSION_ADM_MSG))
            .then(Commands.argument("team", StringArgumentType.word())
                .suggests(teamSuggestions())
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(c -> sendAdminMessage(c.getSource(), StringArgumentType.getString(c, "team"), StringArgumentType.getString(c, "message")))));
    }

    private static SuggestionProvider<CommandSourceStack> identifierSuggestions() {
        return (ctx, builder) -> {
            MinecraftServer server = ctx.getSource().getServer();
            List<String> values = new ArrayList<>();
            server.getPlayerList().getPlayers().forEach(p -> values.add(p.getGameProfile().getName()));
            server.getProfileCache().ifPresent(c -> c.getTopMRUProfiles().forEach(p -> values.add(p.getName())));
            for (Team team : FTBTeamsAPI.api().getManager().getTeams()) { values.add(team.getShortName()); values.add(team.getId().toString()); }
            return SharedSuggestionProvider.suggest(values.stream().distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList(), builder);
        };
    }

    private static SuggestionProvider<CommandSourceStack> teamSuggestions() {
        return (ctx, builder) -> {
            List<String> values = new ArrayList<>();
            for (Team team : FTBTeamsAPI.api().getManager().getTeams()) { values.add(team.getShortName()); values.add(team.getId().toString()); }
            return SharedSuggestionProvider.suggest(values.stream().distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList(), builder);
        };
    }

    private static int showTeam(CommandSourceStack source, String identifier) {
        TeamManager manager = FTBTeamsAPI.api().getManager();
        Team team = resolveTeamOrPlayer(manager, source.getServer(), identifier);
        if (team == null) { source.sendFailure(Component.literal("No FTB Team found for: " + identifier)); return 0; }
        source.sendSuccess(() -> Component.literal("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").withStyle(ChatFormatting.DARK_GRAY), false);
        source.sendSuccess(() -> Component.literal("FTB TEAM").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal("ID: ").withStyle(ChatFormatting.GRAY).append(Component.literal(team.getId().toString()).withStyle(ChatFormatting.YELLOW)), false);
        source.sendSuccess(() -> Component.literal("Name: ").withStyle(ChatFormatting.GRAY).append(Component.literal(team.getShortName()).withStyle(ChatFormatting.AQUA)), false);
        source.sendSuccess(() -> Component.literal("Type: ").withStyle(ChatFormatting.GRAY).append(Component.literal(team.getTypeTranslationKey()).withStyle(ChatFormatting.WHITE)), false);
        source.sendSuccess(() -> Component.literal("Owner: ").withStyle(ChatFormatting.GRAY).append(Component.literal(team.getOwner().toString()).withStyle(ChatFormatting.YELLOW)), false);
        source.sendSuccess(() -> Component.literal("Members:").withStyle(ChatFormatting.GRAY), false);
        List<UUID> members = new ArrayList<>(team.getMembers());
        members.sort(Comparator.comparingInt((UUID id) -> team.getRankForPlayer(id).getPower()).reversed().thenComparing(id -> playerName(source.getServer(), id), String.CASE_INSENSITIVE_ORDER));
        if (members.isEmpty()) { source.sendSuccess(() -> Component.literal("  - none").withStyle(ChatFormatting.DARK_GRAY), false); }
        for (UUID id : members) {
            TeamRank rank = team.getRankForPlayer(id); String name = playerName(source.getServer(), id);
            source.sendSuccess(() -> Component.literal("  - ").append(Component.literal(name).withStyle(ChatFormatting.WHITE)).append(Component.literal(" [" + rank.getSerializedName().toUpperCase(Locale.ROOT) + "]").withStyle(ChatFormatting.GRAY)).append(Component.literal(" " + id).withStyle(ChatFormatting.DARK_GRAY)), false);
        }
        return 1;
    }

    private static int sendAdminMessage(CommandSourceStack source, String identifier, String message) {
        Team team = resolveTeamOnly(FTBTeamsAPI.api().getManager(), identifier);
        if (team == null) { source.sendFailure(Component.literal("Team not found: " + identifier)); return 0; }
        team.sendMessage(net.minecraft.util.Util.NIL_UUID, Component.literal("[ADM] " + message).withStyle(s -> s.withColor(0xFF5555)));
        source.sendSuccess(() -> Component.literal("Sent admin message to ").append(team.getName()).withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    private static Team resolveTeamOrPlayer(TeamManager manager, MinecraftServer server, String id) {
        UUID uuid = parseUuid(id);
        if (uuid != null) { Team t = manager.getTeamByID(uuid).orElse(null); return t != null ? t : manager.getTeamForPlayerID(uuid).orElse(null); }
        Team t = manager.getTeamByName(id).orElse(null);
        if (t != null) return t;
        UUID player = resolvePlayerUuid(server, id);
        return player == null ? null : manager.getTeamForPlayerID(player).orElse(null);
    }

    private static Team resolveTeamOnly(TeamManager manager, String id) {
        UUID uuid = parseUuid(id);
        return uuid != null ? manager.getTeamByID(uuid).orElse(null) : manager.getTeamByName(id).orElse(null);
    }

    private static UUID resolvePlayerUuid(MinecraftServer server, String id) {
        UUID uuid = parseUuid(id); if (uuid != null) return uuid;
        ServerPlayer online = server.getPlayerList().getPlayerByName(id); if (online != null) return online.getUUID();
        return server.getProfileCache().flatMap(c -> c.get(id)).map(p -> p.getId()).orElse(null);
    }

    private static String playerName(MinecraftServer server, UUID uuid) {
        ServerPlayer online = server.getPlayerList().getPlayer(uuid); if (online != null) return online.getGameProfile().getName();
        return server.getProfileCache().flatMap(c -> c.get(uuid)).map(p -> p.getName()).orElse(uuid.toString());
    }

    private static UUID parseUuid(String text) { try { return UUID.fromString(text); } catch (IllegalArgumentException e) { return null; } }
}