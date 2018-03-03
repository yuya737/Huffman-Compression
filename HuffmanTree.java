import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree implement to compress data
 */
public class HuffmanTree {

	private static final short EOF = (short) 256;
	public Node root;
	/**
	 * A Node Class for the implementation of HuffmanTree
	 */
	public class Node {
		short letter;
		Node left;
		Node right;
		int frequency;


		public Node(short letter, int frequency){
			this.letter = letter;
			this.frequency = frequency;
			this.left = null;
			this.right = null;
		}

		public Node(Node left, Node right, int frequency){
			this.left = left;
			this.right = right;
			this.frequency = frequency;
		}
	}
	/**
	 * A comparator for the priority queue
	 */
	class MyComparator implements Comparator<Node>{
		public int compare(Node arg0, Node arg1) {
			int x = arg0.frequency;
			int y = arg1.frequency;

			if (x < y) { 
				return -1;
			}
			else if (x > y){
				return 1;
			}
			return 0;
		}
	}
	/**
	 * Constructs a new HuffmanTree from a given Map
	 * @param a Map with value and frequencies
	 */
	public HuffmanTree(Map<Short, Integer> m){
		PriorityQueue<Node> pq =  new PriorityQueue<>(m.size(), new MyComparator()); //initialize priority queue with mycomparator
		pq.add(new Node(EOF, 1));
		for (Map.Entry<Short, Integer> entry : m.entrySet()){ //for-each loop to add Priority Queue
			pq.add(new Node(entry.getKey(), entry.getValue()));
		}
		while (pq.size() > 1){
			Node first = pq.poll();
			Node second = pq.poll();
			Node newNode = new Node(first, second, first.frequency + second.frequency);
			pq.add(newNode);
		}
		this.root = pq.poll(); //set root to node in Priority Queue
	}

	/**
	 * A Helper to construct a new HuffmanTree from a serialized Tree
	 * @param a BitInputStream
	 * @return a Node that stores the HuffmanTree
	 */
	public Node consHelper (BitInputStream in){
		if (in.readBit() == 1){ //if readBit returns 1, create an internal node
			Node left =  consHelper(in); //recursively create right and left node
			Node right =  consHelper(in);
			return new Node(left, right, 1);
		}
		else {
			short test = (short) in.readBits(9); 
			return new Node(test, 1); //create leaf node
		}
	}

	/**
	 * Constructs a new HuffmanTree from a serialized Tree
	 * @param a BitInputStream
	 */
	public HuffmanTree(BitInputStream in){
		this.root = consHelper(in);
	}

	/**
	 * A helper to serialize a HuffmanTree
	 * @param a BitOutputStream to store the serialized Tree
	 * @param a Node to store the HuffmanTree
	 */
	public void serializeHelper(BitOutputStream out, Node src){
		if((src.left==null)&&(src.right == null)){
			out.writeBit(0);
			out.writeBits(src.letter, 9);
		}else{
			out.writeBit(1);
			serializeHelper(out,src.left);
			serializeHelper(out,src.right);
		}
	}

	/**
	 * Serialize a HuffmanTree
	 * @param a BitOutputStream to store the serialized Tree
	 */
	public void serialize(BitOutputStream out){
		serializeHelper(out,root);
	}

	/**
	 * Construct a Map that store each value and its corresponding positions
	 * @param a Node that store the HuffmanTree
	 * @param a ArrayList that stores the positions
	 * @param a HashMap that relates each value with its positions
	 */
	public void HuffmanCode(Node src, ArrayList<Integer> pos, HashMap<Short, ArrayList<Integer>> map){
		if((src.left == null) && (src.left == null)){
			map.put(src.letter, pos);
		}else{
			ArrayList<Integer> newPos1 = new ArrayList<Integer>(pos); //create new arraylist for both left and right
			ArrayList<Integer> newPos2 = new ArrayList<Integer>(pos);
			newPos1.add(0);
			HuffmanCode(src.left, newPos1, map); //recursively enter left and right node
			newPos2.add(1);
			HuffmanCode(src.right, newPos2, map);
		}
	}

	/**
	 * Encode the file
	 * @param a BitInputStream as the original file
	 * @param a BitOutputStream as the output encoded file
	 */
	public void encode(BitInputStream in, BitOutputStream out){
		out.writeBits(1846, 32);
		serialize(out);
		ArrayList<Integer> position = new ArrayList<Integer>();
		HashMap<Short, ArrayList<Integer>> map = new HashMap<Short, ArrayList<Integer>>(); //intialize HashMap to store short and the respective positions with respect to the root
		HuffmanCode(root, position, map);
		short input = (short) in.readBits(8);
		while(input != -1){
			ArrayList<Integer> pos = map.get(input);
			for(int i = 0; i< pos.size();i++){
				out.writeBit((int)pos.get(i));
			}
			input = (short) in.readBits(8);
		}
		for (int i : map.get((short) 256)){ //writeout EOF
			out.writeBit(i);
		}
	}

	/**
	 * A helper to decode an encoded file
	 * @param a BitInputStream for the encoded file
	 * @param a BitOutputStream for the decoded file
	 * @param a Node that store the HuffmanTree needed
	 * @return a value after decoding the encoded file
	 */
	public short decodeH(BitInputStream in, BitOutputStream out, Node node){
		short ret = 0;
		if ((node.right == null) && (node.left == null)){
			ret = node.letter;
			return ret;
		}
		short bit = (short) in.readBit();
		if (bit == 1){
			ret = decodeH(in, out, node.right);
		}
		else if (bit == 0){
			ret = decodeH(in, out, node.left);
		}
		return ret;
	}


	/**
	 * Decode an encoded file
	 * @param a BitInputStream for the encoded file
	 * @param a BitOutputStream for the decoded file
	 */
	public void decode(BitInputStream in, BitOutputStream out){
		short temp = 0;
		while (in.hasBits()){
			temp = decodeH(in, out, this.root);
			if (temp == (short) 256){
				break;
			}
			out.writeBits((int) temp, 8);

		}
	}
}
