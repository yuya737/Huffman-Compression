import java.io.*;

/**
 * A BitInputStream reads a file bit-by-bit.
 */
public class BitInputStream {
    private FileInputStream input;
    private int digits;     // next set of digits (buffer)
    private int cursor;     // how many digits from buffer have been used

    private static final int BYTE_SIZE = 8;  // digits per byte

    /**
     * Constructs a new BitInputStream attached to the given file
     * @param file the file to open
     */
    public BitInputStream(String file) throws IOException {
        input = new FileInputStream(file);
        nextByte();
    }

    /** @return true iff the stream has bits left to produce */
    public boolean hasBits() { return digits != -1; }

    /**
     * Reads a bit from the stream in big-endian order (msb first)
     * @return the next bit from input (0 or 1) or -1 if the stream is out
     *         of data
     **/
    public int readBit() {
        // if at eof, return -1
        if (digits == -1) { return -1; }
        int result = (digits & (1 << cursor)) >> cursor;
        cursor--;
        if (cursor < 0) { nextByte(); }
        return result;
    }

    /**
     * Reads a number of bits in big-endian order (msb first)
     * @param n the number of bits to read (0--32)
     * @return the next n bits of the stream packed in a single integer or -1
     *         if the stream runs out of data
     */
    public int readBits(int n) {
        int ret = 0;
        for (int i = n - 1; i >= 0; i--) {
            int bit = readBit();
            if (bit == -1) { return -1; }
            ret = ret | (bit << i);
        }
        return ret;
    }

    /** Refreshes the internal buffer with the next BYTE_SIZE bits. */
    private void nextByte() {
        try {
            digits = input.read();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        cursor = BYTE_SIZE - 1;
    }

    /** Closes the stream, flushing any remaining bits to the file. */
    public void close() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    protected void finalize() {
        close();
    }
}