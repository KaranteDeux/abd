package abd.ra.phys;

import java.io.IOException;
import java.util.Arrays;

import abd.phys.Page;

public class DefaultPagesCartesianProduct implements PhysicalOperator {

	Page page1;
	Page page2;
	
	byte[] record1;
	
	
	
	
	public DefaultPagesCartesianProduct(Page page1, Page page2, int integer){
		this.page1 = page1;
		this.page2 = page2;
		amorceTable1();
	}
	
	
	@Override
	public byte[] nextRecord() throws IOException {
		
		
		
		byte[] record2 = page2.getNextRecord();
		
		if(record2 == null){
			/* Si on est à la fin de la page 2, on change le record de la page1 */
			record1 = page1.getNextRecord();
			
			if(record1 == null){
				/* Si on est à la fin de la page 1, le produit cartésien entre ces 2 pages est fini. On return donc null */
				return null;
			}
			
			/* Si on est pas à la fin de la page 1, on revient au début de la page 2 */
			page2.resetPosition();
			record2 = page2.getNextRecord();
			
		}
		
		return merge(record1, record2);
		
	}
	
	public void amorceTable1(){
		record1 = this.page1.getNextRecord();

	}
	
	private byte[] merge(byte[] record1, byte[] record2){
		byte[] result = new byte[record1.length + record2.length];
		
		int cpt = 0;
		
		for(int i=0;i<record1.length;i++){
			result[cpt] = record1[i];
			cpt++;
		}
		
		for(int i=0;i<record2.length;i++){
			result[cpt] = record2[i];
			cpt++;
		}
		
		return result;
	}

	@Override
	public void close() throws IOException {

	}
	
	public void setPage1(Page page1){
		this.page1 = page1;
	}

	
	public void setPage2(Page page2){
		this.page2 = page2;
	}
}
