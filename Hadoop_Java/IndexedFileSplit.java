import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.fs.Path;

public class IndexedFileSplit extends FileSplit {

    public int index;

    public IndexedFileSplit() {}
    
    public IndexedFileSplit(Path file, long start, long length, String[] hosts,
			    int i) {

	super(file, start, length, hosts);
	this.index = i;
    }
}
