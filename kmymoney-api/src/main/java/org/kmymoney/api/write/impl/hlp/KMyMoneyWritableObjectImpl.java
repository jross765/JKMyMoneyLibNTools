package org.kmymoney.api.write.impl.hlp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneyObjectImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritableObjectImpl implements KMyMoneyWritableObject 
{

	/**
	 * Automatically created logger for debug and error-output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableObjectImpl.class);

	// ---------------------------------------------------------------

	private KMyMoneyObjectImpl kmmObj;

	/**
	 * support for firing PropertyChangeEvents. (gets initialized only if we really
	 * have listeners)
	 */
	private volatile PropertyChangeSupport myPtyChg = null;

	// ---------------------------------------------------------------

	public KMyMoneyWritableObjectImpl() {
		super();
		// TODO implement constructor for KMyMoneyWritableObjectHelper
	}

	/**
	 * @param aKMyMoneyObject the object we are helping with
	 */
	public KMyMoneyWritableObjectImpl(final KMyMoneyObjectImpl aKMyMoneyObject) {
		super();
		setKMyMoneyObject(aKMyMoneyObject);
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableFile getWritableKMyMoneyFile() {
		return ((KMyMoneyWritableObject) getKMyMoneyObject()).getWritableKMyMoneyFile();
	}

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableFile getFile() {
		return (KMyMoneyWritableFile) getKMyMoneyObject().getKMyMoneyFile();
		// return ((KMyMoneyWritableObject) getKMyMoneyObject()).getWritableKMyMoneyFile();
	}

	/**
	 * Remove slots with dummy content
	 */
	public void cleanSlots() {
		if ( kmmObj.getSlots() == null )
			return;

		for ( Slot slot : kmmObj.getSlots().getSlot() ) {
			if ( slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				kmmObj.getSlots().getSlot().remove(slot);
				break;
			}
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @param name  the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		List<Slot> slots = getKMyMoneyObject().getSlots().getSlot();
		for ( Slot slot : slots ) {
			if ( slot.getSlotKey().equals(name) ) {
				LOGGER.debug("setUserDefinedAttribute: (name=" + name + ", value=" + value
						+ ") - overwriting existing slot ");

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
		newValue.setType(Const.XML_DATA_TYPE_STRING);
		newValue.getContent().add(value);
		newSlot.setSlotValue(newValue);
		LOGGER.debug("setUserDefinedAttribute: (name=" + name + ", value=" + value + ") - adding new slot ");

		slots.add(newSlot);

		getFile().setModified(true);
	}

	// ------------------------ support for propertyChangeListeners

	/**
	 * Returned value may be null if we never had listeners.
	 *
	 * @return Our support for firing PropertyChangeEvents
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return myPtyChg;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 *
	 * @param ptyName  The name of the property to listen on.
	 * @param listener The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	public final void addPropertyChangeListener(final String ptyName, final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(ptyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param ptyName  The name of the property that was listened on.
	 * @param listener The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	public final void removePropertyChangeListener(final String ptyName, final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(ptyName, listener);
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
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(listener);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @return Returns the gnucashObject.
	 * @see {@link #kmmObj}
	 */
	public KMyMoneyObjectImpl getKMyMoneyObject() {
		return kmmObj;
	}

	/**
	 * @param obj The gnucashObject to set.
	 * @see {@link #kmmObj}
	 */
	public void setKMyMoneyObject(final KMyMoneyObjectImpl obj) {
		if ( obj == null ) {
			throw new IllegalArgumentException("null GnuCash-object given!");
		}

		KMyMoneyObjectImpl oldObj = this.kmmObj;
		if ( oldObj == obj ) {
			return; // nothing has changed
		}

		this.kmmObj = obj;
		// <<insert code to react further to this change here
		PropertyChangeSupport ptyChgFirer = getPropertyChangeSupport();
		if ( ptyChgFirer != null ) {
			ptyChgFirer.firePropertyChange("gnucashObject", oldObj, obj);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * Just an overridden ToString to return this classe's name and hashCode.
	 *
	 * @return className and hashCode
	 */
	@Override
	public String toString() {
		return "KMyMoneyWritableObjectHelper@" + hashCode();
	}

}
