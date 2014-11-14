public class BinaryInputFormat extends FileInputFormat<int, byte[]> {

    public RecordReader<int, byte[]> getRecordReader(InputSplit input,
						     JobConf job,
						     Reporter reporter)
	throws IOException {

	reporter.setStatus(input.toString());
	return new BinaryRecordReader(job, (FileSplit)input);
    }
}
