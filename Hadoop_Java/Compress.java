
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

import java.io.IOException;
import java.nio.ByteBuffer;
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
	extends Mapper<IntTextPair, BytesWritable, IntTextPair, BytesWritable>{

        public void map(IntTextPair key, BytesWritable value, Context context) 
            throws IOException, InterruptedException {

            /*
            Configuration conf = context.getConfiguration();
            TaskID taskID = TaskID.forName(conf.get("mapreduce.task.id"));
            int taskNumber = taskID.getId();

            context.write(new IntWritable(taskNumber), value);
            */
          
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            // Allocate the header space in output stream.
            byte[] header = {0, 0, 0, 0};
            out.write(header, 0, 4);

            // Construct a BZip2 wrapper
            BZip2CompressorOutputStream wrapper = new BZip2CompressorOutputStream(out, 1);
            

            try {
                wrapper.write(value.getBytes(), 0, value.getLength());
            } finally {  
                wrapper.flush();
                wrapper.close();
                context.write(key, new BytesWritable(out.toByteArray()));  
            }   
        }    
    }


    /**
     * InputKey    - 
     * InputValue  - Bytes in a split of the file.
     * OutputKey   - Nothing
     * OutputValue - Same as InputValue.
     */
    public static class WriteFileReducer
        extends Reducer<IntTextPair, BytesWritable, NullWritable, BytesWritable> {

        public void reduce(IntTextPair key, Iterable<BytesWritable> values,
			   Context context) 
            throws IOException, InterruptedException {

            /*UNCOMMENT TO TEST COMPRESSION
            for (IntBytePair value : values) {
                //System.out.println("Split ID Initial: " + value.id.get());
                //System.out.println(value.toString());
                    

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
                }

                sortedList.add(new IntBytePair(new IntWritable(value.id.get()),
                new BytesWritable(value.content.copyBytes())));
            }
            */

            System.out.println("Reducer: this is split " + key.id);

            // Write the size of the compressed
            for (BytesWritable value : values) {
                // Get length of byte array, write it to the int header for
                // decompression use.
                byte[] lengthBytes= ByteBuffer.allocate(4).putInt(value.getLength()-4).array();
                byte[] bufferBytes = value.getBytes();
                for (int i=0; i<4; i++)
                    bufferBytes[i] = lengthBytes[i];

                context.write(NullWritable.get(), value);
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

        job.setMapOutputKeyClass(IntTextPair.class);
        job.setMapOutputValueClass(BytesWritable.class);

        job.setOutputKeyClass(NullWritable.class);
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
