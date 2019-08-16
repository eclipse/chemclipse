/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model;

public abstract class AbstractPeakProcessEntry implements IPeakProcessEntry {

	private String processorId;
	private boolean report = false;

	public AbstractPeakProcessEntry(String processorId) {
		if(processorId != null) {
			this.processorId = processorId;
		}
	}

	public AbstractPeakProcessEntry(String processorId, boolean report) {
		this(processorId);
		this.report = report;
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

	@Override
	public boolean isReport() {

		return report;
	}

	@Override
	public void setReport(boolean report) {

		this.report = report;
	}
}
