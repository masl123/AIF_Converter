package aifConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;

public class Program {
	public static void main(String[] args) {

		if(args.length != 4 || !args[0].equalsIgnoreCase("-i") || !args[2].equalsIgnoreCase("-o")){
			printUsage();
			return;
		}
		
		
		System.out.println("AIFConverter - Converts AIF Files");
		
		RandomAccessFile in = null;
		
		try {
			//create input File
			in = new RandomAccessFile(args[1], "r");
			
			byte[] inBuffer = new byte[(int)in.length()];
			in.read(inBuffer);
			
			//Endianess
			ByteBuffer bb = ByteBuffer.wrap(inBuffer);
			byte[] headb = new byte[4];
			bb.get(headb);
			String head = new String(headb);
			
			if(!head.equalsIgnoreCase("AIF ")){
				 bb.order(ByteOrder.LITTLE_ENDIAN);
			}
			bb.position(0);
			
			//readChunks
			Chunk FIA = Chunk.readChunk(bb);
			Chunk FMA = Chunk.readChunk(bb);
			
			readFIA(FIA);
			readFMA(FMA);			
			
			short[] imgHeader =	readImgX(FIA.getChunks("imgX").get(0));
			System.out.println("\nFile Structure:");
			System.out.println(FIA);
			System.out.println(FMA);
			System.out.println("\nImage Properties:");
			System.out.println("\tWidth: "+imgHeader[1]+"\n\tHeight: "+imgHeader[2]+"\n\tFormat: "+imgHeader[0]+"\n");
						
			if(imgHeader[0] == 0x08){
				PVRFormat format = new PVRFormat(imgHeader[1], imgHeader[2], FMA.getChunks("RAW ").get(0));
				savetoFile(args[3],format);
			}
			//TODO add all other Formats

			in.close();
			System.out.println("DONE");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void readFMA(Chunk FMA) throws Exception{
		FMA.skip(16);
		FMA.readChunk();
		FMA.readChunk();
		FMA.readChunk();
		FMA.readRaw(FMA.size-FMA.getPosition()-1);
	}
	
	public static void readFIA(Chunk FIA) throws Exception{	
		FIA.skip(16);
		FIA.readChunk();
		FIA.readChunk();
	}

	public static short[] readImgX(Chunk IMGX) throws Exception{
		IMGX.skip(16);
		IMGX.skip(0x18);
		short width = IMGX.readShort();
		short height = IMGX.readShort();
		
		
		IMGX.rewind(0x1C);
		IMGX.skip(0x1e);
		short format = IMGX.readShort();
		
		return new short[]{format, width, height};
	}
	
	public static void savetoFile(String path,PVRFormat format){
		try {
			
			//create output File
			File f = new File(path);
			if(f.exists()){
				System.out.println("Overwriting File " + path);
				f.delete();
				f.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(format.getPVRFile());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printUsage(){
		System.out.println("USAGE: AIFConverter -i [INPUT] -o [OUTPUT]\n");
	}
	
}
