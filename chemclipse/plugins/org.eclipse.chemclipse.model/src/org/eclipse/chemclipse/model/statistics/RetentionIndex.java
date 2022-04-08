/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;

public class RetentionIndex extends AbstractVariable implements IRetentionIndex {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0");
	private int retentionIndex = 0;

	public static List<RetentionIndex> create(List<Integer> retentionIndices) {

		List<RetentionIndex> retentionIndexList = new ArrayList<>();
		for(int i = 0; i < retentionIndices.size(); i++) {
			retentionIndexList.add(new RetentionIndex(retentionIndices.get(i)));
		}
		return retentionIndexList;
	}

	public RetentionIndex(int retentionIndex) {

		super();
		this.retentionIndex = retentionIndex;
		setValue(convertValue());
		setType(IRetentionIndex.TYPE);
		setSelected(true);
	}

	public RetentionIndex(int retentionIndex, String description) {

		this(retentionIndex);
		setDescription(description);
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof IRetentionIndex) {
			IRetentionIndex retentionIndex = (IRetentionIndex)o;
			return Integer.compare(getRetentionIndex(), retentionIndex.getRetentionIndex());
		}
		return 0;
	}

	private String convertValue() {

		return decimalFormat.format(getRetentionIndex());
	}

	@Override
	public int getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(int retentionIndex) {

		this.retentionIndex = retentionIndex;
		setValue(convertValue());
	}
}