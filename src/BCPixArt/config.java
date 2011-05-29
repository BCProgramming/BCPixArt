
/**
 * Copyright (c) 2011, Michael Burgwin
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the 
following conditions are met:
-Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
-Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package BCPixArt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.awt.Color;
import java.awt.Image;
import java.io.*;

import com.avaje.ebean.enhance.asm.Type;
public class config implements java.io.Serializable {

  
    
	private final PixArtPlugin plugin;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1175864418412114207L;

	//protected HashTable<Object,String> configdata=new HashMap<Object,String>();
	private Properties p;
	
	
	public Boolean debug;
	public float XScale,YScale;
	public String mappingfile="";
	public long blocksleep=500;
	public Hashtable<String,String> preMappedImages;
	
	public String getmappedImage(String strparam)
	{
		Hashtable<String,String> enumerateit = getMappedImages();
		
		if(enumerateit.containsKey(strparam))
			return (String)enumerateit.get(strparam);
		else
			return strparam;
		
		
	}
	public Hashtable<String,String> getMappedImages() 
	{
		
		
		
		if(preMappedImages==null)
		{
			preMappedImages = new Hashtable<String,String>();
			//attempt to open the mapping file.
			//use another Property object...
			if(new File(mappingfile).exists())
			{
				Properties mappedimages = new Properties();
				try {
				mappedimages.load(new FileInputStream(new File(mappingfile)));
				}
				catch(IOException err)
				{
					//shouldn't happen...
					
				}
				Enumeration<Object> enumkeys = mappedimages.keys();
				//iterate through ALL names...
				while(enumkeys.hasMoreElements())
				{
					String gotelement = (String)enumkeys.nextElement();
					//add a new item to the hashtable...
					//preMappedImages.put(//key, value)
					preMappedImages.put(gotelement,mappedimages.getProperty(gotelement));
					PixArtCommand.debugmessage("mapped element:" + gotelement + " = " + preMappedImages.get(gotelement));
					
				}
				
				
				
			}	
			
		}
		
		return preMappedImages;
		
	}
	
	/**
	 * 
	 * @param <T> Type of data to retrieve (string, integer, etc)
	 * @param name Name of the property to read
	 * @return
	 */
	public <T> T getValue(String name) 
	{
		PixArtCommand.debugmessage("Config.getValue, name=" + name);
		try  {
		return (T)p.get(name);
		}
		catch(ClassCastException e)
		{
			System.out.println("[BCPixArt] ClassCastException in config.java");
			return null;
		
		}
	
	}
	
	public String getString(String entryname)
	{
	return this.getValue(entryname);	
		
	
	}
	public Boolean getBoolean(String entryname)
	{
		String boolstr = getValue(entryname);
		return Boolean.parseBoolean(entryname);
		
		
		
	}
	
	public int getInt(String entryname)
	{
		return getInt(entryname,0);
		
		
	}
	public int getInt(String entryname,int defaultvalue)
	{
		try {
		String intstr = getValue(entryname);
		return Integer.parseInt(entryname);
		}
		catch(NumberFormatException e)
		{
			return defaultvalue;
		}
		
		
	}
	public long getLong(String entryname)
	{
		return getLong(entryname,0);
	}
	public long getLong(String entryname,long defaultvalue)
	{
		try {
			String longstr = getValue(entryname);
			return Long.parseLong(entryname);
			}
			catch(NumberFormatException e)
			{
				return defaultvalue;
			}
		
	}
	public float getFloat(String entryname)
	{
		return getFloat(entryname,0);
		
		
		
		
	}
	public float getFloat(String entryname,float defaultvalue)
	{
		String floatstr = getValue(entryname);
		try {
		return Float.parseFloat(floatstr);
		}
		catch(NumberFormatException e)
		{
			return defaultvalue;
			
		}
		
	}
	public Color getColor(String entryname,Color defaultvalue)
	{
		String colorstr = getValue(entryname);
		return Color.decode(colorstr);
			
		
		
		
	}
	public Color[] getColorList(String entryname)
	{
		String[] strlist = getList(entryname);
		Color[] returncolors = new Color[strlist.length-1];
		for(int i=0;i<strlist.length;i++)
		{
			returncolors[i] = Color.decode(strlist[i]);
			
			
		}
		return returncolors;
		
	}
	public String[] getList(String entryname)
	{
		final String splitregex=",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
		String listtext = p.getProperty(entryname);
		String strresults[];
		//List is result after split...
		strresults = listtext.split(splitregex); //regex used to ignore commas inside quotes...
		
		//also, because of the aforementioned quote thing, remove leading and trailing quotes from the array.
		for(String loopstring : strresults)
		{
			if(loopstring.startsWith("\"") && loopstring.endsWith("\""))
			{
				//remove the quotes...
				loopstring = loopstring.substring(1,loopstring.length()-2);
			}
			
		}
		
		
		return strresults;
		
		
	}
	
	
	public <T> T getValue(String name, T defaultvalue)
	{
		T returnthis = getValue(name);
		if(returnthis==null) return defaultvalue; else return returnthis;
		
	
	}
	public static final String defmapfile = PixArtPlugin.pluginMainDir + "/imgmappings.txt";
	public config(Properties props, PixArtPlugin pluginfill)
	{
		p=props;
		plugin=pluginfill;
		
		debug=getBoolean("debug");
		XScale=getFloat("scaleX",1);
		YScale=getFloat("scaleY",1);
		mappingfile=getValue("mappingfile",defmapfile);
	}
	public static void recreatemappingfile()
	{
		
		//recreate defmapfile...
		
		File mapfile = new File(defmapfile);
		if(mapfile.exists())
		{
			//create the path if not existent...
			new File(new File(PixArtPlugin.pluginMainDir).getPath()).mkdirs();
			
		}
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(defmapfile)));
			out.write("#\r\n");
			out.write("#BCPixArt url/path mapping file");
			out.write("#Mappings can be used to make it easier for players to create pixel art\r\n");
			out.write("#because instead of a full URL they can enter a short name\r\n");
			out.write("#name=value, where name is the short name to use, and value is the full URL or path name to the file to\r\n");
			out.write("#load when that short name is entered.");
			
			
		}
