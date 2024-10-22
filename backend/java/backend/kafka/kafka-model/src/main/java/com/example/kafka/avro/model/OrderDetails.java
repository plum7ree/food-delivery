/**
 * Autogenerated by Avro
 *
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
public class OrderDetails extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = -4233246146627895569L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OrderDetails\",\"namespace\":\"com.example.kafka.avro.model\",\"fields\":[{\"name\":\"orderId\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"}},{\"name\":\"totalAmount\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":10,\"scale\":2}}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    static {
        MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.UUIDConversion());
        MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.DecimalConversion());
    }

    private static final BinaryMessageEncoder<OrderDetails> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<OrderDetails> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     *
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<OrderDetails> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     *
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<OrderDetails> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     *
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<OrderDetails> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this OrderDetails to a ByteBuffer.
     *
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a OrderDetails from a ByteBuffer.
     *
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a OrderDetails instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static OrderDetails fromByteBuffer(
        java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.util.UUID orderId;
    private java.nio.ByteBuffer totalAmount;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public OrderDetails() {
    }

    /**
     * All-args constructor.
     *
     * @param orderId     The new value for orderId
     * @param totalAmount The new value for totalAmount
     */
    public OrderDetails(java.util.UUID orderId, java.nio.ByteBuffer totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
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
                return totalAmount;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    private static final org.apache.avro.Conversion<?>[] conversions =
        new org.apache.avro.Conversion<?>[]{
            new org.apache.avro.Conversions.UUIDConversion(),
            null,
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
                orderId = (java.util.UUID) value$;
                break;
            case 1:
                totalAmount = (java.nio.ByteBuffer) value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'orderId' field.
     * @return The value of the 'orderId' field.
     */
    public java.util.UUID getOrderId() {
        return orderId;
    }


    /**
     * Sets the value of the 'orderId' field.
     * @param value the value to set.
     */
    public void setOrderId(java.util.UUID value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the 'totalAmount' field.
     * @return The value of the 'totalAmount' field.
     */
    public java.nio.ByteBuffer getTotalAmount() {
        return totalAmount;
    }


    /**
     * Sets the value of the 'totalAmount' field.
     *
     * @param value the value to set.
     */
    public void setTotalAmount(java.nio.ByteBuffer value) {
        this.totalAmount = value;
    }

    /**
     * Creates a new OrderDetails RecordBuilder.
     *
     * @return A new OrderDetails RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderDetails.Builder newBuilder() {
        return new com.example.kafka.avro.model.OrderDetails.Builder();
    }

    /**
     * Creates a new OrderDetails RecordBuilder by copying an existing Builder.
     *
     * @param other The existing builder to copy.
     * @return A new OrderDetails RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderDetails.Builder newBuilder(com.example.kafka.avro.model.OrderDetails.Builder other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderDetails.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderDetails.Builder(other);
        }
    }

    /**
     * Creates a new OrderDetails RecordBuilder by copying an existing OrderDetails instance.
     *
     * @param other The existing instance to copy.
     * @return A new OrderDetails RecordBuilder
     */
    public static com.example.kafka.avro.model.OrderDetails.Builder newBuilder(com.example.kafka.avro.model.OrderDetails other) {
        if (other == null) {
            return new com.example.kafka.avro.model.OrderDetails.Builder();
        } else {
            return new com.example.kafka.avro.model.OrderDetails.Builder(other);
        }
    }

    /**
     * RecordBuilder for OrderDetails instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OrderDetails>
        implements org.apache.avro.data.RecordBuilder<OrderDetails> {

        private java.util.UUID orderId;
        private java.nio.ByteBuffer totalAmount;

        /**
         * Creates a new Builder
         */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         *
         * @param other The existing Builder to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderDetails.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.orderId)) {
                this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.totalAmount)) {
                this.totalAmount = data().deepCopy(fields()[1].schema(), other.totalAmount);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
        }

        /**
         * Creates a Builder by copying an existing OrderDetails instance
         *
         * @param other The existing instance to copy.
         */
        private Builder(com.example.kafka.avro.model.OrderDetails other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.orderId)) {
                this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.totalAmount)) {
                this.totalAmount = data().deepCopy(fields()[1].schema(), other.totalAmount);
                fieldSetFlags()[1] = true;
            }
        }

        /**
         * Gets the value of the 'orderId' field.
         *
         * @return The value.
         */
        public java.util.UUID getOrderId() {
            return orderId;
        }


        /**
         * Sets the value of the 'orderId' field.
         *
         * @param value The value of 'orderId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderDetails.Builder setOrderId(java.util.UUID value) {
            validate(fields()[0], value);
            this.orderId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'orderId' field has been set.
         *
         * @return True if the 'orderId' field has been set, false otherwise.
         */
        public boolean hasOrderId() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'orderId' field.
         *
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderDetails.Builder clearOrderId() {
            orderId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'totalAmount' field.
         *
         * @return The value.
         */
        public java.nio.ByteBuffer getTotalAmount() {
            return totalAmount;
        }


        /**
         * Sets the value of the 'totalAmount' field.
         * @param value The value of 'totalAmount'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderDetails.Builder setTotalAmount(java.nio.ByteBuffer value) {
            validate(fields()[1], value);
            this.totalAmount = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'totalAmount' field has been set.
         *
         * @return True if the 'totalAmount' field has been set, false otherwise.
         */
        public boolean hasTotalAmount() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'totalAmount' field.
         *
         * @return This builder.
         */
        public com.example.kafka.avro.model.OrderDetails.Builder clearTotalAmount() {
            totalAmount = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public OrderDetails build() {
            try {
                OrderDetails record = new OrderDetails();
                record.orderId = fieldSetFlags()[0] ? this.orderId : (java.util.UUID) defaultValue(fields()[0]);
                record.totalAmount = fieldSetFlags()[1] ? this.totalAmount : (java.nio.ByteBuffer) defaultValue(fields()[1]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<OrderDetails>
        WRITER$ = (org.apache.avro.io.DatumWriter<OrderDetails>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<OrderDetails>
    READER$ = (org.apache.avro.io.DatumReader<OrderDetails>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










