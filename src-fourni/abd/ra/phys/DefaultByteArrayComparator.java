package abd.ra.phys;

import java.util.Comparator;

public class DefaultByteArrayComparator implements Comparator<byte[]> {

	@Override
	public int compare(byte[] one, byte[] two) {
		int i = 0;
		int min = Math.min(one.length, two.length);
		while (i < min && one[i] == two[i]) {
			i++;
		}
		if (i == min && one.length == two.length)
			return 0;
		else if (i == min) // && one.length != two.length
			return one.length - two.length;
		else // (i != min)
			return one[i] - two[i]; 
	}

}
