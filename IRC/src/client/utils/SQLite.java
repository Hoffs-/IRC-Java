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


import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLite {
    private Connection connection;
    private SQLiteConnectionPoolDataSource poolDataSource;
    private SQLiteConfig cfg;


    private SQLite() {
        File dir = new File("db/");
        poolDataSource = new SQLiteConnectionPoolDataSource();
        this.cfg = new SQLiteConfig();
        this.cfg.setEncoding(SQLiteConfig.Encoding.UTF8);
        poolDataSource.setConfig(this.cfg);
        poolDataSource.setUrl("jdbc:sqlite:db/irc.db");
    }

    public synchronized Connection getConnection() {
        Connection cn = null;
        try {
            cn = this.poolDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cn;
    }

    public static synchronized SQLite getSQLite() {
        if (ref == null) { ref = new SQLite(); }
        return ref;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private static SQLite ref;
}
