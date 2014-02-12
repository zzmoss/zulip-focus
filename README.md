zulip-focus
===========

Zulip bot that helps filter and view content from specific streams and topics


Steps to run it:

1. Fork the repo. Copy default_config.clj to config.clj. 
2. Go to Zulip settings, create a bot, get the api key and bot email, copy the values into config.clj
3. Subscribe the bot to streams you want to listen to. You can do it on the Zulip UI manually by finding the stream, and adding the bot's email to the subscriber's list.
4. If you have leiningen set up for clojure, do lein run on the project folder. 

All new messages on the streams the bot listens to should show up on your console.


