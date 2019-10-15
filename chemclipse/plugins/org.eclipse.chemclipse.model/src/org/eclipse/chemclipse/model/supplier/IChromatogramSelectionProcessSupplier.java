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
package org.eclipse.chemclipse.model.supplier;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

public interface IChromatogramSelectionProcessSupplier<SettingType> extends IProcessSupplier<SettingType> {

	/**
	 * Apply this processor to the given chromatogram selection
	 * 
	 * @param chromatogramSelection
	 *            the {@link IChromatogramSelection} to process
	 * @param processSettings
	 *            settings to use
	 * @param monitor
	 *            the monitor to use for reporting progress or <code>null</code> if no progress is desired
	 * @return the processed {@link IChromatogramSelection}
	 */
	IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, SettingType processSettings, ProcessExecutionContext context);

	static <T> IChromatogramSelection<?, ?> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessSupplier<T> supplier, T processSettings, ProcessExecutionContext context) {

		if(supplier instanceof IChromatogramSelectionProcessSupplier<?>) {
			IChromatogramSelectionProcessSupplier<T> chromatogramSelectionProcessSupplier = (IChromatogramSelectionProcessSupplier<T>)supplier;
			return chromatogramSelectionProcessSupplier.apply(chromatogramSelection, processSettings, context);
		} else if(supplier instanceof IMeasurementProcessSupplier<?>) {
			IMeasurementProcessSupplier<T> measurementProcessSupplier = (IMeasurementProcessSupplier<T>)supplier;
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			measurementProcessSupplier.applyProcessor(Collections.singleton(chromatogram), processSettings, context);
		} else if(supplier instanceof ProcessEntryContainer) {
			ProcessEntryContainer container = (ProcessEntryContainer)supplier;
			return applyProcessEntries(chromatogramSelection, container, context);
		}
		return chromatogramSelection;
	}

	static <X> IChromatogramSelection<?, ?> applyProcessEntries(IChromatogramSelection<?, ?> chromatogramSelection, ProcessEntryContainer processMethod, ProcessExecutionContext context) {

		context.setWorkRemaining(processMethod.getNumberOfEntries());
		AtomicReference<IChromatogramSelection<?, ?>> result = new AtomicReference<IChromatogramSelection<?, ?>>(chromatogramSelection);
		ProcessEntryContainer.applyProcessEntries(processMethod, context, (preferences, subContext) -> {
			try {
				result.set(applyProcessor(result.get(), preferences.getSupplier(), preferences.getSettings(), subContext));
			} catch(IOException e) {
				subContext.addWarnMessage(preferences.getSupplier().getName(), "reading settings failed, processor will be skipped", e);
			}
		});
		return result.get();
	}
}
