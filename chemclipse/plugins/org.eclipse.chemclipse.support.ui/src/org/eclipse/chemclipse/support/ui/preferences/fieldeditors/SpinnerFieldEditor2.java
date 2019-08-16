/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * A field editor for a string type preference.
 * <p>
 * This class may be used as is, or subclassed as required.
 * </p>
 */
public class SpinnerFieldEditor2 extends FieldEditor {

	/**
	 * Validation strategy constant (value <code>0</code>) indicating that
	 * the editor should perform validation after every key stroke.
	 *
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_VALUE_MODIFICATION = 0;
	/**
	 * Validation strategy constant (value <code>1</code>) indicating that
	 * the editor should perform validation only when the text widget
	 * loses focus.
	 *
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_FOCUS_LOST = 1;
	/**
	 * Text limit constant (value <code>-1</code>) indicating unlimited
	 * text limit and width.
	 */
	public static int UNLIMITED = -1;
	/**
	 * Cached valid state.
	 */
	private boolean isValid;
	/**
	 * Old text value.
	 * 
	 * @since 3.4 this field is protected.
	 */
	protected int oldValue;
	/**
	 * The text field, or <code>null</code> if none.
	 */
	protected Spinner spinner;
	/**
	 * The error message, or <code>null</code> if none.
	 */
	private String errorMessage;
	/**
	 * The validation strategy;
	 * <code>VALIDATE_ON_KEY_STROKE</code> by default.
	 */
	private int validateStrategy = VALIDATE_ON_VALUE_MODIFICATION;

	/**
	 * Creates a new string field editor
	 */
	/**
	 * Creates a string field editor.
	 * Use the method <code>setTextLimit</code> to limit the text.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param min
	 *            Minimal value
	 * @param max
	 *            Maximal value
	 * @param strategy
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform
	 *            on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *            perform validation only after the text has been typed in
	 * @param parent
	 *            the parent of the field editor's control
	 * @since 2.0
	 */
	public SpinnerFieldEditor2(String name, String labelText, int strategy, Composite parent) {
		init(name, labelText);
		setValidateStrategy(strategy);
		isValid = false;
		errorMessage = JFaceResources.getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
		createControl(parent);
	}

	/**
	 * Creates a string field editor.
	 * Use the method <code>setTextLimit</code> to limit the text.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SpinnerFieldEditor2(String name, String labelText, Composite parent) {
		this(name, labelText, VALIDATE_ON_VALUE_MODIFICATION, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		GridData gd = (GridData)spinner.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * Checks whether the text input field contains a valid value or not.
	 *
	 * @return <code>true</code> if the field value is valid,
	 *         and <code>false</code> if invalid
	 */
	protected boolean checkState() {

		// call hook for subclasses
		boolean result = doCheckState();
		if(result) {
			clearErrorMessage();
		} else {
			showErrorMessage(errorMessage);
		}
		return result;
	}

	/**
	 * Hook for subclasses to do specific state checks.
	 * <p>
	 * The default implementation of this framework method does
	 * nothing and returns <code>true</code>. Subclasses should
	 * override this method to specific state checks.
	 * </p>
	 *
	 * @return <code>true</code> if the field value is valid,
	 *         and <code>false</code> if invalid
	 */
	protected boolean doCheckState() {

		return true;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code>
	 * framework method contributes the text field. Subclasses may override
	 * but must call <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		spinner = getTextControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		spinner.setLayoutData(gd);
	}

	@Override
	protected void doLoad() {

		if(spinner != null) {
			int value = getPreferenceStore().getInt(getPreferenceName());
			spinner.setSelection(value);
			oldValue = value;
		}
	}

