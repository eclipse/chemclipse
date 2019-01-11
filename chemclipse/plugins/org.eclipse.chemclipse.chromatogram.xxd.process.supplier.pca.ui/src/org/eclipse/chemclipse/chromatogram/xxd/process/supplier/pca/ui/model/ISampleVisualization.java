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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public interface ISampleVisualization extends ISample, IColor {

	static <S extends ISampleVisualization> Callback<S, Observable[]> extractor() {

		return (S s) -> new Observable[]{s.nameProperty(), s.groupNameProperty(), s.selectedProperty(), s.sampleDataHasBeenChangedProperty(), s.colorProperty()};
	}

	StringProperty groupNameProperty();

	StringProperty nameProperty();

	ReadOnlyLongProperty sampleDataHasBeenChangedProperty();

	BooleanProperty selectedProperty();

	long getSampleDataHasBeenChanged();

	void setSampleDataHasBeenChanged();
}
