package tp2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class LectureEcritureFileChannel {
	
	
	// Tous les exercices doivent être faits en utilisant la classe FileChannel pour lire/écrire dans les fichiers
	public static void main(String[] args) throws IOException {
		Path path = Paths.get(System.getProperty("java.io.tmpdir")+ "/"+ "exerciceTP2");
		
		// Effacer le fichier s'il existe
		if (Files.exists(path))
			Files.delete(path);
		
		// Créer le fichier vide
		Files.createFile(path);
		
		// 1. Écrire dans le fichier 
		// abcdefghijklmno 
		// en implémentant la fonction
		ecrire_a_o_dans_fichier(path);
		
		System.out.println("Vérifier que le fichier " + path.toAbsolutePath() + " contient bien abcdefghijklmno. Appuyez sur entrée quand vous avez fini");
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		
		// 2. Remplacer dans le fichier
		// a -> A
		// e -> E
		// i -> I
		// en utilisant la méthode write de FileChannel
		remplacer_a_e_i_dans_fichier (path);
		
		System.out.println("Vérifier que le fichier " + path.toAbsolutePath() + " contient bien AbcdEfghIjklmno. Appuyez sur entrée quand vous avez fini");
		scan.nextLine();
		
		// 3. Lire et afficher les octets qui se trouvent entre les positions 5 (inclus) et 10 (non inclus)
		// Utiliser FileChannel
		lire_et_afficher_positions_5_a_10 (path);
		
		System.out.println("Vérifier que le programme vient d'afficher fghIj");
		scan.nextLine();
		
		// 4. Ajouter en fin de fichier
		// pqrstuvwxyz
		// Utiliser FileChannel
		ajouter_p_z_fin_fichier(path);
		
		System.out.println("Vérifier que le fichier " + path.toAbsolutePath() + " contient bien AbcdEfghIjklmnopqrstuvwxyz");
		
		scan.close();
	}


	static void ecrire_a_o_dans_fichier (Path fichier) {
		// TODO
	}
	
	static void remplacer_a_e_i_dans_fichier(Path path) {
		// TODO
	}
	
	static void lire_et_afficher_positions_5_a_10(Path path) {
		// TODO
	}
	
	static void ajouter_p_z_fin_fichier (Path path) {
		// TODO
	}
	
	
	
}
