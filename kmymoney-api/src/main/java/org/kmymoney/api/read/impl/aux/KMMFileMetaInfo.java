package org.kmymoney.api.read.impl.aux;

import java.math.BigInteger;
import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.api.generated.FILEINFO;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMFileMetaInfo {

	private static final Logger LOGGER = LoggerFactory.getLogger(KMMFileMetaInfo.class);

	// ---------------------------------------------------------------

	private KMyMoneyFileImpl kmmFile = null;
	
	private FILEINFO info = null;

	// ---------------------------------------------------------------

	public KMMFileMetaInfo(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
		this.info = kmmFile.getRootElement().getFILEINFO();
	}

	// ---------------------------------------------------------------

	public LocalDate getCreationDate() {
		XMLGregorianCalendar cal = info.getCREATIONDATE().getDate();
		try {
		    return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
		} catch (Exception e) {
		    IllegalStateException ex = new IllegalStateException("unparsable date '" + cal + "' in creation date!");
		    ex.initCause(e);
		    throw ex;
		}
	}

	public LocalDate getLastModifiedDate() {
		XMLGregorianCalendar cal = info.getLASTMODIFIEDDATE().getDate();
		try {
		    return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
		} catch (Exception e) {
		    IllegalStateException ex = new IllegalStateException("unparsable date '" + cal + "' in last-modified date!");
		    ex.initCause(e);
		    throw ex;
		}
	}

	public BigInteger getVersion() {
		return info.getVERSION().getId();
	}

	public BigInteger getFixVersion() {
		return info.getFIXVERSION().getId();
	}

}
