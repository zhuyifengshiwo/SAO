package com.powernode.util;

import com.powernode.workbench.pojo.TblDicValue;

import java.util.Comparator;

public class MyComparator implements Comparator<TblDicValue> {
    @Override
    public int compare(TblDicValue o1, TblDicValue o2) {
        int i = Integer.parseInt(o2.getOrderno());
        int k = Integer.parseInt(o1.getOrderno());
        return k-i;
    }
}
