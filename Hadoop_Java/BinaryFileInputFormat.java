public class BinaryFileInputFormat extends FileInputFormat<Text, BytesWritable>{
 
    @Override
    public RecordReader<Text, BytesWritable> createRecordReader(
            InputSplit split, TaskAttemptContext context) throws IOException,
            InterruptedException {
        return new BinaryRecordReader();
    }
}