package org.xmlsh.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.saxon.s9api.SaxonApiException;
import org.xmlsh.aws.util.AWSEC2Command;
import org.xmlsh.aws.util.SafeXMLStreamWriter;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;
import org.xmlsh.util.StringPair;
import org.xmlsh.util.Util;

import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.EbsBlockDevice;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesResult;


public class ec2CreateImage extends AWSEC2Command {

	




	/**
	 * @param args
	 * @throws IOException 
	 * 
	 
	 */
	@Override
	public int run(List<XValue> args) throws Exception {

		
		Options opts = getOptions("name:,description:,no-reboot");
		opts.parse(args);

		args = opts.getRemainingArgs();
		

		
		
		
		if( args.size() != 1 ){
			usage(null);
			return 1;
		}
		

		mSerializeOpts = this.getSerializeOpts(opts);
		try {
			mAmazon = getEC2Client(opts);
		} catch (UnexpectedException e) {
			usage( e.getLocalizedMessage() );
			return 1;
			
		}
		
		int ret = createImage( args.get(0).toString(), opts.getOptStringRequired("name"),opts );
		
		
		
		
		
		return ret;	
	}

	private int createImage( String ami_id, String name , Options opts ) throws InvalidArgumentException, IOException, XMLStreamException, SaxonApiException 
	{

		
		CreateImageRequest request = new CreateImageRequest( ami_id , name );
		if( opts.hasOpt("description"))
		 request.setDescription(opts.getOptStringRequired("description"));
		
		if( opts.hasOpt("no-reboot"))
			request.setNoReboot(true);
		
		
	
		CreateImageResult  result = mAmazon.createImage(request);

		writeResult( result );
		
		
		return 0;
	}

	private void writeResult(CreateImageResult result) throws IOException, InvalidArgumentException, XMLStreamException, SaxonApiException {
		OutputPort stdout = this.getStdout();
		mWriter = new SafeXMLStreamWriter(stdout.asXMLStreamWriter(mSerializeOpts));
		
		
		startDocument();
		startElement(this.getName());
		
		startElement("image");
		attribute( "image_id",result.getImageId());
		endElement();
		
		endElement();
		endDocument();
		closeWriter();
		
		stdout.writeSequenceTerminator(mSerializeOpts);
		stdout.release();
		
		
	}

	
	

}