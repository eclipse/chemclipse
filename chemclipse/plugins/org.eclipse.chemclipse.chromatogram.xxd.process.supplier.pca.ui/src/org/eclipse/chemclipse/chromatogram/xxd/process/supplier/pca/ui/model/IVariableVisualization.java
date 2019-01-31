/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.model.statistics.IVariable;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public interface IVariableVisualization extends IColor, IVariable {

	static <V extends IVariableVisualization> Callback<V, Observable[]> extractor() {

		return (V v) -> new Observable[]{v.descriptionProperty(), v.valueProperty(), v.typeProperty(), v.selectedProperty(), v.colorProperty()};
	}

	StringProperty typeProperty();

	BooleanProperty selectedProperty();

	StringProperty valueProperty();

	StringProperty descriptionProperty();

	StringProperty classificationProperty();
}
