/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - dirty flagging
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import java.util.NavigableMap;

public interface ISeparationColumnIndices extends NavigableMap<Integer, IRetentionIndexEntry> {

	ISeparationColumn getSeparationColumn();

	void setSeparationColumn(ISeparationColumn separationColumn);

	void put(IRetentionIndexEntry retentionIndexEntry);

	boolean isDirty();

	void setDirty(boolean isDirty);
}