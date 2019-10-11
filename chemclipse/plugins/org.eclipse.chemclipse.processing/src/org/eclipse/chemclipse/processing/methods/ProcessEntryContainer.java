/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.BiConsumer;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

/**
 * A {@link ProcessEntryContainer} holds some {@link IProcessEntry}s
 *
 */
public interface ProcessEntryContainer extends Iterable<IProcessEntry> {

	int getNumberOfEntries();

	/**
	 * Compares that this all contained {@link IProcessEntry}s are equal to the other one given, the default implementation works as follows:
	 * <ol>
	 * <li>if other is null, and this container does not contains any entry, <code>true</code> is returned</li>
	 * <li>if any entry is not contentEquals to the other one <code>false</code> is returned</li>
	 * <li>if any of the iterator return more elements than the other <code>false</code> is returned
	 * </ol>
	 * 
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(ProcessEntryContainer other) {

		Iterator<IProcessEntry> thisEntries = iterator();
		if(other == null) {
			return !thisEntries.hasNext();
		}
		Iterator<IProcessEntry> otherEntries = other.iterator();
		while(thisEntries.hasNext() && otherEntries.hasNext()) {
			IProcessEntry thisEntry = thisEntries.next();
			IProcessEntry otherEntry = otherEntries.next();
			if(!thisEntry.contentEquals(otherEntry)) {
				return false;
			}
		}
		if(otherEntries.hasNext() || thisEntries.hasNext()) {
			// not all where consumed
			return false;
		}
		return true;
	}

	static <X> void applyProcessors(ProcessEntryContainer processMethod, ProcessExecutionContext context, BiConsumer<IProcessSupplier<X>, X> consumer) {

		for(IProcessEntry processEntry : processMethod) {
			ProcessorPreferences<X> preferences = IProcessEntry.getProcessEntryPreferences(processEntry, context);
			if(preferences == null) {
				context.addWarnMessage(processEntry.getName(), "processor not found, will be skipped");
				continue;
			}
			try {
				X settings = preferences.getSettings();
				consumer.accept(preferences.getSupplier(), settings);
			} catch(IOException e) {
				context.addWarnMessage(processEntry.getName(), "the settings can't be read, will be skipped", e);
			} catch(RuntimeException e) {
				context.addErrorMessage(processEntry.getName(), "internal error", e);
			}
		}
	}
}
