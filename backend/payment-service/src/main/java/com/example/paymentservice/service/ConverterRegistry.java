//package com.example.paymentservice.service;
//import com.example.commondata.domain.aggregate.valueobject.CallId;
//import com.example.commondata.domain.aggregate.valueobject.Money;
//import com.example.commondata.domain.aggregate.valueobject.PaymentId;
//import com.example.commondata.domain.aggregate.valueobject.UserId;
//import org.apache.avro.Conversions;
//import org.apache.commons.beanutils.ConvertUtils;
//import org.apache.commons.beanutils.Converter;
//
//import java.util.UUID;
//
//public class ConverterRegistry {
//
//    private static final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();
//
//    public static void registerConverters() {
//        // UUID 타입 변환기
//        ConvertUtils.register(new UUIDConverter(), UUID.class);
//
//        // Money 타입 변환기
//        ConvertUtils.register(new MoneyConverter(), Money.class);
//
//        // PaymentId 타입 변환기
//        ConvertUtils.register(new PaymentIdConverter(), PaymentId.class);
//
//        // UserId 타입 변환기
//        ConvertUtils.register(new UserIdConverter(), UserId.class);
//
//        // CallId 타입 변환기
//        ConvertUtils.register(new CallIdConverter(), CallId.class);
//
//
//                payment.setId(new PaymentId(UUID.fromString(avroModel.getId().toString())));
//                payment.setUserId(new UserId(UUID.fromString(avroModel.getCallerId().toString())));
//                payment.setCallId(new CallId(UUID.fromString(avroModel.getCallId().toString())));
//                payment.setPrice(new Money(decimalConversion.fromBytes(avroModel.getPrice(),
//                        avroModel.getSchema().getField("price").schema(),
//                        avroModel.getSchema().getField("price").schema().getLogicalType())));
//
//    }
//
//    private static class UUIDConverter implements Converter {
//        @Override
//        public Object convert(Class type, Object value) {
//            if (value instanceof String) {
//                return UUID.fromString((String) value);
//            } else if (value instanceof UUID) {
//                return value;
//            } else {
//                throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to UUID");
//            }
//        }
//    }
//
//    private static class MoneyConverter implements Converter {
//        private final DecimalConversion decimalConversion;
//
//        public MoneyConverter(DecimalConversion decimalConversion) {
//            this.decimalConversion = decimalConversion;
//        }
//
//        @Override
//        public Object convert(Class type, Object value) {
//            if (value instanceof byte[]) {
//
//                return new Money(decimalConversion.fromBytes((byte[]) value, schema, logicalType));
//            } else if (value instanceof Money) {
//                return value;
//            } else {
//                throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to Money");
//            }
//        }
//    }
//
//    private static class PaymentIdConverter implements Converter {
//        @Override
//        public Object convert(Class type, Object value) {
//            if (value instanceof UUID) {
//                return new PaymentId((UUID) value);
//            } else if (value instanceof String) {
//                return new PaymentId(UUID.fromString((String) value));
//            } else {
//                throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to PaymentId");
//            }
//        }
//    }
//
//    private static class UserIdConverter implements Converter {
//        @Override
//        public Object convert(Class type, Object value) {
//            if (value instanceof UUID) {
//                return new UserId((UUID) value);
//            } else if (value instanceof String) {
//                return new UserId(UUID.fromString((String) value));
//            } else {
//                throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to UserId");
//            }
//        }
//    }
//
//    private static class CallIdConverter implements Converter {
//        @Override
//        public Object convert(Class type, Object value) {
//            if (value instanceof UUID) {
//                return new CallId((UUID) value);
//            } else if (value instanceof String) {
//                return new CallId(UUID.fromString((String) value));
//            } else {
//                throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to CallId");
//            }
//        }
//    }
//}