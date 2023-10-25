package org.luaj.vm2.lib.jse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OsLibTest {
    LuaValue jse_lib;
    double time;

    @BeforeEach
    public void setUp() {
        jse_lib = JsePlatform.standardGlobals().get("os");
        ;
        time = new java.util.Date(2001 - 1900, 7, 23, 14, 55, 02).getTime() / 1000.0;
    }

    static final double DAY = 24. * 3600.;

    @Test
    public void testJseOsGetenvForEnvVariables() {
        LuaValue USER = LuaValue.valueOf("USER");
        LuaValue jse_user = jse_lib.get("getenv").call(USER);
        assertFalse(jse_user.isnil());
        System.out.println("User: " + jse_user);
    }

    @Test
    public void testJseOsGetenvForSystemProperties() {
        System.setProperty("test.key.foo", "test.value.bar");
        LuaValue key = LuaValue.valueOf("test.key.foo");
        LuaValue value = LuaValue.valueOf("test.value.bar");
        LuaValue jse_value = jse_lib.get("getenv").call(key);
        assertEquals(value, jse_value);
    }
}
