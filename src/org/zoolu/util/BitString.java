/*
 * Copyright (c) 2018 Luca Veltri, University of Parma
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.zoolu.util;


import java.util.BitSet;


/** Invariant string of bits.
  * <p>
  * For variable-length bit strings you can use {@link BitStringBuffer}.
  */
public class BitString {
	
	/** One-bit string 1 */
	public static final BitString ONE=new BitString(new boolean[]{true});

	/** One-bit string 1 */
	public static final BitString ZERO=new BitString(new boolean[]{false});


	/** Bit buffer */
	boolean[] bits;

	/** Offset */
	//int off;

	/** Length */
	//int len;


	/** Creates a new bit string.
	  * @param len the length of the bit array */
	private BitString(int len) {
		bits=new boolean[len];
	}


	/** Creates a new bit string from an array of bytes.
	  * @param buf the buffer containing the bit string. The most significant bit of each byte is taken first. */
	public BitString(byte[] buf) {
		this(buf,false);
	}


	/** Creates a new bit string from an array of bytes.
	  * @param buf the buffer containing the bit string
	  * @param low_first true if, within each byte, the bit with the lowest value must be taken first; false in the opposite case (i.e. bit with the greatest value must be taken first) */
	public BitString(byte[] buf, boolean low_first) {
		this(buf,0,buf.length,low_first);
	}


	/** Creates a new bit string from an array of bytes.
	  * @param buf the buffer containing the bit string 
	  * @param off the offset of the first byte within the buffer
	  * @param len the total number of bytes. The most significant bit of each byte is taken first. */
	public BitString(byte[] buf, int off, int len) {
		this(buf,off,len,false);
	}


	/** Creates a new bit string from an array of bytes.
	  * @param buf the buffer containing the bit string 
	  * @param off the offset of the first byte within the buffer
	  * @param len the total number of bytes
	  * @param low_first true if, within each byte, the bit with the lowest value must be taken first; false in the opposite case (i.e. bit with the greatest value must be taken first) */
	public BitString(byte[] buf, int off, int len, boolean low_first) {
		int binlen=len*8;
		int binoff=off*8;
		bits=new boolean[binlen];
		for (int i=0; i<binlen; i++) bits[i]=(low_first)? getBitLowFirst(buf,binoff+i) : getBitBigFirst(buf,binoff+i);
	}


	/** Creates a new bit string.
	  * @param buf array of booleans (true=1 and false=0) */
	public BitString(boolean[] buf) {
		this(buf,0,buf.length);
	}


	/** Creates a new bit string.
	  * @param str string of 0s and 1s */
	public BitString(String str) {
		bits=new boolean[str.length()];
		for (int i=0; i<bits.length; i++) bits[i]=(str.charAt(i)=='1');
	}


	/** Creates a new bit string.
	  * @param buf array of booleans (true=1 and false=0)
	  * @param off the starting offset within the array of booleans
	  * @param len the number of bits */
	public BitString(boolean[] buf, int off, int len) {
		bits=new boolean[len];
		for (int i=0; i<len; i++) bits[i]=buf[off+i];
	}


	/** Creates a new bit string from a BitSet.
	  * @param bitset the BitSet containing the bit string */
	public BitString(BitSet bitset) {
		this(bitset,0,bitset.size());
	}


	/** Creates a new bit string from a BitSet.
	  * @param bitset the BitSet containing the bit string
	  * @param off the starting offset within the BitSet
	  * @param len the length of the bit array */
	public BitString(BitSet bitset, int off, int len) {
		bits=new boolean[len];
		for (int i=0; i<len; i++) bits[i]=bitset.get(off+i);
	}


	/** Creates a new bit string.
	  * @param bb bit strings to be concatenated */
	public BitString(BitString... bb) {
		int len=0;
		for (BitString bs : bb) len+=bs.length();
		bits=new boolean[len];
		int index=0;
		for (BitString bs : bb) {
			System.arraycopy(bs.bits,0,bits,index,bs.bits.length);
			index+=bs.bits.length;
		} 
	}


	/** Gets the array length. */
	public int length() {
		return bits.length;
	}


	@Override
	public boolean equals(Object obj) {
		try {
			BitString bit_string=(BitString)obj;
			int len=length();
			if (len!=bit_string.length()) return false;
			// else
			for (int i=0; i<len; i++) if (bitAt(i)!=bit_string.bitAt(i)) return false;
			// else
			return true;
		}
		catch (Exception e) {  return false;  }
	}


