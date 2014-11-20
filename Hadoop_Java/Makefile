# define compiler and compiler flag variables

JFLAGS = -g
JC = hadoop com.sun.tools.javac.Main
#SRCS = Compress.java BinaryFileInputFormat.java BinaryRecordReader.java
SRCS = *.java

# Build wordcount example program
compress: 
	$(JC) $(SRCS)
	jar cf compress.jar *.class

clean:
	rm -rf *.jar *.class

cleanhdfsT:
	hdfs dfs -rm -r -f /user/tgelles1/output/

cleanhdfs:
	hdfs dfs -rm -r -f /user/pzhao1/compressoutput/
