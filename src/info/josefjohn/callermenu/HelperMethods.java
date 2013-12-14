package info.josefjohn.callermenu;

import android.util.Log;

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
			if (option.equals(hasImg)) {
				return Constants.images[i];
			}
			i++;
		}
		return 0;
	}
}
