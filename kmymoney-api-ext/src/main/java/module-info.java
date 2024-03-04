module kmymoney.apiext {
	requires static org.slf4j;
	// requires java.desktop;
	
	requires kmymoney.base;
	requires kmymoney.api;

	exports org.kmymoney.apiext.secacct;
}