// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Commodity.proto

package com.ljh.gamedemo.proto.protoc;

public final class CommodityProto {
  private CommodityProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface CommodityOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Commodity)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 id = 1;</code>
     */
    long getId();

    /**
     * <code>int32 type = 2;</code>
     */
    int getType();

    /**
     * <code>int32 price = 3;</code>
     */
    int getPrice();

    /**
     * <code>int32 limit = 4;</code>
     */
    int getLimit();
  }
  /**
   * Protobuf type {@code Commodity}
   */
  public  static final class Commodity extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Commodity)
      CommodityOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Commodity.newBuilder() to construct.
    private Commodity(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Commodity() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Commodity();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Commodity(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              id_ = input.readInt64();
              break;
            }
            case 16: {

              type_ = input.readInt32();
              break;
            }
            case 24: {

              price_ = input.readInt32();
              break;
            }
            case 32: {

              limit_ = input.readInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ljh.gamedemo.proto.protoc.CommodityProto.internal_static_Commodity_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ljh.gamedemo.proto.protoc.CommodityProto.internal_static_Commodity_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.class, com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int TYPE_FIELD_NUMBER = 2;
    private int type_;
    /**
     * <code>int32 type = 2;</code>
     */
    public int getType() {
      return type_;
    }

    public static final int PRICE_FIELD_NUMBER = 3;
    private int price_;
    /**
     * <code>int32 price = 3;</code>
     */
    public int getPrice() {
      return price_;
    }

    public static final int LIMIT_FIELD_NUMBER = 4;
    private int limit_;
    /**
     * <code>int32 limit = 4;</code>
     */
    public int getLimit() {
      return limit_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (id_ != 0L) {
        output.writeInt64(1, id_);
      }
      if (type_ != 0) {
        output.writeInt32(2, type_);
      }
      if (price_ != 0) {
        output.writeInt32(3, price_);
      }
      if (limit_ != 0) {
        output.writeInt32(4, limit_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (id_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, id_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, type_);
      }
      if (price_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, price_);
      }
      if (limit_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, limit_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity)) {
        return super.equals(obj);
      }
      com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity other = (com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity) obj;

      if (getId()
          != other.getId()) return false;
      if (getType()
          != other.getType()) return false;
      if (getPrice()
          != other.getPrice()) return false;
      if (getLimit()
          != other.getLimit()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + PRICE_FIELD_NUMBER;
      hash = (53 * hash) + getPrice();
      hash = (37 * hash) + LIMIT_FIELD_NUMBER;
      hash = (53 * hash) + getLimit();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Commodity}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Commodity)
        com.ljh.gamedemo.proto.protoc.CommodityProto.CommodityOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ljh.gamedemo.proto.protoc.CommodityProto.internal_static_Commodity_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ljh.gamedemo.proto.protoc.CommodityProto.internal_static_Commodity_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.class, com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.Builder.class);
      }

      // Construct using com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        id_ = 0L;

        type_ = 0;

        price_ = 0;

        limit_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ljh.gamedemo.proto.protoc.CommodityProto.internal_static_Commodity_descriptor;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity getDefaultInstanceForType() {
        return com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.getDefaultInstance();
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity build() {
        com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity buildPartial() {
        com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity result = new com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity(this);
        result.id_ = id_;
        result.type_ = type_;
        result.price_ = price_;
        result.limit_ = limit_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity) {
          return mergeFrom((com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity other) {
        if (other == com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity.getDefaultInstance()) return this;
        if (other.getId() != 0L) {
          setId(other.getId());
        }
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (other.getPrice() != 0) {
          setPrice(other.getPrice());
        }
        if (other.getLimit() != 0) {
          setLimit(other.getLimit());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long id_ ;
      /**
       * <code>int64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder setId(long value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder clearId() {
        
        id_ = 0L;
        onChanged();
        return this;
      }

      private int type_ ;
      /**
       * <code>int32 type = 2;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <code>int32 type = 2;</code>
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 type = 2;</code>
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private int price_ ;
      /**
       * <code>int32 price = 3;</code>
       */
      public int getPrice() {
        return price_;
      }
      /**
       * <code>int32 price = 3;</code>
       */
      public Builder setPrice(int value) {
        
        price_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 price = 3;</code>
       */
      public Builder clearPrice() {
        
        price_ = 0;
        onChanged();
        return this;
      }

      private int limit_ ;
      /**
       * <code>int32 limit = 4;</code>
       */
      public int getLimit() {
        return limit_;
      }
      /**
       * <code>int32 limit = 4;</code>
       */
      public Builder setLimit(int value) {
        
        limit_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 limit = 4;</code>
       */
      public Builder clearLimit() {
        
        limit_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Commodity)
    }

    // @@protoc_insertion_point(class_scope:Commodity)
    private static final com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity();
    }

    public static com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Commodity>
        PARSER = new com.google.protobuf.AbstractParser<Commodity>() {
      @java.lang.Override
      public Commodity parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Commodity(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Commodity> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Commodity> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.ljh.gamedemo.proto.protoc.CommodityProto.Commodity getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Commodity_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Commodity_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017Commodity.proto\"C\n\tCommodity\022\n\n\002id\030\001 \001" +
      "(\003\022\014\n\004type\030\002 \001(\005\022\r\n\005price\030\003 \001(\005\022\r\n\005limit" +
      "\030\004 \001(\005B/\n\035com.ljh.gamedemo.proto.protocB" +
      "\016CommodityProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Commodity_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Commodity_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Commodity_descriptor,
        new java.lang.String[] { "Id", "Type", "Price", "Limit", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
