/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - the exact name option has been added
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.filter;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.classification.core.ClassificationAssigner;
import org.eclipse.chemclipse.xxd.classification.settings.ClassifierAssignFilterSettings;
import org.eclipse.chemclipse.xxd.model.filter.peaks.AbstractPeakFilter;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassificationAssignFilter extends AbstractPeakFilter<ClassifierAssignFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier (Assign)";
	}

	@Override
	public String getDescription() {

		return "Filter peaks and add peak classifier (e.g. for the whole name or parts of the name).";
	}

	@Override
	public Class<ClassifierAssignFilterSettings> getConfigClass() {

		return ClassifierAssignFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, ClassifierAssignFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		for(IPeak peak : peaks) {
			ClassificationAssigner.apply(peak, configuration);
			subMonitor.worked(1);
		}
	}
}