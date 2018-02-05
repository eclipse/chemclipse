/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public interface ISample<D extends ISampleData> {

	static <S extends ISample<? extends ISampleData>> Callback<S, Observable[]> extractor() {

		return (S s) -> new Observable[]{s.nameProperty(), s.groupNameProperty(), s.selectedProperty(), s.sampleDataHasBeenChangedProperty()};
	}

	String getGroupName();

	String getName();

	List<D> getSampleData();

	long getSampleDataHasBeenChanged();

	StringProperty groupNameProperty();

	boolean isSelected();

	/*
	 * boolean isSelected(){
	 * return sampleMode.isSelected();
	 * }
	 */
	StringProperty nameProperty();

	ReadOnlyLongProperty sampleDataHasBeenChangedProperty();

	BooleanProperty selectedProperty();

	void setGroupName(String groupName);

	void setName(String name);

	void setSampleDataHasBeenChanged();

	void setSelected(boolean selected);
}