catch(IOException e)
{
	PixArtCommand.debugmessage("[BCPixArt] Exception while creating mapping file-" + e.getMessage());
	
}
		
		
	}
	public static void recreateconfig()
	{
		//make sure the directory exists...
		File configfile= new File(PixArtPlugin.pluginConfigLocation);
		if(!configfile.exists())
		{
			new File(PixArtPlugin.pluginMainDir).mkdirs();
			
			
		}
		
	    	try{
	    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PixArtPlugin.pluginConfigLocation)));
	    		out.write("#\r\n");
	    		out.write("#BCPixArt Configuration\r\n");
	    		out.write("#\r\n");
	    		out.write("#debug messages... probably best to leave this disabled....\r\n");
	    		out.write("debug=true\r\n");
	    		out.write("#scaleX and scaleY, default scaling values, negative numbers for either will mean that the other positive coordinate\r\n");
	    		out.write("#will be used to determine the size for the negative one to preserve aspect ratio.\r\n");
	    		out.write("scaleX=1.0\r\n");
	    		out.write("scaleY=1.0\r\n");
	    		out.write("#Threading: When enabled, building of the pixelart will be performed in a separate thread\r\n");
	    		out.write("#Threading=true\r\n");
	    		out.write("#if disabled, the main thread will be used. Note that if threading is disabled, trying to build pixel art from");
	    		out.write("#mappingfile: text file containing list of name value pairs to allow shorter entry to the /pixart build command\r\n");
	    		out.write("#if not found, a new file will be created.");
	    		out.write("mappingfile=" + defmapfile + "\r\n");
	    		out.write("#blocksleep: number of milliseconds to sleep between the creation of each block. \r\n");
	    		out.write("#setting this too low can overload the server; too high and it will take a long time to create the art piece.\r\n")
	    		out.write("blocksleep=500");
	    				
	    				
	    		
	    		out.close();
	    	} catch (Exception e) {
	    		PixArtCommand.debugmessage("[BCPixArt] Exception while creating config file-" + e.getMessage());
	    	}
		
		
	}
	
	
	
}
