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
package org.eclipse.chemclipse.converter.model.reports;

import java.util.List;

public interface IReports<T> extends List<T>, IFileAttributes {

	/**
	 * May return null.
	 * 
	 * @param number
	 * @return Report
	 */
	T getReport(int number);

	/**
	 * Returns the report with the highest number.
	 * May return null.
	 * 
	 * @param number
	 * @return Report
	 */
	T getReportFinal();
}