package org.orient.otc.common.core.exception;

import java.text.MessageFormat;
import java.util.Arrays;


/**
 * BaseExceptionAssert
 */
public interface BaseExceptionAssert extends IExceptionEnum, IAssert {

    @Override
    default BaseException newException(Object... args) {
        if (args.length==0) {
            return new BaseException(this, this.getMessage());
        }
        return new BaseException(this, MessageFormat.format("{0} - {1}", this.getMessage(), Arrays.asList(args)));
    }

    @Override
    default BaseException newException(Throwable t, Object... args) {
        return new BaseException(this, t, MessageFormat.format("{0} - {1}", this.getMessage(), Arrays.asList(args)));
    }
}
