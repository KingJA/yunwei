package com.sortlistview;

import com.tdr.yunwei.bean.CityList;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<CityList> {

	public int compare(CityList o1, CityList o2) {
		if (o1.getPinYing().equals("@")
				|| o2.getPinYing().equals("#")) {
			return -1;
		} else if (o1.getPinYing().equals("#")
				|| o2.getPinYing().equals("@")) {
			return 1;
		} else {
			return o1.getPinYing().compareTo(o2.getPinYing());
		}
	}

}

