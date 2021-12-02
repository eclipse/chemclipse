/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.scans;

import java.text.MessageFormat;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.filter.IScanFilter;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.filter.FilterList;
import org.eclipse.chemclipse.xxd.model.filter.TargetsFilter;
import org.eclipse.chemclipse.xxd.model.settings.DeleteTargetsFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IScanFilter.class, Filter.class})
public class DeleteTargetsFilter implements IScanFilter<DeleteTargetsFilterSettings> {

	@Override
	public String getName() {

		return "Delete Target(s) Filter";
	}

	@Override
	public String getDescription() {

		return "Filter Scan Target(s)";
	}

	@Override
	public IProcessingResult<Boolean> filterIScans(FilterList<IScan> filterItems, DeleteTargetsFilterSettings configuration, IProgressMonitor monitor) throws IllegalArgumentException {

		if(configuration == null) {
			configuration = createNewConfiguration();
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(monitor, filterItems.size());
		DefaultProcessingResult<Boolean> result = new DefaultProcessingResult<>();
		int modified = 0;
		for(IScan scan : filterItems) {
			TargetsFilter.filter(scan, configuration);
			subMonitor.worked(1);
		}
		//
		result.setProcessingResult(Boolean.TRUE);
		result.addMessage(new ProcessingMessage(MessageType.INFO, getName(), MessageFormat.format("{0} scan(s) where processed", modified)));
		return result;
	}

	@Override
	public boolean acceptsIScan(IScan item) {

		return true;
	}
}
