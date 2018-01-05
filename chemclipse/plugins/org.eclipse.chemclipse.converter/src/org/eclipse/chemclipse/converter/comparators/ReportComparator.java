/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.comparators;

import java.util.Comparator;

import org.eclipse.chemclipse.converter.model.reports.IReport;

@SuppressWarnings("rawtypes")
public class ReportComparator implements Comparator<IReport> {

	@Override
	public int compare(IReport report1, IReport report2) {

		return Integer.compare(report1.getReportNumber(), report2.getReportNumber());
	}
}
