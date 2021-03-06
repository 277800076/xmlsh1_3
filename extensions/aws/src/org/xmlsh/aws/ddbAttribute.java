package org.xmlsh.aws;

import java.util.List;

import org.xmlsh.aws.util.DDBTypes;
import org.xmlsh.core.BuiltinFunctionCommand;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.Shell;

public class ddbAttribute extends BuiltinFunctionCommand {

    public ddbAttribute() {
        super("ddb-attribute");
    }

    @Override 
    // Attribute
    // name/type Value
    // name type value
    public XValue run(Shell shell, List<XValue> args) throws Exception {
        switch( args.size() ){
  
        case 1:
            return new XValue( DDBTypes.parseAttrNameValue( args.get(0) ));
        case 2:
            return new XValue( DDBTypes.parseAttrNameValue( args.get(0) , args.get(1) ));
        case 3:
            return new XValue( DDBTypes.parseAttrNameValue( args.get(0) , args.get(1), args.get(2) ));
        default :
        case 0:
            throw new InvalidArgumentException("Unexpected arguments: Usage: " + getName() +  " [type [value]" );
        }

    }


}
