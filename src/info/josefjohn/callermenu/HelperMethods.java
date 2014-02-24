/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

public class HelperMethods {
	public static boolean hasNumber(String phoneNumber) {
		for (String num:Constants.allCurNumbers) {
			if (phoneNumber.equals(num)) {
				return true;
			}
		}
		return false;
	}

	public static int getImage(String option) {
		int i = 0;
		for (String hasImg:Constants.optionsWithImages) {
			if (option.equals(hasImg) || option.contains(hasImg)) {
				return Constants.images[i];
			}
			i++;
		}
		return 0;
	}
}
