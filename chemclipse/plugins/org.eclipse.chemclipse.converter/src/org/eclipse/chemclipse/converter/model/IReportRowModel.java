/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model;

import java.util.List;

public interface IReportRowModel extends List<List<String>> {

	/**
	 * Returns -1 of the column identified by the name can't be found.
	 * It is assumed that the first row contains the column header.
	 * 
	 * @param columnName
	 * @return int
	 */
	int getColumnIndex(String columnName);
}
