import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;


/**
 * Extension of RecordReader to handle binary files.
 * Reads in a split of input file, and extracts (filename, BytesWritable) 
 * key-value pairs for mapper.
 */
public class BinaryRecordReader extends RecordReader<IntTextPair, BytesWritable> {
 
    private long start;
    private long end;
    private FSDataInputStream fileIn;
    private IntTextPair key = new IntTextPair();
    private BytesWritable value = new BytesWritable();
    private boolean fileRead;
    private int keyIndex = 0;
    
    private static final Log LOG = LogFactory.getLog(BinaryRecordReader.class);
 
    /**
     * This method takes as arguments the map task’s assigned InputSplit and
     * TaskAttemptContext, and prepares the record reader.
     */
    @Override
    public void initialize(InputSplit genericSplit, TaskAttemptContext context)
	throws IOException {

        // This InputSplit is a FileInputSplit
        IndexedFileSplit split = (IndexedFileSplit) genericSplit;



        // Retrieve configuration
        Configuration conf = context.getConfiguration();
        
        // Set class variables
        this.start = split.getStart();
        this.end = start + split.getLength();

        this.fileRead = false;

        // Load the input file
        final Path path = split.getPath();
        this.key.id.set(split.index);
        this.key.name.set(path.getName());
        FileSystem fs = path.getFileSystem(conf);
        fileIn = fs.open(path);
    }
 
    /**
     * Reads a single key/value pair and returns true until data is exhausted.
     */
    @Override
    public boolean nextKeyValue() throws IOException {

    	// In this simple RecordReader we only give 1 key-value pair, that is
    	// (splitID, all bytes in the split). We may divide bytes into more
    	// pairs later.
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
    /**
     * Get progress of reading. Will be used to print mapping progress.
     */
    public float getProgress() throws IOException, InterruptedException {
	if ( this.fileRead) {
	    return 0.0f;
	} else {
	    return 1.0f;
	}
    }

    /**
     * Get the current key stored in reader.
     */
    @Override
    public IntTextPair getCurrentKey() throws IOException,
	InterruptedException {
	   return key;
    }
 
    /**
     * Get the current value stored in reader.
     */
    @Override
    public BytesWritable getCurrentValue() throws IOException,
	InterruptedException {
	   return value;
    }
 
 
    /**
     * Close the reader.
     */
    @Override
    public void close() throws IOException {
    	if (this.fileIn != null) {
    	    this.fileIn.close();
    	}
    }
 
}
    
