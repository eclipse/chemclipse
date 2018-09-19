/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.model;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramProcessEntry implements IChromatogramProcessEntry {

	private String processCategory;
	private String processorId = "";

	public ChromatogramProcessEntry(String processCategory, String processorId) {
		if(processCategory != null && processorId != null) {
			this.processCategory = processCategory;
			this.processorId = processorId;
		}
	}

	@Override
	public String getProcessCategory() {

		return processCategory;
	}

	@Override
	public void setProcessCategory(String processCategory) {

		if(processCategory != null) {
			this.processCategory = processCategory;
		}
	}

	@Override
	public String getProcessorId() {

		return processorId;
	}

	@Override
	public void setProcessorId(String processorId) {

		if(processorId != null) {
			this.processorId = processorId;
		}
	}
}
