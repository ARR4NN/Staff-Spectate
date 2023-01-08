package com.arr4nn.staffspectate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    static StaffSpectate plugin;

    public Logger(StaffSpectate plugin) {
        this.plugin = plugin;
    }

    public static void log(String message) {
        if(plugin.getConfig().getBoolean("log-to-file")){
            try {
                Writer writer = new FileWriter(StaffSpectate.getLogFile(),true);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String msg = "["+dtf.format(now)+"] "+message+"\n";
                writer.append(msg);
                writer.flush();
                writer.close();
            }catch (IOException e){
                plugin.getLogger().warning("An error occurred writing to the logs file!");
            }
        }
    }
}
