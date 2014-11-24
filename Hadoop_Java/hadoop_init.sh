
source /home/kwebb/public/setup-91.sh
export PATH=$JAVA_HOME/bin:$PATH
export HADOOP_CLASSPATH=$JAVA_HOME/lib/tools.jar:External_Jars/*

if [ $(whoami) == "pzhao1" ]; then
	export HD_USER="pzhao1"
	export HD_IN_DIR="/user/pzhao1/compressinput/"
	export HD_OUT_DIR="/user/pzhao1/compressoutput/"
elif [ $(whoami) == "wliang1" ]; then
	export HD_USER="wliang1"
	export HD_IN_DIR="/user/wliang1/input/"
	export HD_OUT_DIR="/user/wliang1/output/"
elif [ $(whoami) == "tgelles1" ]; then
	export HD_USER="tgelles1"
	export HD_IN_DIR="/user/tgelles/input/"
	export HD_OUT_DIR="/user/tgelles/output/"
else
	printf "\nYou are not one of us!\n"
	return
fi

function clean () {
	make clean
	hdfs dfs -rm -r -f $HD_OUT_DIR
}

function run () {
	hadoop jar compress.jar Compress $HD_IN_DIR$1 $HD_OUT_DIR
}

function upload () {
	if [ -z ${1+x} ]; then
		printf "\nUsage: upload <file>\n\n"
	else
		hdfs dfs -copyFromLocal $1 $HD_IN_DIR"/."
	fi
}

function download () {
	if [ -z ${1+x} ]; then
		hdfs dfs -copyToLocal $HD_OUT_DIR"*" .
	else
		hdfs dfs -copyToLocal $HD_OUT_DIR$1 .
	fi
}

function hls () {
	if [ -z ${1+x} ]; then
		hdfs dfs -ls "/user/"$HD_USER
	else
		hdfs dfs -ls "/user/"$HD_USER"/"$1
	fi	
}

printf "\nHadoop initialized. \n"
printf "  JAVA_HOME        = %s\n" $JAVA_HOME
printf "  HADOOP_HOME      = %s\n" $HADOOP_HOME
printf "  HADOOP_CLASSPATH = %s\n" $HADOOP_CLASSPATH
printf "  HD_USER          = %s\n" $HD_USER
printf "  HD_IN_DIR        = %s\n" $HD_IN_DIR
printf "  HD_OUT_DIR       = %s\n\n" $HD_OUT_DIR

printf "\nUsage:\n"
printf "  clean\n    Runs make clean and cleans hdfs.\n\n"
printf "  run\n    Runs compress with %s* as input\n\n" $HD_IN_DIR
printf "  run 30lines\n    Runs compress with %s30lines as input\n\n" $HD_IN_DIR
printf "  upload 30lines\n    Copy local file 30lines to %s\n\n" $HD_IN_DIR
printf "  download\n    Copy all files in %s to here\n\n" $HD_OUT_DIR
printf "  download part-r-00000\n    Copy %spart-r-00000 to here\n\n" $HD_OUT_DIR
printf "  hls\n    List files in hdfs::/user/%s/\n\n" $HD_USER
printf "  hls input/\n    List files in hdfs::/user/%s/input/\n\n" $HD_USER
