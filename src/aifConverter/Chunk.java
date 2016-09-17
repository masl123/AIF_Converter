package aifConverter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

public class Chunk {
	
	public List<Chunk> chunks;
	int offset;
	int size;
	ByteBuffer bb;
	String ident;
	
	private int position;
	
	
	public Chunk(String ident, int offset, int size, ByteBuffer bb){
		chunks = new LinkedList<Chunk>();
		this.offset = offset;
		this.size = size;
		this.bb = bb;
		this.position = 0;
		this.ident = ident;
	}
	
	public void readRaw(int size) throws RuntimeException{
		if(position+size >= this.size){
			throw new RuntimeException("No More Bytes to Read");
		}
		Chunk c = new Chunk("RAW ", position+offset, size, bb);
		position+=c.size;
		chunks.add(c);
	}
	
	public String readIdent() throws RuntimeException{
		if(position+4 >= size){
			throw new RuntimeException("No More Bytes to Read");
		}
		bb.position(offset+position);
		position+=4;
		return readIdent(bb);
	}
	
	public byte[] readByteArray(int size) throws RuntimeException{
		if(position+size >= this.size){
			throw new RuntimeException("No More Bytes to Read");
		}
		byte[] b = new byte[size];
		bb.position(offset+position);
		bb.get(b);
		return b;
	}
	public byte[] readByteArrayEvery(int size, int skip, int read, boolean order) throws RuntimeException{
		if(position+size >= this.size){
			throw new RuntimeException("No More Bytes to Read");
		}
		
		double mul = (read)/(double)(skip+read);
		byte[] b = new byte[(int)(size*mul)];
		bb.position(offset+position);
		
		
		
		
		
		for(int i = 0; i+read<size; ){
			
			if(order){
				for(int a = 0; a<skip; a++, i++){
					bb.get();
				}
			}
			
			for(int a = 0; a<read; a++, i++){
				int in = ((int)(i*mul)) + a;
				if(b.length>in)
					b[in] = bb.get();
			}
			
			if(!order){
				for(int a = 0; a<skip; a++, i++){
					bb.get();
				}
			}
		}
		return b;
		
		
	}
	
	public Chunk readChunk() throws RuntimeException{
		if(position >= size){
			throw new RuntimeException("No More Bytes to Read");
		}
		bb.position(offset+position);
		Chunk c = readChunk(bb);
		position+=c.size;
		chunks.add(c);
		return c;
	}
	
	public int readInt() throws RuntimeException{
		if(position+4 >= size){
			throw new RuntimeException("No More Bytes to Read");
		}
		
		bb.position(offset+position);
		position += 4;
		return bb.getInt();
	}
	
	public short readShort() throws RuntimeException{
		if(position+2 >= size){
			throw new RuntimeException("No More Bytes to Read");
		}
		
		bb.position(offset+position);
		position += 2;
		return bb.getShort();
		
		
	}
	
	public void reset(){
		position = 0;
	}
	
	public void skip(int bytes) throws RuntimeException{
		if(position+bytes >= size){
			throw new RuntimeException("No More Bytes to Read");
		}
		position += bytes;
	}
	
	public void rewind(int bytes) throws RuntimeException{
		if(position-bytes < 0){
			throw new RuntimeException("No More Bytes to Read");
		}
		position -= bytes;
	}
	
	
	public int getPosition(){
		return position;
	}
	
	public List<Chunk> getChunks(String ident){
		List<Chunk> l = new LinkedList<Chunk>();
		for(Chunk c : chunks){
			if(c.ident.equalsIgnoreCase(ident)){
				l.add(c);
			}
		}
		return l;
	}

	
	@Override
	public String toString() {
		String s = "[Name: "+ident+";\n"
				+  " Offset: 0x"+Integer.toHexString(offset)+";\n"
				+  " Size: 0x"+Integer.toHexString(size)+";";
		for(Chunk c : chunks){
			String a = c.toString();
			a = a.replaceAll("(?m)^", "\t");
			
			s+="\n"+a+"\n";
		}
		return s+="]";
	}
	
	public static Chunk readChunk(ByteBuffer bb){	
		if(bb.position()+8 >= bb.capacity()){
			return null;
		}
		
		int offset = bb.position();
		String ident = readIdent(bb);
		int size = bb.getInt();
		bb.position(offset+size);
		
		return new Chunk(ident,offset, size, bb);
	}
	
	private static String readIdent(ByteBuffer bb){
		String s = ((char)bb.get())+""+((char)bb.get())+""+((char)bb.get())+""+((char)bb.get());
		if(bb.order() == ByteOrder.LITTLE_ENDIAN)
			return new StringBuffer(s).reverse().toString();
		else 
			return s;
			
	}
}
