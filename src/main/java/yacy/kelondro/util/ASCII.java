/**
 *  ASCIIComparator
 *  Copyright 2010 by Michael Peter Christen
 *  First released 25.2.2011 at http://yacy.net
 *
 *  $LastChangedDate$
 *  $LastChangedRevision$
 *  $LastChangedBy$
 *
 *  This file is part of YaCy Content Integration
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package yacy.kelondro.util;

import java.util.Comparator;

/**
 * this is a replacement of an insensitive collator object, produced by a RuleBasedCollator Class
 * The RuleBasedCollator is a very inefficient class if it is used only for insensitive ASCII comparisments
 * This class is a very simple comparator for Strings which can be used to compare also Strings with upper/lowercase
 * Strings without applying .toUpperCase or .toLowerCase
 * Strings must contain no other than ASCII code.
 */
public class ASCII implements Comparator<String> {

    public static final ASCII insensitiveASCIIComparator = new ASCII(true);
    public static final ASCII identityASCIIComparator = new ASCII(false);

    public boolean insensitive;

    public ASCII(boolean insensitive) {
        this.insensitive = insensitive;
    }

    @Override
    public Object clone() {
        return this; // because we do not have any class variables that changes
    }

    @Override
    public int compare(String s0, String s1) {
        if (s0 == null && s1 == null) return 0;
        if (s0 == null) return -1;
        if (s1 == null) return 1;
        int i = 0;
        int l0 = s0.length(), l1 = s1.length();
        int lm = Math.min(l0, l1);
        char c0, c1;
        while (i < lm) {
            c0 = s0.charAt(i);
            c1 = s1.charAt(i);
            if (this.insensitive && c0 >= 'A' && c0 <='Z') c0 = (char) ((byte) c0 + 32);
            if (this.insensitive && c1 >= 'A' && c1 <='Z') c1 = (char) ((byte) c1 + 32);
            if (c0 > c1) return 1;
            if (c1 > c0) return -1;
            i++;
        }
        if (l0 > l1) return 1;
        if (l1 > l0) return -1;
        return 0;
    }

    public boolean equals(String s0, String s1) {
        if (s0 == null && s1 == null) return true;
        if (s0 == null) return false;
        if (s1 == null) return false;
        int i = 0;
        int l0 = s0.length(), l1 = s1.length();
        int lm = Math.min(l0, l1);
        char c0, c1;
        while (i < lm) {
            c0 = s0.charAt(i);
            c1 = s1.charAt(i);
            if (this.insensitive && c0 >= 'A' && c0 <='Z') c0 = (char) ((byte) c0 + 32);
            if (this.insensitive && c1 >= 'A' && c1 <='Z') c1 = (char) ((byte) c1 + 32);
            if (c0 != c1) return false;
            i++;
        }
        if (l0 != l1) return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public final static String String(final byte[] bytes) {
        if (bytes == null) return null;
        final char[] c = new char[bytes.length];
        for (int i = bytes.length - 1; i >= 0; i--) c[i] = (char) bytes[i];
        return new String(c);
    }

    public final static String String(final byte[] bytes, final int offset, final int length) {
        int l = Math.min(length, bytes.length - offset);
        final char[] c = new char[l];
        for (int i = 0; i < l; ++ i) {
            if (bytes[i + offset] < 0) throw new IllegalArgumentException();
            c[i] = (char) bytes[i + offset];
        }
        return new String(c);
    }

    public final static byte[] getBytes(final CharSequence s, byte next1, byte next2) {
        int count = s.length();
        final byte[] b = new byte[count+2];
        int i;
        for (i = 0; i < count; i++) {
            b[i] = (byte) s.charAt(i);
        }
        b[i++] = next1;
        b[i++] = next2;
        return b;
    }
    
    public final static byte[] getBytes(long decimalizable) {
        if (decimalizable < 10000) //or 100k, etc.. if getBytes(int) is updated
            return getBytes((int)decimalizable);
        else
            return getBytes(Long.toString(decimalizable));
    }
    
    /** calculates the bytes which would be the decimal representation of int, without involving any string */
    public final static byte[] getBytes(int decimalizable) {
        if (decimalizable < 10) {
            return new byte[] { (byte)('0' + decimalizable) };
        }
        else if (decimalizable < 100) {
            return new byte[] { (byte)('0' + (decimalizable/10)), (byte)('0' + (decimalizable % 10)) };            
        }
        else if (decimalizable < 1000) {
            int hundreds = decimalizable / 100;
            int remainder = decimalizable - hundreds;
            return new byte[] { 
                (byte)('0' + (hundreds)), 
                (byte)('0' + (remainder/10)), 
                (byte)('0' + (decimalizable % 10)) 
            };             
        }
        else if (decimalizable < 10000) {
            int thousands = decimalizable / 1000;
            int remainder = decimalizable - thousands;
            int hundreds = remainder / 100;
            remainder = decimalizable - hundreds;
            return new byte[] { 
                (byte)('0' + (thousands)), 
                (byte)('0' + (hundreds)), 
                (byte)('0' + (remainder/10)), 
                (byte)('0' + (decimalizable % 10)) 
            };             
        }        
        //TODO 100k, ...
        else {
            return getBytes(Integer.toString(decimalizable));
        }
    }
    
    public final static byte[] getBytes(final CharSequence s) {
        assert s != null;
        //assert s.length() < 3 || s.charAt(2) != '@';
        int count = s.length();
        final byte[] b = new byte[count];
        for (int i = 0; i < count; i++) {
            b[i] = (byte) s.charAt(i);
        }
        return b;
    }

    public final static byte[] getBytes(final CharSequence s, final int beginIndex, final int endIndex) {
        assert s != null;
        //assert s.length() < 3 || s.charAt(2) != '@';
        int count = endIndex - beginIndex;
        final byte[] b = new byte[count];
        for (int i = 0; i < count; i++) {
            b[i] = (byte) s.charAt(i + beginIndex);
        }
        return b;
    }
}