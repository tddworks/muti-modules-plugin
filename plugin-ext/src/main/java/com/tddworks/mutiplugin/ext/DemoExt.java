package com.tddworks.mutiplugin.ext;

import com.tddworks.mutiplugin.base.DemoInterface;

public class DemoExt implements DemoInterface {
    @Override
    public void play() {
        System.out.println("Let's play");
    }
}
