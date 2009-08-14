package de.cismet.tools;
import java.io.*;

import java.util.*;



public class TextFromFile
{
    private final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private byte[] data;
    
    /////////////////////////////////////////////
    
    public TextFromFile(String filepath)
    {
        
        try
        {
            File inFile;
            InputStream stream;
            
            inFile = new File(filepath);
            
            data = new byte[(int) inFile.length()];
            stream = new FileInputStream(inFile);
            
            //read the file into data
            int bytesRead = stream.read(data,0,(int) inFile.length());
            
            if (bytesRead == -1) // error occured during readingprocess
                throw new Exception("read fehlgeschlagen");
            else if (bytesRead != (int) inFile.length())
                throw new Exception("Information wahrscheinlich Fehlerhaft");
            
            stream.close();
        }
        catch(Exception e)
        {
            logger.error(e);
            data= new byte[0];
        }
        
        
    }// end constructor
    
    ///////////////////////////////////////////
    
    public TextFromFile()
    {data = new byte[0];}
    
    
    public  String getText()
    {
        return new String(data);
    }
    
    
    public Vector getWordVector()
    {
        Vector words = new Vector(10,10);
        
        
        
        String txt = getText();
        
        StringTokenizer split = new StringTokenizer(txt,",");
        
        while(split.hasMoreTokens())
        {
            String word = split.nextToken();
            
            words.add(word);
            
        }
        
        return words;
    }
    
    
    //---------------------------------------------------------------
}