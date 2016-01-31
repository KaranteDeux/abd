import java.nio.file.Paths;

import abd.Factory;
import abd.tp1.FileTable;


public class Test {


	private static final String TMP_DIR = System.getProperty("java.io.tmpdir")+"/";

	private static final String TABLE_FILE = TMP_DIR + "table_file";
	private static final String RESULT_FILE = TMP_DIR + "result_file";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 5);

			System.out.println(5 == table.getArity());
		} catch(Exception e){

		}

	}

}
