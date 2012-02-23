package core;

import java.awt.List;
import java.util.LinkedList;

class OrderList extends LinkedList<PrimitiveOrder> {
	byte[] getBytes(){
		int commandSize=5;
		int bufIndex=0;
		byte[] buf=new byte[size()*4*commandSize];
		int[] intArray=new int[size()*commandSize];
		for(PrimitiveOrder order:this){
			if(order!=null){
				int[] orderArray=order.getIntArray();
				for(int i=0;i<orderArray.length;i++){
					intArray[bufIndex++]=orderArray[i];
				}
			}
			
		}
		return int2byte(intArray);
	}
	private static byte[] int2byte(int[]src) {
	    int srcLength = src.length;
	    byte[]dst = new byte[srcLength << 2];
	    
	    for (int i=0; i<srcLength; i++) {
	        int x = src[i];
	        
	        int j = i << 2;
	        dst[j++] = (byte) ((x >>> 0) & 0xff);           
	        dst[j++] = (byte) ((x >>> 8) & 0xff);
	        dst[j++] = (byte) ((x >>> 16) & 0xff);
	        dst[j++] = (byte) ((x >>> 24) & 0xff);
	    }
	    
	    return dst;
	}
}
