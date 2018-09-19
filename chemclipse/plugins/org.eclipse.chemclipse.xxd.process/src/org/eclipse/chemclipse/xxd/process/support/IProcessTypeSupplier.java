/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IProcessTypeSupplier {

	String NOT_AVAILABLE = "n.a.";

	String getCategory();

	String getSupportedDataTypes();

	String getProcessorName(String processorId) throws Exception;

	List<String> getPluginIds() throws Exception;

	@SuppressWarnings("rawtypes")
	IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProgressMonitor monitor);
}
