package org.orient.otc.quote.service;

import org.orient.otc.quote.dto.jni.QuoteRequestDto;
import org.orient.otc.quote.vo.jni.QuoteResultVo;

public interface JniSerevice {
    QuoteResultVo callSo(QuoteRequestDto quoteSoDto);
}
