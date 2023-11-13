module kmymoney.api {
	requires static org.slf4j;
	requires java.desktop;
	requires jakarta.xml.bind;
	
	exports org.kmymoney.basetypes.simple;
	exports org.kmymoney.basetypes.complex;
	exports org.kmymoney.currency;
	exports org.kmymoney.numbers;
	
	exports org.kmymoney.read;
	exports org.kmymoney.read.aux;
	exports org.kmymoney.read.impl;
	exports org.kmymoney.read.impl.aux;
	
//	exports org.kmymoney.write;
//	exports org.kmymoney.write.impl;
}
