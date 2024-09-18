# chat-app
Spring boot application, REST API for vue spa with websocket support.

Message flow:

Client's browser ->(via Websocket(stomp)) -> Spring boot backend -> db write -> rabbitmq stomp broker -> backend (stomp)-> browsers

JWT based Authentication, refresh and access tokens stored in httponly secure samesite cookie, inspired by: https://web.archive.org/web/20180819014446/http://jaspan.com/improved_persistent_login_cookie_best_practice

Every time a refresh token is generated, its hash gets stored in a db with a succession id and expiration date.
When user renews access token, refresh token gets rotated as well, hash gets updated, id stays the same.
When a user tries to authenticate with a token with a succession id already present in DB and the hash doesnt match, the user gets logged out of all devices due to suspected theft.  

Websocket connection gets disconnected if access token doesnt get renewed.

Runs in docker alongside rabbitmq broker
