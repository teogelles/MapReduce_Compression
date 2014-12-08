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

public class CompatibleCompress {

    private static int numReduceTasks = 1;


    public static class CompressMapper 
            extends Mapper<IntTextPair, BytesWritable, IntTextPair, BytesWritable>{

        public void map(IntTextPair key, BytesWritable value, Context context) 
            throws IOException, InterruptedException {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
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



    public static class WriteFileReducer
        extends Reducer<IntTextPair, BytesWritable, NullWritable, BytesWritable> {

        public void reduce(IntTextPair key, Iterable<BytesWritable> values,
                Context context) 
            throws IOException, InterruptedException {

 
            System.out.println("This is split " + key.id);
            for (BytesWritable value : values) {

                context.write(NullWritable.get(), value);
            }

        }
    }

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        // Create Job and Configuration instances.
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "CompatibleCompress");

        // Set types
        job.setInputFormatClass(BinaryFileInputFormat.class);

        job.setOutputFormatClass(BytesValueOutputFormat.class);

        job.setJarByClass(CompatibleCompress.class);
        job.setMapperClass(CompressMapper.class);
        job.setReducerClass(WriteFileReducer.class);

        job.setMapOutputKeyClass(IntTextPair.class);
        job.setMapOutputValueClass(BytesWritable.class);

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
