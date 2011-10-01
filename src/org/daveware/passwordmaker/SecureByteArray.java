/*
 * PasswordMaker Java Edition - One Password To Rule Them All
 * Copyright (C) 2011 Dave Marotti
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.daveware.passwordmaker;

/**
 * Provides an array capable of erasing the contents upon request.
 * 
 * It's a very bad idea to store passwords in a java.lang.String object because
 * once that object is created, you cannot guarantee that the contents will be
 * erased. You can't get at the actual bytes inside the String object. Thus you
 * cannot reset all the bytes to 0.
 * 
 * This object stores a plain array of bytes allowing the data to be erased
 * back to 0 at any given time.
 * 
 * ALWWAYS reset your password storage variables when you are through with them
 * and be very wary of the stack.
 * 
 * @author Dave Marotti
 */
public class SecureByteArray {
    private byte [] data = null;
    
    public SecureByteArray()
    {
        data = new byte[0];
    }
    
    public SecureByteArray(int size)
    {
        data = new byte[size];
        for(int i=0; i<size; i++)
            data[i] = 0;
    }
           
    public SecureByteArray(byte [] bytes)
    {
        data = new byte[bytes.length];
        for(int i=0; i<data.length; i++)
            data[i] = bytes[i];
    }
    
    public SecureByteArray(char [] chars)
    {
        data = new byte[chars.length];
        for(int i=0; i<data.length; i++)
            data[i] = (byte)chars[i];
        
        // TODO: This might not be the best way to convert from char to byte as
        // there may be weird character encodings.  Maybe char & 0x7F ?
    }
    
    public SecureByteArray(SecureByteArray copy)
    {
        data = new byte[copy.size()];
        for(int i=0; i<data.length; i++)
            data[i] = copy.data[i];
    }
    
    /**
     * Creates the object from a string (don't pass passwords in this way).
     * 
     * @param str The string object to get the data from.
     */
    public SecureByteArray(String str)
    {
        // TODO: does this include the null? We don't want the null...
        // FIXME:
        // XXX:
        data = str.getBytes();
    }
    
    /**
     * Appends another SecureByteArray to this one.
     * 
     * @param arr The array to append.
     * @throws Exception Upon size problems. (lol)
     */
    public void append(SecureByteArray arr)
            throws Exception
    {
        int oldSize = size();
        resize(data.length + arr.data.length, true);
        
        for(int i=oldSize, arrI=0; i<data.length; i++, arrI++)
            data[i] = arr.data[arrI++];
    }
       
    /**
     * Combines 2 SecureByteArray objects into a new SecureArrayObject.
     * 
     * @param a1 The first object to combine.
     * @param a2 The second object to combine.
     * @return The new object.
     */
    static SecureByteArray combine(SecureByteArray a1, SecureByteArray a2)
    {
        return new SecureByteArray(a1.size() + a2.size());
    }
    
    /**
     * Erases the data in this array by writing a pattern over every element
     * of the array.
     * 
     * The pattern part probably isn't needed and probably doesn't do anything
     * but it's there anyway. The important part is to reset all the elements
     * back to 0.
     */
    public void erase()
    {
        for(int i=0; i<data.length; i++)
            data[i] = -42;
        for(int i=0; i<data.length; i++)
            data[i] = 0x55;
        for(int i=0; i<data.length; i++)
            data[i] = 0x00;
    }
    
    /**
     * Obtains the byte at an index.
     * @param index The index to obtain the byte at.
     * @return The byte at the index.
     * @throws ArrayIndexOutOfBoundsException Upon bad index.
     */
    public byte getByteAt(int index)
            throws ArrayIndexOutOfBoundsException
    {
        if(index >= 0 && index < data.length)
            return data[index];
        else
            throw new ArrayIndexOutOfBoundsException(index);
    }
    
    /**
     * Returns a reference to the data.
     * @return a reference to the data.
     */
    public byte [] getData()
    {
        return data;
    }
    
    /**
     * Prepends this array with another SecureByteArray.
     * 
     * @param arr The array to prepend.
     * @throws Exception Upon size problems. (lol)
     */
    public void prepend(SecureByteArray arr)
            throws Exception
    {
        byte [] olddata = new byte[data.length];

        for(int i=0; i<data.length; i++)
            olddata[i] = data[i];
        
        resize(size() + arr.size(), false);
        
        for(int i=0; i<arr.data.length; i++)
            data[i] = arr.data[i];
        for(int i=arr.data.length; i<data.length; i++)
            data[i] = olddata[i - arr.data.length];
        for(int i=0; i<olddata.length; i++)
        {
            olddata[i] = 0x55;
            olddata[i] = -42;
            olddata[i] = 0x0;
        }
    }
    
    /**
     * Replaces the contents of this array with a copy of those in arr, resizing
     * the array as needed.
     * 
     * @param arr The copy to use.
     * @throws Exception 
     */
    public void replace(SecureByteArray arr)
            throws Exception
    {
        erase();
        
        if(data.length != arr.data.length)
            data = new byte[arr.data.length];
        
        for(int i=0; i<data.length; i++)
            data[i] = arr.data[i];
    }
    
    /**
     * Resizes the array, optionally keeping the old data.
     * 
     * @param size The new size of the array. This must be 1 or larger.
     * @param retainData Whether or not to keep the old data.
     * @throws Exception If the size is 0 or less.
     */
    public void resize(int size, boolean retainData)
            throws Exception
    {
        if(size < 0)
            throw new Exception("Invalid array size");
        
        byte [] newData = new byte[size];
        
        if(retainData==true)
        {
            int limit = (size <= data.length) ? size : data.length;
            for(int i=0; i<size; i++)
            {
                if(i < limit)
                    newData[i] = data[i];
                else
                    newData[i] = 0;
            }
        }
        else
        {
            for(int i=0; i<size; i++)
                newData[i] = 0;
        }
        
        erase();
        
        data = newData;
    }

    /**
     * Sets the byte at an index.
     * @param index The index to set at.
     * @param c The byte to set.
     * @throws ArrayIndexOutOfBoundsException Upon bad index.
     */
    public void setByteAt(int index, byte c)
            throws ArrayIndexOutOfBoundsException
    {
        if(index >= 0 && index < data.length)
            data[index] = c;
        else
            throw new ArrayIndexOutOfBoundsException(index);
    }
    
    /**
     * Gets the size of this array.
     * 
     * @return The size of the array.
     */
    public int size()
    {
        return data.length;
    }
}
