module kmymoney.api {
	requires static org.slf4j;
	requires java.desktop;
	requires jakarta.xml.bind;
	// requires junit;
	
	exports org.kmymoney.currency;
	exports org.kmymoney.numbers;
	
	exports org.kmymoney.read;
	exports org.kmymoney.read.impl;
	
	exports org.kmymoney.write;
	exports org.kmymoney.write.impl;
}
