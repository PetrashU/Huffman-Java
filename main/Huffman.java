
import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    private String path;
    private Node root;

    private String text;

    public int huffman(String pathToRootDir, boolean compress) {
        // compress: true if compress, false if decompress
        if (pathToRootDir == null) {
            throw new IllegalArgumentException("File path can not be null!");
        }
        path = pathToRootDir + "/";
        if (compress) {
            text = ReadText();
            HashMap<Character, String> codes = new HashMap<>();
            root = builtTree();
            codeChars(codes, root, "");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.toCharArray().length; i++) {
                char c = text.toCharArray()[i];
                if ((int) c < 128) {
                    sb.append(codes.get(c));
                }
            }
            saveCode(sb.toString());
            saveTree();
            return sb.length();
        } else {
            readTree();
            String s;
            s = readCoded();
            text = decode(s);
            saveDecoded();
            return text.length();
        }
    }

    private String ReadText() {
        FileReader f;
        try {
            f = new FileReader(path + "text.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(("File " + path + "text.txt" + " not found!"));
        }
        BufferedReader reader = new BufferedReader(f);
        StringBuilder builder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException("Problem with reading file " + path + "text.txt" + " : " + e);
            }
            builder.append(line);
            builder.append(ls);
        }
        if (builder.length() != 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "text.txt" + " : " + e);
        }

        return builder.toString();
    }

    private Node builtTree() {
        if (text.length() == 0) {
            throw new IllegalArgumentException("Text is empty, nothing to compress!");
        }

        HashMap<Character, Integer> f = new HashMap();
        for (int i = 0; i < text.toCharArray().length; i++) {
            char c = text.toCharArray()[i];
            if ((int) c < 128) {
                f.put(c, f.getOrDefault(c, 0) + 1);
            }
        }

        PriorityQueue<Node> tree = new PriorityQueue<>(new NodeComparator());
        for (Character c : f.keySet()) {
            tree.add(new Node(c, f.get(c)));
        }

        while (tree.size() > 1) {
            Node left = tree.poll();
            Node right = tree.poll();

            Node sum = new Node(null, right != null ? left.freq + right.freq : left.freq, left, right);
            tree.add(sum);
        }
        return tree.peek();
    }

    private void codeChars(HashMap<Character, String> codes, Node node, String s) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            codes.put(node.elem, s.length() > 0 ? s : "1");
        }
        codeChars(codes, node.left, s + '0');
        codeChars(codes, node.right, s + '1');
    }

    private void saveCode(String code) {

        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new FileOutputStream(path + "code.bin"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Problem with creating file for encoded text in " + path + " : " + e);
        }
        int size = code.length();

        BitArray bitArray = new BitArray(size);
        for (int i = 0; i < size; i++) {
            bitArray.set(i, code.charAt(i) != '0' ? 1 : 0);
        }
        try {
            dos.writeInt(size);
            dos.write(bitArray.bytes, 0, bitArray.getSizeInBytes());
        } catch (IOException e) {
            throw new RuntimeException("Problem with writing to " + path + "code.bin : " + e);
        }
        try {
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem with writing to " + path + "code.bin : " + e);
        }
        try {
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "code.bin :" + e);
        }
    }

    private void saveTree() {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new FileOutputStream(path + "tree.bin"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Problem with creating file " + path + "tree.bin :" + e);
        }
        saveNode(dos, root);
        try {
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Problem with writing in file " + path + "tree.bin :" + e);
        }
        try {
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "tree.bin :" + e);
        }
    }

    private void saveNode(DataOutputStream dos, Node node) {
        try {
            if (node.left == null && node.right == null) {
                dos.writeBoolean(true);
                dos.writeChar(node.elem);
            } else {
                dos.writeBoolean(false);
                saveNode(dos, node.left);
                saveNode(dos, node.right);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem with writing to " + path + "tree.bin : " + e);
        }
    }

    private void readTree() {
        DataInputStream dis;
        try {
            dis = new DataInputStream(new FileInputStream(path + "tree.bin"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + path + "tree.bin" + " not found!");
        }
        root = readNode(dis);
        try {
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "tree.bin :" + e);
        }
    }

    private Node readNode(DataInputStream dis) {
        try {
            if (dis.readBoolean())
                return new Node(dis.readChar(), null, null);
            else {
                Node left = readNode(dis);
                Node right = readNode(dis);
                return new Node(null, left, right);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem with reading from " + path + "tree.bin : " + e);
        }
    }

    private String readCoded() {
        DataInputStream dis;
        try {
            dis = new DataInputStream(new FileInputStream(path + "code.bin"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + path + "code.bin" + " not found!");
        }
        int dataSizeBits;
        BitArray bitArray;
        try {
            dataSizeBits = dis.readInt();
            bitArray = new BitArray(dataSizeBits);
            dis.read(bitArray.bytes, 0, bitArray.getSizeInBytes());
        } catch (IOException e) {
            throw new RuntimeException("Problem with reading file " + path + "code.bin" + " : " + e);
        }
        try {
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "code.bin :" + e);
        }

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < bitArray.size; i++) {
            code.append(bitArray.get(i));

        }
        return code.toString();
    }

    private String decode(String code) {
        StringBuilder text = new StringBuilder();
        Node curr = root;

        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '0')
                curr = curr.left;
            else
                curr = curr.right;
            if (curr.left == null && curr.right == null) {
                text.append(curr.elem);
                curr = root;
            }
        }
        return text.toString();
    }

    private void saveDecoded() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(path + "text.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Problem with creating file for encoded text in " + path + " : " + e);
        }
        try {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException("Problem with writing to " + path + "text.txt : " + e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem with closing file " + path + "text.txt :" + e);
        }
    }


    private static class Node {

        Character elem;
        int freq;
        Node left = null;
        Node right = null;

        Node(Character elem, int freq) {
            this.elem = elem;
            this.freq = freq;
        }

        public Node(Character elem, int freq, Node left, Node right) {
            this.elem = elem;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public Node(Character elem, Node left, Node right) {
            this.elem = elem;
            this.left = left;
            this.right = right;
        }
    }

    private static class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            return o1.freq - o2.freq;
        }
    }

    public static class BitArray {	//BirArray representation taken from https://github.com/Arhiser/java_tutorials/blob/master/src/ru/arhiser/huffman/Main.java
        byte[] bytes;

        int size;

        private final byte[] masks = {0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000};

        public BitArray(int size) {
            this.size = size;
            int sizeInBytes = size / 8;
            if (size % 8 > 0) {
                sizeInBytes = sizeInBytes + 1;
            }
            bytes = new byte[sizeInBytes];
        }

        public void set(int index, int value) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if (value != 0) {
                bytes[byteIndex] = (byte) (bytes[byteIndex] | masks[bitIndex]);
            } else {
                bytes[byteIndex] = (byte) (bytes[byteIndex] & ~masks[bitIndex]);
            }
        }

        public char get(int index) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            return (bytes[byteIndex] & masks[bitIndex]) != 0 ? '1' : '0';
        }

        public int getSizeInBytes() {
            return bytes.length;
        }
    }


    /*codeString() and decodeString() are for tests. Once we know that codeString is right, we test decodeString()*/

    public String codeString(String s) {
        if (s == null)
            throw new IllegalArgumentException("String can not be Null!");

        text = s;
        HashMap<Character, String> codes = new HashMap<>();
        root = builtTree();

        codeChars(codes, root, "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.toCharArray().length; i++) {
            char c = text.toCharArray()[i];
            if ((int) c < 128) {
                sb.append(codes.get(c));
            }
        }
        return sb.toString();
    }

    public String decodeString(String s) {
        String code = codeString(s);
        return decode(code);
    }

    public String getText() {
        return this.text;
    }
}
