package aifConverter;

//see http://cdn.imgtec.com/sdk-documentation/PVR+File+Format.Specification.pdf 
//and http://cdn.imgtec.com/sdk-documentation/PVRTC+%26+Texture+Compression.User+Guide.pdf for Specs

public class PVRHeader {
	int Version = 0x03525650, Flags = 0x2, ColourSpace = 1, ChannelType = 2, Height, Width, Depth = 1, NumSurfaces = 2, NumFaces = 1, MipMapCount = 1, MetaData = 0;
	long PFormat = 2;
}
