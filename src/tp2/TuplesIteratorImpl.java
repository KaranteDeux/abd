package tp2;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import abd.tp1.TuplesIterator;

public class TuplesIteratorImpl implements TuplesIterator {

	Scanner scanner;

	public TuplesIteratorImpl(Path path) throws IOException{

		scanner = new Scanner(path.toFile());
	}

	@Override
	public boolean hasNext() throws IOException {
		return scanner.hasNext();
	}

	@Override
	public String[] next() throws IOException {
		String next = scanner.next();
		if(next != null)
			return next.split("\\,");
		return null;
	}

	@Override
	public void close() throws IOException {
		scanner.close();

	}


}
