
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
	export HD_IN_DIR="/user/tgelles1/input/"
	export HD_OUT_DIR="/user/tgelles1/output/"
else
	printf "\nYou are not one of us!\n"
	return
fi

function clean () {
	echo
	make clean
	printf "\n\nhdfs dfs -rm -r -f %s\n\n" $HD_OUT_DIR
	hdfs dfs -rm -r -f $HD_OUT_DIR
	echo
}

function compress () {
	if [ -z ${1+x} ]; then
		printf "\nhadoop jar compress.jar Compress %s %s\n\n" $HD_IN_DIR $HD_OUT_DIR
		hadoop jar compress.jar Compress $HD_IN_DIR $HD_OUT_DIR
	else
		printf "\nhadoop jar compress.jar Compress %s%s %s\n\n" $HD_IN_DIR $1 $HD_OUT_DIR
		hadoop jar compress.jar Compress $HD_IN_DIR$1 $HD_OUT_DIR
	fi
	echo
}

function decompress () {
	if [ -z ${1+x} ]; then
		printf "\nhadoop jar compress.jar Decompress %s %s\n\n" $HD_IN_DIR $HD_OUT_DIR
		hadoop jar compress.jar Decompress $HD_IN_DIR $HD_OUT_DIR
	else
		printf "\nhadoop jar compress.jar Decompress %s%s %s\n\n" $HD_IN_DIR $1 $HD_OUT_DIR
		hadoop jar compress.jar Decompress $HD_IN_DIR$1 $HD_OUT_DIR
	fi
	echo
}

function upload () {
	if [ -z ${1+x} ]; then
		printf "\nUsage: upload <file>\n\n"
	else
		printf "\nhdfs dfs -copyFromLocal %s %s.\n\n" $1 $HD_IN_DIR
		hdfs dfs -copyFromLocal $1 $HD_IN_DIR"."
		echo
	fi
}

function download () {
	if [ -z ${1+x} ]; then
		printf "\nhdfs dfs -copyToLocal %s* .\n\n" $HD_OUT_DIR
		hdfs dfs -copyToLocal $HD_OUT_DIR"*" .
		echo
	else
		printf "\nhdfs dfs -copyToLocal %s%s .\n\n" $HD_OUT_DIR $1
		hdfs dfs -copyToLocal $HD_OUT_DIR$1 .
		echo
	fi
}

function hls () {
	if [ -z ${1+x} ]; then
		printf "\nhdfs dfs -ls /user/%s\n\n" $HD_USER
		hdfs dfs -ls "/user/"$HD_USER
		echo
	else
		printf "\nhdfs dfs -ls /user/%s/%s\n\n" $HD_USER $1
		hdfs dfs -ls "/user/"$HD_USER"/"$1
		echo
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
printf "  compress\n    Runs compress with %s* as input\n\n" $HD_IN_DIR
printf "  compress 30lines\n    Runs compress with %s30lines as input\n\n" $HD_IN_DIR
printf "  decompress\n    Runs decompress with %s* as input\n\n" $HD_IN_DIR
printf "  decompress 30lines\n    Runs decompress with %s30lines as input\n\n" $HD_IN_DIR
printf "  upload 30lines\n    Copy local file 30lines to %s\n\n" $HD_IN_DIR
printf "  download\n    Copy all files in %s to here\n\n" $HD_OUT_DIR
printf "  download part-r-00000\n    Copy %spart-r-00000 to here\n\n" $HD_OUT_DIR
printf "  hls\n    List files in hdfs::/user/%s/\n\n" $HD_USER
printf "  hls input/\n    List files in hdfs::/user/%s/input/\n\n" $HD_USER
