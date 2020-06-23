# Notification API Project
This is a sample project using Spring Boot, Spring Security, [STOMP](http://stomp.github.io/) (over WebSocket), and [SockJS](https://github.com/sockjs/sockjs-protocol) for WebSocket fallback options to send/receive notifications and messages
The design of this project is modelled after the current Twitter layout using HTML and CSS

## Getting Started
* To test the application, clone the repository on your local file system.

### Running the Application
* Ensure the application is running on localhost:8080
* You can register a new user at the "localhost:8080/register" endpoint or use the following user info:
```
Username 1: bart
Password 1: password1

Username 2: liluziburner
Password 2: password2
```
## Built With
* Java
* Spring Boot
* Spring Security
* Javascript
* HTML
* CSS
### Client-side libraries used
* [stomp-websocket](http://jmesnil.net/stomp-websocket/doc/)
* [sockjs-client](https://github.com/sockjs/sockjs-client)
* [Bootstrap 4](https://getbootstrap.com/)
