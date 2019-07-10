/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.filter;

import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.ProcessorCategory;

/**
 * A {@link Filter} is responsible to filter different objects in a configurable manner.
 * This is only a base interface all Filters inherit from, a concrete Filter implementation might implement other interfaces to specify the exact items it is able to filter
 * 
 * @author Christoph Läubrich
 *
 */
public interface Filter<ConfigType> extends Processor<ConfigType> {

	@Override
	default String getID() {

		return "filter:" + Processor.super.getID();
	}

	@Override
	default ProcessorCategory[] getProcessorCategories() {

		return new ProcessorCategory[]{ProcessorCategory.FILTER};
	}
}
