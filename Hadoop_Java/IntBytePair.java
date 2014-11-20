import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BytesWritable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

public class IntBytePair implements WritableComparable<IntBytePair> {

    public IntWritable id;
    public BytesWritable content;

    public IntBytePair() {

	this.id = new IntWritable();
	this.content = new BytesWritable();
    }
    
    public IntBytePair(IntWritable i, BytesWritable c) {

	this.id = i;
	this.content = c;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id.readFields(in);
        content.readFields(in);
    }
 
    @Override
    public void write(DataOutput out) throws IOException {
        id.write(out);
        content.write(out);
    }
 
    @Override
    public String toString() {
        return "ID: " + id.toString() + "\nContent: " + content.toString();
    }
 
    @Override
    public int compareTo(IntBytePair other) {

	int cmp = content.compareTo(other.content);
 
        if (cmp != 0) {
            return cmp;
        }
 
        return id.compareTo(other.id);
    }
 
    @Override
    public int hashCode(){
        return id.hashCode()*163 + content.hashCode();
    }
 
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof IntBytePair)
        {
            IntBytePair ibp = (IntBytePair) other;
            return id.equals(ibp.id) && content.equals(ibp.content);
        }
        return false;
    }
}

    
