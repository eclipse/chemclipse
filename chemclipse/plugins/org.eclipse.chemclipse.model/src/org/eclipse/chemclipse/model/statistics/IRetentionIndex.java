/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - group by retention index
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

public interface IRetentionIndex extends IVariable {

	String TYPE = "Retention Index";

	int getRetentionIndex();

	void setRetentionIndex(int retentionIndex);
}
