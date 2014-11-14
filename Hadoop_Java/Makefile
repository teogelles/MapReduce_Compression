# define compiler and compiler flag variables

JFLAGS = -g
JC = hadoop com.sun.tools.javac.Main


# Build wordcount example program
wc: WordCount.java
	$(JC) WordCount.java
	jar cf wc.jar WordCount*.class

clean:
	rm -rf *.jar *.class