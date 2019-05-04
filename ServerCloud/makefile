JFLAGS = -g
JC = javac
JVM= java 
FILE=

.SUFFIXES: .java .class

.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
          ServerCloud.java \
          Client.java \
          ClientHandler.java \
          User.java \
          Menu.java \
	  Server.java \
          Reserve.java \
	  ClientReceive.java \
	  Tuple.java \
	  ControlerImpl.java \
	  Controler.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class