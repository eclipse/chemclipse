/*******************************************************************************
 * Copyright (c) 2019, 202 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing;

public interface IDataModificator extends IPreprocessing {

	boolean isModifyOnlySelectedVariable();

	void setModifyOnlySelectedVariable(boolean modifyOnlySelectedVariable);

	boolean isRemoveUselessVariables();

	void setRemoveUselessVariables(boolean removeUselessVariables);
}
