/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditHistory implements IEditHistory {

	private List<IEditInformation> history;

	public EditHistory() {
		history = new ArrayList<IEditInformation>();
	}

	@Override
	public void add(IEditInformation editInformation) {

		history.add(editInformation);
	}

	@Override
	public List<IEditInformation> getHistoryList() {

		return getHistoryList(EditHistorySortOrder.DATE_ASC);
	}

	@Override
	public List<IEditInformation> getHistoryList(EditHistorySortOrder editHistorySortOrder) {

		Collections.sort(history, new EditInformationComparator(editHistorySortOrder));
		return history;
	}
}
