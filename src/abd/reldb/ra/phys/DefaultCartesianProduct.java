package abd.reldb.ra.phys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import reldb.DBTableImpl;
import tp2.DefaultPageSequentialAccess;
import tp2.TuplesIteratorImpl;
import abd.tp1.TuplesIterator;
import abd.tp2.Page;

public class DefaultCartesianProduct implements PhysicalOperator {

	private String table1_foldername;
	private String table2_foldername;

	private String[] pagesTab1;
	private String[] pagesTab2;

	private String currentPage1_filename;
	private String currentPage2_filename;


	private DefaultCartesianProductPages dcpp;


	public DefaultCartesianProduct(String table1_foldername, String table2_foldername) throws IOException{
		this.table1_foldername = table1_foldername;
		this.table2_foldername = table2_foldername;

		File table_folder_1 = new File(table1_foldername);
		pagesTab1 = table_folder_1.list();

		File table_folder_2 = new File(table2_foldername);
		pagesTab2 = table_folder_2.list();

		currentPage1_filename = pagesTab1[0];
		currentPage2_filename = pagesTab2[0];

		
		/* On charge les 2 premières pages et on les passe en paramètre du dcpp */
		Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);
		Page pageTable1 = loadPage(table1_foldername, currentPage1_filename);

		dcpp = new DefaultCartesianProductPages(pageTable1, pageTable2);

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
			Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);

			dcpp.setPage2(pageTable2);

		} else if(!pagesTab1[pagesTab1.length - 1].equals(currentPage1_filename)){
			currentPage2_filename = pagesTab2[0];
			currentPage1_filename = pagesTab1[getPosOfElementIntoArray(currentPage1_filename, pagesTab1) +1];

			Page pageTable2 = loadPage(table2_foldername, currentPage2_filename);

			dcpp.setPage2(pageTable2);

			Page pageTable1 = loadPage(table1_foldername, currentPage1_filename);

			dcpp.setPage1(pageTable1);


		} else
			return null;

		return dcpp.nextRecord();





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

		FileInputStream streamPage1 = new FileInputStream(new File(table_foldername + "/" + page_filename));
		ByteBuffer buffer = ByteBuffer.allocate(DBTableImpl.PAGE_SIZE);

		byte b[] = buffer.array();
		streamPage1.read(b);

		streamPage1.close();

		return new DefaultPageSequentialAccess(buffer, DBTableImpl.PAGE_SIZE);
	}

	@Override
	public void close() throws IOException {

	}

}
