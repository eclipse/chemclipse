/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model;

import java.util.HashMap;
import java.util.Map;

public class LigninRatios implements ILigninRatios {

	private Map<String, Double> results = new HashMap<String, Double>();

	@Override
	public Map<String, Double> getResults() {

		return results;
	}
}
