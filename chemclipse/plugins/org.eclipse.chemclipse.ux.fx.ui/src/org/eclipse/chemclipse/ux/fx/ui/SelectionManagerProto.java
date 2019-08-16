/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import java.util.Collection;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 * A prototype implementation for simple selection handling.
 * <p>
 * Usage:</br>
 * Update the selection anywhere:</br>
 *
 * <pre>
 * SelectionManagerLibraryPeakEntities.getInstance().selectionProperty()
 * 		.setAll(table.getSelectionModel().getSelectedItems());
 * </pre>
 *
 * Listen for selection changes somewhere else:</br>
 *
 * <pre>
 * SelectionManagerReferencePeakEntities.getInstance().getSelection().addListener(listener);
 * </pre>
 * </p>
 *
 * @author Alexander Kerner
 *
 * @param <T>
 *            Type of elements
 */
public class SelectionManagerProto<T> {

	/**
	 * All elements available.
	 */
	protected ListProperty<T> elements;
	/**
	 * Selected elements.
	 */
	protected ListProperty<T> selection;
	/**
	 * Read-only property holding the number of currently selected elements.
	 */
	protected SimpleIntegerProperty selectionSize = new SimpleIntegerProperty();
	/**
	 * Read-only property holding the number of all elements available.
	 */
	protected SimpleIntegerProperty elementsSize = new SimpleIntegerProperty();

	/**
	 * Creates a new, bound instance of {@code SelectionManagerProto}.
	 *
	 * @see #bindProperties()
	 */
	protected SelectionManagerProto() {
		elements = new SimpleListProperty<>(FXCollections.observableArrayList());
		selection = new SimpleListProperty<>(FXCollections.observableArrayList());
		bindProperties();
	}

	/**
	 * Binds the {@link #selectionSize} and {@link #elementsSize} properties.
	 */
	protected void bindProperties() {
		selectionSize.bind(Bindings.createIntegerBinding(() -> selection.size(), selection));
		elementsSize.bind(Bindings.createIntegerBinding(() -> elements.size(), elements));
	}

	/**
	 * Creates a new, bound instance of {@code SelectionManagerProto} using the
	 * provided {@code extractor}.
	 *
	 * @param extractor
	 *            element to Observable[] converter. Observable objects are listened
	 *            for changes on the element.
	 *
	 * @see FXCollections#observableArrayList(Callback)
	 */
	protected SelectionManagerProto(final Callback<T, Observable[]> extractor) {
		elements = new SimpleListProperty<>(FXCollections.observableArrayList(extractor));
		selection = new SimpleListProperty<>(FXCollections.observableArrayList(extractor));
		bindProperties();
	}

	// property accessors

	/**
	 *
	 */
	public final ListProperty<T> elementsProperty() {
		return this.elements;
	}

	/**
	 *
	 */
	public final ObservableList<T> getElements() {
		return this.elementsProperty().get();
	}

	/**
	 *
	 */
	public final void setElements(final Collection<? extends T> elements) {
		this.elementsProperty().setAll(elements);
	}

	/**
	 *
	 */
	public final ListProperty<T> selectionProperty() {
		return this.selection;
	}

	/**
	 *
	 */
	public final ObservableList<T> getSelection() {
		return this.selectionProperty().get();
	}

	/**
	 *
	 */
	public final void setSelection(final Collection<? extends T> selection) {
		this.selectionProperty().setAll(selection);
	}

	/**
	 *
	 */
	public final SimpleIntegerProperty selectionSizeProperty() {
		return this.selectionSize;
	}

	/**
	 *
	 */
	public final int getSelectionSize() {
		return this.selectionSizeProperty().get();
	}

	/**
	 *
	 */
	public final void setSelectionSize(final int selectionSize) {
		this.selectionSizeProperty().set(selectionSize);
	}

	/**
	 *
	 */
	public final SimpleIntegerProperty elementsSizeProperty() {
		return this.elementsSize;
	}

	/**
	 *
	 */
	public final int getElementsSize() {
		return this.elementsSizeProperty().get();
	}

	/**
	 *
	 */
	public final void setElementsSize(final int elementsSize) {
		this.elementsSizeProperty().set(elementsSize);
	}

}