	/** Gets the <i>i</i>-th bit. */
	public boolean bitAt(int i) {
		return bits[i];
	}


	/** Returns a new string that is a substring of this string.
	  * The substring begins with the bit at the specified index <i>begin</i> and extends to the end of this string. */
	public BitString substring(int begin) {
		return substring(begin,bits.length);
	}


	/** Returns a new string that is a substring of this string.
	  * The substring begins at the specified index <i>begin</i> and extends to the bit at index <i>end</i> - 1. Thus the length of the substring is end-begin.*/
	public BitString substring(int begin, int end) {
		BitString bs=new BitString(end-begin);
		System.arraycopy(bits,begin,bs.bits,0,bs.bits.length);
		return bs;
	}


	/** Tests if this string starts with the specified prefix.
	  * @param prefix the prefix
	  * @return true if the sequence represented by the argument is a prefix of this string; false otherwise. Note also that true will be returned if the argument is an empty string or is equal to this string */
	public boolean startsWith(BitString prefix) {
		return startsWith(prefix,0);
	}


	/** Tests if the substring of this string beginning at the specified offset starts with the specified prefix.
	  * @param prefix the prefix
	  * @param offset where to begin looking in this string
	  * @return true if the sequence represented by the argument is a prefix of the substring of this object starting at index toffset; false otherwise. The result is false if toffset is negative or greater than the length of this string */
	public boolean startsWith(BitString prefix, int offset) {
		if (offset+prefix.length()>length()) return false;
		// else
		for (int i=0; i<prefix.length(); i++, offset++) {
			if (bitAt(offset)!=prefix.bitAt(i)) return false;
		}
		return true;
	}


	/** Sets the <i>i</i> bit.
	  * @param bit the new bit value (true=1, false=0)
	  * @param i the index of the bit to be set */
	public void setBit(boolean bit, int i) {
		bits[i]=bit;
	}


	/** Sets bits starting from a given offset.
	  * @param substring the substring that replaces bits starting from offset
	  * @param offset the offset from which to start the replacement */
	public void setBits(BitString substring, int offset) {
		for (int j=0; j<substring.length(); j++) bits[offset++]=substring.bitAt(j);
	}


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * @param low_first true if, within each byte, the bit with the lowest value must be taken first; false in the opposite case (i.e. bit with the greatest value must be taken first)
	  * @return the array of bytes containing the BitString */
	public byte[] getBytes(boolean low_first) {
		int bitstr_len=length();
		byte[] buf=new byte[(bitstr_len+7)>>3];
		getBytes(buf,0,low_first);
		return buf;
	}


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * @param buf the buffer where the BitString bytes are written
	  * @param off the offset within the buffer
	  * @param low_first true if, within each byte, the bit with the lowest value must be taken first; false in the opposite case (i.e. bit with the greatest value must be taken first)
	  * @return the number of bytes */
	public int getBytes(byte[] buf, int off, boolean low_first) {
		return (low_first)? getBytesLowFirst(buf,off) : getBytesBigFirst(buf,off);
	}


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * Within each byte, the bit with the lowest value is taken first.
	  * @return the array of bytes containing the BitString */
	/*private byte[] getBytesLowFirst() {
		int bitstr_len=length();
		byte[] buf=new byte[(bitstr_len+7)>>3];
		for (int i=0; i<buf.length; i++) {
			byte b=0;
			int bit=0x1;
			for (int j=0; j<8; j++) {
				int k=(i<<3)+j;
				if (k<bitstr_len) {
					if (bits[k]) b|=bit;
					bit<<=1; 
				}
			}
			buf[i]=b;
		}
		return buf;
	}*/


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * Within each byte, the bit with the greatest value is taken first.
	  * @return the array of bytes containing the BitString */
	/*private byte[] getBytesBigFirst() {
		int bitstr_len=length();
		byte[] buf=new byte[(bitstr_len+7)>>3];
		for (int i=0; i<buf.length; i++) {
			byte b=0;
			int bit=0x80;
			for (int j=0; j<8; j++) {
				int k=(i<<3)+j;
				if (k<bitstr_len) {
					if (bits[k]) b|=bit;
					bit>>=1; 
				}
			}
			buf[i]=b;
		}
		return buf;
	}*/


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * Within each byte, the bit with the lowest value is taken first.
	  * @param buf the buffer where the BitString bytes are written
	  * @param off the offset within the buffer
	  * @return the number of bytes */
	private int getBytesLowFirst(byte[] buf, int off) {
		int bitstr_len=length();
		int buf_len=(bitstr_len+7)>>3;
		int index=0;
		for (int i=0; i<buf_len; i++) {
			byte b=0;
			int num=i>0?8:(bitstr_len-1)%8+1; // number of bits in this byte
			int bit=0x80>>(num-1);
			for (int j=0; j<num; j++) {
				if (bits[index++]) b|=bit;
				bit<<=1; 
			}
			buf[off+i]=b;
		}
		return buf_len;
	}


