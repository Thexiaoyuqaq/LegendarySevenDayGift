package com.gyzer.sevendaygift.DataBase;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MysqlStore extends DataProvider{
    public MysqlStore(){
        setConnectPool();
    }

    private static HikariDataSource connectPool;

    public static void setConnectPool()
    {
        HikariConfig hikariConfig=new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        ConfigurationSection section= LegendarySevenDayGift.getInstance().getConfig().getConfigurationSection("HikariCP");
        hikariConfig.setConnectionTimeout(section.getLong("connectionTimeout"));
        hikariConfig.setMinimumIdle(section.getInt("minimumIdle"));
        hikariConfig.setMaximumPoolSize(section.getInt("maximumPoolSize"));
        section= LegendarySevenDayGift.getInstance().getConfig().getConfigurationSection("Mysql");
        String url="jdbc:mysql://"+
                section.getString("address")+":"+
                section.getString("port")+"/"+
                section.getString("database")+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(section.getString("user"));
        hikariConfig.setPassword(section.getString("password"));
        hikariConfig.setAutoCommit(true);
        connectPool=new HikariDataSource(hikariConfig);
        enableMysql();
    }



    public static void enableMysql()
    {
        Connection connection=null;
        Statement statement=null;
        try {
            connection= connectPool.getConnection();
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS lsg_data (" +
                    "`player` varchar(32) NOT NULL," +
                    "`lastlogin` int(32) NOT NULL," +
                    "`claimday` int(8) NOT NULL," +
                    "`claim` boolean NOT NULL," +
                    "PRIMARY KEY (`player`)" +
                    ");");
            System.out.println("成功与数据库建立连接！");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null)
                {
                    statement.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }


    }

    @Override
    public void saveData(PlayerData data) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectPool.getConnection();
            ps = conn.prepareStatement("REPLACE INTO lsg_data (player,lastlogin,claimday,claim) VALUES(?,?,?,?)");
            ps.setString(1, data.getPlayer());
            ps.setInt(2, data.getLastLogin());
            ps.setInt(3, data.getClaimDay());
            ps.setBoolean(4, data.isClaim());
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return;
    }

    @Override
    public PlayerData getData(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connectPool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM lsg_data WHERE player = '"+player+"';");
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(player)){
                    PlayerData data=new PlayerData(player,rs.getInt("lastlogin"),rs.getInt("claimday"),rs.getBoolean("claim"));
                    return data;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        Calendar calendar=Calendar.getInstance();
        return new PlayerData(player,calendar.get(Calendar.DATE),1,false);

    }

    @Override
    public List<String> getPlayers() {
        List<String> list=new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connectPool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM lsg_data;");
            rs = ps.executeQuery();
            while(rs.next()){
                list.add(rs.getString("player"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public void disable() {
        if (connectPool != null && !connectPool.isClosed()) {
            connectPool.close();
        }
    }
}
