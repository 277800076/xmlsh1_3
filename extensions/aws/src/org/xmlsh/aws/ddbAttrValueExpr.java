package org.xmlsh.aws;

import java.util.List;

import org.xmlsh.aws.util.DDBTypes;
import org.xmlsh.aws.util.DDBTypes.AttrValueExpr;
import org.xmlsh.core.BuiltinFunctionCommand;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.Shell;

public class ddbAttrValueExpr extends BuiltinFunctionCommand {

    public ddbAttrValueExpr() {
        super("ddb-attr-value-expr");
    }

    @Override
    public XValue run(Shell shell, List<XValue> args) throws Exception {
        switch( args.size() ){
        case 0 :
            throw new InvalidArgumentException("Required arguments: " + getName() + " expr ... ");
        case 1:
            return new XValue( DDBTypes.addValuePrefix(DDBTypes.parseAttrValueExpr(args.get(0) )));
        case 2:
            return new XValue( DDBTypes.addValuePrefix(
                                    new AttrValueExpr( 
                                            args.get(0).toString() ,  DDBTypes.parseAttrValue( args.get(1) ) )));
            
        default :
            return new XValue( DDBTypes.addValuePrefix(DDBTypes.parseAttrValueExprs( args ) ));

        }

    }

}
