import java.io.IOException;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;


//Imports needed for getSplits
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.fs.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Extension of FileInputFormat to handle binary files.
 */
public class BinaryFileInputFormat extends FileInputFormat<IntTextPair, BytesWritable>{

    //These variables are private in FileInputFormat, but we use them in
    //getSplits
    private static final Log LOG = LogFactory.getLog(FileInputFormat.class);
    private static final double SPLIT_SLOP = 1.1;   // 10% slop

    private final int NUM_SPLITS = 50;

    @Override
    public RecordReader<IntTextPair, BytesWritable>
    createRecordReader(InputSplit split, TaskAttemptContext context)
    throws IOException, InterruptedException {

        return new BinaryRecordReader();
    }


    /** 
     * Generate the list of files and make them into FileSplits.
     */
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException {

        int index = 0;

        long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));

        //long maxSize = getMaxSplitSize(job); // Original max size
        long maxSize = 50000000;               // We fix max size to 50MB

        // generate splits
        List<InputSplit> splits = new ArrayList<InputSplit>();

        for (FileStatus file: listStatus(job)) {
            
            Path path = file.getPath();
            FileSystem fs = path.getFileSystem(job.getConfiguration());
            long length = file.getLen();
            BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0, length);
            if ((length != 0) && isSplitable(job, path)) {

                // Divide file into a fixed number of splits.
                // long blockSize = length/NUM_SPLITS;
                // long splitSize = blockSize;

                // Divide file based on a fixed block size.
                long blockSize = file.getBlockSize();
                long splitSize = computeSplitSize(blockSize, minSize, maxSize);



                long bytesRemaining = length;
                while (((double) bytesRemaining)/splitSize > SPLIT_SLOP) {
                    int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
                    splits.add(new IndexedFileSplit(path, length-bytesRemaining, splitSize, 
                            blkLocations[blkIndex].getHosts(), index));
                    bytesRemaining -= splitSize;
                    index++;
                }

                if (bytesRemaining != 0) {
                    splits.add(new IndexedFileSplit(path, length-bytesRemaining, bytesRemaining, 
                		    blkLocations[blkLocations.length-1].getHosts(), index));
                }
            }

            else if (length != 0) {
                splits.add(new IndexedFileSplit(path, 0, length, blkLocations[0].getHosts(), index));
            }

            else { 
                //Create empty hosts array for zero length files
                splits.add(new IndexedFileSplit(path, 0, length, new String[0], index));
            }

            index++;
        }
        LOG.debug("Total # of splits: " + splits.size());
        return splits;
    }
}
