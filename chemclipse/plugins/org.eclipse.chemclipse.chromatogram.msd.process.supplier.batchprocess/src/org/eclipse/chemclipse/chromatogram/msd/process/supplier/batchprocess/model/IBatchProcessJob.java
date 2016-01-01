/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model;

import java.util.List;

import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IBatchProcessJob {

	/**
	 * Returns the list of chromatogram input entries.
	 * 
	 * @return List<ChromatogramInputEntry>
	 */
	List<IChromatogramInputEntry> getChromatogramInputEntries();

	/**
	 * Returns the list of all entries to process each chromatogram.
	 * 
	 * @return List<IChromatogramProcessEntry>
	 */
	List<IChromatogramProcessEntry> getChromatogramProcessEntries();

	/**
	 * Returns the chromatogram output entries.
	 * 
	 * @return List<IChromatogramOutputEntry>
	 */
	List<IChromatogramOutputEntry> getChromatogramOutputEntries();

	/**
	 * Returns the chromatogram report entries.
	 * 
	 * @return List<IChromatogramReportEntry>
	 */
	List<IChromatogramReportSupplierEntry> getChromatogramReportEntries();
}
