package cly;

public class transferm{
	public int byte4ToInt(byte[] a){
		int c0=a[0]&0xFF;
		int c1=a[1]&0xFF;
		int c2=a[2]&0xFF;
		int c3=a[3]&0xFF;
		int x=(c0<<24)|(c1<<16)|(c2<<8)|c3;
		return x;
	}
	
	public int byte3ToInt(byte[] a){
		int c0=a[0]&0xFF;
		int c1=a[1]&0xFF;
		int c2=a[2]&0xFF;
		int x=(c0<<16)|(c1<<8)|(c2);
		return x;
	}
	
	public int byte2ToInt(byte[] a){
		int c0=a[0]&0xFF;
		int c1=a[1]&0xFF;
		int x=(c0<<8)|(c1);
		return x;
	}
	
	public int byte1ToInt(byte[] a){
		int c0=a[0]&0xFF;
		int x1=c0;
		return x1;
	}
	
	public int byte0ToInt(byte[] a){
		int c0=a[0]&0x0F;
		int x=c0;
		return x;
	}
    public byte[] intToByte4(int i) {  
        byte[] targets = new byte[4];  
        targets[3] = (byte) (i & 0xFF);  
        targets[2] = (byte) (i >> 8 & 0xFF);  
        targets[1] = (byte) (i >> 16 & 0xFF);  
        targets[0] = (byte) (i >> 24 & 0xFF);  
        return targets;  
    }

    public byte[] intToByte1(int i) {  
        byte[] targets1 = new byte[1];  
        targets1[0] = (byte) (i & 0xFF);   
        return targets1;  
    }
    
    public byte[] intToByte2(int i) {  
        byte[] targets1 = new byte[2];  
        targets1[1] = (byte) (i & 0xFF);  
        targets1[0] = (byte) (i >> 8 & 0xFF);    
        return targets1;  
    }
    
    public byte[] intToByte3(int i) {  
        byte[] targets1 = new byte[3];  
        targets1[2] = (byte) (i & 0xFF);  
        targets1[1] = (byte) (i >> 8 & 0xFF);  
        targets1[0] = (byte) (i >> 16 & 0xFF);    
        return targets1;  
    }
    
    public byte[] intToByte0(int i) {  
        byte[] targets1 = new byte[1];  
        targets1[0] = (byte) (i & 0x0F);    
        return targets1;
    }	
}