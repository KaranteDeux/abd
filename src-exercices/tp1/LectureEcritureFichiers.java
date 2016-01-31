package tp1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LectureEcritureFichiers {

	public static void main(String[] args) throws IOException {
		
		// Récupérer l'emplacement du répertoire TMP, et l'encodage de caractères par défaut
		String TMP_DIR = System.getProperty("java.io.tmpdir")+"/";
		Charset charset = Charset.defaultCharset();
		
		// Créer un chemin de fichier à partir d'une chaîne de caractères
		Path path = Paths.get(TMP_DIR + "nom-fichier.txt");
		
		// Tester si le fichier existe, et le créer s'il n'existe pas
		if (! Files.exists(path)) {
			Files.createFile(path);
		}
		
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			// Écrire deux lignes de texte le fichier
			writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND);
			writer.write("première ligne de texte\n");
			writer.write("deuxième ligne de texte\n");
			writer.close();

			// Lire et afficher la première ligne
			reader = Files.newBufferedReader(path, charset);
			String line = reader.readLine();
			System.out.println(line);	
			reader.close();

			// Lire et afficher toutes les lignes
			reader = Files.newBufferedReader(path, charset);
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();

			// Ajouter une ligne en fin du fichier
			// Le paramètre "true" dans le constructeur de FileWriter 
			//     indique que le fichier est ouvert en mode concaténation
			writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND);
			writer.write("troisième ligne de texte\n");
			writer.close();

			// Lire et afficher toutes les lignes
			reader = Files.newBufferedReader(path, charset);
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
			
		} finally {
			// Libérer les ressources en cas d'exception
			if (reader != null) reader.close();
			if (writer != null) writer.close();
			Files.delete(path);
		}
	}
	
	
}
