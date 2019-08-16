#!/bin/bash

#*******************************************************************************
# Copyright (c) 2015, 2018 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
# 	Dr. Philip Wenig - initial API and implementation
#*******************************************************************************

#
# Update descriptions on http://git.eclipse.org/c/chemclipse/
# Login on build.eclipse.org and go to /gitroot/chemclipse
#
echo "Start setting project descriptions"

echo 'Retention index calculation based on AMDIS *.cal files.' > org.eclipse.chemclipse.amdiscalri.git/description
echo 'Import/export of AMDIS file formats (*.msl, *.msp, *.elu).' > org.eclipse.chemclipse.amdismsdata.git/description
echo 'Baseline detector.' > org.eclipse.chemclipse.baselinedetec.git/description
echo 'Baseline detector and mass spectrum filter (SNIP algorithm).' > org.eclipse.chemclipse.baselinesnip.git/description
echo 'Chromatogram batch processing.' > org.eclipse.chemclipse.chemclipsebatchj.git/description
echo 'ChemClipse core platform.' > org.eclipse.chemclipse.chemclipsecore.git/description
echo 'ChemClipse product build files.' > org.eclipse.chemclipse.chemclipse.git/description
echo 'ChemClipse file format (*.ocb)' > org.eclipse.chemclipse.chemclipsems.git/description
echo 'XML based file format for chromatogram.' > org.eclipse.chemclipse.chemclipsemsx.git/description
echo 'Chromatogram quantitation support.' > org.eclipse.chemclipse.chemclipsequant.git/description
echo 'Export of chromatograms to *.svg format.' > org.eclipse.chemclipse.chemclipsesvg.git/description
echo 'Durbin-Watson classifier.' > org.eclipse.chemclipse.classifierdurbinwatson.git/description
echo 'Water, nitrogen and carbon dioxide classifier.' > org.eclipse.chemclipse.classifierwnc.git/description
echo 'Feature defintions for the compilation.' > org.eclipse.chemclipse.compilationbase.git/description
echo 'Mass spectrum comparison based on cosine phi.' > org.eclipse.chemclipse.compmsdistance.git/description
echo 'Mass spectrum comparison based on INCOS.' > org.eclipse.chemclipse.compmsincos.git/description
echo 'Import/export of AniML file format (*.animl).' > org.eclipse.chemclipse.converteraniml.git/description
echo 'Import/export of MassBank file format (*.txt).' > org.eclipse.chemclipse.convertermassbank.git/description
echo 'Import/export of HDF5 mz5 file format (*.mz5).' > org.eclipse.chemclipse.convertermz5.git/description
echo 'Import/export of mzData file format (*.mzData).' > org.eclipse.chemclipse.convertermzdata.git/description
echo 'Peak and mass spectrum identification using *.msl files.' > org.eclipse.chemclipse.filemsdidentifier.git/description
echo 'Chromatogram filter (Backfolding).' > org.eclipse.chemclipse.filterbackfold.git/description
echo 'Chromatogram filter (CODA).' > org.eclipse.chemclipse.filtercoda.git/description
echo 'Chromatogram filter (DENOISING).' > org.eclipse.chemclipse.filterdenoising.git/description
echo 'Chromatogram zero set filter.' > org.eclipse.chemclipse.filterfidzeroset.git/description
echo 'Chromatogram Ion remover filter.' > org.eclipse.chemclipse.filtermfremover.git/description
echo 'Chromatogram filter (Savitzky-Golay).' > org.eclipse.chemclipse.filtersavgolay.git/description
echo 'Manual peak identification.' > org.eclipse.chemclipse.identpeakmanual.git/description
echo 'Import/export of Matlab/PARAFAC file format (*.mpl).' > org.eclipse.chemclipse.matlabparafac.git/description
echo 'Import/export of mzML file format (*.mzML).' > org.eclipse.chemclipse.mzmlconverter.git/description
echo 'Import/export of mzXML file format (*.mzXML).' > org.eclipse.chemclipse.mzxmlmschrom.git/description
echo 'Chromatogram report generator.' > org.eclipse.chemclipse.occhromareport.git/description
echo 'Import/export of JCAMP-DX file format (*.jdx).' > org.eclipse.chemclipse.ocjcampdx.git/description
echo 'Application office connector.' > org.eclipse.chemclipse.officeconnector.git/description
echo 'Peak detector (deconvolution).' > org.eclipse.chemclipse.peakdeconvdetec.git/description
echo 'Peak detector (first derivative).' > org.eclipse.chemclipse.peakdetecchemst.git/description
echo 'Peak detector (manual).' > org.eclipse.chemclipse.peakdetecmanual.git/description
echo 'Peak detector (third derivative).' > org.eclipse.chemclipse.peakdetecthird.git/description
echo 'Peak identification batch job.' > org.eclipse.chemclipse.peakidentbatch.git/description
echo 'Chromatogram and peak integrator (trapezoid).' > org.eclipse.chemclipse.peakintegrchems.git/description
echo 'Peak integrator (max signal)' > org.eclipse.chemclipse.peakmax.git/description
echo 'Chromatogram and peak PCA support.' > org.eclipse.chemclipse.processpeakspca.git/description
echo 'Chromatogram retention time shifter.' > org.eclipse.chemclipse.rtshifter.git/description
echo 'Chromatogram S/N calculator (Dyson).' > org.eclipse.chemclipse.sncalcdyson.git/description
echo 'Chromatogram S/N calculator (Stein).' > org.eclipse.chemclipse.sncalcstein.git/description
echo 'Mass spectrum subtraction support.' > org.eclipse.chemclipse.subtractfilter.git/description
echo 'Chromatogram integrator (summed m/z area).' > org.eclipse.chemclipse.sumarea.git/description
echo 'Developer utils.' > org.eclipse.chemclipse.utils.git/description
echo 'High and low pass m/z filter.' > org.eclipse.chemclipse.xpassmsfilter.git/description
echo 'Import/export of xy file format (*.xy).' > org.eclipse.chemclipse.xyconverter.git/description

echo "finished"
