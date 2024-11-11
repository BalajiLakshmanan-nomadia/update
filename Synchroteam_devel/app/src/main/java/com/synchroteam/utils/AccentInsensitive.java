package com.synchroteam.utils;

import java.text.Normalizer;

/**
 * <p>
 * This is a utility class used to remove the accent sensitive letters to
 * ordinary letters. It will be used in case of search functionality in list
 * views.
 * </p>
 *
 * @author Trident
 */
public class AccentInsensitive {

    public static String removeAccentCase(String accentedString) {
//		String unAccentedString = null;
//		unAccentedString = Normalizer.normalize(accentedString,
//				Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
//		return unAccentedString;

        //new logic to support greek
        return accentedString == null ? null :
                Normalizer.normalize(accentedString, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String stripAccents(String input) {
        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
