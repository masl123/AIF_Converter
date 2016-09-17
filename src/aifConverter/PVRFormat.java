package aifConverter;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

//use PVRTexTool to Open

public class PVRFormat {
	public PVRHeader header;
	public byte[] data;
	public ByteOrder order;
	
	
	public PVRFormat(int width, int height, Chunk data){
		header = new PVRHeader();
		header.Width = width;
		header.Height = height;
		order = data.bb.order();

		data.reset();
		this.data = data.readByteArrayEvery(data.size-1, 1, 1, false); //<--- set the Boolean to TRUE for other Image

		
		//Color
		if(order == ByteOrder.LITTLE_ENDIAN){
			for(int i = 0; i+3<this.data.length; i++){
				if(i%8 == 4){
					byte[] da = new byte[]{this.data[i], this.data[i+1] , this.data[i+2], this.data[i+3]};
					da = dWordSwap(da);
					this.data[i] = da[0];
					this.data[i+1] = da[1];
					this.data[i+2] = da[2];
					this.data[i+3] = da[3];
				}	
			}
		}
	}

	public byte[] getPVRFile(){
		ByteBuffer bb = ByteBuffer.allocate(data.length+52);
		bb.order(order);
		
		
		bb.putInt(header.Version);
		bb.putInt(header.Flags);
		bb.putLong(header.PFormat);
		bb.putInt(header.ColourSpace);
		bb.putInt(header.ChannelType);
		bb.putInt(header.Height);
		bb.putInt(header.Width);
		bb.putInt(header.Depth);
		bb.putInt(header.NumSurfaces);
		bb.putInt(header.NumFaces);
		bb.putInt(header.MipMapCount);
		bb.putInt(header.MetaData);
		bb.put(data);
		return bb.array();
	}
	
	
	public static byte[] wordSwap(byte[] in){
		for(int i = 0; i+1<in.length; i+=2){
			wordSwap(in, i, i+1);
		}
		return in;
	}

	public static byte[] wordSwap(byte[] in, int a, int b){
		byte temp = in[a];
		in[a] = in[b];
		in[b] = temp;
		return in;
	}
	
	public static byte[] dWordSwap(byte[] in){
		for(int i = 0; (i+3)<in.length; i+=4){
			wordSwap(in, i, i+3);
			wordSwap(in, i+1, i+2);
		}
		return in;
	}
	
	
	
	//Not Used
	public static byte[] spaceOut(byte[] data){
		
		int offset = 0;
		int skip = 1;
		
		byte[] b = new byte[data.length*skip];
		for(int i = 0; i+offset<data.length; i++){
			b[(i*skip)] = (byte) (data[i+offset]);
		}
		
		
		//COLOR
		for(int i = 0; i+1<b.length/4.0; i++){
			
			
			ByteBuffer buf =((ByteBuffer.allocate(4).put(b, i*4, 4)));
			buf.position(0);
			int a = buf.getInt();
			a = a<<0;
			byte[] da = ByteBuffer.allocate(4).putInt(a).array();		
			b[i*4] = da[0];
			b[i*4+1] = da[1];
			b[i*4+2] = da[2];
			b[i*4+3] = da[3];	
		}
		return b;
	}
}







