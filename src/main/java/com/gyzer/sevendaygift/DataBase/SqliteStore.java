package com.gyzer.sevendaygift.DataBase;

import com.gyzer.sevendaygift.DataBase.PlayerData.PlayerData;
import com.gyzer.sevendaygift.LegendarySevenDayGift;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SqliteStore extends DataProvider {

    public SqliteStore(){
        createTable();
    }

    public Connection getConnection() throws SQLException {
        File dataFolder = new File(LegendarySevenDayGift.getInstance().getDataFolder(),  "lsg_data.db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
           ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void createTable(){
        String str="CREATE TABLE IF NOT EXISTS lsg_data (" + // make sure to put your table name in here too.
                "`player` varchar(32) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
                "`lastlogin` int(32) NOT NULL," +
                "`claimday` int(8) NOT NULL," +
                "`claim` boolean NOT NULL," +
                "PRIMARY KEY (`player`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
                ");";
        try {
            Connection connection=getConnection();
            Statement stat = null;
            stat = connection.createStatement();
            stat.executeUpdate(str);
            Bukkit.getLogger().info("[LegendarySevenDayGift] 成功连接sqlite数据库.");
            if (connection != null && !connection.isClosed()){
                connection.close();
            }
            if (stat != null && !stat.isClosed()){
                stat.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void saveData(PlayerData data) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
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
            conn = getConnection();
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
            conn = getConnection();
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

    }
}
