import java.io.IOException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import java.io.ByteArrayOutputStream;

public class TestBZip {
    public static void main(String[] args) throws IOException  {
        String testStr = "is is test input line number 3\n" +
            "This is test input line number 4\n" +
            "This is test input line number 5\n" +
            "This";
        byte[] bytes = testStr.getBytes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BZip2CompressorOutputStream wrapper = new BZip2CompressorOutputStream(out);

        try {
            wrapper.write(bytes); 
        } finally {
            wrapper.flush();
            wrapper.close();
            System.out.write(out.toByteArray());
        }

    }
}
