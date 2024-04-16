module kmymoney.base {
	requires static org.slf4j;
	requires java.desktop;
	
	requires transitive schnorxoborx.schnorxolib;

	exports org.kmymoney.base.basetypes.simple;
	exports org.kmymoney.base.basetypes.complex;
	exports org.kmymoney.base.tuples;
}
