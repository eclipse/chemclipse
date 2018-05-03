/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class OplsCalculatorNipals extends AbstractMultivariateCalculator {

	private DenseMatrix64F getYVector() {

		HashSet<String> groupNamesSet = new HashSet<>();
		ArrayList<String> groupNames = getGroupNames();
		double[] vector = new double[groupNames.size()];
		groupNamesSet.addAll(groupNames);
		List<String> uniqueGroupNames = Arrays.asList(groupNamesSet.toArray(new String[groupNamesSet.size()]));
		int yIterator = 0;
		for(String myString : groupNames) {
			vector[yIterator] = (double)uniqueGroupNames.indexOf(myString);
			yIterator++;
		}
		DenseMatrix64F yVector = new DenseMatrix64F(groupNames.size(), 1, true, vector);
		return yVector;
	}

	private DenseMatrix64F getAvgYVector() {

		double[] yVector = getYVector().data;
		double[] avgYData = new double[yVector.length];
		OptionalDouble avgValue = Arrays.stream(yVector).average();
		if(avgValue.isPresent()) {
			Arrays.fill(avgYData, avgValue.getAsDouble());
		}
		DenseMatrix64F avgYVector = new DenseMatrix64F(yVector.length, 1, true, avgYData);
		return avgYVector;
	}

	private DenseMatrix64F getAvgXVector() {

		DenseMatrix64F X = getSampleData();
		DenseMatrix64F avgOfCols = new DenseMatrix64F(1, getSampleData().getNumCols());
		CommonOps.sumCols(X, avgOfCols);
		CommonOps.divide(avgOfCols, getSampleData().getNumRows());
		return avgOfCols;
	}

	private DenseMatrix64F getSDXVector() {

		DenseMatrix64F X = getSampleData().copy();
		DenseMatrix64F avgOfCols = getAvgXVector();
		DenseMatrix64F sdXVector = new DenseMatrix64F(1, getSampleData().getNumCols());
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			for(int j = 0; j < getSampleData().getNumRows(); j++) {
				X.set(j, i, X.get(j, i) - avgOfCols.get(0, i));
				X.set(j, i, X.get(j, i) * X.get(j, i));
			}
		}
		CommonOps.sumCols(X, sdXVector);
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			sdXVector.set(0, i, Math.sqrt(sdXVector.get(0, i)));
		}
		return sdXVector;
	}

	@Override
	public void compute(int numComps) {

		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		DenseMatrix64F T_ortho = new DenseMatrix64F(numberOfSamples, numComps - 1);
		DenseMatrix64F P_ortho = new DenseMatrix64F(numComps - 1, numberOfVariables);
		DenseMatrix64F W_ortho = new DenseMatrix64F(numComps - 1, numberOfVariables);
		DenseMatrix64F t_ortho = new DenseMatrix64F(numberOfSamples, 1);
		DenseMatrix64F p_ortho = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F w_ortho = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F X = new DenseMatrix64F(1, 1);
		X.set(getSampleData());
		DenseMatrix64F y = new DenseMatrix64F(1, 1);
		y.set(getYVector());
		DenseMatrix64F y_avg = getAvgYVector();
		DenseMatrix64F x_avg = getAvgXVector();
		DenseMatrix64F x_sd = getSDXVector();
		DenseMatrix64F w = new DenseMatrix64F(1, numberOfVariables);
		//
		// ##########################################################################
		// ### Start algorithm
		// #1
		// w<-(t(y)%*%X)/as.vector((t(y)%*%y)) ### Dot (scalar) product calculations
		// w<-t(w)
		DenseMatrix64F yy = new DenseMatrix64F(1, 1);
		CommonOps.multInner(y, yy);
		CommonOps.multTransA(y, X, w);
		CommonOps.divide(w, yy.get(0));
		// #2
		// w<-w/as.vector(sqrt(t(w)%*%w)) # Generates vector
		DenseMatrix64F ww = new DenseMatrix64F(1, 1);
		CommonOps.transpose(w);
		CommonOps.multInner(w, ww);
		double absW = Math.sqrt(yy.get(0));
		CommonOps.divide(w, absW);
		// ##########################################################################
		// ### Start calculation for ortho factors first and predictive in the end
		// #3
		// for (counterLVs in 1:nLVs){
		for(int i = 0; i < numComps; i++) {
			// te<-X%*%w/as.vector(t(w)%*%w) # Generates vector
			DenseMatrix64F wTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(w, wTemp);
			DenseMatrix64F te = new DenseMatrix64F(numberOfSamples, 1);
			CommonOps.mult(X, w, te);
			CommonOps.divide(te, wTemp.get(0));
			// #4
			// ce<-(t(te)%*%y)/as.vector(t(te)%*%te) # Generates scalar
			// ce<-t(ce)
			DenseMatrix64F ce = new DenseMatrix64F(1, 1);
			DenseMatrix64F tTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(te, tTemp);
			CommonOps.multTransA(te, y, ce);
			CommonOps.divide(ce, tTemp.get(0));
			// #5
			// u<-(y%*%ce)/as.vector(t(ce)%*%ce) # Generates vector
			DenseMatrix64F cTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(ce, cTemp);
			DenseMatrix64F u = new DenseMatrix64F(numberOfSamples, 1);
			CommonOps.mult(y, ce, u);
			CommonOps.divide(u, cTemp.get(0));
			// #6
			// p<-t(te)%*%X/as.vector(t(te)%*%te)
			// p<-t(p)
			DenseMatrix64F p = new DenseMatrix64F(1, numberOfVariables);
			CommonOps.multTransA(te, X, p);
			CommonOps.divide(p, tTemp.get(0));
			// ### End of calculation of predictive vector
			// ### The algorithms will stop here after having already calculated the orthogonol components
			if(i < numComps - 1) {
				// #7
				// w_ortho<-p-(as.vector(t(w)%*%p)/as.vector(t(w)%*%w))*(w)
				DenseMatrix64F wTemp2 = new DenseMatrix64F(1, 1);
				DenseMatrix64F w_ortho_temp = new DenseMatrix64F(numberOfVariables, 1);
				DenseMatrix64F wTemp3 = new DenseMatrix64F(1, numberOfVariables);
				CommonOps.multTransAB(w, p, wTemp2);
				CommonOps.divide(wTemp2, wTemp.get(0));
				CommonOps.mult(w, wTemp2, w_ortho_temp);
				CommonOps.transpose(w_ortho_temp);
				CommonOps.subtract(p, w_ortho_temp, w_ortho);
				// #8
				// w_ortho<-w_ortho/as.vector((sqrt(t(w_ortho)%*%w_ortho)))
				DenseMatrix64F ww_ortho = new DenseMatrix64F(1, 1);
				CommonOps.transpose(w_ortho);
				CommonOps.multInner(w_ortho, ww_ortho);
				double absW_ortho = Math.sqrt(ww_ortho.get(0));
				CommonOps.divide(w_ortho, absW_ortho);
				// #9
				// t_ortho<-(X%*%w_ortho)/as.vector(t(w_ortho)%*%w_ortho)
				CommonOps.transpose(w_ortho_temp);
				CommonOps.multInner(w_ortho_temp, ww_ortho);
				CommonOps.mult(X, w_ortho, t_ortho);
				CommonOps.divide(t_ortho, ww_ortho.get(0));
				// #10
				// p_ortho<-(t(t_ortho)%*%X)/as.vector(t(t_ortho)%*%t_ortho) # Generates a row vector
				// p_ortho<-t(p_ortho)
				DenseMatrix64F tt_temp = new DenseMatrix64F(1, 1);
				CommonOps.multInner(t_ortho, tt_temp);
				CommonOps.multTransA(t_ortho, X, p_ortho);
				// #11
				// Eo_PLS<-X-as.vector(t_ortho%*%t(p_ortho))
				DenseMatrix64F X_temp = new DenseMatrix64F(numberOfSamples, numberOfVariables);
				CommonOps.mult(t_ortho, p_ortho, X_temp);
				CommonOps.subtract(X, X_temp, X);
				// #12
				// T_ortho<-c(T_ortho,t_ortho)
				// P_ortho<-c(P_ortho,p_ortho)
				// W_ortho<-c(W_ortho,w_ortho)
				// X<-Eo_PLS (was in this case already conducted in #11
				for(int j = 0; j < numberOfSamples; j++) {
					T_ortho.set(j, i, t_ortho.get(j));
				}
				for(int k = 0; k < numberOfVariables; k++) {
					P_ortho.set(i, k, p_ortho.get(k));
					W_ortho.set(i, k, w_ortho.get(k));
				}
				System.out.println("matrix calc");
			}
		}
		setScores(new DenseMatrix64F(numberOfSamples, numComps));
		setLoadings(new DenseMatrix64F(numComps, numberOfVariables));
		//
		// b<-w%*%ce
		//
		// #OPLS_result<-list(p_OPLS=p, u_OPLS=u,w_OPLS=w, t_OPLS=te, c_OPLS=ce, b_OPLS=b, T_ortho=T_ortho, P_ortho=P_ortho,W_ortho=W_ortho,Eo_PLS=X, pre_vectors=pre_data_vector$vectors, avg_y=avg_y)
		//
		// opls_result <- list()
		// opls_result$p_opls <- p
		// opls_result$u_opls <- u
		// opls_result$w_opls <- w
		// opls_result$t_opls <- te
		// opls_result$c_opls <- ce
		// opls_result$b_opls <- b
		// opls_result$t_ortho <- T_ortho
		// opls_result$p_ortho <- P_ortho
		// opls_result$w_ortho <- W_ortho
		// opls_result$Eo_PLS <- X
		// opls_result$pre_vectors <- pre_data_vector$vectors
		// opls_result$avg_y <- avg_y
		//
		// return(opls_result)
	}
}
