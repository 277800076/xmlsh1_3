/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.commands.builtin;

import java.util.List;

import net.sf.saxon.s9api.XdmItem;
import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.IXdmItemInputStream;
import org.xmlsh.core.InputPort;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.StreamInputPort;
import org.xmlsh.core.XValue;

public class xread extends BuiltinCommand {

	/*
	 *  Read a line of text from stdin and assign to variables
	 *  
	 */


	public int run( List<XValue> args ) throws Exception {

		mSerializeOpts = getSerializeOpts();
		boolean bParse = false ;
		if( args.size() > 0 && args.get(0).toString().equals("-parse") ){
			
		    args.remove(0);
			bParse = true ;
		}
		if( args.size() != 1 )
			throw new InvalidArgumentException("requires 1 argument");
		
		mShell.getEnv().unsetVar(args.get(0).toString());

		InputPort stdin = mShell.getEnv().getStdin();
		XdmItem item = null ;
		if( bParse ) {
			StreamInputPort ip = new StreamInputPort( stdin.asInputStream(mSerializeOpts) , stdin.getSystemId() );
			
			item = ip.asXdmItem(mSerializeOpts);
			ip.release();
		} else {
			IXdmItemInputStream is = stdin.asXdmItemInputStream(mSerializeOpts);
			item = is.read();
		}
		if( item != null )
			mShell.getEnv().setVar(args.get(0).toString(), new XValue(item),false);
		stdin.release();

		return item == null ? 1 : 0 ;
	}
}


//Copyright (C) 2008-2014    David A. Lee.

//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 

//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.

//The Original Code is: all this file.

//The Initial Developer of the Original Code is David A. Lee

//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.

//Contributor(s): none.

