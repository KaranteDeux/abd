package abd.ra.phys;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import abd.schemas.TableDescription;

public class DefaultRecordFilter extends AbstractRecordFilter implements RecordFilter{

	public DefaultRecordFilter(TableDescription inputDescription, TableDescription outputDescription) {
		super(inputDescription, outputDescription);
	}

	@Override
	public byte[] filter(byte[] record) {
		int sizeTab=0;
		for(Selector selector : selectors){
			if(!selector.isSelected(record))
				return null;
		}
			
		for(Integer projector : projectors) {
			sizeTab += inputDescription.getAttributeType(projector).getLength();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(sizeTab);

		int offset = 0, cpt = 0;
		for(int i=0;i<inputDescription.getArity();i++) {
			if(projectors.contains(i)){
				int columnSize = inputDescription.getAttributeType(i).getLength();

				ByteBuffer current = ByteBuffer.wrap(record, offset, columnSize);
				byte [] byteArray = new byte[columnSize];
				current = current.get(byteArray);
				
				byteBuffer.position(cpt);
				byteBuffer.put(byteArray);
				
				cpt += inputDescription.getAttributeType(i).getLength();

			}
			
			offset += inputDescription.getAttributeType(i).getLength();
			
		}
		
		return byteBuffer.array();
	}
	
	@Override
	public void addSelector(Selector selector){
		super.addSelector(selector);
	}
	
	@Override
	public void setProjectors(int [] projectors){
		super.setProjectors(projectors);
	}
	

}
