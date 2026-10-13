/*******************************************************************************
 * Copyright (c) 2019, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.detector;

import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.text.ILabel;

public enum DetectorCategory implements ILabel {

	PEAK(TranslationSupport.getTranslationService().translate("%DetectorCategory.PEAK", TranslationSupport.getContributorURI(DetectorCategory.class)));

	private String label;

	private DetectorCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}