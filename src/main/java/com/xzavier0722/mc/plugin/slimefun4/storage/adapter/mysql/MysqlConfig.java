package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlcommon.SqlCommonConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MysqlConfig extends SqlCommonConfig {
    public MysqlConfig(String host, int port, String database, String tablePrefix, String user, String passwd, boolean useSsl, int maxConnection) {
        super(host, port, database, tablePrefix, user, passwd, useSsl, maxConnection);
    }

    @Override
    public String jdbcUrl() {
        return "jdbc:mysql://"
                + host + ":" + port + "/" + database
                + "?characterEncoding=utf8&useSSL=" + useSsl;
    }

    @Override
    public String driver() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
