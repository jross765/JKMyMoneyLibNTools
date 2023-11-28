package org.kmymoney.api.basetypes.simple;

public class InvalidKMMIDException extends RuntimeException {

    private static final long serialVersionUID = -8293041843807512970L;
    
    public InvalidKMMIDException() {
	super();
    }

    public InvalidKMMIDException(String msg) {
	super(msg);
    }

}
