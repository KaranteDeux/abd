package tp2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import abd.tp1.FileTable;
import abd.tp1.TuplesIterator;


public class FileTableImpl implements FileTable {

	int arity;
	Path path;

	TuplesIteratorImpl tuplesIteratorImpl;
	

	public FileTableImpl(int arity, Path path) throws IOException{
		if(arity <= 0)
			throw new IllegalArgumentException("Arity cannot be equals or less than 0");
		this.arity = arity;
		this.path = path;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public void addTuple(String... tuples) throws IllegalArgumentException,	IOException {
		if(tuples.length != arity)
			throw new IllegalArgumentException();
		
		
		
		List<String> list = new ArrayList<String>();
		String str = "";
		for(int i=0;i<tuples.length-1;i++){
			str += tuples[i];
			str += ",";
		}
		str += tuples[tuples.length-1];
		list.add(str);
		
		Files.write(path, list, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		
		if(tuplesIteratorImpl != null)
			tuplesIteratorImpl.close();
		
	}

	@Override
	public TuplesIterator tuplesIterator() throws IOException {
		tuplesIteratorImpl = new TuplesIteratorImpl(path);
		return tuplesIteratorImpl;
	}

	@Override
	public FileTable select(Path resultPath, String contained, int columnRank)
			throws IllegalArgumentException, IOException {
		
		if(columnRank >= arity)
			throw new IllegalArgumentException("Illegal columnRank");
		
		
		FileTableImpl fileTableImpl = new FileTableImpl(arity, resultPath);
		
		
		TuplesIterator it = tuplesIterator();
		
		while(it.hasNext()){
			String[] elements = it.next();
			if(elements[columnRank].equals(contained))
				fileTableImpl.addTuple(elements);
		}
		
		return fileTableImpl;
	}

	@Override
	public FileTable project(Path resultPath, int... columnRanks)
			throws IllegalArgumentException, IOException {
		
		if(columnRanks.length >= arity)
			throw new IllegalArgumentException();
		
		for(Integer columnRank : columnRanks){
			if(columnRank < 0 && columnRank > arity)
				throw new IllegalArgumentException();
		}
		
		
		
		FileTableImpl fileTableImpl = new FileTableImpl(columnRanks.length, resultPath);
		
		
		TuplesIterator it = tuplesIterator();
		
		while(it.hasNext()){
			String[] elements = it.next();
			String[] newElements = new String[columnRanks.length];
			for(int i=0;i<columnRanks.length;i++){
				newElements[i] = elements[columnRanks[i]];
				
			}
		}
		
		return fileTableImpl;
	}

	@Override
	public void close() throws IOException {
		if(tuplesIteratorImpl != null)
			tuplesIteratorImpl.close();
	}

}
