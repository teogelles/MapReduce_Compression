import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;


public class BinaryRecordReader extends RecordReader<Text, BytesWritable> {
 
    private long start;
    private long end;
    private long pos;
	private FSDataInputStream fileIn;
    private Text key = new Text();
    private BytesWritable value = new BytesWritable();
 	private boolean fileRead;

    private static final Log LOG = LogFactory.getLog(
            BinaryRecordReader.class);
 
    /**
     * From Design Pattern, O'Reilly...
     * This method takes as arguments the map task’s assigned InputSplit and
     * TaskAttemptContext, and prepares the record reader. For file-based input
     * formats, this is a good place to seek to the byte position in the file to
     * begin reading.
     */
    @Override
    public void initialize(
            InputSplit genericSplit,
            TaskAttemptContext context)
            throws IOException {
 
        // This InputSplit is a FileInputSplit
        FileSplit split = (FileSplit) genericSplit;
 
        // Retrieve configuration, and Max allowed
        // bytes for a single record
        Configuration job = context.getConfiguration();
       	
        // Split "S" is responsible for all records
        // starting from "start" and "end" positions
        this.start = split.getStart();
        this.end = start + split.getLength();
        this.pos = this.start;
 
        // Retrieve file containing Split "S"
        final Path path = split.getPath();
        this.key.set(path.getName());
        FileSystem fs = path.getFileSystem(job);
        fileIn = fs.open(path);


        fileRead = false;
    }
 
    /**
     * From Design Pattern, O'Reilly...
     * Like the corresponding method of the InputFormat class, this reads a
     * single key/ value pair and returns true until the data is consumed.
     */
    @Override
    public boolean nextKeyValue() throws IOException {
    	if (! this.fileRead) {
    		byte[] buffer = new byte[ (int) (this.end - this.start)];
    		this.fileIn.readFully(this.start, buffer, 0, buffer.length);
    		this.value.set(new BytesWritable(buffer));
    		this.fileRead = true;
    		return true;
    	}
    	else {
    		key = null;
    		value = null;
    		return false;
    	}
    }
 
 	@Override
    public float getProgress() throws IOException, InterruptedException {
        if ( this.fileRead) {
            return 0.0f;
        } else {
            return 1.0f;
        }
    }

    /**
     * From Design Pattern, O'Reilly...
     * This methods are used by the framework to give generated key/value pairs
     * to an implementation of Mapper. Be sure to reuse the objects returned by
     * these methods if at all possible!
     */
    @Override
    public Text getCurrentKey() throws IOException,
            InterruptedException {
        return key;
    }
 
    /**
     * From Design Pattern, O'Reilly...
     * This methods are used by the framework to give generated key/value pairs
     * to an implementation of Mapper. Be sure to reuse the objects returned by
     * these methods if at all possible!
     */
    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }
 
 
    /**
     * From Design Pattern, O'Reilly...
     * This method is used by the framework for cleanup after there are no more
     * key/value pairs to process.
     */
    @Override
    public void close() throws IOException {
        if (this.fileIn != null) {
            this.fileIn.close();
        }
    }
 
}