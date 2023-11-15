/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core.support;

import org.eclipse.chemclipse.pcr.model.l10n.Messages;
import org.eclipse.chemclipse.support.text.ILabel;

public enum LabelSetting implements ILabel {
	SAMPLENAME(Messages.sampleID), //
	COORDINATE(Messages.coordinate), //
	COORDINATE_SAMPLENAME(Messages.sampleCoordinate); //

	private String label = ""; //$NON-NLS-1$

	private LabelSetting(String label) {

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
