/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.chromatogram.settings.TargetTransferFilterSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class TargetTransferFilter implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.xxd.filter.chromatogram.peakTargetTransfer";
	private static final String NAME = "Target Transfer";
	private static final String DESCRIPTION = "Transfers the peak targets from the master to its references.";

	@Override
	public String getCategory() {

		return ICategories.PEAK_IDENTIFIER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<TargetTransferFilterSettings> implements IChromatogramSelectionProcessSupplier<TargetTransferFilterSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, TargetTransferFilterSettings.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.VSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, TargetTransferFilterSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			//
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			float limitMatchFactor = processSettings.getLimitMatchFactor();
			float matchFactor = processSettings.getMatchQuality();
			boolean useBestTargetOnly = processSettings.isUseBestTargetOnly();
			//
			List<? extends IPeak> peaks = chromatogram.getPeaks(startRetentionTime, stopRetentionTime);
			List<IChromatogram<?>> referenceChromatograms = chromatogram.getReferencedChromatograms();
			for(IChromatogram<?> referenceChromatogram : referenceChromatograms) {
				for(IPeak peak : peaks) {
					if(!peak.getTargets().isEmpty()) {
						List<IPeak> peakReferences = getReferencePeaks(peak, referenceChromatogram);
						if(!peakReferences.isEmpty()) {
							/*
							 * Transfer Targets
							 */
							List<IIdentificationTarget> identificationTargets = new ArrayList<>(peak.getTargets());
							if(useBestTargetOnly) {
								Collections.sort(identificationTargets, (t1, t2) -> Float.compare(t2.getComparisonResult().getMatchFactor(), t1.getComparisonResult().getMatchFactor()));
								IIdentificationTarget identificationTarget = identificationTargets.get(0);
								identificationTargets.clear();
								identificationTargets.add(identificationTarget);
							}
							/*
							 * Reference Peaks
							 */
							for(IPeak peakReference : peakReferences) {
								if(LimitSupport.doIdentify(peakReference.getTargets(), limitMatchFactor)) {
									for(IIdentificationTarget identificationTarget : identificationTargets) {
										ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
										String name = libraryInformation.getName();
										String casNumber = libraryInformation.getCasNumber();
										addIdentificationTarget(peakReference, name, casNumber, matchFactor);
									}
								}
							}
						}
					}
				}
			}
			//
			return chromatogramSelection;
		}

		private void addIdentificationTarget(IPeak peak, String name, String casNumber, float matchFactor) {

			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setName(name);
			libraryInformation.setCasNumber(casNumber);
			IComparisonResult comparisonResult = new ComparisonResult(matchFactor);
			peak.getTargets().add(new IdentificationTarget(libraryInformation, comparisonResult, NAME));
		}

		private List<IPeak> getReferencePeaks(IPeak peak, IChromatogram<?> referenceChromatogram) {

			List<IPeak> peaks = new ArrayList<>();
			//
			IPeakModel peakModel = peak.getPeakModel();
			int retentionTimeStart = peakModel.getStartRetentionTime();
			int retentionTimeStop = peakModel.getStopRetentionTime();
			//
			for(IPeak referencePeak : referenceChromatogram.getPeaks()) {
				int retentionTime = referencePeak.getPeakModel().getPeakMaximum().getRetentionTime();
				if(retentionTime >= retentionTimeStart && retentionTime <= retentionTimeStop) {
					peaks.add(referencePeak);
				}
			}
			//
			return peaks;
		}
	}
}