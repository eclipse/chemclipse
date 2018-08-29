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

public class ScanProcessorSupplier implements IScanProcessorSupplier {

	private String id = "";
	private String description = "";
	private String processorName = "";

	@Override
	public String getId() {

		return id;
	}

	@Override
	public void setId(String id) {

		this.id = id;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String getProcessorName() {

		return processorName;
	}

	@Override
	public void setProcessorName(String processorName) {

		this.processorName = processorName;
	}
}
