package org.kmymoney.api.read.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper-Class used to implement functions all gnucash-objects support.
 */
public class KMyMoneyObjectImpl implements KMyMoneyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyObjectImpl.class);

    private final KMyMoneyFile myFile;

    // -----------------------------------------------------------------

    public KMyMoneyObjectImpl(final KMyMoneyFile myFile) {
	super();

	this.myFile = myFile;
    }
    
    // ---------------------------------------------------------------
    
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return myFile;
    }

    // ------------------------ support for propertyChangeListeners

    /**
     * support for firing PropertyChangeEvents. (gets initialized only if we really
     * have listeners)
     */
    private volatile PropertyChangeSupport myPropertyChange = null;

    /**
     * Returned value may be null if we never had listeners.
     *
     * @return Our support for firing PropertyChangeEvents
     */
    protected PropertyChangeSupport getPropertyChangeSupport() {
	return myPropertyChange;
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
    public final void addPropertyChangeListener(final PropertyChangeListener listener) {
	if (myPropertyChange == null) {
	    myPropertyChange = new PropertyChangeSupport(this);
	}
	myPropertyChange.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be
     * invoked only when a call on firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
    public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
	if (myPropertyChange == null) {
	    myPropertyChange = new PropertyChangeSupport(this);
	}
	myPropertyChange.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
    public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
	if (myPropertyChange != null) {
	    myPropertyChange.removePropertyChangeListener(propertyName, listener);
	}
    }

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
	if (myPropertyChange != null) {
	    myPropertyChange.removePropertyChangeListener(listener);
	}
    }

    // -------------------------------------------------------

    /**
     * Just an overridden ToString to return this classe's name and hashCode.
     *
     * @return className and hashCode
     */
    @Override
    public String toString() {
	return "KMyMoneyObjectImpl@" + hashCode();
    }

}
