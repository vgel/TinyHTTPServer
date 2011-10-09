What is this?
=============

TinyHTTPServer is a tiny http server (how surprising) written by myself, for fun. It supports all the HTTP verbs (GET, POST, PUT, DELETE, and TRACE) through a module system.

How do I use it?
================

First, build it.

    git clone git://github.com/Rotten194/TinyHTTPServer.git
    cd TinyHTTPServer/src
    javac -g net/thttpserv/*.java
    java net.thttpserv.Server -help

Next, create two classes: a class extending ConnectionHandler and a "Factory" class extending HandlerCreator. The "Factory" needs to override createInstance(Socket) to return an instance of your new ConnectionHandler. The ConnectionHandler can override the various verb methods.  

    MyFactory extends HandlerCreator  
    MyHandler extends ConnectionHandler

Next, call Server.setFactoy(new Myfactory(...)). Finally, start the server by calling server.main(String[]). Make sure to pass on any args you didn't use!
