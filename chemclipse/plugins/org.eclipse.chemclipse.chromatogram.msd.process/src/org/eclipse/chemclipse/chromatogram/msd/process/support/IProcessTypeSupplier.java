/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.support;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public interface IProcessTypeSupplier {

	String NOT_AVAILABLE = "n.a.";

	String getCategory();

	String getProcessorName(String processorId) throws Exception;

	List<String> getPluginIds() throws Exception;

	IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor);
}
