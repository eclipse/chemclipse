/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add generics, add method for custom error msg
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;

public abstract class AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	protected static final DataType[] ALL_DATA_TYPES = new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD};
	protected static final DataType[] MSD_DATA_TYPES = new DataType[]{DataType.MSD};
	protected static final DataType[] MSD_CSD_DATA_TYPES = new DataType[]{DataType.MSD, DataType.CSD};
	protected static final DataType[] CSD_DATA_TYPES = new DataType[]{DataType.CSD};
	protected static final DataType[] WSD_DATA_TYPES = new DataType[]{DataType.WSD};
	private Map<String, IProcessSupplier<?>> processorSuppliers = new HashMap<>();
	//
	private String category;

	public AbstractProcessTypeSupplier(String category) {
		this.category = category;
	}

	@Override
	public String getCategory() {

		return category;
	}

	@Override
	public List<IProcessSupplier<?>> getProcessorSuppliers() {

		return new ArrayList<>(processorSuppliers.values());
	}

	protected void addProcessorSupplier(ProcessorSupplier processorSupplier) {

		processorSuppliers.put(processorSupplier.getId(), processorSupplier);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <ConfigType> IProcessSupplier<ConfigType> getProcessorSupplier(String id) {

		IProcessSupplier<?> supplier = processorSuppliers.get(id);
		if(supplier == null) {
			String backCompatId = getBackCompatId(id);
			if(backCompatId != null) {
				return (IProcessSupplier<ConfigType>)processorSuppliers.get(backCompatId);
			}
		}
		return (IProcessSupplier<ConfigType>)supplier;
	}

	protected String getBackCompatId(String id) {

		return null;
	}

	protected <T> IProcessingInfo<T> getProcessingResult(MessageProvider messages, T result) {

		ProcessingInfo<T> processingInfo = new ProcessingInfo<T>();
		processingInfo.setProcessingResult(result);
		if(messages != null) {
			processingInfo.addMessages(messages);
		}
		return processingInfo;
	}

	protected <T> IProcessingInfo<T> getProcessingInfoError(String processorId) {

		return getProcessingInfoError(processorId, "The data is not supported by the processor.");
	}

	protected <T> IProcessingInfo<T> getProcessingInfoError(String processorId, String message) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<T>();
		processingInfo.addErrorMessage(processorId, message);
		return processingInfo;
	}
}
