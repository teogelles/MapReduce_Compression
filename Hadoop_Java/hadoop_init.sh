source /home/kwebb/public/setup-91.sh
export PATH=$JAVA_HOME/bin:$PATH
export HADOOP_CLASSPATH=$JAVA_HOME/lib/tools.jar

printf "\nHadoop initialized. \n"
printf "  JAVA_HOME        = %s\n" $JAVA_HOME
printf "  HADOOP_HOME      = %s\n" $HADOOP_HOME
printf "  HADOOP_CLASSPATH = %s\n\n" $HADOOP_CLASSPATH
