/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - formatting
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Set;

public interface IMarkedTraces<S extends IMarkedTrace> extends Set<S> {

	MarkedTraceModus getMarkedTraceModus();

	Set<Integer> getTraces();
}