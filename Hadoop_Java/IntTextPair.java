import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

public class IntTextPair implements WritableComparable<IntTextPair> {

    public IntWritable id;
    public Text name;

    public IntTextPair() {

	this.id = new IntWritable();
	this.name = new Text();
    }
    
    public IntTextPair(IntWritable i, Text t) {

	this.id = i;
	this.name = t;
    }
    
    @Override
    public void readFields(DataInput in) throws IOException {
        id.readFields(in);
        name.readFields(in);
    }
 
    @Override
    public void write(DataOutput out) throws IOException {
        id.write(out);
        name.write(out);
    }
 
    @Override
    public String toString() {
        return "ID: " + id.toString() + "\nFilename: " + name.toString();
    }
 
    @Override
    public int compareTo(IntTextPair other) {

	int cmp = name.compareTo(other.name);
	
        if (cmp != 0) {
            return cmp;
        }
 
        return id.compareTo(other.id);
    }
 
    @Override
    public int hashCode(){
        return id.hashCode()*163 + name.hashCode();
    }
 
    @Override
    public boolean equals(Object other) {
	
        if(other instanceof IntTextPair)
        {
            IntTextPair itp = (IntTextPair) other;
            return id.equals(itp.id) && name.equals(itp.name);
        }
        return false;
    }
}

    
