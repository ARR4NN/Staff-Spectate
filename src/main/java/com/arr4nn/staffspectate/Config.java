package com.arr4nn.staffspectate;

public class Config {
    public static final String RETURN_TO_LOCATION = "return-to-location";
    public static final String NOTIFY_UPDATES = "notify_updates";
    public static final String CMD_NOPERM = "language.commands.no-permissions";
    public static final String SELF_SPECTATE = "language.commands.spectate.self-spectate";
    public static final String NOW_SPECTATING_SPECTATE = "language.commands.spectate.spectating";
    public static final String NO_USER_SPECTATE = "language.commands.spectate.no-user";
    public static final String IN_USE_SPECTATE = "language.commands.spectate.already-spectating";

    public static final String SPECTATE_END = "language.commands.stopSpectate.spectate-end";
    public static final String NOT_SPECTATING = "language.commands.stopSpectate.not-spectating";
    public static final String CONFIG_VERSION = "config-version";
    public static final String PLUGIN_VERSION = "plugin-version";



    public Config(StaffSpectate main) {
        main.getConfig().addDefault(RETURN_TO_LOCATION,true);
        main.getConfig().addDefault(NOTIFY_UPDATES,true);
        main.getConfig().addDefault(CMD_NOPERM,"<red>You don't have enough permissions to do this!");
        main.getConfig().addDefault(SELF_SPECTATE,"<red>You can't spectate yourself.");
        main.getConfig().addDefault(NOW_SPECTATING_SPECTATE,"<green>You are now spectating {user}.");
        main.getConfig().addDefault(NO_USER_SPECTATE,"<red>Please provide a user to spectate! (/spectate <player>)");
        main.getConfig().addDefault(IN_USE_SPECTATE,"<red>You need to leave your spectating session to use this!");
        main.getConfig().addDefault(SPECTATE_END,"<green>Returned you to your previous location.");
        main.getConfig().addDefault(NOT_SPECTATING,"<red>You are not spectating anyone!");
        main.getConfig().addDefault(CONFIG_VERSION,"${config.version}");
        main.getConfig().addDefault(PLUGIN_VERSION,"${project.version}");
    }
}