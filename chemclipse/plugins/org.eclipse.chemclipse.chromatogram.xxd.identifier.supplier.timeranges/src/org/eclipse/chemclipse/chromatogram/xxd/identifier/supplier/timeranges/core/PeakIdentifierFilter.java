/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.settings.PeakIdentifierFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.PeakFilterOption;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class PeakIdentifierFilter implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.xxd.filter.chromatogram.timeRangePeakIdentifier";
	private static final String NAME = "Time Ranges";
	private static final String DESCRIPTION = "Identify peaks by the given time ranges.";

	@Override
	public String getCategory() {

		return ICategories.PEAK_IDENTIFIER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<PeakIdentifierFilterSettings> implements IChromatogramSelectionProcessSupplier<PeakIdentifierFilterSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, PeakIdentifierFilterSettings.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.VSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, PeakIdentifierFilterSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			/*
			 * Assign the peaks
			 */
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			float limitMatchFactor = processSettings.getLimitMatchFactor();
			float matchQuality = processSettings.getMatchQuality();
			PeakFilterOption peakFilterOption = processSettings.getPeakFilterOption();
			TimeRanges timeRanges = processSettings.getTimeRanges();
			//
			for(TimeRange timeRange : timeRanges.values()) {
				List<IPeak> peaks = getPeaksInFocus(chromatogram, timeRange, limitMatchFactor);
				if(!peaks.isEmpty()) {
					/*
					 * Target
					 */
					switch(peakFilterOption) {
						case AREA:
							Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getIntegratedArea(), p1.getIntegratedArea()));
							break;
						default:
							Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getPeakModel().getPeakMaximum().getTotalSignal(), p1.getPeakModel().getPeakMaximum().getTotalSignal()));
							break;
					}
					IPeak peak = peaks.get(0);
					String name = timeRange.getIdentifier();
					ILibraryInformation libraryInformation = new LibraryInformation();
					libraryInformation.setName(name);
					ComparisonResult comparisonResult = new ComparisonResult(matchQuality);
					IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
					peak.getTargets().add(identificationTarget);
				}
			}
			//
			return chromatogramSelection;
		}

		private List<IPeak> getPeaksInFocus(IChromatogram<?> chromatogram, TimeRange timeRange, float limitMatchFactor) {

			List<IPeak> peaks = new ArrayList<>();
			for(IPeak peak : chromatogram.getPeaks()) {
				if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
					IPeakModel peakModel = peak.getPeakModel();
					IScan scan = peakModel.getPeakMaximum();
					int retentionTime = scan.getRetentionTime();
					if(retentionTime >= timeRange.getStart() && retentionTime <= timeRange.getStop()) {
						peaks.add(peak);
					}
				}
			}
			//
			return peaks;
		}
	}
}