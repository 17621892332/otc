package org.orient.otc.common.core.feign;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * LocalDateTime
 */
@Component
public class LocalDateFeignFormatterRegistrar implements FeignFormatterRegistrar {

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(LocalDate.class, String.class, source -> LocalDateTimeUtil.format(source, DatePattern.NORM_DATE_PATTERN));
    }
}
