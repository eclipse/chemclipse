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
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;

public abstract class AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private String category = "";
	private List<DataType> supportedDataTypes = new ArrayList<>();
	private Map<String, Class<? extends IProcessSettings>> settingsClassMap = new HashMap<>();
	private Map<String, String> nameMap = new HashMap<>();
	private Map<String, String> descriptionMap = new HashMap<>();
	private List<String> processorIds = new ArrayList<>();

	public AbstractProcessTypeSupplier(String category, DataType[] dataTypes) {
		this.category = category;
		for(DataType dataType : dataTypes) {
			supportedDataTypes.add(dataType);
		}
	}

	@Override
	public String getCategory() {

		return category;
	}

	@Override
	public List<DataType> getSupportedDataTypes() {

		return supportedDataTypes;
	}

	@Override
	public Class<? extends IProcessSettings> getProcessSettingsClass(String processorId) throws Exception {

		return settingsClassMap.get(processorId);
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		return nameMap.getOrDefault(processorId, NOT_AVAILABLE);
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		return descriptionMap.getOrDefault(processorId, NOT_AVAILABLE);
	}

	@Override
	public List<String> getProcessorIds() throws Exception {

		return processorIds;
	}

	protected void addProcessorSettingsClass(String processorId, Class<? extends IProcessSettings> settingsClass) {

		settingsClassMap.put(processorId, settingsClass);
	}

	protected void addProcessorName(String processorId, String name) {

		nameMap.put(processorId, name);
	}

	protected void addProcessorDescription(String processorId, String description) {

		descriptionMap.put(processorId, description);
	}

	protected void addProcessorId(String processorId) {

		processorIds.add(processorId);
	}
}
