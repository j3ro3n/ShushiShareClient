import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class ReadFileFilechannelTest {

    @Test
    public void ReadFile_FileChannelUsingRandomAccessFile()
            throws IOException {
        try (RandomAccessFile reader = new RandomAccessFile("/Users/jeroenb/Documents/Studie/Jaar3/ELU3-3/testfiles/HelloSushiWorld.txt", "r");
             FileChannel channel = reader.getChannel();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            int bufferSize = 1024;
            if (bufferSize > channel.size()) {
                bufferSize = (int) channel.size();
            }
            ByteBuffer buff = ByteBuffer.allocate(bufferSize);

            while (channel.read(buff) > 0) {
                out.write(buff.array(), 0, buff.position());
                buff.clear();
            }

            String fileContent = new String(out.toByteArray(), StandardCharsets.UTF_8);

            assertEquals("Hello SushiWorld!", fileContent);
        }
    }
}
