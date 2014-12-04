


import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;

/**
 * Extension of FileInputFormat to handle binary files.
 */
public class ChunkedFileInputFormat extends FileInputFormat<IntTextPair, BytesWritable>{

    //These variables are private in FileInputFormat, but we use them in
    //getSplits
    private static final Log LOG = LogFactory.getLog(FileInputFormat.class);
    private static final double SPLIT_SLOP = 1.1;   // 10% slop

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


        // generate splits
        List<InputSplit> splits = new ArrayList<InputSplit>();

        for (FileStatus file: listStatus(job)) {
            
            // Open Input File
            Path path = file.getPath();
            FileSystem fs = path.getFileSystem(job.getConfiguration());
            FSDataInputStream fileIn = fs.open(path);

            // Get file properties.
            long length = file.getLen();
            BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0, length);

            if ((length == 0) || !isSplitable(job, path)) {
                throw new IOException("ChunkedFileInputFormat: File length = 0 or specified unsplittable");
            }

            // Define loop variables
            int index = 0;
            byte[] headerBytes = new byte[4];
            long position = 0;
            
            // Keep reading chunks until we exhaust the file.
            while (position < length) {

                // Read header length
                fileIn.readFully(position, headerBytes, 0, 4);
                position += 4;
                int chunkSize = ByteBuffer.wrap(headerBytes).getInt();

                // Add new split
                int blkIndex = getBlockIndex(blkLocations, position);
                splits.add(new IndexedFileSplit(path, position, chunkSize, 
                        blkLocations[blkIndex].getHosts(), index));
                index++;
                position += chunkSize;

                // If end of one chunk is out of file boundary, throw exception.
                if (position > length) {
                    throw new IOException("ChunkedFileInputFormat: out of boundary error!");
                }
            }
        }
        LOG.debug("Total # of splits: " + splits.size());
        return splits;
    }
}
