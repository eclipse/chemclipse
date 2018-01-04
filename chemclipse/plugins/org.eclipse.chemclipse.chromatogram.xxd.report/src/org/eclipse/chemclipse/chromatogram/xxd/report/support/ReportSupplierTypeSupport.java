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
package org.eclipse.chemclipse.chromatogram.xxd.report.support;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;
import org.eclipse.chemclipse.logging.core.Logger;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ReportSupplierTypeSupport {

	private static final Logger logger = Logger.getLogger(ReportSupplierTypeSupport.class);
	public static final String NOT_AVAILABLE = "n.a.";

	public String getReportName(IChromatogramReportSupplierEntry entry) {

		String reportName = NOT_AVAILABLE;
		try {
			IChromatogramReportSupplier reportSupplier = ChromatogramReports.getChromatogramReportSupplierSupport().getReportSupplier(entry.getReportSupplierId());
			reportName = reportSupplier.getReportName();
		} catch(NoReportSupplierAvailableException e) {
			logger.warn(e);
		}
		return reportName;
	}
}
