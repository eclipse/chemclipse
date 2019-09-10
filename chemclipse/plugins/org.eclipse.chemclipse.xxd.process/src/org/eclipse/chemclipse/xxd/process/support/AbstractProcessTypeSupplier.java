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
package org.eclipse.chemclipse.xxd.process.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private Map<String, IProcessSupplier> processorSuppliers = new HashMap<>();
	private String category;

	public AbstractProcessTypeSupplier(String category) {
		this.category = category;
	}

	@Override
	public String getCategory() {

		return category;
	}

	@Override
	public Collection<IProcessSupplier> getProcessorSuppliers() {

		return Collections.unmodifiableCollection(processorSuppliers.values());
	}

	protected IProcessSupplier addProcessorSupplier(String id, String name, String description, Class<?> settingsClass, DataType... dataTypes) {

		ProcessorSupplier supplier = new ProcessorSupplier(id, dataTypes);
		supplier.setDescription(description);
		supplier.setName(name);
		supplier.setSettingsClass(settingsClass);
		addProcessorSupplier(supplier);
		return supplier;
	}

	protected void addProcessorSupplier(IProcessSupplier processorSupplier) {

		processorSuppliers.put(processorSupplier.getId(), processorSupplier);
	}

	@Override
	public final IProcessSupplier getProcessorSupplier(String id) {

		return processorSuppliers.get(id);
	}

	protected <T> IProcessingInfo<T> invalidId(String processorId) {

		ProcessingInfo<T> info = new ProcessingInfo<>();
		info.addErrorMessage(getClass().getSimpleName(), "Unknwon processor ID " + processorId);
		return info;
	}
}
