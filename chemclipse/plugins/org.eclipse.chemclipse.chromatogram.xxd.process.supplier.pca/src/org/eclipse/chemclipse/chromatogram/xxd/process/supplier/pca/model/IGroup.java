/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

import javafx.beans.Observable;
import javafx.util.Callback;

public interface IGroup<S extends ISample<? extends ISampleData>> extends ISample<ISampleDataGroup<S>> {

	static <G extends IGroup<? extends ISample<? extends ISampleData>>> Callback<G, Observable[]> extractor() {

		return (G g) -> new Observable[]{g.nameProperty(), g.groupNameProperty(), g.selectedProperty(), g.sampleDataHasBeenChangedProperty()};
	}

	List<S> getSamples();
}
