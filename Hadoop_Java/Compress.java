import java.io.IOException;

import org.apache.hadoop.mapred.TaskID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Compress {

    private static int numReduceTasks = 1;
    
    /**
     * InputKey    - File name of the input file
     * InputValue  - Bytes in a split of the file (split length = file block size). 
     * OutputKey   - Task number of this mapping task.
     * OutputValue - Same as InputValue.
     */
    public static class CompressMapper 
	extends Mapper<IntTextPair, BytesWritable, Text, IntBytePair>{

	public void map(IntTextPair key, BytesWritable value, Context context) 
	    throws IOException, InterruptedException {

	    /*
	    Configuration conf = context.getConfiguration();
	    TaskID taskID = TaskID.forName(conf.get("mapreduce.task.id"));
	    int taskNumber = taskID.getId();
	    
	    context.write(new IntWritable(taskNumber), value);
	    */

	    IntBytePair outputValue = new IntBytePair();
	    outputValue.id = key.id;
	    outputValue.content = value;
	    
	    context.write(key.name, outputValue);
	}
    }


    /**
     * InputKey    - Task number of the mapping task where input is coming from.
     * InputValue  - Bytes in a split of the file.
     * OutputKey   - Nothing
     * OutputValue - Same as InputValue.
     */
    public static class WriteFileReducer
	extends Reducer<Text, IntBytePair, NullWritable, IntBytePair> {

	public void reduce(Text key, Iterable<IntBytePair> values,
			   Context context) 
	    throws IOException, InterruptedException {


	    for (IntBytePair value : values) {
		System.out.println("Split ID: %d\n" + value.id.get());
		context.write(NullWritable.get(), value);
	    }
	}
    }

    public static void main(String[] args) throws Exception {

  	// Create Job and Configuration instances.
	Configuration conf = new Configuration();
	Job job = Job.getInstance(conf, "Compress");

	// Set types
	job.setInputFormatClass(BinaryFileInputFormat.class);
	job.setJarByClass(Compress.class);
	job.setMapperClass(CompressMapper.class);
	job.setReducerClass(WriteFileReducer.class);

	job.setMapOutputKeyClass(Text.class);
	job.setMapOutputValueClass(IntBytePair.class);

	job.setOutputKeyClass(NullWritable.class);
	//job.setOutputValueClass(BytesWritable.class);
	job.setOutputValueClass(IntBytePair.class);

	
	// Set number of reducers
	job.setNumReduceTasks(numReduceTasks);

	
	// Set input / output paths
	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));

	
	// Execute
	System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
