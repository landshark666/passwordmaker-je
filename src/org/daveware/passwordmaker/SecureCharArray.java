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
 * This object stores a plain array of chars allowing the data to be erased
 * back to 0 at any given time.
 * 
 * ALWWAYS reset your password storage variables when you are through with them
 * and be very wary of the stack.
 * 
 * @author Dave Marotti
 */
public class SecureCharArray {
    private char [] data = null;
    
    public SecureCharArray()
    {
        data = new char[0];
    }
    
    public SecureCharArray(int size)
    {
        data = new char[size];
        for(int i=0; i<size; i++)
            data[i] = 0;
    }
            
    public SecureCharArray(byte [] bytes)
    {
        data = new char[bytes.length];
        for(int i=0; i<data.length; i++)
            data[i] = (char)(bytes[i] & 0xFF);
    }
    
    public SecureCharArray(char [] chars)
    {
        data = new char[chars.length];
        for(int i=0; i<data.length; i++)
            data[i] = chars[i];
    }
    
    public SecureCharArray(SecureCharArray copy)
    {
        data = new char[copy.size()];
        for(int i=0; i<data.length; i++)
            data[i] = copy.data[i];
    }
    
    /**
     * Creates the object from a string (don't pass passwords in this way).
     * 
     * @param str The string object to get the data from.
     */
    public SecureCharArray(String str)
    {
        // TODO: does this include the null? We don't want the null...
        // FIXME:
        // XXX:
        data = str.toCharArray();
    }
    
    /**
     * Appends another SecureCharArray to this one.
     * 
     * @param arr The array to append.
     * @throws Exception Upon size problems. (lol)
     */
    public void append(SecureCharArray arr)
            throws Exception
    {
        int oldSize = size();
        resize(size() + arr.size(), true);
        
        for(int i=oldSize, arrI=0; i<size(); i++, arrI++)
            data[i] = arr.data[arrI];
    }
       
    /**
     * Combines 2 SecureCharArray objects into a new SecureArrayObject.
     * 
     * @param a1 The first object to combine.
     * @param a2 The second object to combine.
     * @return The new object.
     */
    static SecureCharArray combine(SecureCharArray a1, SecureCharArray a2)
    {
        return new SecureCharArray(a1.size() + a2.size());
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
            data[i] = 0xAA;
        for(int i=0; i<data.length; i++)
            data[i] = 0x55;
        for(int i=0; i<data.length; i++)
            data[i] = 0x00;
    }
    
    /**
     * Obtains the character at an index.
     * @param index The index to obtain the character at.
     * @return The character at the index.
     * @throws ArrayIndexOutOfBoundsException Upon bad index.
     */
    public char getCharAt(int index)
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
    public char [] getData()
    {
        return data;
    }
    
    /**
     * Prepends this array with another SecureCharArray.
     * 
     * @param arr The array to prepend.
     * @throws Exception Upon size problems. (lol)
     */
    public void prepend(SecureCharArray arr)
            throws Exception
    {
        char [] olddata = new char[data.length];

        for(int i=0; i<olddata.length; i++)
            olddata[i] = data[i];
        
        resize(size() + arr.size(), false);
        
        for(int i=0; i<arr.data.length; i++)
            data[i] = arr.data[i];
        for(int i=arr.data.length; i<data.length; i++)
            data[i] = olddata[i - arr.data.length];
        for(int i=0; i<olddata.length; i++)
        {
            olddata[i] = 0x55;
            olddata[i] = 0xAA;
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
    public void replace(SecureCharArray arr)
            throws Exception
    {
        erase();
        
        if(data.length != arr.data.length)
            data = new char[arr.data.length];
        
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
        
        char [] newData = new char[size];
        
        if(retainData==true)
        {
            int limit = (size <= data.length) ? size : data.length;
            for(int i=0; i<limit; i++)
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
     * Sets the character at an index.
     * @param index The index to set at.
     * @param c The character to set.
     * @throws ArrayIndexOutOfBoundsException Upon bad index.
     */
    public void setCharAt(int index, char c)
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
