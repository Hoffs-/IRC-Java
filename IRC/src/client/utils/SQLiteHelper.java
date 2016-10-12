/*
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Copyright 2016 Ignas Maslinskas
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package client.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHelper {
    private SQLite sql = null;

    public SQLiteHelper() {
        this.sql = SQLite.getSQLite();
    }

    public int sqlUpdate(String table, String qr) {
        Connection cnn = sql.getConnection();
        int status = 0;
        try {
            Statement statement = cnn.createStatement();
            String sqlQr = "UPDATE " + table +
                    " " + qr + ";";
            status = statement.executeUpdate(sqlQr);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public int sqlInsert(String table, String qr) {
        Connection cnn = sql.getConnection();
        int status = 0;
        try {
            Statement statement = cnn.createStatement();
            String sqlQr = "INSERT INTO " + table +
                    " " + qr + ";";
            status = statement.executeUpdate(sqlQr);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public int sqlDelete(String table, String qr) {
        Connection cnn = sql.getConnection();
        int status = 0;
        try {
            Statement statement = cnn.createStatement();
            String sqlQr = "DELETE FROM " + table +
                    " " + qr + ";";
            status = statement.executeUpdate(sqlQr);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public List<Map<String, String>> sqlSelect(String table, String qr, String ws) {
        ResultSet rs = null;
        Connection cnn = sql.getConnection();
        List<Map<String, String>> result = new ArrayList<>();
        try {
            Statement statement = cnn.createStatement();
            String sqlQr = "SELECT " + ws + " FROM " + table +
                    " " + qr + ";";
            rs = statement.executeQuery(sqlQr);
            result = makeList(rs);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Map<String, String>> sqlSelect(String table, String qr) {
        Connection cnn = sql.getConnection();
        ResultSet rs = null;
        List<Map<String, String>> result = new ArrayList<>();
        try {
            this.checkTable(table);
            Statement statement = cnn.createStatement();
            String sqlQr = "SELECT * FROM " + table +
                    " " + qr + ";";
            rs = statement.executeQuery(sqlQr);
            result = makeList(rs);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Map<String, String>> makeList(ResultSet rs) {
        List<Map<String, String>> result = new ArrayList<>();
        ResultSetMetaData meta = null;
        try {
            meta = rs.getMetaData();
            int cCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, String> mp = new HashMap<>();
                for (int i = 1; i <= cCount; i++) {
                    String column = meta.getColumnName(i);
                    String type = meta.getColumnTypeName(i);
                    if (type.equals("TEXT")) {
                        mp.put(column, rs.getString(column));
                    } else if (type.equals("INTEGER")) {
                        String tmp = Integer.toString(rs.getInt(column));
                        mp.put(column, tmp);
                    }
                }
                result.add(mp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void checkTable(String table) {
        try {
            Connection cnn = sql.getConnection();
            Statement statement = cnn.createStatement();
            String qr = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                    "user TEXT PRIMARY KEY," +
                    "display TEXT," +
                    "points INTEGER," +
                    "seen INTEGER);";
            statement.executeUpdate(qr);
            statement.close();
            cnn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}