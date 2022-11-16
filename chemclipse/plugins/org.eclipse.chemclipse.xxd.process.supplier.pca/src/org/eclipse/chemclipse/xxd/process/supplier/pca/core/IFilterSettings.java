/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core;

import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.IFilter;

public interface IFilterSettings extends IDataModification {

	List<IFilter> getFilters();

	boolean isResetSelectedRetentionTimes();

	void setResetSelectedRetentionTimes(boolean resetSelectedRetentionTimes);
}
