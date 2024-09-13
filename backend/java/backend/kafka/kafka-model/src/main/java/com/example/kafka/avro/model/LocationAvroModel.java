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
public class LocationAvroModel extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = -6037790467884780652L;


    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"LocationAvroModel\",\"namespace\":\"com.example.kafka.avro.model\",\"fields\":[{\"name\":\"driverId\",\"type\":\"string\"},{\"name\":\"edgeId\",\"type\":\"string\"},{\"name\":\"oldEdgeId\",\"type\":\"string\"},{\"name\":\"coord\",\"type\":{\"type\":\"record\",\"name\":\"Coordinates\",\"fields\":[{\"name\":\"lat\",\"type\":\"float\"},{\"name\":\"lon\",\"type\":\"float\"}]}},{\"name\":\"createdAt\",\"type\":[\"null\",\"long\"],\"logicalType\":[\"null\",\"date\"]}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<LocationAvroModel> ENCODER =
        new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<LocationAvroModel> DECODER =
        new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     *
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<LocationAvroModel> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     *
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<LocationAvroModel> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     *
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<LocationAvroModel> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this LocationAvroModel to a ByteBuffer.
     *
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a LocationAvroModel from a ByteBuffer.
     *
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a LocationAvroModel instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static LocationAvroModel fromByteBuffer(
        java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.CharSequence driverId;
    private java.lang.CharSequence edgeId;
    private java.lang.CharSequence oldEdgeId;
    private com.example.kafka.avro.model.Coordinates coord;
    private java.lang.Long createdAt;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public LocationAvroModel() {
    }

    /**
     * All-args constructor.
     *
     * @param driverId  The new value for driverId
     * @param edgeId    The new value for edgeId
     * @param oldEdgeId The new value for oldEdgeId
     * @param coord     The new value for coord
     * @param createdAt The new value for createdAt
     */
    public LocationAvroModel(java.lang.CharSequence driverId, java.lang.CharSequence edgeId, java.lang.CharSequence oldEdgeId, com.example.kafka.avro.model.Coordinates coord, java.lang.Long createdAt) {
        this.driverId = driverId;
        this.edgeId = edgeId;
        this.oldEdgeId = oldEdgeId;
        this.coord = coord;
        this.createdAt = createdAt;
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
                return driverId;
            case 1:
                return edgeId;
            case 2:
                return oldEdgeId;
            case 3:
                return coord;
            case 4:
                return createdAt;
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
                driverId = (java.lang.CharSequence) value$;
                break;
            case 1:
                edgeId = (java.lang.CharSequence) value$;
                break;
            case 2:
                oldEdgeId = (java.lang.CharSequence) value$;
                break;
            case 3:
                coord = (com.example.kafka.avro.model.Coordinates) value$;
                break;
            case 4:
                createdAt = (java.lang.Long)value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'driverId' field.
     * @return The value of the 'driverId' field.
     */
    public java.lang.CharSequence getDriverId() {
        return driverId;
    }


    /**
     * Sets the value of the 'driverId' field.
     * @param value the value to set.
     */
    public void setDriverId(java.lang.CharSequence value) {
        this.driverId = value;
    }

    /**
     * Gets the value of the 'edgeId' field.
     * @return The value of the 'edgeId' field.
     */
    public java.lang.CharSequence getEdgeId() {
        return edgeId;
    }


    /**
     * Sets the value of the 'edgeId' field.
     * @param value the value to set.
     */
    public void setEdgeId(java.lang.CharSequence value) {
        this.edgeId = value;
    }

    /**
     * Gets the value of the 'oldEdgeId' field.
     * @return The value of the 'oldEdgeId' field.
     */
    public java.lang.CharSequence getOldEdgeId() {
        return oldEdgeId;
    }


    /**
     * Sets the value of the 'oldEdgeId' field.
     * @param value the value to set.
     */
    public void setOldEdgeId(java.lang.CharSequence value) {
        this.oldEdgeId = value;
    }

    /**
     * Gets the value of the 'coord' field.
     * @return The value of the 'coord' field.
     */
    public com.example.kafka.avro.model.Coordinates getCoord() {
        return coord;
    }


    /**
     * Sets the value of the 'coord' field.
     * @param value the value to set.
     */
    public void setCoord(com.example.kafka.avro.model.Coordinates value) {
        this.coord = value;
    }

    /**
     * Gets the value of the 'createdAt' field.
     * @return The value of the 'createdAt' field.
     */
    public java.lang.Long getCreatedAt() {
        return createdAt;
    }


    /**
     * Sets the value of the 'createdAt' field.
     * @param value the value to set.
     */
    public void setCreatedAt(java.lang.Long value) {
        this.createdAt = value;
    }

    /**
     * Creates a new LocationAvroModel RecordBuilder.
     *
     * @return A new LocationAvroModel RecordBuilder
     */
    public static com.example.kafka.avro.model.LocationAvroModel.Builder newBuilder() {
        return new com.example.kafka.avro.model.LocationAvroModel.Builder();
    }

    /**
     * Creates a new LocationAvroModel RecordBuilder by copying an existing Builder.
     *
     * @param other The existing builder to copy.
     * @return A new LocationAvroModel RecordBuilder
     */
    public static com.example.kafka.avro.model.LocationAvroModel.Builder newBuilder(com.example.kafka.avro.model.LocationAvroModel.Builder other) {
        if (other == null) {
            return new com.example.kafka.avro.model.LocationAvroModel.Builder();
        } else {
            return new com.example.kafka.avro.model.LocationAvroModel.Builder(other);
        }
    }

    /**
     * Creates a new LocationAvroModel RecordBuilder by copying an existing LocationAvroModel instance.
     *
     * @param other The existing instance to copy.
     * @return A new LocationAvroModel RecordBuilder
     */
    public static com.example.kafka.avro.model.LocationAvroModel.Builder newBuilder(com.example.kafka.avro.model.LocationAvroModel other) {
        if (other == null) {
            return new com.example.kafka.avro.model.LocationAvroModel.Builder();
        } else {
            return new com.example.kafka.avro.model.LocationAvroModel.Builder(other);
        }
    }

    /**
     * RecordBuilder for LocationAvroModel instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<LocationAvroModel>
        implements org.apache.avro.data.RecordBuilder<LocationAvroModel> {

        private java.lang.CharSequence driverId;
        private java.lang.CharSequence edgeId;
        private java.lang.CharSequence oldEdgeId;
        private com.example.kafka.avro.model.Coordinates coord;
        private com.example.kafka.avro.model.Coordinates.Builder coordBuilder;
        private java.lang.Long createdAt;

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
        private Builder(com.example.kafka.avro.model.LocationAvroModel.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.driverId)) {
                this.driverId = data().deepCopy(fields()[0].schema(), other.driverId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.edgeId)) {
                this.edgeId = data().deepCopy(fields()[1].schema(), other.edgeId);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.oldEdgeId)) {
                this.oldEdgeId = data().deepCopy(fields()[2].schema(), other.oldEdgeId);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (isValidValue(fields()[3], other.coord)) {
                this.coord = data().deepCopy(fields()[3].schema(), other.coord);
                fieldSetFlags()[3] = other.fieldSetFlags()[3];
            }
            if (other.hasCoordBuilder()) {
                this.coordBuilder = com.example.kafka.avro.model.Coordinates.newBuilder(other.getCoordBuilder());
            }
            if (isValidValue(fields()[4], other.createdAt)) {
                this.createdAt = data().deepCopy(fields()[4].schema(), other.createdAt);
                fieldSetFlags()[4] = other.fieldSetFlags()[4];
            }
        }

        /**
         * Creates a Builder by copying an existing LocationAvroModel instance
         *
         * @param other The existing instance to copy.
         */
        private Builder(com.example.kafka.avro.model.LocationAvroModel other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.driverId)) {
                this.driverId = data().deepCopy(fields()[0].schema(), other.driverId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.edgeId)) {
                this.edgeId = data().deepCopy(fields()[1].schema(), other.edgeId);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.oldEdgeId)) {
                this.oldEdgeId = data().deepCopy(fields()[2].schema(), other.oldEdgeId);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.coord)) {
                this.coord = data().deepCopy(fields()[3].schema(), other.coord);
                fieldSetFlags()[3] = true;
            }
            this.coordBuilder = null;
            if (isValidValue(fields()[4], other.createdAt)) {
                this.createdAt = data().deepCopy(fields()[4].schema(), other.createdAt);
                fieldSetFlags()[4] = true;
            }
        }

        /**
         * Gets the value of the 'driverId' field.
         *
         * @return The value.
         */
        public java.lang.CharSequence getDriverId() {
            return driverId;
        }


        /**
         * Sets the value of the 'driverId' field.
         *
         * @param value The value of 'driverId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder setDriverId(java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.driverId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'driverId' field has been set.
         *
         * @return True if the 'driverId' field has been set, false otherwise.
         */
        public boolean hasDriverId() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'driverId' field.
         *
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder clearDriverId() {
            driverId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'edgeId' field.
         *
         * @return The value.
         */
        public java.lang.CharSequence getEdgeId() {
            return edgeId;
        }


        /**
         * Sets the value of the 'edgeId' field.
         * @param value The value of 'edgeId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder setEdgeId(java.lang.CharSequence value) {
            validate(fields()[1], value);
            this.edgeId = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'edgeId' field has been set.
         *
         * @return True if the 'edgeId' field has been set, false otherwise.
         */
        public boolean hasEdgeId() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'edgeId' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder clearEdgeId() {
            edgeId = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'oldEdgeId' field.
         *
         * @return The value.
         */
        public java.lang.CharSequence getOldEdgeId() {
            return oldEdgeId;
        }


        /**
         * Sets the value of the 'oldEdgeId' field.
         * @param value The value of 'oldEdgeId'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder setOldEdgeId(java.lang.CharSequence value) {
            validate(fields()[2], value);
            this.oldEdgeId = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'oldEdgeId' field has been set.
         * @return True if the 'oldEdgeId' field has been set, false otherwise.
         */
        public boolean hasOldEdgeId() {
            return fieldSetFlags()[2];
        }


        /**
         * Clears the value of the 'oldEdgeId' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder clearOldEdgeId() {
            oldEdgeId = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /**
         * Gets the value of the 'coord' field.
         *
         * @return The value.
         */
        public com.example.kafka.avro.model.Coordinates getCoord() {
            return coord;
        }


        /**
         * Sets the value of the 'coord' field.
         * @param value The value of 'coord'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder setCoord(com.example.kafka.avro.model.Coordinates value) {
            validate(fields()[3], value);
            this.coordBuilder = null;
            this.coord = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /**
         * Checks whether the 'coord' field has been set.
         *
         * @return True if the 'coord' field has been set, false otherwise.
         */
        public boolean hasCoord() {
            return fieldSetFlags()[3];
        }

        /**
         * Gets the Builder instance for the 'coord' field and creates one if it doesn't exist yet.
         * @return This builder.
         */
        public com.example.kafka.avro.model.Coordinates.Builder getCoordBuilder() {
            if (coordBuilder == null) {
                if (hasCoord()) {
                    setCoordBuilder(com.example.kafka.avro.model.Coordinates.newBuilder(coord));
                } else {
                    setCoordBuilder(com.example.kafka.avro.model.Coordinates.newBuilder());
                }
            }
            return coordBuilder;
        }

        /**
         * Sets the Builder instance for the 'coord' field
         *
         * @param value The builder instance that must be set.
         * @return This builder.
         */

        public com.example.kafka.avro.model.LocationAvroModel.Builder setCoordBuilder(com.example.kafka.avro.model.Coordinates.Builder value) {
            clearCoord();
            coordBuilder = value;
            return this;
        }

        /**
         * Checks whether the 'coord' field has an active Builder instance
         * @return True if the 'coord' field has an active Builder instance
         */
        public boolean hasCoordBuilder() {
            return coordBuilder != null;
        }

        /**
         * Clears the value of the 'coord' field.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder clearCoord() {
            coord = null;
            coordBuilder = null;
            fieldSetFlags()[3] = false;
            return this;
        }

        /**
         * Gets the value of the 'createdAt' field.
         *
         * @return The value.
         */
        public java.lang.Long getCreatedAt() {
            return createdAt;
        }


        /**
         * Sets the value of the 'createdAt' field.
         *
         * @param value The value of 'createdAt'.
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder setCreatedAt(java.lang.Long value) {
            validate(fields()[4], value);
            this.createdAt = value;
            fieldSetFlags()[4] = true;
            return this;
        }

        /**
         * Checks whether the 'createdAt' field has been set.
         * @return True if the 'createdAt' field has been set, false otherwise.
         */
        public boolean hasCreatedAt() {
            return fieldSetFlags()[4];
        }


        /**
         * Clears the value of the 'createdAt' field.
         *
         * @return This builder.
         */
        public com.example.kafka.avro.model.LocationAvroModel.Builder clearCreatedAt() {
            createdAt = null;
            fieldSetFlags()[4] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public LocationAvroModel build() {
            try {
                LocationAvroModel record = new LocationAvroModel();
                record.driverId = fieldSetFlags()[0] ? this.driverId : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.edgeId = fieldSetFlags()[1] ? this.edgeId : (java.lang.CharSequence) defaultValue(fields()[1]);
                record.oldEdgeId = fieldSetFlags()[2] ? this.oldEdgeId : (java.lang.CharSequence) defaultValue(fields()[2]);
                if (coordBuilder != null) {
                    try {
                        record.coord = this.coordBuilder.build();
                    } catch (org.apache.avro.AvroMissingFieldException e) {
                        e.addParentField(record.getSchema().getField("coord"));
                        throw e;
                    }
                } else {
                    record.coord = fieldSetFlags()[3] ? this.coord : (com.example.kafka.avro.model.Coordinates) defaultValue(fields()[3]);
                }
                record.createdAt = fieldSetFlags()[4] ? this.createdAt : (java.lang.Long) defaultValue(fields()[4]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<LocationAvroModel>
        WRITER$ = (org.apache.avro.io.DatumWriter<LocationAvroModel>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<LocationAvroModel>
        READER$ = (org.apache.avro.io.DatumReader<LocationAvroModel>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override
    protected boolean hasCustomCoders() {
        return true;
    }

    @Override
    public void customEncode(org.apache.avro.io.Encoder out)
        throws java.io.IOException {
        out.writeString(this.driverId);

        out.writeString(this.edgeId);

        out.writeString(this.oldEdgeId);

        this.coord.customEncode(out);

        if (this.createdAt == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeLong(this.createdAt);
        }

    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in)
        throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.driverId = in.readString(this.driverId instanceof Utf8 ? (Utf8) this.driverId : null);

            this.edgeId = in.readString(this.edgeId instanceof Utf8 ? (Utf8) this.edgeId : null);

            this.oldEdgeId = in.readString(this.oldEdgeId instanceof Utf8 ? (Utf8) this.oldEdgeId : null);

            if (this.coord == null) {
                this.coord = new com.example.kafka.avro.model.Coordinates();
            }
            this.coord.customDecode(in);

            if (in.readIndex() != 1) {
                in.readNull();
                this.createdAt = null;
            } else {
                this.createdAt = in.readLong();
            }

        } else {
            for (int i = 0; i < 5; i++) {
                switch (fieldOrder[i].pos()) {
        case 0:
          this.driverId = in.readString(this.driverId instanceof Utf8 ? (Utf8)this.driverId : null);
          break;

        case 1:
          this.edgeId = in.readString(this.edgeId instanceof Utf8 ? (Utf8)this.edgeId : null);
          break;

        case 2:
          this.oldEdgeId = in.readString(this.oldEdgeId instanceof Utf8 ? (Utf8)this.oldEdgeId : null);
          break;

        case 3:
          if (this.coord == null) {
            this.coord = new com.example.kafka.avro.model.Coordinates();
          }
          this.coord.customDecode(in);
          break;

        case 4:
          if (in.readIndex() != 1) {
            in.readNull();
            this.createdAt = null;
          } else {
            this.createdAt = in.readLong();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










