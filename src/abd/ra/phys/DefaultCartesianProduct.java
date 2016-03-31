package abd.ra.phys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import abd.DBTableImpl;
import abd.SGBD;
import abd.phys.DefaultPageSequentialAccess;
import abd.phys.Page;

public class DefaultCartesianProduct implements PhysicalOperator {

	private String table1_foldername;
	private String table2_foldername;

	private String[] pagesTab1;
	private String[] pagesTab2;

	private String currentPage1_filename;
	private String currentPage2_filename;


	private DefaultPagesCartesianProduct dcpp;


	public DefaultCartesianProduct(String table1_foldername, String table2_foldername, int integer) throws IOException{
		
		this.table1_foldername = table1_foldername;
		this.table2_foldername = table2_foldername;

		File table_folder_1 = new File(SGBD.DATA_FOLDER + "/" + table1_foldername);
		pagesTab1 = table_folder_1.list();
		Arrays.sort(pagesTab1);

		File table_folder_2 = new File(SGBD.DATA_FOLDER + "/" + table2_foldername);
		pagesTab2 = table_folder_2.list();
		Arrays.sort(pagesTab2);

		currentPage1_filename = pagesTab1[0];
		currentPage2_filename = pagesTab2[0];
		
		
		/* On charge les 2 premières pages et on les passe en paramètre du dcpp */
		Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);
		Page pageTable1 = loadPage(table1_foldername, currentPage1_filename);
		
		dcpp = new DefaultPagesCartesianProduct(pageTable1, pageTable2, 0);

	}

	@Override
	public byte[] nextRecord() throws IOException {
		//Page page1Current = new DefaultP

		byte []record = dcpp.nextRecord();
		
		if(record!= null)
			return record;

		/* Il faut changer de page :
		 * 	On regarde si la page courante de la table 2 est la dernière
		 *		- Si non, on prend la page suivante de la table 2, et la page de la table 1 reste inchangée
		 *		- Si oui et la page courante de la table 1 n'est pas la dernière, on reprend la première page de la table 2, et on prend la page suivante de la table 1
		 *		- Si oui et la page courante de la table 1 est la dernière, alors on return null parce qu'on est à la fin du produit cartésien
		 */
		
		
		if(!pagesTab2[pagesTab2.length - 1].equals(currentPage2_filename)){
			/* On change la page 2 */
			currentPage2_filename = pagesTab2[getPosOfElementIntoArray(currentPage2_filename, pagesTab2) +1];
			
			Page pageTable1 = loadPage(table1_foldername, currentPage1_filename);
			Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);

			dcpp.setPage1(pageTable1);
			dcpp.setPage2(pageTable2);
			
			dcpp.amorceTable1();
			

		} else if(!pagesTab1[pagesTab1.length - 1].equals(currentPage1_filename)){
			currentPage2_filename = pagesTab2[0];
			currentPage1_filename = pagesTab1[getPosOfElementIntoArray(currentPage1_filename, pagesTab1) +1];

			Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);

			dcpp.setPage2(pageTable2);

			Page pageTable1 = loadPage(table1_foldername, currentPage1_filename);

			dcpp.setPage1(pageTable1);
			dcpp.amorceTable1();

			

		} else
			return null;
		
		
		byte[] tuple = null;
		
		try {
			 tuple = dcpp.nextRecord();
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return tuple;

		


	}

	private int getPosOfElementIntoArray(String element, String[] array){
		for(int i=0;i<array.length;i++){
			String elementArray = array[i];
			if(element.equals(elementArray)){
				return i;
			}
		}
		return -1;
	}


	/* Charge la page de nom 'page_filename' de la table 'table_foldername' */
	private Page loadPage(String table_foldername, String page_filename) throws IOException{

		FileInputStream streamPage1 = new FileInputStream(new File(SGBD.DATA_FOLDER + "/" + table_foldername + "/" + page_filename));
		ByteBuffer buffer = ByteBuffer.allocate(DBTableImpl.PAGE_SIZE);

		byte b[] = buffer.array();
		streamPage1.read(b);

		streamPage1.close();
		
		int tuple_length = SGBD.getRelation(table_foldername).getTableDescription().getTupleLength();
		
		return new DefaultPageSequentialAccess(buffer, tuple_length);
	}

	@Override
	public void close() throws IOException {

	}

}
