class BinaryRecordReader implements RecordReader<int, byte[]> {

    private SequenceFileAsBinaryInputFormat.SequenceFileAsBinaryRecordReader
	splitReader;
    private LongWritable splitKey;
    private byte[] splitValue;

    public BinaryRecordReader(JobConf job, FileSplit split) throws IOException {

	splitReader = new SequenceFileAsBinaryInputFormat.
	    SequenceFileAsBinaryRecordReader(job, split);

	splitKey = splitReader.createKey();
	splitValue = splitReader.createValue();
    }

    public boolean next(int key, byte[] value) throws IOException {
      
	// get the next split
	if (!splitReader.next(splitKey, splitValue)) {
	    return false;
	}

	key = splitKey;
	splitKey++;

	value = splitValue;
	
	return true;
    }

    public int createKey() {
	return 0;
    }

    public byte[] createValue() {
	return new byte[1024];
    }

    public long getPos() throws IOException {
	return splitReader.getPos();
    }

    public void close() throws IOException {
	splitReader.close();
    }

    public float getProgress() throws IOException {
	return splitReader.getProgress();
    }
}
