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
package org.eclipse.chemclipse.processing;

import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.text.ILabel;

/**
 * Allows to specify the general processing category of an item
 * 
 * @author Christoph Läubrich
 *
 */
public enum ProcessorCategory implements ILabel {

	/**
	 * Indicates that this item is located in the Filtering domain, that means it adds/removes/changes a specific item
	 */
	FILTER(TranslationSupport.getTranslationService().translate("%ProcessorCategory.FILTER", TranslationSupport.getContributorURI(ProcessorCategory.class))), //$NON-NLS-1$
	DETECTOR(TranslationSupport.getTranslationService().translate("%ProcessorCategory.DETECTOR", TranslationSupport.getContributorURI(ProcessorCategory.class))), //$NON-NLS-1$
	EXPORT(TranslationSupport.getTranslationService().translate("%ProcessorCategory.DETECTOR", TranslationSupport.getContributorURI(ProcessorCategory.class))), //$NON-NLS-1$
	CALCULATOR(TranslationSupport.getTranslationService().translate("%ProcessorCategory.CALCULATOR", TranslationSupport.getContributorURI(ProcessorCategory.class))); //$NON-NLS-1$

	private String label;

	ProcessorCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}
