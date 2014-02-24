/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*
 * The object that represents a company menu. When a menu is retrieved, the menu 
 * is converted into this object.
 */
public class CompanyMenu {
	String companyName;
	String phone;
	String key;
	HashMap<String, String> menu;
	HashMap<String, String> reverseMenu;
	List<String> responses;
	List<String> completeMapping;
	int type;
	String[] standardStart;
	String[] standardMore;
	boolean english = true;

	CompanyMenu(JSONObject jsonObj) {
		String language = MainActivity.language;
		if (!language.equals("English")) {
			english = false;
		}
		menu = new HashMap<String, String>();
		reverseMenu = new HashMap<String, String>();
		responses = new ArrayList<String>();
		completeMapping = new ArrayList<String>();
		try {
			companyName = jsonObj.getString("name");
			phone = jsonObj.getString("number");
			type = jsonObj.getInt("type");
			MainActivity.phoneNumber = phone;
			Log.i("phone is ", MainActivity.phoneNumber);
			jsonObj.remove("name");
			jsonObj.remove("number");
			Iterator<String> jsonKeys = jsonObj.keys();
			while (jsonKeys.hasNext()) {
				key = jsonKeys.next();
				if (key.endsWith("?")) {
					responses.add(key);
				}
				menu.put(key, jsonObj.getString(key));
				if (key.length() < 2 && !key.contains("~")) {
				reverseMenu.put(jsonObj.getString(key), key);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	String getCompanyName() {
		return companyName;
	}

	String getPhoneNum() {
		return phone;
	}
	
	int getType() {
		return type;
	}

	List<String> getResponses() {
		return responses;
	}

	public boolean has(String string) {
		if (menu.get(string) == null) {
			return false;
		} else {
			return true;
		}
	}

	//returns the numbers to dial. does not have the pauses yet.
	String getCompleteMapping(int position) {
		return completeMapping.get(position);
	}

	String[] getAllOptions() {
		Stack<String> stack = new Stack<String>();
		List<String> list = new ArrayList<String>();
		for (String s:Constants.allCharsReversed) {
			if (menu.containsKey(s)) {
				stack.add(s);
			}
		}
		while (!stack.isEmpty()) {
			String cur = stack.pop();
			String gap = "";
			for (int i=0;i<cur.length();i++) {
				if (i > 0) {
					gap += "- ";
				}
			}
			list.add(gap + menu.get(cur));
			completeMapping.add(cur);
			for (String s:Constants.allCharsReversed) {
				if (menu.containsKey(cur+s)) {
					stack.add(cur+s);
				}
			}
		}
		String[] ret = new String[list.size()];
		for (int i=0;i<list.size();i++) {
			ret[i] = list.get(i);
		}
		return ret;
	}

	public String getFinalSelection(String selection) {
		if (menu.containsKey(selection+"|")) {
			return menu.get(selection+"|");
		}
		String ret = menu.get("~");
		for (int i=1;i<=selection.length();i++) {
			String cur = selection.substring(0, i);
			//Log.i("cur", cur);
			if (!((menu.get(cur).equals("Make Call")) || (menu.get(cur).equals("Llamar")))) {
			ret += selection.substring(i-1, i)+",";
			}
			//Log.i("ret", ret);
			if (menu.containsKey(cur+"~")) {
				ret += menu.get(cur+"~");
			}
			//Log.i("ret", ret);
			if (menu.containsKey(cur+"?")) {
				String input = CallerActivity.getInput(menu.get(cur+"?"));
				if (input != null) {
					ret += ","+input+",,";
				} else {
					return MainActivity.phoneNumber+ret;
				}
			}
			if (getNumChildren(cur) == 0) { 
				return MainActivity.phoneNumber+ret;
			}
		}
		return ret;
	}

	public String isLink(String selection) {
		if (menu.containsKey(selection+"_")) {
			return menu.get(selection+"_");
		} else {
			return null;
		}
	}

	public String hasQuestion(String selection) {
		for (int i=1;i<=selection.length();i++) {
			String cur = selection.substring(0, i);
			if (menu.containsKey(cur+"?")) {
				return menu.get(cur+"?");
			}
		}
		return null;
	}

	public String get(String selection) {
		return menu.get(selection);
	}
	
	int getNumChildren(String s) {
		boolean done = false;
		int j=0;
		for (int i=0;i<Constants.allChars.length;i++) {
			
			if (menu.get(s+Constants.allChars[i]) != null) {
				
				j++;
			}
		}
		return j;
	}

	public String[] getChildren(String curOption) {
		String[] ret;
		int numChildren = getNumChildren(curOption);
		if (numChildren == 0) {
			return null;
		}
		ret = new String[numChildren];
		String[] allChars = Constants.allChars;
		for (int j=0;j<numChildren;j++) {
			ret[j] = menu.get(curOption+allChars[j]);
		}
		return ret;
	}
	
	public String[] getStandardMenu() {
		String[] base = getChildren("");
		if (base == null) {
			return base;
		}
		String[] standard;
		List<String> standardMenu = new ArrayList<String>();
		HashSet<String> standardSet = new HashSet<String>();
		List<String> moreOptions = new ArrayList<String>();
		if (type == 1) {
			if (!english) {
				standard = Constants.AIRLINES_SPANISH;
			} else {
			standard = Constants.AIRLINES;
			}
		} else {
			return null;
		}
		for (String opt:standard) {
			if (reverseMenu.containsKey(opt)) {
				standardMenu.add(opt);
				standardSet.add(opt);
			}
			
		}
		if (!english) {
			standardMenu.add("Mas");
		} else {
			standardMenu.add("More");
		}
		
		standard = new String[standardMenu.size()];
		int i=0;
		for (String opt:standardMenu) {
			standard[i] = opt;
			i++;
		}
		for (String opt:base) {
			if (!standardMenu.contains(opt)) {
				moreOptions.add(opt);
			}
		}
		standardMore = new String[moreOptions.size()];
		i=0;
		for (String opt:moreOptions) {
			standardMore[i] = opt;
			i++;
		}
		return standard;
	}

	public String[] getStandardChildren(List<String> cur) {
		String selection = getSelection(cur);
		if (selection == "") {
			return standardMore;
		} else {
			return getChildren(selection);
		}
		
	}

	public String getSelection(List<String> cur) {
		if (cur.size() < 1) {
			return "";
		}
		String first = cur.get(0);
		String selection = reverseMenu.get(first);
		int j = 1;
		if (first.equals("More") || first.equals("Mas")) {
			selection = "";
			if (cur.size() > 1) {
				selection = reverseMenu.get(cur.get(1));
				Log.i("cur.get1", selection);
				j = 2;
			}
		}
		for (int i=j;i<cur.size();i++) {
			selection += cur.get(i);
		}
		Log.i("selection is ", selection);
		return selection;
	}
}
