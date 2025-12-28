package com.jjsttk.goodswarehouse.testutil;

import org.instancio.Instancio;

import static org.instancio.Select.root;

public class StringTestUtils {

    public static String getStringByLength(int length) {
        return Instancio.of(String.class)
                .generate(root(), gen -> gen.string().length(length))
                .create();
    }
}
