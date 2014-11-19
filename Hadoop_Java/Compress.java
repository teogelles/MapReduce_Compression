

import java.io.IOException;

import org.apache.hadoop.mapred.TaskID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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

	/**
	 * InputKey    - File name of the input file
	 * InputValue  - Bytes in a split of the file (split length = file block size). 
	 * OutputKey   - Task number of this mapping task.
	 * OutputValue - Same as InputValue.
	 */
	public static class CompressMapper 
			extends Mapper<Text, BytesWritable, IntWritable, BytesWritable>{

		public void map(Text key, BytesWritable value, Context context) 
				throws IOException, InterruptedException {

			Configuration conf = context.getConfiguration();
			TaskID taskID = TaskID.forName(conf.get("mapreduce.task.id"));
			int taskNumber = taskID.getId();
			context.write(new IntWritable(taskNumber), value);
		}
	}


	/**
	 * InputKey    - Task number of the mapping task where input is coming from.
	 * InputValue  - Bytes in a split of the file.
	 * OutputKey   - Nothing
	 * OutputValue - Same as InputValue.
	 */
	public static class WriteFileReducer 
			extends Reducer<IntWritable, BytesWritable, NullWritable, BytesWritable> {

		public void reduce(IntWritable key, Iterable<BytesWritable> values, Context context) 
			throws IOException, InterruptedException {


			for (BytesWritable value : values) {
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
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(BytesWritable.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(BytesWritable.class);

    // Set input / output types
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    // Execute
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}