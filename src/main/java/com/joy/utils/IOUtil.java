package com.joy.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * IO工具类
 */
public class IOUtil {

    public static void closeInStream(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOutStream(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFileFromRaw(InputStream in, File outPutFile) {
        boolean result = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outPutFile);

            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInStream(in);
            closeOutStream(fos);
        }
        return result;
    }

    private static final int EOF = -1;
    /**
     * The default buffer size() to use for {@link #copyLarge(InputStream, OutputStream)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
//    private static final int SKIP_BUFFER_SIZE = 2048;
//    private static byte[] SKIP_BYTE_BUFFER;

    /**
     * Get the contents of an InputStream as a byte[].
     * This method buffers the input internally, so there is no need to use a BufferedInputStream.
     *
     * @param input the InputStream to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Get the contents of an InputStream as a byte[].
     * Use this method instead of toByteArray(InputStream) when InputStream size is known
     *
     * @param input the InputStream to read from
     * @param size  the size of InputStream
     * @return the requested byte array
     * @throws IllegalArgumentException if size is less than zero
     * @throws IOException              if an I/O error occurs or InputStream size differ from parameter size
     */
    public static byte[] toByteArray(InputStream input, int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }
        if (size == 0) {
            return new byte[0];
        }
        byte[] data = new byte[size];
        int offset = 0;
        int readed;
        while (offset < size && (readed = input.read(data, offset, size - offset)) != EOF) {
            offset += readed;
        }
        if (offset != size) {
            throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
        }
        return data;
    }

    /**
     * Copy bytes from an InputStream to an OutputStream.
     * This method buffers the input internally, so there is no need to use a BufferedInputStream.
     * Large streams (over 2GB) will return a bytes copied value of -1 after the copy has completed
     * since the correct number of bytes cannot be returned as an int.
     * For large streams use the copyLarge(InputStream, OutputStream) method.
     *
     * @param input  the InputStream to read from
     * @param output the OutputStream to write to
     * @return the number of bytes copied, or -1 if > Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) InputStream to an OutputStream.
     * This method buffers the input internally, so there is no need to use a BufferedInputStream.
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the InputStream to read from
     * @param output the OutputStream to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy bytes from a large (over 2GB) InputStream to an OutputStream.
     * This method uses the provided buffer, so there is no need to use a BufferedInputStream.
     *
     * @param input  the InputStream to read from
     * @param output the OutputStream to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
            LogMgr.e("daisw", "====" + count);
        }
        return count;
    }

//    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length) throws IOException {
//        return copyLarge(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
//    }

//    public static long copyLarge(InputStream input, OutputStream output,
//                                 final long inputOffset, final long length, byte[] buffer) throws IOException {
//        if (inputOffset > 0) {
//            skipFully(input, inputOffset);
//        }
//        if (length == 0) {
//            return 0;
//        }
//        final int bufferLength = buffer.length;
//        int bytesToRead = bufferLength;
//        if (length > 0 && length < bufferLength) {
//            bytesToRead = (int) length;
//        }
//        int read;
//        long totalRead = 0;
//        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
//            output.write(buffer, 0, read);
//            totalRead += read;
//            if (length > 0) { // only adjust length if not reading to the end
//                // Note the cast must work because buffer.length is an integer
//                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
//            }
//        }
//        return totalRead;
//    }

//    public static void skipFully(InputStream input, long toSkip) throws IOException {
//        if (toSkip < 0) {
//            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
//        }
//        long skipped = skip(input, toSkip);
//        if (skipped != toSkip) {
//            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
//        }
//    }

//    public static long skip(InputStream input, long toSkip) throws IOException {
//        if (toSkip < 0) {
//            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
//        }
//        /*
//         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
//         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
//         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
//         */
//        if (SKIP_BYTE_BUFFER == null) {
//            SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
//        }
//        long remain = toSkip;
//        while (remain > 0) {
//            long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
//            if (n < 0) { // EOF
//                break;
//            }
//            remain -= n;
//        }
//        return toSkip - remain;
//    }

    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static void copy(InputStream input, Writer output, Charset encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(encoding));
        copy(in, output);
    }

    public static String toString(InputStream input, Charset encoding) throws IOException {
        if (input == null) {
            return "";
        }
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static String toString(InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }
}
