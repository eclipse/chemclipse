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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessJob implements IBatchProcessJob {

	private List<IChromatogramInputEntry> chromatogramInputEntries;
	private List<IChromatogramProcessEntry> chromatogramProcessEntries;
	private List<IChromatogramOutputEntry> chromatogramOutputEntries;
	private List<IChromatogramReportSupplierEntry> chromatogramReportEntries;

	/**
	 * Creates a new batch process job.
	 */
	public BatchProcessJob() {
		/*
		 * Why are ArrayLists used here?
		 * The entries shall be processed in the order the user has chosen them.
		 */
		chromatogramInputEntries = new ArrayList<IChromatogramInputEntry>();
		chromatogramProcessEntries = new ArrayList<IChromatogramProcessEntry>();
		chromatogramOutputEntries = new ArrayList<IChromatogramOutputEntry>();
		chromatogramReportEntries = new ArrayList<IChromatogramReportSupplierEntry>();
	}

	@Override
	public List<IChromatogramInputEntry> getChromatogramInputEntries() {

		return chromatogramInputEntries;
	}

	@Override
	public List<IChromatogramProcessEntry> getChromatogramProcessEntries() {

		return chromatogramProcessEntries;
	}

	@Override
	public List<IChromatogramOutputEntry> getChromatogramOutputEntries() {

		return chromatogramOutputEntries;
	}

	@Override
	public List<IChromatogramReportSupplierEntry> getChromatogramReportEntries() {

		return chromatogramReportEntries;
	}
}
