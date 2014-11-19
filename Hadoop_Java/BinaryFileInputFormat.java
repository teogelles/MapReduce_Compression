import java.io.IOException;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;


/**
 * Extension of FileInputFormat to handle binary files.
 */
public class BinaryFileInputFormat extends FileInputFormat<Text, BytesWritable>{
 
    @Override
    public RecordReader<Text, BytesWritable> createRecordReader(
            InputSplit split, TaskAttemptContext context) throws IOException,
            InterruptedException {
        return new BinaryRecordReader();
    }
}