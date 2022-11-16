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

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.IReplacer;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.ITransformation;

public interface IPreprocessingSettings extends IDataModification {

	ICentering getCentering();

	void setCentering(ICentering centering);

	INormalization getNormalization();

	void setNormalization(INormalization normalization);

	ITransformation getTransformation();

	void setTransformation(ITransformation transformation);

	IReplacer getReplacer();

	void setReplacer(IReplacer replacer);

	boolean isModifyOnlySelectedVariable();

	void setModifyOnlySelectedVariable(boolean modifyOnlySelectedVariable);

	boolean isOnlySelected();

	void setOnlySelected(boolean onlySelected);

	boolean isRemoveUselessVariables();

	void setRemoveUselessVariables(boolean removeUselessVariables);
}