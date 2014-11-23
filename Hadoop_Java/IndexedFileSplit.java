
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;

import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.fs.Path;

/**
 * Peng: Notice FileSplit implements Writable, It will be serialized to a 
 * file when submitting tasks (See org.apache.hadoop.mapreduce.JobSubmitter 
 * ine 453), and deserialized into objects when starting mappers (see 
 * org.apache.hadoop.mapred.MapTask line 348). During this process start/end
 * preserved because FileSplit::write() writes them to a file, and index 
 * is lost because our inplementation didn't write to a file. The overwritten
 * methods write() and readFields() handle this problem.
 */
public class IndexedFileSplit extends FileSplit {

    public int index;

    /**
     * This was called in deserializer. It uses an empty constructor to construct
     * an instance and call readFields() with serialized file.
     */
    public IndexedFileSplit() {
    }
    
    public IndexedFileSplit(Path file, long start, long length, String[] hosts,
			    int i) {
		super(file, start, length, hosts);
		this.index = i;
    }


	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeInt(index);
	}

	@Override
	public void  readFields(DataInput in) throws IOException {
		super.readFields(in);
		index = in.readInt();
	}
}
