/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands.internal;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.IXdmValueOutputStream;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.Options.OptionDef;
import org.xmlsh.core.Options.OptionValue;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.StAXUtils;

public class xgetopts extends XCommand {

	private static final String kOPTION 	= "option";
	private static final String kOPTIONS 	= "options";
	private static final String kROOT 		= "xgetopts";
	private static final String kARG 		= "arg";
	private static final String kARGS 		= "args";
	private static final String kVALUE 	= "value";
	@Override
	public int run(List<XValue> args) throws Exception {
		
		Options opts = new Options("a=argindex,o=optdef:,c=command:,p=passthrough:,+s=seralize,+ps=pass-serialize,noargs,novalues",SerializeOpts.getOptionDefs());
		opts.parse(args);
		
		String command = opts.getOptString("c", getShell().getArg0());
		String optdef = opts.getOptString("o", null);
		String passthrough = opts.getOptString("p", null);
		boolean bSerialize = opts.getOptBool("s", true);
		boolean bPassSerialize = opts.getOptBool("ps", true);
		boolean bArgIndex = opts.hasOpt("a");
		
		args = opts.getRemainingArgs();
		
		// Backwards compatible - arg[0] is optdef
		if( optdef == null ){
			if( args.size() == 0 ){
				usage();
				return 1;
			}
			if( passthrough != null )
				optdef = passthrough ;
			else // backwards compatiblity take first arg as optdef
				optdef = args.remove(0).toString();
		}
		
		boolean bNoArgs = opts.hasOpt("noargs");
		boolean bNoValues = opts.hasOpt("novalues");
		
		
		
		
		
		
		Options prog_opts = new Options(optdef , bSerialize ? SerializeOpts.getOptionDefs() : null );
		List<OptionValue>  prog_optvalues = prog_opts.parse(args);
		
		SerializeOpts serializeOpts = this.getSerializeOpts(opts);
		
		
		List<XValue> remaining_args = prog_opts.getRemainingArgs();
		int	arg_index = remaining_args.isEmpty() ? 
				args.size() : args.indexOf(remaining_args.get(0));
		

		if( passthrough == null )
			writeOptions(args, opts, bNoArgs, bNoValues, prog_opts, prog_optvalues);
		
		else  
		{
			/*
			 * Passthrough only those options specified ingoring all others
			 * Use a sequence capibile output stream
			 */
			OutputPort stdout = getStdout();
			IXdmValueOutputStream out = stdout.asXdmValueOutputStream( serializeOpts );
			
			Options pass_opts = new Options( passthrough ,bPassSerialize ? SerializeOpts.getOptionDefs() : null );
			List<OptionDef> pass_optdefs = pass_opts.getOptDefs();
			
			
			for( OptionDef def : pass_optdefs ){
				OptionValue value = prog_opts.getOpt(def.name);
				if( value != null ){
					writeOption( stdout , serializeOpts , out , value );
					
				}
				
				
			}
			
			stdout.writeSequenceTerminator(serializeOpts);
			stdout.release();
		}
		
		
		return bArgIndex ? arg_index : 0 ;
	}

	private void writeOption(OutputPort stdout, SerializeOpts serializeOpts, IXdmValueOutputStream out, OptionValue value) throws CoreException, IOException {
		XdmValue argFlag = (new XValue((value.getFlag() ? "-" : "+") + value.getOptionDef().name)).asXdmValue();
		

		 if( ! value.getOptionDef().hasArgs ){
			 out.write( argFlag );
			 stdout.writeSequenceSeperator(serializeOpts);
			 return ;
		 }
		
		 for( XValue v : value.getValues() ){
			 out.write( argFlag );
			 stdout.writeSequenceSeperator(serializeOpts);
			 out.write( v.asXdmValue() );
			 stdout.writeSequenceSeperator(serializeOpts);

		 }
		
		
	}

	private void writeOptions(List<XValue> args, Options opts, boolean bNoArgs, boolean bNoValues,
			Options prog_opts, List<OptionValue> prog_optvalues) throws InvalidArgumentException,
			XMLStreamException, IOException {
		XMLStreamWriter out = getStdout().asXMLStreamWriter(getSerializeOpts(opts));

		out.writeStartDocument();
		out.writeStartElement(kROOT);
		out.writeStartElement(kOPTIONS);

		
		for( OptionValue option : prog_optvalues ){
			out.writeStartElement(kOPTION);
			out.writeAttribute("name",option.getOptionDef().name);
			
			if( option.getOptionDef().hasArgs  ){
				
				
				
				for( XValue value : option.getValues() ) {
					
					int index = args.indexOf(value);
					out.writeStartElement( kVALUE );
					
					
					
					out.writeAttribute("index", String.valueOf(index) );
					
					
					if( ! bNoValues ){
						if( value.isAtomic())
							out.writeCharacters(value.toString());
						else
							write( out , value.asXdmNode() );
						
					}
					out.writeEndElement();
					

				}
			}
			out.writeEndElement();
			
		}
		out.writeEndElement();
		
		
		if( ! bNoArgs ){
			out.writeStartElement( kARGS );
	
			
			for( XValue value : prog_opts.getRemainingArgs() ){
				out.writeStartElement(kARG);
				int index = args.indexOf(value);
				out.writeAttribute("index", String.valueOf(index) );
				
				
				if( ! bNoValues ){
			
	
					if( value.isAtomic())
						out.writeCharacters( value.toString());
					else
						write( out , value.asXdmNode() );
				}
				
				out.writeEndElement();
			}
			out.writeEndElement();
		}
		
		
		out.writeEndDocument();
		out.close();
	}

	private void write(XMLStreamWriter out, XdmNode node) throws XMLStreamException {
		
		StAXUtils.copy( node.getUnderlyingNode() , out );
		// XMLStreamUtils.copy( node.asSource() , out );

		
	}



}



//
//
//Copyright (C) 2008,2009,2010 , David A. Lee.
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
