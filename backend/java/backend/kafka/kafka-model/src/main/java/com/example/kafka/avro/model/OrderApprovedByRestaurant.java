/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package com.example.kafka.avro.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class OrderApprovedByRestaurant extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = -787311526227936354L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OrderApprovedByRestaurant\",\"namespace\":\"com.example.kafka.avro.model\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    static {
        MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    }

    private static final BinaryMessageEncoder<OrderApprovedByRestaurant> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<OrderApprovedByRestaurant> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<OrderApprovedByRestaurant> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<OrderApprovedByRestaurant> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<OrderApprovedByRestaurant> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this OrderApprovedByRestaurant to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a OrderApprovedByRestaurant from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a OrderApprovedByRestaurant instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static OrderApprovedByRestaurant fromByteBuffer(
        java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.CharSequence orderId;
    private java.time.Instant createdAt;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public OrderApprovedByRestaurant() {
    }

    /**
     * All-args constructor.
     * @param orderId The new value for orderId
     * @param createdAt The new value for createdAt
     */
    public OrderApprovedByRestaurant(java.lang.CharSequence orderId, java.time.Instant createdAt) {
        this.orderId = orderId;
        this.createdAt = createdAt.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    }

    @Override
    public org.apache.avro.specific.SpecificData getSpecificData() {
        return MODEL$;
    }

    @Override
    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    @Override
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return orderId;
            case 1:
                return createdAt;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    private static final org.apache.avro.Conversion<?>[] conversions =
        new org.apache.avro.Conversion<?>[]{
            null,
            new org.apache.avro.data.TimeConversions.TimestampMillisConversion(),
            null
        };

    @Override
    public org.apache.avro.Conversion<?> getConversion(int field) {
        return conversions[field];
    }

    // Used by DatumReader.  Applications should not call.
    @Override
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                orderId = (java.lang.CharSequence) value$;
                break;
            case 1:
                createdAt = (java.time.Instant) value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'orderId' field.
     * @return The value of the 'orderId' field.
     */
    public java.lang.CharSequence getOrderId() {
        return orderId;
    }


    /**
     * Sets the value of the 'orderId' field.
     * @param value the value to set.
     */
    public void setOrderId(java.lang.CharSequence value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the 'createdAt' field.
     * @return The value of the 'createdAt' field.
     */
    public java.time.Instant getCreatedAt() {
        return createdAt;
    }


    /**
     * Sets the value of the 'createdAt' field.
     * @param value the value to set.
     */
    public void setCreatedAt(java.time.Instant value) {
        this.createdAt = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    }

    /**
     * Creates a new OrderApprovedByRestaurant RecordBuilder.
     * @return A new OrderApprovedByRestaurant RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder newBuilder() {
        return new com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder();
    }

    /**
     * Creates a new OrderApprovedByRestaurant RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new OrderApprovedByRestaurant RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder newBuilder(com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder(other);
        }
    }

    /**
     * Creates a new OrderApprovedByRestaurant RecordBuilder by copying an existing OrderApprovedByRestaurant instance.
     * @param other The existing instance to copy.
     * @return A new OrderApprovedByRestaurant RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder newBuilder(com.example.kafka.avro.model.OrderApprovedByRestaurant other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder(other);
        }
    }

    /**
     * RecordBuilder for OrderApprovedByRestaurant instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OrderApprovedByRestaurant>
        implements org.apache.avro.data.RecordBuilder<OrderApprovedByRestaurant> {

        private java.lang.CharSequence orderId;
        private java.time.Instant createdAt;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.orderId)) {
                this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.createdAt)) {
                this.createdAt = data().deepCopy(fields()[1].schema(), other.createdAt);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
        }

        /**
         * Creates a Builder by copying an existing OrderApprovedByRestaurant instance
         * @param other The existing instance to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderApprovedByRestaurant other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.orderId)) {
                this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.createdAt)) {
                this.createdAt = data().deepCopy(fields()[1].schema(), other.createdAt);
                fieldSetFlags()[1] = true;
            }
        }

        /**
         * Gets the value of the 'orderId' field.
         * @return The value.
         */
        public java.lang.CharSequence getOrderId() {
            return orderId;
        }


        /**
         * Sets the value of the 'orderId' field.
         * @param value The value of 'orderId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder setOrderId(java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.orderId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'orderId' field has been set.
         * @return True if the 'orderId' field has been set, false otherwise.
         */
        public boolean hasOrderId() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'orderId' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder clearOrderId() {
            orderId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'createdAt' field.
         * @return The value.
         */
        public java.time.Instant getCreatedAt() {
            return createdAt;
        }


        /**
         * Sets the value of the 'createdAt' field.
         * @param value The value of 'createdAt'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder setCreatedAt(java.time.Instant value) {
            validate(fields()[1], value);
            this.createdAt = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'createdAt' field has been set.
         * @return True if the 'createdAt' field has been set, false otherwise.
         */
        public boolean hasCreatedAt() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'createdAt' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderApprovedByRestaurant.Builder clearCreatedAt() {
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public OrderApprovedByRestaurant build() {
            try {
                OrderApprovedByRestaurant record = new OrderApprovedByRestaurant();
                record.orderId = fieldSetFlags()[0] ? this.orderId : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.createdAt = fieldSetFlags()[1] ? this.createdAt : (java.time.Instant) defaultValue(fields()[1]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<OrderApprovedByRestaurant>
        WRITER$ = (org.apache.avro.io.DatumWriter<OrderApprovedByRestaurant>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<OrderApprovedByRestaurant>
        READER$ = (org.apache.avro.io.DatumReader<OrderApprovedByRestaurant>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

}










