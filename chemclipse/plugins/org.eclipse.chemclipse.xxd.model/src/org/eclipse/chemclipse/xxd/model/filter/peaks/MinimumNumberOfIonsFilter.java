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
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class MinimumNumberOfIonsFilter implements IPeakFilter<MinimumNumberOfIonsFilterSettings> {

	@Override
	public String getName() {

		return "Minimum Number of m/z Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks that are below a given number of ions in the m/z";
	}

	@Override
	public Class<MinimumNumberOfIonsFilterSettings> getConfigClass() {

		return MinimumNumberOfIonsFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, MinimumNumberOfIonsFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		int minIons = configuration.getMinNumberOfIons();
		for(X peak : read) {
			if(peak instanceof IPeakMSD) {
				IPeakMassSpectrum massSpectrum = ((IPeakMSD)peak).getExtractedMassSpectrum();
				if(massSpectrum.getIons().size() < minIons) {
					listener.delete(peak);
				}
			} else {
				throw new IllegalArgumentException("invalid peak");
			}
			subMonitor.worked(1);
		}
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		for(IPeak peak : items) {
			if(!(peak instanceof IPeakMSD)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.MSD};
	}
}
