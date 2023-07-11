/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IndexNameMarker;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexAssigner;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.IndexAssignerSettings;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramIndexAssigner implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.retentionindexassigner";
	private static final String NAME = "Retention Index Assigner";
	private static final String DESCRIPTION = "Assigns retention indices by peak names";

	@Override
	public String getCategory() {

		return "Chromatogram Calculator";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<IndexAssignerSettings> implements IChromatogramSelectionProcessSupplier<IndexAssignerSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, IndexAssignerSettings.class, parent, DataCategory.MSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IndexAssignerSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			RetentionIndexAssigner retentionIndexAssigner = processSettings.getRetentionIndexAssigner();
			//
			Map<String, Integer> retentionIndexMap = new HashMap<>();
			for(IndexNameMarker indexNameMarker : retentionIndexAssigner) {
				retentionIndexMap.put(indexNameMarker.getName(), indexNameMarker.getRetentionIndex());
			}
			/*
			 * Peaks
			 */
			for(IPeak peak : chromatogram.getPeaks()) {
				IPeakModel peakModel = peak.getPeakModel();
				float retentionIndexPeak = peakModel.getPeakMaximum().getRetentionIndex();
				IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndexPeak);
				IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), identificationTargetComparator);
				if(identificationTarget != null) {
					ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
					int retentionIndex = retentionIndexMap.getOrDefault(libraryInformation.getName(), 0);
					peakModel.getPeakMaximum().setRetentionIndex(retentionIndex);
				}
			}
			//
			return chromatogramSelection;
		}
	}
}