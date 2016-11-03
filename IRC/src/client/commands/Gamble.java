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

package client.commands;

import client.utils.Message;
import client.utils.MessageOut;
import client.utils.PointsHelper;
import client.utils.Settings;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

class Gamble extends Command {
    Gamble(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        super(msg, mq);
        this.setPermissionLevel(Settings.getSettings().getLocalized("Gamble").get("permission_level"));
    }

    @Override
    public void run() {
        if (isAllowed()) {
            String indices[] = this.m.getMessage().split(" ");
            if (indices.length > 2) {
                try {
                    int pointsToGamble = Integer.parseInt(indices[1]);
                    int chance = Integer.parseInt(indices[2]);
                    if (!(chance > 100 || chance <= 0)) {
                        gambleLogic(pointsToGamble, chance);
                    } else {
                        this.mq.offer(new MessageOut(this.m.getChannel(), Settings.getSettings().getLocalized("Gamble").get("gamble_info")));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    try {
                        this.mq.offer(new MessageOut(this.m.getChannel(), Settings.getSettings().getLocalized("Gamble").get("gamble_info")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (indices.length > 1) {
                try {
                    int pointsToGamble = Integer.parseInt(indices[1]);
                    gambleLogic(pointsToGamble);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    try {
                        this.mq.offer(new MessageOut(this.m.getChannel(), Settings.getSettings().getLocalized("Gamble").get("gamble_info")));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    this.mq.offer(new MessageOut(this.m.getChannel(), Settings.getSettings().getLocalized("Gamble").get("gamble_info")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean pointCheck(int points) {
        return (Integer.parseInt(new PointsHelper().GetPoints(this.m.getDisplayName(), this.m.getChannel())) >= points);
    }

    private void gambleLogic(int points) throws IOException {
        if (pointCheck(points)) {
            if (Math.random()*100 > 50) {
                new PointsHelper().AddPoints(this.m.getDisplayName(), points, this.m.getChannel());
                this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_win"), this.m.getDisplayName(), Integer.toString(points*2))));
            } else {
                new PointsHelper().RemovePoints(this.m.getDisplayName(), points, this.m.getChannel());
                this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_lost"), this.m.getDisplayName(), Integer.toString(points))));
            }
        } else {
            this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_insufficient"), this.m.getDisplayName())));
        }
    }

    private void gambleLogic(int points, int chance) throws IOException {
        if (pointCheck(points)) {
            if (Math.random()*100 <= chance) {
                double prize = (points / (chance/100.0)) - points;
                int rounded = (int) prize;
                new PointsHelper().AddPoints(this.m.getDisplayName(), rounded, this.m.getChannel());
                this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_win"), this.m.getDisplayName(), Integer.toString(points + rounded))));
            } else {
                new PointsHelper().RemovePoints(this.m.getDisplayName(), points, this.m.getChannel());
                this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_lost"), this.m.getDisplayName(), Integer.toString(points))));
            }
        } else {
            this.mq.offer(new MessageOut(this.m.getChannel(), String.format(Settings.getSettings().getLocalized("Gamble").get("gamble_insufficient"), this.m.getDisplayName())));
        }
    }

    @Override
    public String toString() {
        return "Gamble";
    }
}
