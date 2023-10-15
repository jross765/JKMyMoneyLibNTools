package org.kmymoney.write.impl;

//automatically created logger for debug and error -output

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.gnucash.generated.ObjectFactory;
import org.gnucash.generated.Slot;
import org.gnucash.generated.SlotValue;
import org.kmymoney.read.KMyMoneyObject;
import org.kmymoney.read.impl.KMyMoneyObjectImpl;
import org.kmymoney.write.KMyMoneyWritableFile;
import org.kmymoney.write.KMyMoneyWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface all writable gnucash-entities implement.
 */
public class KMyMoneyWritableObjectImpl implements KMyMoneyWritableObject {

	/**
	 *
	 */
	private KMyMoneyObjectImpl gnucashObject;

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableFile getWritableGnucashFile() {
		return ((KMyMoneyWritableObject) getGnucashObject()).getWritableGnucashFile();
	}

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableFile getFile() {
		return (KMyMoneyWritableFile) getGnucashObject().getKMyMoneyFile();
		//return ((GnucashWritableObject) getGnucashObject()).getWritableGnucashFile();
	}

	/**
	 * @param aGnucashObject the object we are helping with
	 */
	public KMyMoneyWritableObjectImpl(final KMyMoneyObjectImpl aGnucashObject) {
		super();
		setGnucashObject(aGnucashObject);
	}

	/**
	 * @param name  the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
	 */
	@SuppressWarnings("unchecked")
	public void setUserDefinedAttribute(final String name, final String value) {

		List<Slot> slots = getGnucashObject().getSlots().getSlot();
		for (Slot slot : slots) {
			if (slot.getSlotKey().equals(name)) {
				LOGGER.debug("GnucashWritableObjectHelper.setUserDefinedAttribute(name="
						+ name + ", value="
						+ value + ") - overwriting existing slot ");

				slot.getSlotValue().getContent().clear();
				slot.getSlotValue().getContent().add(value);
				getFile().setModified(true);
				return;
			}
		}
		ObjectFactory objectFactory = new ObjectFactory();
		Slot newSlot = objectFactory.createSlot();
		newSlot.setSlotKey(name);
		SlotValue newValue = objectFactory.createSlotValue();
		newValue.setType("string");
		newValue.getContent().add(value);
		newSlot.setSlotValue(newValue);
		LOGGER.debug("GnucashWritableObjectHelper.setUserDefinedAttribute(name="
				+ name + ", value="
				+ value + ") - adding new slot ");

		slots.add(newSlot);

		getFile().setModified(true);
	}

	/**
	 * Automatically created logger for debug and error-output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableObjectImpl.class);

	//------------------------ support for propertyChangeListeners ------------------

	/**
	 * support for firing PropertyChangeEvents.
	 * (gets initialized only if we really have listeners)
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
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(
			final PropertyChangeListener listener) {
		if (myPropertyChange == null) {
			myPropertyChange = new PropertyChangeSupport(this);
		}
		myPropertyChange.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property.  The listener
	 * will be invoked only when a call on firePropertyChange names that
	 * specific property.
	 *
	 * @param propertyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(
			final String propertyName,
			final PropertyChangeListener listener) {
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
	public final void removePropertyChangeListener(
			final String propertyName,
			final PropertyChangeListener listener) {
		if (myPropertyChange != null) {
			myPropertyChange.removePropertyChangeListener(propertyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		if (myPropertyChange != null) {
			myPropertyChange.removePropertyChangeListener(listener);
		}
	}

	//-------------------------------------------------------

	/**
	 * Just an overridden ToString to return this classe's name
	 * and hashCode.
	 *
	 * @return className and hashCode
	 */
	@Override
	public String toString() {
		return "GnucashWritableObjectHelper@" + hashCode();
	}

	/**
	 *
	 */
	public KMyMoneyWritableObjectImpl() {
		super();
		// TODO implement constructor for GnucashWritableObjectHelper
	}

	/**
	 * @return Returns the gnucashObject.
	 * @see {@link #gnucashObject}
	 */
	public KMyMoneyObjectImpl getGnucashObject() {
		return gnucashObject;
	}

	/**
	 * @param gnucashObject The gnucashObject to set.
	 * @see {@link #gnucashObject}
	 */
	public void setGnucashObject(final KMyMoneyObjectImpl gnucashObject) {
		if (gnucashObject == null) {
			throw new IllegalArgumentException("null 'gnucashObject' given!");
		}

		KMyMoneyObjectImpl oldObj = this.gnucashObject;
		if (oldObj == gnucashObject) {
			return; // nothing has changed
		}
		this.gnucashObject = gnucashObject;
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if (propertyChangeFirer != null) {
			propertyChangeFirer.firePropertyChange("gnucashObject", oldObj, gnucashObject);
		}
	}

}
