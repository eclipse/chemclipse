/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;

public abstract class LazyLoadingBooleanProperty extends SimpleBooleanProperty {

	private boolean loaded = false;

	public LazyLoadingBooleanProperty() {

	}

	public boolean isLoaded() {
		return loaded;
	}

	private final ChangeListener<Boolean> valueChangeListener = (o, ov, nv) -> {
		if (nv != null) {
			valueExternallyUpdated(nv);
		}
	};

	public void setLoaded(final boolean loaded) {

		// the value has been loaded. Trigger callback for write-access from now on.
		if (!this.loaded && loaded) {
			addListener(valueChangeListener);
		}
		// the loaded property has been reset manually. Remove change listener
		if (this.loaded && !loaded) {
			removeListener(valueChangeListener);
		}

		this.loaded = loaded;
	}

	/**
	 * Override this callback method to handle external value-change-events
	 * (not-lazily-loaded). This callback is only called, if the value is updated
	 * manually, i.e., not via the lazy-loading mechanism.
	 *
	 * @param nv
	 *            the new value
	 */
	protected void valueExternallyUpdated(final Boolean nv) {
		// override if needed

	}

	@Override
	public Boolean getValue() {
		if (!loaded) {
			startLoadingService();
		}
		return super.getValue();
	}

	protected void startLoadingService() {

		final Task<Boolean> s = LazyLoadingBooleanProperty.this.createTask();

		LazyLoadingThreads.getExecutorService().submit(s);

		s.setOnSucceeded(e -> {

			setValue(s.getValue());
			setLoaded(true);

		});
		// s.start();
		// System.err.println("Started");
	}

	protected abstract Task<Boolean> createTask();
}
