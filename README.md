# IRC-Java

[![Build Status](https://travis-ci.org/Hoffs-/IRC-Java.svg?branch=master)](https://travis-ci.org/Hoffs-/IRC-Java)

IRC Client/Bot written using JAVA







# Changelog


Version 0.3

- Added support for SQLite databases.

- Added points framework using SQLite.

- Basic HTTP Client functionality with TwitchAPI support.

- More commands.

- Changed so that every command and message can be changed using commands.json file.

- Refactoring

Version 0.2
  
- Added MessageOut class for WriterThread/Out queue. So that messages that are being sent out would be structured.

- Added console listening thread using which you can send messages to channels as a bot.

- Added chat logging.

- Fixed the issue where if display name isn't set it would show user id. Not it should show default name from raw message body.

Version 0.1

- Initial commit.
