/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	FILTER(TranslationSupport.getTranslationService().translate("%ProcessorCategory.FILTER", Activator.getContributorURI())), //$NON-NLS-1$
	DETECTOR(TranslationSupport.getTranslationService().translate("%ProcessorCategory.DETECTOR", Activator.getContributorURI())), //$NON-NLS-1$
	EXPORT(TranslationSupport.getTranslationService().translate("%ProcessorCategory.DETECTOR", Activator.getContributorURI())), //$NON-NLS-1$
	CALCULATOR(TranslationSupport.getTranslationService().translate("%ProcessorCategory.CALCULATOR", Activator.getContributorURI())); //$NON-NLS-1$

	private String label;

	ProcessorCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}
