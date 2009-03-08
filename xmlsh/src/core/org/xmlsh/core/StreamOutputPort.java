/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.TransformerHandler;

import net.sf.saxon.s9api.Destination;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.SynchronizedOutputStream;
import org.xmlsh.util.Util;

/*
 * An OutputPort represents an output sync of data, either Stream (bytes) or XML data
 * 
 */




public class StreamOutputPort extends OutputPort
{
	private static byte kNEWLINE_BYTES[] = { '\n' };
	

	private OutputStream	 mStream;


	public StreamOutputPort( OutputStream os ) 
	{
		mStream = os;
	}

	
	
	
	/*
	 * Standard input stream - created on first request
	 */
	
	public	synchronized OutputStream asOutputStream() 
	{
		return new SynchronizedOutputStream(mStream,mStream != System.out);
	}

	public synchronized void flush() throws InvalidArgumentException, IOException
	{
		if( mStream != null )
			mStream.flush();
		
	
	}
	
	
	
	public synchronized void close() throws IOException, InvalidArgumentException {
		
			

			if( mStream != null )
				mStream.close();
		
			
		
	
		
	}


	public synchronized TransformerHandler asTransformerHandler() throws TransformerConfigurationException, IllegalArgumentException, TransformerFactoryConfigurationError
	{
		return Util.getTransformerHander(asOutputStream());
	}
	
	public synchronized PrintStream asPrintStream()
	{
		return new PrintStream(asOutputStream());
	}

	public synchronized Destination asDestination() throws InvalidArgumentException
	{

		return Util.streamToDestination(asOutputStream());
	}
	

	
	

	public synchronized PrintWriter asPrintWriter() throws UnsupportedEncodingException {
		return new PrintWriter( 		
				new OutputStreamWriter(asOutputStream() , 
						Shell.getTextEncoding() ));
	}


	
	
	public synchronized void writeSequenceSeperator() throws IOException, InvalidArgumentException
	{
		
		asOutputStream().write(kNEWLINE_BYTES  );
		
		
	}

	public void writeSequenceTerminator() throws IOException {
			asOutputStream().write(kNEWLINE_BYTES  );
		
	}

}



//
//
//Copyright (C) 2008, David A. Lee.
//
//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.
//
//The Original Code is: all this file.
//
//The Initial Developer of the Original Code is David A. Lee
//
//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
//Contributor(s): none.
//