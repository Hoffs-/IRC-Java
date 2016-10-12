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

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class PointsHelper {
    private SQLiteHelper sqLiteHelper = null;

    public PointsHelper() {

    }

    public int AddPoints(String user, int amount, String table) {
        sqLiteHelper = new SQLiteHelper();
        sqLiteHelper.checkTable(table);
        String query = "SET points = points + " + amount +
                " WHERE user = '" + user.toLowerCase() + "'";
        int status = sqLiteHelper.sqlUpdate(table, query);
        if (status == 0) {
            query = "(user, display, points, seen) VALUES ('" +
                    user.toLowerCase() + "', '" + user + "', " +
                    amount + ", " + Instant.now().getEpochSecond() +
                    ")";
            status = sqLiteHelper.sqlInsert(table, query);
        }
        return status;
    }

    public String GetPoints(String user, String table) {
        sqLiteHelper = new SQLiteHelper();
        sqLiteHelper.checkTable(table);
        String ws = "points";
        String query = "WHERE user = '" +
                user.toLowerCase() + "'";
        List<Map<String, String>> result = sqLiteHelper.sqlSelect(table, query, ws);
        if (!result.isEmpty()) return result.get(0).get("points");
        return "0";
    }

    public int RemovePoints(String user, int amount, String table) {
        sqLiteHelper = new SQLiteHelper();
        sqLiteHelper.checkTable(table);
        String query = "SET points = points - " + amount +
                " WHERE user = '" + user.toLowerCase() + "'";
        return sqLiteHelper.sqlUpdate(table, query);
    }

}
