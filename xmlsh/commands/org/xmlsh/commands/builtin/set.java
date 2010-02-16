/**
 * $Id: set.java 356 2010-01-01 15:46:31Z daldei $
 * $Date: 2010-01-01 10:46:31 -0500 (Fri, 01 Jan 2010) $
 *
 */

package org.xmlsh.commands.builtin;

import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.XEnvironment;
import org.xmlsh.core.XValue;
import org.xmlsh.core.XVariable;
import org.xmlsh.sh.shell.SerializeOpts;

public class set extends BuiltinCommand {

	static final String sDocRoot = "env";
	public int run(   List<XValue> args ) throws Exception {
		
		
		if( args == null || args.size() == 0 )	{
			printVars(  );
			return 0;
		}
		
		Options opts = new Options( "+x,+v,+xpipe,+e" , SerializeOpts.getOptionDefs()  );
		opts.parse(args);
		
		setShellOptions(opts);
		
		
		
		args = opts.getRemainingArgs();
		// Only set args here if there are any left
		// could be set +x which would clear $*
		if( args != null && (args.size() > 0  || opts.hasDashDash() ))
			mShell.setArgs(args);
		return 0;
	}

	private void setShellOptions(Options opts) throws InvalidArgumentException {
		mShell.setOptions( opts );
	}

	private void printVars() throws Exception {
		

		XEnvironment env = mShell.getEnv();
		

	    XMLStreamWriter writer = env.getStdout().asXMLStreamWriter(mShell.getSerializeOpts());

	    writer.writeStartDocument();
	    writer.writeStartElement( sDocRoot );
	    
		
		Collection<String> names = env.getVarNames();
		for( String name : names ){
			XVariable var = env.getVar(name);
			var.serialize(writer);
			
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		
		writer.close();
		
		
	}
}
//
//
//Copyright (C) 2008,2009 , David A. Lee.
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