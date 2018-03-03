
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Grin {

	/**
	 * Create a FrequencyMap 
	 * @param a string which represents the file name
	 * @throws IOException if the Stream does not initialize correctly
	 * @return a Map that store each value and its corresponding frequency
	 */
	public static Map<Short, Integer> createFrequencyMap(String file) throws IOException{
		HashMap<Short, Integer> ret = new HashMap<Short, Integer>(); //intialize HashMap
		BitInputStream in = new BitInputStream(file);
		short input = (short) in.readBits(8);
		while(input != -1){
			if(ret.containsKey(input)){ //if the short was in the HashMap
				int freq = ret.get(input);
				freq++;
				ret.put(input, freq);
			}else{
				ret.put(input, 1);
			}
			input = (short) in.readBits(8);
		}
		return ret;
	}
	/**
	 * Encode a file 
	 * @param a string which represents the input file name
	 * @param a string which represents the output file name
	 * @throws IOException if the Stream does not initialize correctly
	 */
	public static void encode(String infile, String outfile) throws IOException{
		BitInputStream in = new BitInputStream(infile); //intialize input/output streams
		BitOutputStream out = new BitOutputStream(outfile);
		Map<Short, Integer> map =  createFrequencyMap(infile); //
		HuffmanTree ht = new HuffmanTree(map);
		ht.encode(in, out); //encode file
		in.close(); //close streams
		out.close();
	}
	/**
	 * Decode a file 
	 * @param a string which represents the input file name
	 * @param a string which represents the output file name
	 * @throws IOException if the Stream does not initialize correctly
	 */
	public static void decode(String infile, String outfile) throws IOException{
		BitInputStream in = new BitInputStream(infile); //intialize input/output streams
		BitOutputStream out = new BitOutputStream(outfile);
		if (in.readBits(32) != 1846) { //if the magic number isn't 1846 throw an error message
			throw new IllegalArgumentException();
		}
		HuffmanTree tree = new HuffmanTree(in);
		tree.decode(in, out); //decode file
		in.close(); //close streams
		out.close();
	}

	public static void main(String[] args) throws IOException {
		if (args[0].equals("encode")){
			encode(args[1], args[2]);
		}
		else if (args[0].equals("decode")){
			decode(args[1], args[2]);
		}
		else {
			System.out.println("Invaid Input. Exiting.");
		}
	}
}
