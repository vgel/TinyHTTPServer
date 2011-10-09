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

Next, call Server.setFactory(new MyFactory(...)). Finally, start the server by calling Server.main(String[]). Make sure to pass on any args you didn't use!

    //setting a custom factory
    public static void main(String[] args){
        Server.setFactory(new MyFactory());
	Server.main(args);
    }

-help output -- reference
=========================

-help    : print this help message and quit
-threads : max number of threads to pool
-port    : port to run server on - must be above 1000 or running as root on linux
-ip      : ip to run server on
-ssl     : use SSL sockets
-v       : 0=nothing 1=verbose 2=debug