	@Override
	protected void doLoadDefault() {

		if(spinner != null) {
			int value = getPreferenceStore().getDefaultInt(getPreferenceName());
			spinner.setSelection(value);
		}
		valueChanged();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), spinner.getText());
	}

	/**
	 * Returns the error message that will be displayed when and if
	 * an error occurs.
	 *
	 * @return the error message, or <code>null</code> if none
	 */
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}

	/**
	 * Returns the field editor's value.
	 *
	 * @return the current value
	 */
	public int getIntValue() {

		if(spinner != null) {
			return spinner.getSelection();
		}
		return getPreferenceStore().getInt(getPreferenceName());
	}

	/**
	 * Returns this field editor's text control.
	 *
	 * @return the text control, or <code>null</code> if no
	 *         text field is created yet
	 */
	protected Spinner getTextControl() {

		return spinner;
	}

	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 *
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Spinner getTextControl(Composite parent) {

		if(spinner == null) {
			spinner = new Spinner(parent, SWT.SINGLE | SWT.BORDER);
			spinner.setMinimum(Integer.MIN_VALUE);
			spinner.setMaximum(Integer.MAX_VALUE);
			spinner.setFont(parent.getFont());
			switch(validateStrategy) {
				case VALIDATE_ON_VALUE_MODIFICATION:
					spinner.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {

							valueChanged();
						}
					});
					break;
				case VALIDATE_ON_FOCUS_LOST:
					spinner.addKeyListener(new KeyAdapter() {

						@Override
						public void keyPressed(KeyEvent e) {

							clearErrorMessage();
						}
					});
					spinner.addFocusListener(new FocusAdapter() {

						@Override
						public void focusGained(FocusEvent e) {

							refreshValidState();
						}

						@Override
						public void focusLost(FocusEvent e) {

							valueChanged();
							clearErrorMessage();
						}
					});
					break;
				default:
					Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
			}
			spinner.addDisposeListener(event -> spinner = null);
		} else {
			checkParent(spinner, parent);
		}
		return spinner;
	}

	@Override
	public boolean isValid() {

		return isValid;
	}

	@Override
	protected void refreshValidState() {

		isValid = checkState();
	}

	/**
	 * Sets the error message that will be displayed when and if
	 * an error occurs.
	 *
	 * @param message
	 *            the error message
	 */
	public void setErrorMessage(String message) {

		errorMessage = message;
	}

	@Override
	public void setFocus() {

		if(spinner != null) {
			spinner.setFocus();
		}
	}

	/**
	 * Sets this field editor's value.
	 *
	 * @param value
	 *            the new value, or <code>null</code> meaning the empty string
	 */
	public void setValue(Integer value) {

		if(spinner != null) {
			if(value == null) {
				value = 0;// $NON-NLS-1$
			}
			oldValue = spinner.getSelection();
			if(oldValue != value) {
				spinner.setSelection(value);
				valueChanged();
			}
		}
	}

	/**
	 * Sets the strategy for validating the text.
	 * <p>
	 * Calling this method has no effect after <code>createPartControl</code>
	 * is called. Thus this method is really only useful for subclasses to call
	 * in their constructor. However, it has public visibility for backward
	 * compatibility.
	 * </p>
	 *
	 * @param value
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform
	 *            on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *            perform validation only after the text has been typed in
	 */
	public void setValidateStrategy(int value) {

		Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_VALUE_MODIFICATION);
		validateStrategy = value;
	}

	/**
	 * Shows the error message set via <code>setErrorMessage</code>.
	 */
	public void showErrorMessage() {

		showErrorMessage(errorMessage);
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change
	 * to the value (<code>VALUE</code> property) provided that the old and
	 * new values are different.
	 * <p>
	 * This hook is <em>not</em> called when the text is initialized
	 * (or reset to the default value) from the preference store.
	 * </p>
	 */
	protected void valueChanged() {

		setPresentsDefaultValue(false);
		boolean oldState = isValid;
		refreshValidState();
		if(isValid != oldState) {
			fireStateChanged(IS_VALID, oldState, isValid);
		}
		int newValue = spinner.getSelection();
		if(newValue != oldValue) {
			fireValueChanged(VALUE, oldValue, newValue);
			oldValue = newValue;
		}
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		super.setEnabled(enabled, parent);
		getTextControl(parent).setEnabled(enabled);
	}
}
