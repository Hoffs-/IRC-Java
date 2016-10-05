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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private String prefix;
    private File file;
    private FileWriter fw;
    private BufferedWriter bw;


    public Logger(String fileName, String tag) {
        this.prefix = tag;
        File theDir = new File("logs");
        if (!theDir.exists()) theDir.mkdir();
        this.file = new File("logs/" + LocalDate.now() + "_" + fileName + ".log");
        if (this.file.exists()) {
            int iterate = 1;
            while (file.exists()) {
                this.file = new File("logs/" + LocalDate.now() + "_" + fileName + "(" + iterate + ")" + ".log");
                iterate++;
            }
        }

        try {
            this.fw = new FileWriter(file);
            this.bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void write(String message, String module) throws IOException {
        DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.bw.write("[" + LocalDateTime.now().format(defaultFormat) + "][" + this.prefix + "][" + module + "] " + message + "\n");
        this.bw.flush();
    }

    public void write(String message) throws IOException {
        DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.bw.write("[" + LocalDateTime.now().format(defaultFormat) + "][" + this.prefix + "] " + message + "\n");
        this.bw.flush();
    }
}
