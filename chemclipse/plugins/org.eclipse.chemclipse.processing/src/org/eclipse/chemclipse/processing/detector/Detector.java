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
package org.eclipse.chemclipse.processing.detector;

import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.ProcessorCategory;
import org.eclipse.chemclipse.processing.filter.Filter;

/**
 * A {@link Detector} detects aspects of an input and return the result to the caller, this is different to e.g. a {@link Filter} in the way that the input data is not modified but new data is generated
 * This is only a base interface all Detectors inherit from, a concrete Detector implementation might implement other interfaces to specify the exact items it is able to detect
 * 
 * @author Christoph Läubrich
 *
 */
public interface Detector<ConfigType> extends Processor<ConfigType> {

	@Override
	default String getID() {

		return "detector:" + Processor.super.getID();
	}

	@Override
	default ProcessorCategory[] getProcessorCategories() {

		return new ProcessorCategory[]{ProcessorCategory.DETECTOR};
	}

	DetectorCategory getDetectorCategory();
}
