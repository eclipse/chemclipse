/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
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
	FILTER(Messages.getString("ProcessorCategory.FILTER")), //$NON-NLS-1$
	DETECTOR(Messages.getString("ProcessorCategory.DETECTOR")), //$NON-NLS-1$
	EXPORT(Messages.getString("ProcessorCategory.DETECTOR")), //$NON-NLS-1$
	CALCULATOR(Messages.getString("ProcessorCategory.CALCULATOR")); //$NON-NLS-1$

	private String label;

	ProcessorCategory(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}
