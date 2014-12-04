# define compiler and compiler flag variables

JFLAGS = -g
JC = hadoop com.sun.tools.javac.Main
SRCS = *.java

# Build wordcount example program
compress: 
	$(JC) $(SRCS)
	jar cf compress.jar *.class

clean:
	rm -rf *.jar *.class

