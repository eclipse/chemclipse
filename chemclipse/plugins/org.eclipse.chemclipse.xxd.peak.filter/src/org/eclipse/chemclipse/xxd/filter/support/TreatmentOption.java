/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - refactoring ILabel support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.support;

import org.eclipse.chemclipse.support.text.ILabel;

public enum TreatmentOption implements ILabel {
	ACTIVATE_PEAK("Activate"), // Select to enable a peak if certain constraints are matched
	DEACTIVATE_PEAK("Deactivate"), // Select to deactivate a peak if certain constraints are matched
	KEEP_PEAK("Keep"), // Select to keep a peak if certain constraints are matched
	DELETE_PEAK("Delete"); // Select to delete a peak if certain constraints are matched

	private String label;

	private TreatmentOption(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}
