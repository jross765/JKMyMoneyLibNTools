module kmymoney.base {
	requires static org.slf4j;
	requires java.desktop;
	
	exports org.kmymoney.base.basetypes.simple;
	exports org.kmymoney.base.basetypes.complex;
	exports org.kmymoney.base.numbers;
	exports org.kmymoney.base.tuples;
}
