/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SamplesSelectionTree {

	private PcaEditor pcaEditor;
	private List<ISample> samples = new ArrayList<>();

	public SamplesSelectionTree(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
	}

	public void create(Composite parent) {

		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		TreeItem groupTreeItem;
		String groupName;
		boolean isSelectSample = false;
		TreeItem treeItem;
		Iterator<ISample> it = samples.iterator();
		if(it.hasNext()) {
			/*
			 * set first branch, which contains group name
			 */
			ISample sample = it.next();
			groupTreeItem = new TreeItem(tree, SWT.None);
			groupName = sample.getGroupName();
			if(groupName != null) {
				groupTreeItem.setText(groupName);
			} else {
				groupTreeItem.setText("----");
			}
			treeItem = new TreeItem(groupTreeItem, SWT.None);
			setSampleTreeItem(sample, treeItem);
			isSelectSample = isSelectSample || sample.getPcaResult().isDisplayed();
			groupTreeItem.setExpanded(true);
			while(it.hasNext()) {
				sample = it.next();
				if(ObjectUtils.compare(sample.getGroupName(), groupName) == 0) {
					treeItem = new TreeItem(groupTreeItem, SWT.None);
					setSampleTreeItem(sample, treeItem);
					isSelectSample = isSelectSample || sample.getPcaResult().isDisplayed();
				} else {
					groupTreeItem.setChecked(isSelectSample);
					/*
					 * set branch, which contains group name
					 */
					groupTreeItem = new TreeItem(tree, SWT.None);
					isSelectSample = false;
					groupName = sample.getGroupName();
					if(groupName != null) {
						groupTreeItem.setText(groupName);
					} else {
						groupTreeItem.setText("----");
					}
					treeItem = new TreeItem(groupTreeItem, SWT.None);
					setSampleTreeItem(sample, treeItem);
					isSelectSample = isSelectSample || sample.getPcaResult().isDisplayed();
				}
			}
			groupTreeItem.setChecked(isSelectSample);
		}
		/*
		 * add check listener
		 */
		tree.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail != SWT.CHECK) {
					return;
				}
				TreeItem item = (TreeItem)event.item;
				boolean isChecked = item.getChecked();
				/*
				 * set selected item
				 */
				ISample sample = (ISample)item.getData();
				if(sample != null) {
					sample.getPcaResult().setDisplayed(isChecked);
				}
				/*
				 * set children are checked according to selected item
				 */
				TreeItem[] children = item.getItems();
				for(TreeItem treeItem : children) {
					treeItem.setChecked(isChecked);
					sample = (ISample)treeItem.getData();
					if(sample != null) {
						sample.getPcaResult().setDisplayed(isChecked);
					}
				}
				/*
				 * set parent if any child of parent is set checked, parent is checked also.
				 */
				TreeItem parentItem = item.getParentItem();
				if(parentItem != null) {
					TreeItem[] sibs = parentItem.getItems();
					boolean checkParentItem = false;
					for(TreeItem sib : sibs) {
						sample = (ISample)sib.getData();
						if(sample != null) {
							checkParentItem = checkParentItem || sample.getPcaResult().isDisplayed();
						}
						parentItem.setChecked(checkParentItem);
					}
				}
				/*
				 * update editors
				 */
				pcaEditor.updateSelection();
			}
		});
	}

	private void setSampleTreeItem(ISample sample, TreeItem item) {

		item.setChecked(sample.getPcaResult().isDisplayed());
		item.setText(sample.getName());
		item.setData(sample);
	}

	public void update() {

		/*
		 * clear all
		 */
		samples.clear();
		/*
		 * insert and sort samples
		 */
		IPcaResults results = pcaEditor.getPcaResults();
		if(results != null) {
			results.getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toCollection(() -> samples));
			PcaUtils.sortSampleListByName(samples);
			PcaUtils.sortSampleListByGroup(samples);
		}
	}
}
