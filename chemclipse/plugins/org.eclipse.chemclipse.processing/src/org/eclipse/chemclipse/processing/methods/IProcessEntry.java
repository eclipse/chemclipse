/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make unmodifiable except the Settings
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public interface IProcessEntry {

	String getProcessorId();

	String getName();

	String getDescription();

	String getSettings();

	/**
	 * Set the settings for this entry
	 * 
	 * @param settings
	 * @throws IllegalArgumentException
	 *             if the entry is readonly
	 */
	void setSettings(String settings) throws IllegalArgumentException;

	boolean isReadOnly();

	ProcessEntryContainer getParent();

	public static <T> ProcessorPreferences<T> getProcessEntryPreferences(IProcessEntry entry, ProcessSupplierContext context) {

		IProcessSupplier<T> supplier = context.getSupplier(entry.getProcessorId());
		if(supplier == null) {
			return null;
		}
		return new ProcessEntryProcessorPreferences<>(supplier, entry);
	}

	/**
	 * Compares this entry content to the other entries content, the default implementation compares {@link #getName()}, {@link #getDescription()}, {@link #getSettings()}, {@link #isReadOnly()} {@link #getProcessorId()},
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare differnt instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(IProcessEntry other) {

		if(other == null) {
			return false;
		}
		if(other == this) {
			return true;
		}
		if(isReadOnly() != other.isReadOnly()) {
			return false;
		}
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(!getDescription().equals(other.getDescription())) {
			return false;
		}
		String settings = getSettings();
		if(settings == null) {
			settings = "";
		}
		String otherSettings = other.getSettings();
		if(otherSettings == null) {
			otherSettings = "";
		}
		if(!otherSettings.equals(settings)) {
			return false;
		}
		if(!getProcessorId().equals(other.getProcessorId())) {
			return false;
		}
		if(this instanceof ProcessEntryContainer) {
			ProcessEntryContainer container = (ProcessEntryContainer)this;
			if(other instanceof ProcessEntryContainer) {
				container.contentEquals((ProcessEntryContainer)other);
			} else {
				return container.getNumberOfEntries() == 0;
			}
		} else if(other instanceof ProcessEntryContainer) {
			ProcessEntryContainer container = (ProcessEntryContainer)other;
			return container.getNumberOfEntries() == 0;
		}
		return true;
	}
}