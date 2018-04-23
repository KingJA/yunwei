package com.sortlistview;

import com.tdr.yunwei.bean.StationCity;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator2 implements Comparator<StationCity> {

	public int compare(StationCity o1, StationCity o2) {
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