	/** Gets bytes.
	  * Gets an array of bytes containing the BitString.
	  * Within each byte, the bit with the greatest value is taken first.
	  * @param buf the buffer where the BitString bytes are written
	  * @param off the offset within the buffer
	  * @return the number of bytes */
	private int getBytesBigFirst(byte[] buf, int off) {
		int bitstr_len=length();
		int buf_len=(bitstr_len+7)>>3;
		int index=0;
		for (int i=0; i<buf_len; i++) {
			byte b=0;
			int num=i==0?(bitstr_len-1)%8+1:8; // number of bits in this byte
			int bit=1<<(num-1);
			for (int j=0; j<num; j++) {
				if (bits[index++]) b|=bit;
				bit>>=1; 
			}
			buf[off+i]=b;
		}
		return buf_len;
	}


	/** Returns the index within this bit string of the first occurrence of the specified substring.
	  * @param substring the substring to search for.
	  * @return the index of the first occurrence of the specified substring, or -1 if there is no such occurrence. */
	public int indexOf(BitString substring) {
		return indexOf(substring,0);
	}


	/** Returns the index within this string of the first occurrence of the specified substring, starting at the specified offset.
	  * @param substring the substring to search for.
	  * @param offset the offset from which to start the search.
	  * @return the index of the first occurrence of the specified substring, starting at the specified offset, or -1 if there is no such occurrence. */
	public int indexOf(BitString substring, int offset) {
		int len=length();
		for (int i=offset; i<(len-substring.length()+1); i++) {
			int j=0;
			for (; j<substring.length(); j++) {
				if (bits[i+j]!=substring.bitAt(j)) break;
			}
			if (j==substring.length()) return i;
		}
		return -1;
	}


	/** Inverts the bit string. */
	public BitString invert() {
		for (int i=0; i<bits.length; i++) bits[i]=!bits[i];
		return this;
	}



	/** Gets a string representation of this object. */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		for (int i=0; i<length(); i++) sb.append((bitAt(i))?'1':'0');
		return sb.toString();
	}


	/** Gets the <i>i</i> bit of a given byte array (<i>buf</i>).
	  * @param low_first true if, within a byte, the bit with the lowest value must be taken first; false in the opposite case (i.e. bit with the greatest value must be taken first)
	  * @return the <i>i</i> bit of the given array */
	public static boolean getBit(byte[] buf, int i, boolean low_first) {
		return (low_first)? getBitLowFirst(buf,i) : getBitBigFirst(buf,i);
	}


	/** Gets the <i>i</i> bit of a given byte array (<i>b</i>). Within a byte, the bit with the lowest value is taken first. */
	private static boolean getBitLowFirst(byte[] buf, int i) {
		int b=buf[i>>3];
		int o=i&0x7;
		return ((b>>o)&0x1)!=0;
	}


	/** Gets the <i>i</i> bit of a given byte array (<i>b</i>). Within a byte, the bit with the greatest value is taken first. */
	private static boolean getBitBigFirst(byte[] buf, int i) {
		int b=buf[i>>3];
		int o=7-(i&0x7);
		return ((b>>o)&0x1)!=0;
	}


	/** Reverses the bit order of a byte. */
	public static byte reverse(byte b) {
		return (byte)(((b&0x80)>>7) | ((b&0x40)>>5) | ((b&0x20)>>3) | ((b&0x10)>>1) | ((b&0x08)<<1) | ((b&0x04)<<3) | ((b&0x02)<<5) | ((b&0x01)<<7));
	}

}
	
