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
import org.apache.log4j.BasicConfigurator;

import org.apache.commons.collections.IteratorUtils;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

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
          
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BZip2CompressorOutputStream wrapper = new BZip2CompressorOutputStream(out, 1);
            
            try {
                wrapper.write(value.getBytes(), 0, value.getLength());

            } finally {  
                wrapper.flush();
                wrapper.close();

                outputValue.content = new BytesWritable(out.toByteArray());
            
                System.out.println("Original: " + value);
                System.out.println("Compressed: " + outputValue.content);
                context.write(key.name, outputValue);  
            }   
        }    
    }


    /**
     * InputKey    - Task number of the mapping task where input is coming from.
     * InputValue  - Bytes in a split of the file.
     * OutputKey   - Nothing
     * OutputValue - Same as InputValue.
     */
    public static class WriteFileReducer
        extends Reducer<Text, IntBytePair, NullWritable, BytesWritable> {

        public void reduce(Text key, Iterable<IntBytePair> values,
                Context context) 
            throws IOException, InterruptedException {

            try {
                Boolean bool = null;
                bool.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            // Code to sort the input values.  This is best for testing.
            // With the final program this may cause a performance hit we
            // want to avoid
            List<IntBytePair> sortedList = new ArrayList<IntBytePair>();

            for (IntBytePair value : values) {
                System.out.println("Split ID Initial: " + value.id.get());
                System.out.println(value.toString());
                
                /*UNCOMMENT TO TEST COMPRESSION
                 *
                ByteArrayInputStream in = new ByteArrayInputStream(value.content.getBytes());
                BZip2CompressorInputStream wrapper = new BZip2CompressorInputStream(in);
                byte[] decompressedData = new byte[1];
                
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = wrapper.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    decompressedData = buffer.toByteArray();
                    System.out.println(new BytesWritable(decompressedData));
                } finally {
                    wrapper.close();
                    sortedList.add(new IntBytePair(new IntWritable(value.id.get()),
                            new BytesWritable(decompressedData)));

                }*/

                sortedList.add(new IntBytePair(new IntWritable(value.id.get()),
                            new BytesWritable(value.content.copyBytes())));
            }

            System.out.println("Initial sortedList:");
            System.out.println(Arrays.toString(sortedList.toArray()));
            Collections.sort(sortedList);
            System.out.println("Final sortedList:");
            System.out.println(Arrays.toString(sortedList.toArray()));

            for (IntBytePair value : sortedList) {
                System.out.println("Split ID Sorted: " + value.id.get());
                context.write(NullWritable.get(), value.content);
            }

        }
    }

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        // Create Job and Configuration instances.
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Compress");

        // Set types
        job.setInputFormatClass(BinaryFileInputFormat.class);

        job.setOutputFormatClass(BytesValueOutputFormat.class);

        job.setJarByClass(Compress.class);
        job.setMapperClass(CompressMapper.class);
        job.setReducerClass(WriteFileReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntBytePair.class);

        job.setOutputKeyClass(NullWritable.class);
        //	job.setOutputValueClass(IntBytePair.class);
        job.setOutputValueClass(BytesWritable.class);


        // Set number of reducers
        job.setNumReduceTasks(numReduceTasks);


        // Set input / output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));


        // Execute
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
