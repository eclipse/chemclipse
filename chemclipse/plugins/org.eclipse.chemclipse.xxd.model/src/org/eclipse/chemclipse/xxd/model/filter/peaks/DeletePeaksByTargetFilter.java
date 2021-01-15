/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - implementation of a peak/target filter
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeletePeaksByTargetFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksByTargetFilter implements IPeakFilter<DeletePeaksByTargetFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peak(s) by Target";
	}

	@Override
	public Class<DeletePeaksByTargetFilterSettings> getConfigClass() {

		return DeletePeaksByTargetFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, DeletePeaksByTargetFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		if(isConfigurationValid(configuration)) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
			for(X peak : peaks) {
				if(isDeletePeak(peak, configuration)) {
					listener.delete(peak);
				}
				subMonitor.worked(1);
			}
		}
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	public static boolean isDeletePeak(IPeak peak, DeletePeaksByTargetFilterSettings configuration) {

		if(peak != null) {
			if(isConfigurationValid(configuration)) {
				String searchValue = getSearchValue(configuration);
				//
				for(IIdentificationTarget identificationTarget : peak.getTargets()) {
					ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
					String targetValue = getTargetValue(configuration, libraryInformation);
					if(targetValue != null && !targetValue.isEmpty()) {
						if(configuration.isRegularExpression()) {
							if(targetValue.matches(searchValue)) {
								return true;
							}
						} else {
							if(targetValue.equals(searchValue)) {
								return true;
							}
						}
					}
				}
			}
		}
		//
		return false;
	}

	private static String getSearchValue(DeletePeaksByTargetFilterSettings configuration) {

		String value = configuration.getValue();
		if(!configuration.isRegularExpression()) {
			boolean caseSensitive = configuration.isCaseSensitive();
			value = caseSensitive ? value : value.toLowerCase();
		}
		//
		return value;
	}

	private static String getTargetValue(DeletePeaksByTargetFilterSettings configuration, ILibraryInformation libraryInformation) {

		String value;
		switch(configuration.getPeaksDeleteOption()) {
			case NAME:
				value = libraryInformation.getName();
				break;
			case CAS:
				value = libraryInformation.getCasNumber();
				break;
			default:
				value = null;
				break;
		}
		//
		if(value != null) {
			if(!configuration.isRegularExpression()) {
				boolean caseSensitive = configuration.isCaseSensitive();
				value = caseSensitive ? value : value.toLowerCase();
			}
		}
		//
		return value;
	}

	private static boolean isConfigurationValid(DeletePeaksByTargetFilterSettings configuration) {

		if(configuration != null) {
			String value = configuration.getValue();
			if(value != null && !value.isEmpty()) {
				return true;
			}
		}
		//
		return false;
	}
}
