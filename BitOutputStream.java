import java.io.*;

/**
 * A BitOutputStream allows bit-by-bit writing to a file.
 */
public class BitOutputStream {
    private PrintStream output;
    private int digits;     // a buffer used to build up next set of digits
    private int cursor;     // our current position in the buffer.
    private boolean debug;  // set to true to write ASCII 0s and 1s rather than
                            // bits

    private static final int BYTE_SIZE = 8; // digits per byte

    /**
     * Constructs a new BitOutputStream attached to the given file.
     * @param file the file to write to
     * @param debug true iff you want to output the bits as ASCII 0s and 1s
     * @throws FileNotFoundException if the file is not found
     */
    public BitOutputStream(String file, boolean debug) throws IOException {
        this.output = new PrintStream(file);
        this.debug = debug;
        digits = 0;
        cursor = BYTE_SIZE - 1;
    }

    /**
     * Constructs a new BitOutputStream attached to the given file.
     * @param file the file to write to
     * @throws FileNotFoundException if the file is not found
     */
    public BitOutputStream(String file) throws IOException {
        this(file, false);
    }

    /**
     * Writes the given bit to the stream.
     * @param bit the bit to write (0 or 1)
     */
    public void writeBit(int bit) {
        if (bit < 0 || bit > 1) {
            throw new IllegalArgumentException("Illegal bit: " + bit);
        } else if (debug) {
            output.print(bit);
        } else {
            digits += bit << cursor;
            cursor--;
            if (cursor < 0) {
                flush();
            }
        }
    }

    /**
     * Writes the lower n bits to the stream in big-endian style.
     * @param bits the bits to write as an integer
     * @param n the number of bits to write from the integer
     */
    public void writeBits(int bits, int n) {
        for (int i = n-1; i >= 0; i--) {
            writeBit((bits >>> i) % 2);
        }
    }

    /**
     * Flushes the buffer. If numDigits < BYTE_SIZE, flush will pad the output
     * with extra 0s in the least-significant bits so that a full byte is
     * written to the file.
     */
    private void flush() {
        if (cursor == BYTE_SIZE - 1) {
            return;
        }
        output.write(digits);
        digits = 0;
        cursor = BYTE_SIZE - 1;
    }

    /** Closes the stream, flushing any remaining bits to the file */
    public void close() {
        if (cursor >= 0) {
            flush();
        }
        output.close();
    }

    protected void finalize() {
        close();
    }
}