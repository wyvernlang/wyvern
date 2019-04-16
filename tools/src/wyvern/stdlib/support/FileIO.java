package wyvern.stdlib.support;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/** New imports! **/
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.math.BigInteger;
import java.nio.ByteBuffer;


public class FileIO {
    public static final FileIO file = new FileIO();

    public PrintWriter openForAppend(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return new PrintWriter(bufferedWriter);
    }

    public BufferedReader openForRead(String path) throws IOException {
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        return new BufferedReader(bufferedReader);
    }

    public String readFileIntoString(BufferedReader br) throws IOException {
        String line = "";
        String message = "";
        while ((line = br.readLine()) != null) {
            message += line;
        }
        return message;
    }

    public void writeStringIntoFile(String content, String filename) throws IOException {
        File file = new File(filename + "-files/" + filename + "" + System.currentTimeMillis() + ".txt");
        file.getParentFile().mkdirs();
        PrintWriter writer = new PrintWriter(file);
        writer.println(content);
        writer.close();
    }

    /** NEW METHODS ADDED FOR IO LIBRARY **/
    
    public File createNewFile(String pathname) {
        return new File(pathname);
    }
    
    /** Naming conventions a bit shady but won't conflict with previously defined methods **/
    public BufferedWriter openBWForWrite(Object f) throws IOException {
        return new BufferedWriter(new FileWriter((File) f));
    }
    
    public BufferedWriter openBWForAppend(Object f) throws IOException {
        return new BufferedWriter(new FileWriter((File) f, true));
    }
    
    public BufferedReader openBRForRead(Object f) throws IOException {
        return new BufferedReader(new FileReader((File) f));
    }
    
    /* Used for both append and write, since both are BufferedWriter */
    public void writeString(BufferedWriter bw, String s) throws IOException {
        bw.write(s, 0, s.length());
    }
    
    public String readLineFromFile(BufferedReader br) throws IOException {
        return br.readLine();
    }
    
    public boolean isNull(Object o) {
        return o == null;
    }
    
    public String readFullyFile(BufferedReader br) throws IOException {
        String next = br.readLine();
        if (next == null) {
            return "";
        } else {
            String acc = next;
            next = br.readLine();
            while (next != null) {
                acc += "\n" + next;
                next = br.readLine();
            }
            return acc;
        }
    }
    
    public int readCharFromFile(BufferedReader br) throws IOException {
        return br.read();
    }
    
    public void closeWriter(BufferedWriter bw) throws IOException {
        bw.close();
    }
    
    public void closeReader(BufferedReader br) throws IOException {
        br.close();
    }
    
    /** functionality for RandomAccessFile **/
    
    public RandomAccessFile makeRandomAccessFile(Object f, String mode) throws IOException {
        return new RandomAccessFile((File) f, mode);
    }
    
    public void closeRandomAccessFile(RandomAccessFile r) throws IOException {
        r.close();
    }
    
    public void writeUTFRandomAccess(RandomAccessFile r, String s) throws IOException {
        r.writeUTF(s);
    }
    
    public void writeStringRandomAccess(RandomAccessFile r, String s) throws IOException {
        r.writeBytes(s);
    }
    
    public String readUTFRandomAccess(RandomAccessFile r) throws IOException {
        return r.readUTF();
    }
    
    public String readLineRandomAccess(RandomAccessFile r) throws IOException {
        return r.readLine();
    }
    
    public long accessFilePointer(RandomAccessFile r) throws IOException {
        return r.getFilePointer();
    }
    
    public void seekFilePointer(RandomAccessFile r, long pos) throws IOException {
        r.seek(pos);
    }
    
    public long getRandomAccessFileLength(RandomAccessFile r) throws IOException {
        return r.length();
    }
    
    /** way more read/write methods to add here **/
    
    /** read/write bytes functionality **/
    
    public FileInputStream makeFileInputStream(Object f) throws IOException {
        return new FileInputStream((File) f);
    }
    
    public void closeFileInputStream(FileInputStream f) throws IOException {
        f.close();
    }
    
    // read with blocking, -1 for EOF
    public int readByteFileInputStream(FileInputStream f) throws IOException {
        return f.read();
    }
    
    public FileOutputStream makeFileOutputStream(Object f) throws IOException {
        return new FileOutputStream((File) f);
    }
    
    public void closeFileOutputStream(FileOutputStream f) throws IOException {
        f.close();
    }
    
    public void writeByteFileOutputStream(FileOutputStream f, int b) throws IOException {
        f.write(b);
    }
    
    public void writeArbitraryPrecisionInteger(FileOutputStream f, BigInteger n) throws IOException {
		byte[] contentBytes = n.toByteArray();
		int size = contentBytes.length;
		
        //int size = n.bitLength();
        if(size > 127) { //might want to catch case where size > 255
			//calculate the number of bytes needed to represent the number
			//(definitely a better way to do this
			int numRealBytes = 0;
			BigInteger temp = n;
			BigInteger byteSize = new BigInteger("256");
			while(temp.compareTo(BigInteger.ZERO) > 0) {
				temp = temp.divide(byteSize);
				numRealBytes++;
			}
            f.write(128 + numRealBytes); //need to account for exactly 128 bytes
            byte[] realSizeBytes = new byte[numRealBytes];
            for(int i = numRealBytes - 1; i >= 0; i--) {
                realSizeBytes[i] = (byte) (size % 256);
				size /= 256;
            }
            f.write(realSizeBytes);
        } else {
            f.write(size);
        }
		
		f.write(contentBytes);
		
    }
    
    public BigInteger readArbitraryPrecisionInteger(FileInputStream f) throws IOException {
        int size = f.read();
        if(size > 127) {
            byte[] realSizeBytes = new byte[size - 128];
            for(int i = 128; i < size; i++) {
                realSizeBytes[i - 128] = (byte) f.read();
            }
            size = ByteBuffer.wrap(realSizeBytes).getInt();
        }
		byte[] contentBytes = new byte[size];
        for(int i = 0; i < size; i++) {
			contentBytes[i] = (byte) f.read();
		}
		return new BigInteger(contentBytes);
    }
    
    
    
    /** for the byte array abstraction (bytes not int subtype in wyvern) **/
    
    public int[] makeByteArray(int size) {
        return new int[size];
    }
    
    public void setByteArray(Object b, int i, int n) {
        int[] a = (int[]) b;
        a[i] = n;
    }
    
    public int getByteArray(Object b, int i) {
        int[] a = (int[]) b;
        return a[i];
    }


}
