package utils;

import java.util.Arrays;

import abd.schemas.TableDescription;

public class Utils {
	public static byte[] getColumnFromColumnRank(TableDescription defTabDesc, int columnRank, byte[] tuple){

		int posDansTuple = 0, sizeColumn = defTabDesc.getAttributeType(columnRank).getLength();
		for(int pos = 0;pos<columnRank;pos++){
			posDansTuple += defTabDesc.getAttributeType(pos).getLength();
		}
		return Arrays.copyOfRange(tuple, posDansTuple, posDansTuple + sizeColumn);

	}
}
