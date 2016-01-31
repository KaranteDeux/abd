package tp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import abd.Factory;
import abd.tp1.FileTable;
import abd.tp1.TuplesIterator;

public class ExampleQuery {
	
	public static void main(String[] args) throws IOException {
		Path inputPath = Paths.get("/tmp/input_table");
		
		if (Files.exists(inputPath)) {
			Files.delete(inputPath);
		}
		// La table d'entrée, la table de résultat, et quelques tables pour des résultats intermédiaires
		FileTable table = null, result1 = null, result2 = null, result = null; 
		
		try {
			table = Factory.createFileTable(inputPath, 3);
			chargerFilms(table);
			
			// --------------- Complétez ici ---------------- 
			// Construire la table result qui contient les titres des films de Martin Scorsese 
			// sortis dans les années 1990 (de 1990 à 1999)
			
			
			
			
			
			
			
			
			// Afficher ces titres de films.
			// Vous devriez obtenir 9 films
			TuplesIterator it = result.tuplesIterator();
			while (it.hasNext()) {
				System.out.println(Arrays.toString(it.next()));
			}
			
			
		} finally {
			if (table != null) Factory.destroyFileTable(table);
			if (result1 != null) Factory.destroyFileTable(result1);
			if (result2 != null) Factory.destroyFileTable(result2);
			if (result != null) Factory.destroyFileTable(result);
		}
		
		
	}
	
	public static void chargerFilms (FileTable table) throws IOException {
		
		Path path = Paths.get("test-resources-fourni/films.csv");
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset())) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tuple = line.split(";");
				table.addTuple(tuple);
			}
		}
		
		
	}

}
