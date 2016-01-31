package abd.reldb;

import java.util.Arrays;

public class DefaultTableDescription implements TableDescription {
	
	private int arity;
	private AttributeType[] attributeTypes;
	private String name;
	
	public DefaultTableDescription(String name, AttributeType ... attributeTypes) {
		if (attributeTypes.length <= 0)
			throw new IllegalArgumentException("At least one attribute type required.");
		this.arity = attributeTypes.length;
		this.attributeTypes = Arrays.copyOf(attributeTypes, attributeTypes.length);
		this.name = name;
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
}
