package org.orient.otc.common.core.exception;


public interface IAssert {
    BaseException newException(Object ... args);

    BaseException newException(Throwable t, Object ... args);

    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            this.doThrow(args);
        }
    }

    /**
     * 是否抛出异常
     * @param obj  true 不抛出 false 抛出
     * @param args 异常参数
     */
    default void assertTrue(Boolean obj, Object... args) {
        if (!obj) {
            this.doThrow(args);
        }
    }
    default void assertTrue(Boolean obj) {
        if (!obj) {
            this.doThrow();
        }
    }
    default void doThrow(Object... args) {
            throw newException(args);
    }

    default void doThrow(Throwable t, Object... args) {
        throw newException(t, args);
    }

}
