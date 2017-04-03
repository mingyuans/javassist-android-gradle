package com.mingyuans.javassist.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yanxq on 16/11/3.
 */

public class StreamUtils {
    public static final int BUFFER_SIZE = 8192;

    public static int copy(InputStream is, OutputStream os)
            throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int count = 0;
        int i;
        while ((i = is.read(buf)) != -1) {
            os.write(buf, 0, i);
            count += i;
        }
        return count;
    }

    public static void close(InputStream is){
        if(is != null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
