package combookservice.service.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class NettyDemo {


    public void test(){
        /*ByteBuf header = "";
			ByteBuf body = "";
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(true, header, body);
        Unpooled.wrappedBuffer();*/
        //从而避免了拷贝操作 0拷贝即最
        //后我们生成的生成的 ByteBuf 对象是和 bytes 数组共用了同一个存储空间, 对 bytes 的修改也会反映到 ByteBuf 对象中.
        byte[] bytes = "323232".getBytes();
        ByteBuf a = Unpooled.wrappedBuffer(bytes);
    }
    //实现文件传输的零拷贝,
    public static void copyFileWithFileChannel(String srcFileName, String destFileName) throws Exception {
        RandomAccessFile srcFile = new RandomAccessFile(srcFileName, "r");
        FileChannel srcFileChannel = srcFile.getChannel();

        RandomAccessFile destFile = new RandomAccessFile(destFileName, "rw");
        FileChannel destFileChannel = destFile.getChannel();

        long position = 0;
        long count = srcFileChannel.size();

        srcFileChannel.transferTo(position, count, destFileChannel);

        new HashMap<>();
    }
}
