/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MarkedTraces extends ArrayList<IMarkedTrace> implements IMarkedTraces<IMarkedTrace> {

	private static final long serialVersionUID = -7575358523289086994L;
	//
	private MarkedTraceModus markedTraceModus;

	public MarkedTraces() {

		this(MarkedTraceModus.INCLUDE);
	}

	public MarkedTraces(MarkedTraceModus markedTraceModus) {

		this.markedTraceModus = markedTraceModus;
	}

	@Override
	public MarkedTraceModus getMarkedTraceModus() {

		return markedTraceModus;
	}

	@Override
	public Set<Integer> getTraces() {

		Set<Integer> traces = new HashSet<Integer>();
		//
		for(IMarkedTrace markedTrace : this) {
			traces.add(markedTrace.castTrace());
		}
		//
		return traces;
	}
}