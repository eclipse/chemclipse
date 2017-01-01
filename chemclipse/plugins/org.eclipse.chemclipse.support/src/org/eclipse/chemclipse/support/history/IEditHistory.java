/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.history;

import java.util.List;

public interface IEditHistory {

	/**
	 * This method adds a entry to the edit history.
	 * 
	 * @param editInformation
	 */
	void add(IEditInformation editInformation);

	/**
	 * If no value is given, the sort order will be ascending by date.<br/>
	 * You can also choose a editHistorySortOrder.
	 * 
	 * @return List<IEditInformation>
	 */
	List<IEditInformation> getHistoryList();

	/**
	 * The editHistorySortOder defines the sort order of the history list.<br/>
	 * If no value is given, the sort order will be ascending by date.
	 * 
	 * @param editHistorySortOrder
	 * @return List<IEditInformation>
	 */
	List<IEditInformation> getHistoryList(EditHistorySortOrder editHistorySortOrder);
}