/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Gives information about the history of the chromatogram.
 * 
 * @author eselmeister
 */
public class EditHistory implements IEditHistory {

	/*
	 * Why do we use a TreeMap here instead of a ... ArrayList? A TreeMap
	 * implements NavigableMap so it is possible to search after a subset of
	 * time values. Let me say, if you are only interested in a certain time
	 * range (and not the whole history) you could extract this time range by
	 * using the NavigableMap interface.
	 */
	private NavigableMap<Long, IEditInformation> history;

	public EditHistory() {

		history = new TreeMap<Long, IEditInformation>();
	}

	@Override
	public void add(IEditInformation editInformation) {

		history.put(editInformation.getDate().getTime(), editInformation);
	}

	@Override
	public List<IEditInformation> getHistoryList() {

		return getHistoryList(EditHistorySortOrder.DATE_ASC);
	}

	@Override
	public List<IEditInformation> getHistoryList(EditHistorySortOrder editHistorySortOrder) {

		List<IEditInformation> historyList = new ArrayList<IEditInformation>(history.values());
		Collections.sort(historyList, new EditInformationComparator(editHistorySortOrder));
		return historyList;
	}
}
