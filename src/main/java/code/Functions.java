/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package code;


/**
 *
 * @author tahoa_000
 */
public class Functions {
    public static String umlaut_decode(String text) {
        text = text.replace("&Uuml;","Ü");
	text = text.replace("&uuml;","ü");
	text = text.replace("&Auml;","Ä");
	text = text.replace("&auml;","ä");
	text = text.replace("&Ouml;","Ö");
	text = text.replace("&ouml;","ö");
	text = text.replace("&szlig;","ß");
	text = text.replace("&quot;","\"");
        return text;
    }
}
