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
