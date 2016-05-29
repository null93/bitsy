all:
	javac Server.java Connection.java
	javac EchoServer.java EchoClient.java

server:
	java Server server 10007

client:
	java Server client 192.168.0.10 10007

test_server:
	java EchoServer

test_client:
	java EchoClient