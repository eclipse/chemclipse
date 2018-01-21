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

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public interface IVariable extends Comparable<IVariable> {

	static <V extends IVariable> Callback<V, Observable[]> extractor() {

		return (V v) -> new Observable[]{v.descriptionProperty(), v.valueProperty(), v.typeProperty(), v.selectedProperty()};
	}

	StringProperty descriptionProperty();

	String getDescription();

	String getType();

	String getValue();

	boolean isSelected();

	BooleanProperty selectedProperty();

	void setDescription(String description);

	void setSelected(boolean selected);

	void setType(String type);

	void setValue(String value);

	StringProperty typeProperty();

	StringProperty valueProperty();
}
