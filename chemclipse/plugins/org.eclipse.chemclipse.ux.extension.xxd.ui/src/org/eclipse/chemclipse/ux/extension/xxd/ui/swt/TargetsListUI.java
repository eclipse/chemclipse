/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make more generic useable
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TargetsListUI extends ExtendedTableViewer {

	private static final String[] TITLES = TargetsLabelProvider.TITLES;
	private static final int[] BOUNDS = TargetsLabelProvider.BOUNDS;
	//
	private final TargetsLabelProvider labelProvider = new TargetsLabelProvider();
	private final TargetsComparator targetsComparator = new TargetsComparator();
	private final TargetListFilter targetListFilter = new TargetListFilter();
	//
	private Integer retentionTime = null;
	private Float retentionIndex = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
	private DecimalFormat decimalFormatInteger = ValueFormat.getDecimalFormatEnglish("0");
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public TargetsListUI(Composite parent, int style) {

		this(parent, TITLES, style);
	}

	public void setComparator(boolean active) {

		if(active) {
			setComparator(targetsComparator);
			sortTable();
		} else {
			setComparator(null);
			refresh();
		}
	}

	public TargetsListUI(Composite parent, String[] alternativeTitles, int style) {

		super(parent, style | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		//
		createColumns(alternativeTitles, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(false);
		setFilters(new ViewerFilter[]{targetListFilter});
		setCellColorProvider();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		targetListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void updateSourceRange(Integer retentionTime, Float retentionIndex) {

		this.retentionTime = retentionTime;
		this.retentionIndex = retentionIndex;
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

		if(getComparator() != null) {
			int column = 0;
			int sortOrder = TargetsComparator.DESCENDING;
			//
			targetsComparator.setColumn(column);
			targetsComparator.setDirection(sortOrder);
			refresh();
			targetsComparator.setDirection(1 - sortOrder);
			targetsComparator.setColumn(column);
		}
	}

	public void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(TargetsLabelProvider.VERIFIED_MANUALLY)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.CAS)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.COMMENTS)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.FORMULA)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.SMILES)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.INCHI)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.CONTRIBUTOR)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.REFERENCE_ID)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			}
		}
	}

	private void setCellColorProvider() {

		setColorProviderRetentionTime();
		setColorProviderRetentionIndex();
	}

	private void setColorProviderRetentionTime() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(TargetsLabelProvider.INDEX_RETENTION_TIME);
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@Override
				public void update(ViewerCell cell) {

					if(cell != null) {
						Object element = cell.getElement();
						if(element instanceof IIdentificationTarget) {
							IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
							ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
							int retentionTimeTarget = libraryInformation.getRetentionTime();
							//
							if(retentionTime != null && retentionTimeTarget != 0) {
								//
								boolean useAbsoluteDeviation = preferenceStore.getBoolean(PreferenceConstants.P_USE_ABSOLUTE_DEVIATION_RETENTION_TIME);
								double deviation;
								double deviationWarn;
								double deviationError;
								//
								if(useAbsoluteDeviation) {
									deviation = Math.abs(retentionTime - retentionTimeTarget);
									deviationWarn = preferenceStore.getInt(PreferenceConstants.P_RETENTION_TIME_DEVIATION_ABS_OK);
									deviationError = preferenceStore.getInt(PreferenceConstants.P_RETENTION_TIME_DEVIATION_ABS_WARN);
								} else {
									deviation = (Math.abs(retentionTime - retentionTimeTarget) / retentionTimeTarget) * 100.0d;
									deviationWarn = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_TIME_DEVIATION_REL_OK);
									deviationError = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_TIME_DEVIATION_REL_WARN);
								}
								//
								if(deviation < deviationWarn) {
									cell.setBackground(Colors.GREEN);
									cell.setForeground(Colors.BLACK);
								} else if(deviation >= deviationWarn && deviation < deviationError) {
									cell.setBackground(Colors.YELLOW);
									cell.setForeground(Colors.BLACK);
								} else if(deviation >= deviationError) {
									cell.setBackground(Colors.RED);
									cell.setForeground(Colors.BLACK);
								}
							}
							//
							String text = decimalFormat.format(libraryInformation.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
							//
							cell.setText(text);
							super.update(cell);
						}
					}
				}
			});
		}
	}

	private void setColorProviderRetentionIndex() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(TargetsLabelProvider.INDEX_RETENTION_INDEX);
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@Override
				public void update(ViewerCell cell) {

					if(cell != null) {
						Object element = cell.getElement();
						if(element instanceof IIdentificationTarget) {
							IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
							ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
							float retentionIndexTarget = libraryInformation.getRetentionIndex();
							//
							if(retentionIndex != null && retentionIndexTarget != 0) {
								//
								boolean useAbsoluteDeviation = preferenceStore.getBoolean(PreferenceConstants.P_USE_ABSOLUTE_DEVIATION_RETENTION_INDEX);
								double deviation;
								double deviationWarn;
								double deviationError;
								//
								if(useAbsoluteDeviation) {
									deviation = Math.abs(retentionIndex - retentionIndexTarget);
									deviationWarn = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_ABS_OK);
									deviationError = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_ABS_WARN);
								} else {
									deviation = (Math.abs(retentionIndex - retentionIndexTarget) / retentionIndexTarget) * 100.0d;
									deviationWarn = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_REL_OK);
									deviationError = preferenceStore.getFloat(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_REL_WARN);
								}
								//
								if(deviation < deviationWarn) {
									cell.setBackground(Colors.GREEN);
									cell.setForeground(Colors.BLACK);
								} else if(deviation >= deviationWarn && deviation < deviationError) {
									cell.setBackground(Colors.YELLOW);
									cell.setForeground(Colors.BLACK);
								} else if(deviation >= deviationError) {
									cell.setBackground(Colors.RED);
									cell.setForeground(Colors.BLACK);
								}
							}
							//
							String text;
							if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
								text = decimalFormatInteger.format(libraryInformation.getRetentionIndex());
							} else {
								text = decimalFormat.format(libraryInformation.getRetentionIndex());
							}
							//
							cell.setText(text);
							super.update(cell);
						}
					}
				}
			});
		}
	}
}
