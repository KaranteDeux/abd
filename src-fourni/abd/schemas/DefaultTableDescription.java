package abd.schemas;

import java.util.Arrays;

public class DefaultTableDescription implements TableDescription {
	
	private int arity;
	private AttributeType[] attributeTypes;
	private String name;
	private int[] columnsOffsets;
	private int totalLength;
	
	public DefaultTableDescription(String name, AttributeType ... attributeTypes) {
		if (attributeTypes.length <= 0)
			throw new IllegalArgumentException("At least one attribute type required.");
		this.arity = attributeTypes.length;
		this.attributeTypes = Arrays.copyOf(attributeTypes, attributeTypes.length);
		this.name = name;
		
		// Computes length and offsets
		columnsOffsets = new int[arity];
		totalLength = 0;
		for (int i = 0; i < arity; i++) {
			columnsOffsets[i] = totalLength;
			totalLength += attributeTypes[i].getLength();
		}
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public AttributeType getAttributeType(int attributeRank) {
		if (attributeRank < 0 || attributeRank >= arity)
			throw new IllegalArgumentException("Incorrect attribute rank : " + attributeRank);
		return attributeTypes[attributeRank];
	}
	
	public int getTupleLength () {
		return totalLength;
	}

	@Override
	public int getColumnOffset(int columnRank) {
		return columnsOffsets[columnRank];
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(attributeTypes);
	}

	public static TableDescription getDescriptionForJoin (String name, TableDescription left, TableDescription right) {
		AttributeType[] attributes = new AttributeType[left.getArity() + right.getArity()];
		int leftArity = left.getArity();
		int rightArity = right.getArity();
		int i = 0;
		for (; i < leftArity; i++) {
			attributes[i] = left.getAttributeType(i);
		}
		for (; i < leftArity + rightArity; i++) {
			attributes[i] = right.getAttributeType(i-leftArity);
		}
		DefaultTableDescription result = new DefaultTableDescription(name, attributes);
		return result;
	}
	
}