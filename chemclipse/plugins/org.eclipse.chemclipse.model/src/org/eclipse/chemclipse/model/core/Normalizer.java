/*******************************************************************************
 * Copyright (c) 2016 <a href="mailto:alexander.kerner@openchrom.net">Alexander Kerner</a>.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * <a href="mailto:alexander.kerner@openchrom.net">Alexander Kerner</a> - initial API and implementation
*******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.sf.kerner.utils.UtilArray;

public class Normalizer {
    
    private final double multiplicator;

    public Normalizer(double referenceValue, Collection<? extends Double> values) {
	this.multiplicator = referenceValue / getMaxValue(values);
    }
    
    public Normalizer(double referenceValue, double... values) {
	this.multiplicator = referenceValue / getMaxValue(Arrays.asList(UtilArray.toObject(values)));
    }
    
    private double getMaxValue(Collection<? extends Double> values) {
	return Collections.max(values);
    }

    public Normalizer(double referenceValue, double maxValue) {
	super();
	this.multiplicator = referenceValue / maxValue;
    }

    public double calculateNormalizedValue(double value) {
	return value * multiplicator;
    }

}
