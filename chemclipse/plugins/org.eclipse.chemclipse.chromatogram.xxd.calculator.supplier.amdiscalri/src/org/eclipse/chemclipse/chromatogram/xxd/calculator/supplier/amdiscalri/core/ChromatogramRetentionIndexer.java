/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexExtrapolator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexSettings;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramRetentionIndexer implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.retentionindex";
	private static final String NAME = "Retention Index Calculator (embedded)";
	private static final String DESCRIPTION = "Calculates the retention indices for scans and peaks in the chromatogram.";

	@Override
	public String getCategory() {

		return "Chromatogram Calculator";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<RetentionIndexSettings> implements IChromatogramSelectionProcessSupplier<RetentionIndexSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, RetentionIndexSettings.class, parent, DataCategory.MSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, RetentionIndexSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			boolean extrapolateLeft = processSettings.isExtrapolateLeft();
			boolean extrapolateRight = processSettings.isExtrapolateRight();
			RetentionIndexMarker retentionIndexMarker = getRetentionIndexMarker(processSettings.getRetentionIndexMarker(), chromatogram, extrapolateLeft, extrapolateRight);
			boolean processReferenceChromatograms = processSettings.isProcessReferenceChromatograms();
			RetentionIndexCalculator.calculateIndex(chromatogram, retentionIndexMarker, processReferenceChromatograms);
			/*
			 * Store the retention index marker in the chromatogram.
			 */
			if(processSettings.isStoreInChromatogram()) {
				ISeparationColumnIndices separationColumnIndices = chromatogram.getSeparationColumnIndices();
				separationColumnIndices.clear();
				for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
					int retentionTime = retentionIndexEntry.getRetentionTime();
					float retentionIndex = retentionIndexEntry.getRetentionIndex();
					String name = retentionIndexEntry.getName();
					separationColumnIndices.put(new RetentionIndexEntry(retentionTime, retentionIndex, name));
				}
			}
			//
			return chromatogramSelection;
		}
	}

	private static RetentionIndexMarker getRetentionIndexMarker(RetentionIndexMarker retentionIndexMarker, IChromatogram<?> chromatogram, boolean extrapolateLeft, boolean extrapolateRight) {

		if(retentionIndexMarker != null && retentionIndexMarker.size() >= 2) {
			if(extrapolateData(extrapolateLeft, extrapolateRight)) {
				/*
				 * Get the start/stop marker
				 */
				Optional<IRetentionIndexEntry> markerStart = retentionIndexMarker.stream().min((m1, m2) -> Integer.compare(m1.getRetentionTime(), m2.getRetentionTime()));
				Optional<IRetentionIndexEntry> markerStop = retentionIndexMarker.stream().max((m1, m2) -> Integer.compare(m1.getRetentionTime(), m2.getRetentionTime()));
				/*
				 * Calculate the missing ranges.
				 */
				RetentionIndexExtrapolator retentionIndexExtrapolator = new RetentionIndexExtrapolator();
				retentionIndexExtrapolator.extrapolateMissingAlkaneRanges(retentionIndexMarker);
				/*
				 * Constraint Remove (Left)
				 */
				if(!extrapolateLeft && markerStart.isPresent()) {
					List<IRetentionIndexEntry> removeEntries = new ArrayList<>();
					int retentionTimeStart = markerStart.get().getRetentionTime();
					for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
						if(retentionIndexEntry.getRetentionTime() < retentionTimeStart) {
							removeEntries.add(retentionIndexEntry);
						}
					}
					retentionIndexMarker.removeAll(removeEntries);
				}
				/*
				 * Constraint Remove (Right)
				 */
				if(!extrapolateRight && markerStop.isPresent()) {
					List<IRetentionIndexEntry> removeEntries = new ArrayList<>();
					int retentionTimeStop = markerStop.get().getRetentionTime();
					for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
						if(retentionIndexEntry.getRetentionTime() > retentionTimeStop) {
							removeEntries.add(retentionIndexEntry);
						}
					}
					retentionIndexMarker.removeAll(removeEntries);
				}
			}
		}
		/*
		 * Return the initial marker.
		 */
		return retentionIndexMarker;
	}

	private static boolean extrapolateData(boolean extrapolateLeft, boolean extrapolateRight) {

		return extrapolateLeft == true || extrapolateRight == true;
	}
}