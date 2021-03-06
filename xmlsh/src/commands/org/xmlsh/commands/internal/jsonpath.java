/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonModel;
import com.jayway.jsonpath.JsonPath;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.IXdmItemOutputStream;
import org.xmlsh.core.InputPort;
import org.xmlsh.core.Namespaces;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.Options.OptionValue;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.Util;

public class jsonpath extends XCommand {


	@Override
	public int run(List<XValue> args) throws Exception {
		
		Options opts = new Options("c=context:,cf=context-file:,f=file:,i=input:,q=query:,n,e=exists,b=bool,s=string", SerializeOpts.getOptionDefs());
		opts.parse(args);

		
		boolean bString = 	opts.hasOpt("s");
		

		JsonModel context = null;

		
		
		InputPort in = null;

		// boolean bReadStdin = false ;
		
		SerializeOpts serializeOpts = getSerializeOpts(opts);
		if( ! opts.hasOpt("n" ) ){ // Has XML data input
			// Order of prevelence 
			// -context
			// -context-file
			// -i
			
			if( opts.hasOpt("c") )
				context = opts.getOptValue("c").asJson();
			else
			if( opts.hasOpt("cf"))
				context = (in=getInput( new XValue(opts.getOptString("cf", "-")))).asJson(serializeOpts);
			else
			if( opts.hasOpt("i") )
				context = (in=getInput( opts.getOptValue("i"))).asJson(serializeOpts);
			else
				context = (in=getStdin()).asJson(serializeOpts);
			
		}

		List<XValue> xvargs = opts.getRemainingArgs();

		boolean bQuiet = opts.hasOpt("e");
		boolean bBool = opts.hasOpt("b");
		if (bBool)
			bQuiet = true;

		OptionValue ov = opts.getOpt("f");
		String xpath = null;
		if (ov != null)
			xpath = readString(ov.getValue(),serializeOpts);
		else {
			ov = opts.getOpt("q");
			if (ov != null)
				xpath = ov.getValue().toString();
		}

		if (xpath == null)
			xpath = xvargs.remove(0).toString();


		JsonPath path = JsonPath.compile(xpath);
		Object result = context.get(path);
		XValue xvr = new XValue(result);
		
		// Hack - cant get at JsonProvider
		if( result instanceof List || result instanceof Map )
			result = JsonModel.create(result);

	    OutputPort stdout = getStdout();
	    PrintStream os = stdout.asPrintStream(getSerializeOpts());
	    os.println( xvr.toString() );
	    
		
		return 0;
	}
	
	private String readString(XValue v, SerializeOpts opts) throws CoreException, IOException  {
		
		InputPort in = getInput( v );
		InputStream is = in.asInputStream(opts);
		
		String s = Util.readString(is,opts.getInputTextEncoding());
		is.close();
		in.close();
		return s ;
	}
}



/*
 * Copyright (C) 2008-2014   David A. Lee.
 * 
 * The contents of this file are subject to the "Simplified BSD License" (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.opensource.org/licenses/bsd-license.php 

 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is David A. Lee
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): David A. Lee
 * 
 */