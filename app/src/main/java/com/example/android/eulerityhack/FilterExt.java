package com.example.android.eulerityhack;

import com.zomato.photofilters.imageprocessors.Filter;

public class FilterExt extends Filter {

    private String name;

    public FilterExt() {
        super();
    }

    public FilterExt(Filter filter) {
        super(filter);
    }

    public FilterExt(String name) {

        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
