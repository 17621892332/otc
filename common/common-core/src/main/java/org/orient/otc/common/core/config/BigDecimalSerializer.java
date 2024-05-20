package org.orient.otc.common.core.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author jianyang.liu
 * @date 8/7/2023 3:43 PM
 */
@JsonComponent
public class BigDecimalSerializer extends StdSerializer<BigDecimal> implements ContextualSerializer {

    private  String pattern;

    private  JsonFormat.Shape shape;

    private  int newScale;

    private RoundingMode roundingMode;


    public BigDecimalSerializer(){
        super(BigDecimal.class);
    }

    public BigDecimalSerializer(BigDecimalFormatter annotation){
        super(BigDecimal.class);
        this.pattern = annotation.pattern();
        this.shape = annotation.shape();
        this.newScale = annotation.newScale();
        this.roundingMode = annotation.roundingMode();
    }


    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        if (shape == JsonFormat.Shape.STRING){
            String output = null;
            if (value != null) {
                output = new DecimalFormat(pattern).format(value);
            }
            jsonGenerator.writeString(output);
        }else{
            BigDecimal output = value.setScale(newScale, roundingMode);
            jsonGenerator.writeNumber(output);
        }

    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        if (beanProperty==null){
            return NumberSerializer.instance;
        }
        AnnotatedMember member = beanProperty.getMember();
        BigDecimalFormatter annotation = member.getAnnotation(BigDecimalFormatter.class);
        if (annotation != null){
            return new BigDecimalSerializer(annotation);
        }
        return NumberSerializer.instance;
    }
}

