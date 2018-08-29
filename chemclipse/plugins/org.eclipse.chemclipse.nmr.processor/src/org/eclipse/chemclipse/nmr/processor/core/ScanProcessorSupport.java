/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.processor.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.nmr.processor.exceptions.NoProcessorAvailableException;

public class ScanProcessorSupport extends ArrayList<IScanProcessorSupplier> implements IScanProcessorSupport {

	private static final long serialVersionUID = 4697045350686553539L;

	@Override
	public String[] getProcessorNames() throws NoProcessorAvailableException {

		List<String> processorNames = new ArrayList<>();
		for(IScanProcessorSupplier supplier : this) {
			processorNames.add(supplier.getProcessorName());
		}
		return processorNames.toArray(new String[processorNames.size()]);
	}

	@Override
	public String getProcessorId(int index) throws NoProcessorAvailableException {

		String processorId = "";
		IScanProcessorSupplier supplier = get(index);
		if(supplier != null) {
			processorId = supplier.getId();
		}
		return processorId;
	}

	@Override
	public String getProcessorId(String name) throws NoProcessorAvailableException {

		for(IScanProcessorSupplier supplier : this) {
			if(supplier.getProcessorName().equals(name)) {
				return supplier.getId();
			}
		}
		return "";
	}

	@Override
	public List<String> getAvailableProcessorIds() throws NoProcessorAvailableException {

		List<String> processorIds = new ArrayList<>();
		for(IScanProcessorSupplier supplier : this) {
			processorIds.add(supplier.getId());
		}
		return processorIds;
	}

	@Override
	public List<IScanProcessorSupplier> getSupplier() {

		return Collections.unmodifiableList(this);
	}

	@Override
	public IScanProcessorSupplier getSupplier(String id) throws NoProcessorAvailableException {

		for(IScanProcessorSupplier supplier : this) {
			if(supplier.getId().equals(id)) {
				return supplier;
			}
		}
		return null;
	}
}
