module kmymoney.base {
	requires static org.slf4j;
	requires java.desktop;
	
	exports org.kmymoney.base.basetypes.simple;
	exports org.kmymoney.base.basetypes.complex;
}
