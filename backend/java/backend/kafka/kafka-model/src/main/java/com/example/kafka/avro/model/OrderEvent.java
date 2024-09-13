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
public class OrderEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = -7844815707276060218L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OrderEvent\",\"namespace\":\"com.example.kafka.avro.model\",\"fields\":[{\"name\":\"correlationId\",\"type\":\"string\"},{\"name\":\"event\",\"type\":[{\"type\":\"record\",\"name\":\"OrderCreated\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]},{\"type\":\"record\",\"name\":\"OrderApprovedByRestaurant\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]},{\"type\":\"record\",\"name\":\"OrderRejectedByRestaurant\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]},{\"type\":\"record\",\"name\":\"OrderCompleted\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"createdAt\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]}]}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    static {
        MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    }

    private static final BinaryMessageEncoder<OrderEvent> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<OrderEvent> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<OrderEvent> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<OrderEvent> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<OrderEvent> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this OrderEvent to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a OrderEvent from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a OrderEvent instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static OrderEvent fromByteBuffer(
        java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.CharSequence correlationId;
    private java.lang.Object event;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public OrderEvent() {
    }

    /**
     * All-args constructor.
     * @param correlationId The new value for correlationId
     * @param event The new value for event
     */
    public OrderEvent(java.lang.CharSequence correlationId, java.lang.Object event) {
        this.correlationId = correlationId;
        this.event = event;
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
                return correlationId;
            case 1:
                return event;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    // Used by DatumReader.  Applications should not call.
    @Override
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                correlationId = (java.lang.CharSequence) value$;
                break;
            case 1:
                event = value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'correlationId' field.
     * @return The value of the 'correlationId' field.
     */
    public java.lang.CharSequence getCorrelationId() {
        return correlationId;
    }


    /**
     * Sets the value of the 'correlationId' field.
     * @param value the value to set.
     */
    public void setCorrelationId(java.lang.CharSequence value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the 'event' field.
     * @return The value of the 'event' field.
     */
    public java.lang.Object getEvent() {
        return event;
    }


    /**
     * Sets the value of the 'event' field.
     * @param value the value to set.
     */
    public void setEvent(java.lang.Object value) {
        this.event = value;
    }

    /**
     * Creates a new OrderEvent RecordBuilder.
     * @return A new OrderEvent RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderEvent.Builder newBuilder() {
        return new com.example.kafka.avro.model.OrderEvent.Builder();
    }

    /**
     * Creates a new OrderEvent RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new OrderEvent RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderEvent.Builder newBuilder(com.example.kafka.avro.model.OrderEvent.Builder other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderEvent.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderEvent.Builder(other);
        }
    }

    /**
     * Creates a new OrderEvent RecordBuilder by copying an existing OrderEvent instance.
     * @param other The existing instance to copy.
     * @return A new OrderEvent RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderEvent.Builder newBuilder(com.example.kafka.avro.model.OrderEvent other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderEvent.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderEvent.Builder(other);
        }
    }

    /**
     * RecordBuilder for OrderEvent instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OrderEvent>
        implements org.apache.avro.data.RecordBuilder<OrderEvent> {

        private java.lang.CharSequence correlationId;
        private java.lang.Object event;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderEvent.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.correlationId)) {
                this.correlationId = data().deepCopy(fields()[0].schema(), other.correlationId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.event)) {
                this.event = data().deepCopy(fields()[1].schema(), other.event);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
        }

        /**
         * Creates a Builder by copying an existing OrderEvent instance
         * @param other The existing instance to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderEvent other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.correlationId)) {
                this.correlationId = data().deepCopy(fields()[0].schema(), other.correlationId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.event)) {
                this.event = data().deepCopy(fields()[1].schema(), other.event);
                fieldSetFlags()[1] = true;
            }
        }

        /**
         * Gets the value of the 'correlationId' field.
         * @return The value.
         */
        public java.lang.CharSequence getCorrelationId() {
            return correlationId;
        }


        /**
         * Sets the value of the 'correlationId' field.
         * @param value The value of 'correlationId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderEvent.Builder setCorrelationId(java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.correlationId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'correlationId' field has been set.
         * @return True if the 'correlationId' field has been set, false otherwise.
         */
        public boolean hasCorrelationId() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'correlationId' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderEvent.Builder clearCorrelationId() {
            correlationId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'event' field.
         * @return The value.
         */
        public java.lang.Object getEvent() {
            return event;
        }


        /**
         * Sets the value of the 'event' field.
         * @param value The value of 'event'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderEvent.Builder setEvent(java.lang.Object value) {
            validate(fields()[1], value);
            this.event = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'event' field has been set.
         * @return True if the 'event' field has been set, false otherwise.
         */
        public boolean hasEvent() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'event' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderEvent.Builder clearEvent() {
            event = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public OrderEvent build() {
            try {
                OrderEvent record = new OrderEvent();
                record.correlationId = fieldSetFlags()[0] ? this.correlationId : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.event = fieldSetFlags()[1] ? this.event : defaultValue(fields()[1]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<OrderEvent>
        WRITER$ = (org.apache.avro.io.DatumWriter<OrderEvent>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<OrderEvent>
        READER$ = (org.apache.avro.io.DatumReader<OrderEvent>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

}










