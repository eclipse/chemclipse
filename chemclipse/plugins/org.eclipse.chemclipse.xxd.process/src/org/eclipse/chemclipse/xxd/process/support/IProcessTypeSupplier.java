/*******************************************************************************
 * Copyright (c) 2012, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IProcessTypeSupplier<T> {

	String NOT_AVAILABLE = "n.a.";

	String getCategory();

	List<DataType> getSupportedDataTypes();

	List<ProcessorSupplier> getProcessorSuppliers();

	Class<? extends IProcessSettings> getProcessSettingsClass(String processorId) throws Exception;

	String getProcessorName(String processorId) throws Exception;

	String getProcessorDescription(String processorId) throws Exception;

	List<String> getProcessorIds() throws Exception;

	IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor);
}
