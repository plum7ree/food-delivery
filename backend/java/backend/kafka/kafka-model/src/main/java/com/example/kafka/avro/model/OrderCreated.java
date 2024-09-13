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
public class OrderCreated extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = 1013520220630442462L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OrderCreated\",\"namespace\":\"com.example.kafka.avro.model\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    static {
        MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    }

    private static final BinaryMessageEncoder<OrderCreated> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<OrderCreated> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<OrderCreated> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<OrderCreated> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<OrderCreated> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this OrderCreated to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a OrderCreated from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a OrderCreated instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static OrderCreated fromByteBuffer(
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
    public OrderCreated() {
    }

    /**
     * All-args constructor.
     * @param orderId The new value for orderId
     * @param createdAt The new value for createdAt
     */
    public OrderCreated(java.lang.CharSequence orderId, java.time.Instant createdAt) {
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
     * Creates a new OrderCreated RecordBuilder.
     * @return A new OrderCreated RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderCreated.Builder newBuilder() {
        return new com.example.kafka.avro.model.OrderCreated.Builder();
    }

    /**
     * Creates a new OrderCreated RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new OrderCreated RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderCreated.Builder newBuilder(com.example.kafka.avro.model.OrderCreated.Builder other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderCreated.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderCreated.Builder(other);
        }
    }

    /**
     * Creates a new OrderCreated RecordBuilder by copying an existing OrderCreated instance.
     * @param other The existing instance to copy.
     * @return A new OrderCreated RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderCreated.Builder newBuilder(com.example.kafka.avro.model.OrderCreated other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderCreated.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderCreated.Builder(other);
        }
    }

    /**
     * RecordBuilder for OrderCreated instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OrderCreated>
        implements org.apache.avro.data.RecordBuilder<OrderCreated> {

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
        private Builder(com.example.kafka.avro.model.OrderCreated.Builder other) {
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
         * Creates a Builder by copying an existing OrderCreated instance
         * @param other The existing instance to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderCreated other) {
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
        public com.example.kafka.avro.model.OrderCreated.Builder setOrderId(java.lang.CharSequence value) {
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
        public com.example.kafka.avro.model.OrderCreated.Builder clearOrderId() {
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
        public com.example.kafka.avro.model.OrderCreated.Builder setCreatedAt(java.time.Instant value) {
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
        public com.example.kafka.avro.model.OrderCreated.Builder clearCreatedAt() {
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public OrderCreated build() {
            try {
                OrderCreated record = new OrderCreated();
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
    private static final org.apache.avro.io.DatumWriter<OrderCreated>
        WRITER$ = (org.apache.avro.io.DatumWriter<OrderCreated>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<OrderCreated>
        READER$ = (org.apache.avro.io.DatumReader<OrderCreated>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

}










